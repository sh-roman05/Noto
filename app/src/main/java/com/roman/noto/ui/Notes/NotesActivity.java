package com.roman.noto.ui.Notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.common.base.Strings;
import com.roman.noto.RecyclerViewEmptySupport;
import com.roman.noto.ui.About.AboutActivity;
import com.roman.noto.data.Note;
import com.roman.noto.ui.Settings.SettingsActivity;
import com.roman.noto.util.NoteColor;
import com.roman.noto.ui.NoteDetail.NoteDetailActivity;
import com.roman.noto.NoteTouchHelperClass;
import com.roman.noto.R;
import com.roman.noto.data.repository.CacheRepository;
import com.roman.noto.data.repository.LocalRepository;
import com.roman.noto.util.AppExecutors;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class NotesActivity extends AppCompatActivity implements NotesContract.View {

    //todo В старом приложении огромное количество полезного кода
    static final String TAG = "NotesActivity";

    public NotesContract.Presenter presenter;
    RecyclerViewEmptySupport mainNoteView;
    FloatingActionButton addNoteButton;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    View emptyView;
    //Toolbar
    Toolbar toolbar;
    AppBarLayout appBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        toolbar = findViewById(R.id.activity_notes_toolbar);
        toolbar.setTitle(getString(R.string.navigation_view_notes));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        //Инициализация
        mainNoteView = (RecyclerViewEmptySupport) findViewById(R.id.activity_notes_list);
        addNoteButton = (FloatingActionButton) findViewById(R.id.activity_notes_add_button);
        navigationView = (NavigationView) findViewById(R.id.activity_notes_navigation);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_notes_drawer);
        appBarLayout = (AppBarLayout) findViewById(R.id.activity_notes_collapsing_appbar);

        View emptyView = findViewById(R.id.activity_notes_empty_view);
        mainNoteView.setEmptyView(emptyView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.navigation_menu_notes)
                {
                    toolbar.setTitle(getString(R.string.navigation_view_notes));
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.notes_toolbar);
                    addNoteButton.show();
                    presenter.loadNotes();
                }
                if (id == R.id.navigation_menu_archive)
                {
                    toolbar.setTitle(getString(R.string.navigation_view_archive));
                    //Сменить меню
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.notes_toolbar_archive);
                    addNoteButton.hide();
                    presenter.loadArchiveNotes();
                    appBarLayout.setExpanded(true);
                }
                if (id == R.id.navigation_menu_settings)
                {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.navigation_menu_about)
                {
                    Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                    startActivity(intent);
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_notes_drawer);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });


        /* Передаем в Presenter - View и Repository */
        presenter = new NotesPresenter(this, CacheRepository.getInstance(
                LocalRepository.getRepository(
                        new AppExecutors(), getApplicationContext()
                )
        ));



        mainNoteView.addItemDecoration(new SpacesItemDecoration(8));
        mainNoteView.setAdapter(adapter);

        presenter.loadNotes();

        //Кнопка добавления записи
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.addNote();

            }
        });

        ItemTouchHelper itemTouchHelper;
        ItemTouchHelper.Callback callback = new NoteTouchHelperClass(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mainNoteView);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Восстановить заметку
            case R.id.activity_notes_archive_delete_all:
                DialogInterface.OnClickListener alertCallback = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.clearArchiveNotes();
                    }
                };
                new MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.activity_notes_delete_archive))
                        .setMessage(getString(R.string.activity_notes_delete_archive_text))
                        .setPositiveButton(getString(R.string.activity_notes_delete_archive_yes), alertCallback)
                        .setNegativeButton(getString(R.string.activity_notes_delete_archive_no), null)
                        .show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getApplicationContext()));
        String temp = settings.getString("columns_list", "2");

        int columns = Integer.valueOf(temp);
        mainNoteView.setLayoutManager(new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL));


        MenuItem item = navigationView.getCheckedItem();

        if(Objects.requireNonNull(item).getItemId() == R.id.navigation_menu_notes)
        {
            presenter.loadNotes();
            toolbar.setTitle(getString(R.string.navigation_view_notes));
        }
        if(item.getItemId() == R.id.navigation_menu_archive)
        {
            presenter.loadArchiveNotes();
            toolbar.setTitle(getString(R.string.navigation_view_archive));
        }
    }


    @Override
    public void showNotes(List<Note> notes) {
        adapter.replaceData(notes);
    }


    @Override
    //Заметка от Presenter, которую нужно отредактировать
    public void editNote(String targetId) {
        Intent intent = new Intent(getApplicationContext(), NoteDetailActivity.class);
        intent.putExtra("id", targetId);
        intent.putExtra("from", "main_edit_note");
        startActivity(intent);
    }

    NotesAdapter adapter = new NotesAdapter(new ArrayList<Note>(), new NoteListener() {
        @Override
        public void onItemClick(String targetId) {
            presenter.clickNote(targetId);
        }
    });


    public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> implements NoteTouchHelperClass.ItemTouchHelperAdapter {
        private List<Note> list;
        private NoteListener listener;

        NotesAdapter(List<Note> notes, NoteListener listener) {
            setList(notes);
            this.listener = listener;
        }

        @NonNull
        @Override
        public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_view, null);
            return new NoteViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
            Note item = list.get(position);

            if(Strings.isNullOrEmpty(item.getTitle())) {
                holder.title.setVisibility(View.GONE);
            } else {
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setText(item.getTitle());
            }
            if(Strings.isNullOrEmpty(item.getText()))
                holder.text.setVisibility(View.GONE);
            else holder.text.setText(item.getText());

            holder.card.setCardBackgroundColor(Color.parseColor(NoteColor.getInstance().getItemColor(item.getColor()).getColorBackground()));
        }

        public List<Note> getList() {
            return list;
        }

        @Override
        public int getItemCount() {
            return (list != null ? list.size() : 0);
        }

        public void replaceData(List<Note> notes) {
            setList(notes);
            notifyDataSetChanged();
        }

        private void setList(List<Note> notes) {
            this.list = notes;
        }

        @Override
        public void onItemMoved(int fromPosition, int toPosition) {
            presenter.swapNotes(list.get(fromPosition), list.get(toPosition));
            Collections.swap(list, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRemoved(int position) {
            Note item = list.get(position);
            presenter.archiveNote(item);
            //Удалить из адаптера
            list.remove(position);
            notifyItemRemoved(position);
        }


        class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            TextView title;
            TextView text;
            CardView card;
            List<Note> innerList;

            public NoteViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                itemView.setLongClickable(true);
                itemView.setOnLongClickListener(this);
                this.title = (TextView) itemView.findViewById(R.id.notes_list_view_title);
                this.text = (TextView) itemView.findViewById(R.id.notes_list_view_text);
                this.card = (CardView) itemView.findViewById(R.id.notes_list_card_view);
                innerList = list;
            }

            @Override
            public void onClick(View v) {
                //Обрабатывать клик
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(list.get(adapterPosition).getId());
                }
            }

            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        }
    }




    public interface NoteListener {
        void onItemClick(String targetId);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration
    {
        private int space;
        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
        {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.right = space / 2;
            outRect.left = space / 2;
            outRect.top = space / 4;
            outRect.bottom = space / 4;
        }
    }
}
