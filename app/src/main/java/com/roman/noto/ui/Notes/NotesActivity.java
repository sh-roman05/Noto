package com.roman.noto.ui.Notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.OnDragInitiatedListener;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageButton;


import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.common.collect.Lists;
import com.roman.noto.RecyclerViewEmptySupport;
import com.roman.noto.data.repository.NavigationHashtag;
import com.roman.noto.ui.About.AboutActivity;
import com.roman.noto.data.Note;
import com.roman.noto.ui.EditHashtags.EditHashtagsActivity;
import com.roman.noto.ui.Settings.SettingsActivity;
import com.roman.noto.util.NoteColor;
import com.roman.noto.ui.NoteDetail.NoteDetailActivity;
import com.roman.noto.NoteTouchHelperClass;
import com.roman.noto.R;
import com.roman.noto.data.repository.CacheRepository;
import com.roman.noto.data.repository.LocalRepository;
import com.roman.noto.util.AppExecutors;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class NotesActivity extends AppCompatActivity implements NotesContract.View {

    static final String TAG = "NotesActivity";

    public NotesContract.Presenter presenter;
    RecyclerViewEmptySupport mainNoteView;
    FloatingActionButton addNoteButton;

    //Toolbar
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;

    //Выделение заметок
    private SelectionTracker<Note> mSelectionTracker;
    //Замена Toolbar в режиме выделения заметок
    private ActionMode actionMode;

    View emptyView;

    //Адаптер
    NotesAdapter adapter;

    //NavigationView
    View buttonNotes, buttonDelete, buttonSettings, buttonAbout, buttonAddHashtag, buttonChange;
    View nvNoHashtags, nvHasHashtags;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    //NotesState
    NotesState state = NotesState.NOTES;

    //Хештеги в навигации
    RecyclerView navHashtagsRecyclerView;
    NotesHashtagsAdapter hashtagsAdapter;
    List<NavigationHashtag> navHashtagsList;
    NavigationHashtag selectedHashtag;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        toolbar = findViewById(R.id.activity_notes_toolbar);
        toolbar.setTitle(getString(R.string.navigation_view_notes));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //Инициализация
        mainNoteView = findViewById(R.id.activity_notes_list);
        addNoteButton = findViewById(R.id.activity_notes_add_button);
        appBarLayout = findViewById(R.id.activity_notes_collapsing_appbar);
        emptyView = findViewById(R.id.activity_notes_empty_view);
        //NavigationView
        buttonNotes = findViewById(R.id.activity_notes_nv_notes);
        buttonDelete = findViewById(R.id.activity_notes_nv_delete);
        buttonSettings = findViewById(R.id.activity_notes_nv_settings);
        buttonAbout = findViewById(R.id.activity_notes_nv_about);
        buttonAddHashtag = findViewById(R.id.activity_notes_nv_add_hashtag);
        buttonChange = findViewById(R.id.activity_notes_nv_hashtags_change);
        navHashtagsRecyclerView = findViewById(R.id.activity_notes_hashtags_list);
        navigationView = findViewById(R.id.activity_notes_navigation);
        drawerLayout = findViewById(R.id.activity_notes_drawer);
        nvNoHashtags = findViewById(R.id.activity_notes_nv_no_hashtags);
        nvHasHashtags = findViewById(R.id.activity_notes_nv_has_hashtags);
        //
        //
        //Выделенная по умолчанию кнопка
        navSelectNotes();








        /* Передаем в Presenter - View и Repository */
        presenter = new NotesPresenter(this, CacheRepository.getInstance(
                LocalRepository.getRepository(
                        new AppExecutors(), getApplicationContext()
                )
        ));


        adapter = new NotesAdapter(new ArrayList<Note>(), new NoteListener() {
            @Override
            public void onItemClick(Note target) {
                presenter.clickNote(target);
            }
        }, presenter, getResources().getString(R.string.activity_notes_hashtag_more));

        adapter.setHasStableIds(true);




        mainNoteView.setEmptyView(emptyView);


        ItemTouchHelper itemTouchHelper;
        ItemTouchHelper.Callback callback = new NoteTouchHelperClass(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mainNoteView);

        mainNoteView.addItemDecoration(new SpacesItemDecoration(8));
        mainNoteView.setAdapter(adapter);

        mSelectionTracker = new SelectionTracker.Builder<>(
                "note-items-selection",
                mainNoteView,
                new NotesAdapter.NoteItemKeyProvider(ItemKeyProvider.SCOPE_MAPPED, adapter.getList()),
                new NotesAdapter.NoteItemDetailsLookup(mainNoteView),
                StorageStrategy.createParcelableStorage(Note.class)
        ).withSelectionPredicate(SelectionPredicates.<Note>createSelectAnything()).withOnDragInitiatedListener(new OnDragInitiatedListener() {
            @Override
            public boolean onDragInitiated(@NonNull MotionEvent e) {
                Log.i(TAG, "onDragInitiated");
                return true;
            }
        }).build();

        adapter.setSelectionTracker(mSelectionTracker);



        //Callback для отслеживания событий SelectionTracker
        mSelectionTracker.addObserver(new SelectionTracker.SelectionObserver<Note>() {
            @Override
            public void onItemStateChanged(@NonNull Note key, boolean selected) {
                super.onItemStateChanged(key, selected);
                if(actionMode == null)
                {
                    if(mSelectionTracker.hasSelection()){
                        actionMode = startSupportActionMode(actionModeCallback);
                        if(actionMode != null) {
                            actionMode.setTitle(String.valueOf(1));
                        }
                        //adapter.notifyDataSetChanged();
                    }
                } else {
                    if(mSelectionTracker.hasSelection())
                    {
                        actionMode.setTitle(String.valueOf(mSelectionTracker.getSelection().size()));
                    }else {
                        actionMode.finish();
                    }
                }
            }

            @Override
            public void onSelectionRefresh() {
                super.onSelectionRefresh();
            }

            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
            }

            @Override
            public void onSelectionRestored() {
                super.onSelectionRestored();
            }
        });


        presenter.loadNotes();

        //Callback для отслеживания клика на FloatingActionButton
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (state){
                    case HASHTAGS:
                        presenter.addEmptyNoteWithHashtag(selectedHashtag);
                        break;
                    case NOTES:
                        presenter.addEmptyNote();
                        break;
                    default:
                        break;
                }
            }
        });



        //Callback для отслеживания событий DrawerLayout
        drawerLayout.addDrawerListener(drawerLayoutListener);


        buttonNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setTitle(getString(R.string.navigation_view_notes));
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.notes_toolbar);
                addNoteButton.show();
                presenter.loadNotes();
                //Выделить кнопку
                navSelectNotes();

                if(navHashtagsList != null) {
                    for (int i = 0; i < navHashtagsList.size(); i++) {
                        NavigationHashtag item = navHashtagsList.get(i);
                        if(item.isSelected()){
                            item.setSelected(false);
                            hashtagsAdapter.notifyItemChanged(i);
                        }
                    }
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                state = NotesState.NOTES;
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setTitle(getString(R.string.navigation_view_archive));
                //Сменить меню
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.notes_toolbar_archive);
                addNoteButton.hide();
                presenter.loadArchiveNotes();
                appBarLayout.setExpanded(true);
                //Выделить кнопку
                navSelectDelete();

                if(navHashtagsList != null) {
                    for (int i = 0; i < navHashtagsList.size(); i++) {
                        NavigationHashtag item = navHashtagsList.get(i);
                        if(item.isSelected()){
                            item.setSelected(false);
                            hashtagsAdapter.notifyItemChanged(i);
                        }
                    }
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                state = NotesState.DELETE;
            }
        });
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                //drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        buttonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                //drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        buttonAddHashtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo Станица редактирования
                //todo Добавить метку
                Intent intent = new Intent(getApplicationContext(), EditHashtagsActivity.class);
                startActivity(intent);
                //drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditHashtagsActivity.class);
                startActivity(intent);
                //drawerLayout.closeDrawer(GravityCompat.START);
            }
        });


        /* Хэштеги */
        hashtagsAdapter = new NotesHashtagsAdapter(hashtagListener);
        navHashtagsRecyclerView.setAdapter(hashtagsAdapter);
        navHashtagsRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        //


        //






    }

    private final DrawerLayout.DrawerListener drawerLayoutListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) { }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) { }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) { }

        @Override
        public void onDrawerStateChanged(int newState) {
            if(actionMode != null)
                actionMode.finish();
        }
    };

    //Клик по хэтегу в меню навигации
    private final NotesHashtagListener hashtagListener = new NotesHashtagListener() {
        @Override
        public void onItemClick(int position) {
            //Снять выделение с статичных кнопок
            navSelectClear();
            //Выделить этот элемент и снять с других
            //обновить данные на этот хештег

            if(navHashtagsList != null) {
                for (int i = 0; i < navHashtagsList.size(); i++) {
                    NavigationHashtag item = navHashtagsList.get(i);
                    if(item.isSelected()){
                        item.setSelected(false);
                        hashtagsAdapter.notifyItemChanged(i);
                    }
                }

                NavigationHashtag item = navHashtagsList.get(position);

                item.setSelected(true);
                hashtagsAdapter.notifyItemChanged(position);

                presenter.loadNotesWithHashtag(item.getId());

                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.notes_toolbar);
                toolbar.setTitle(item.getName());

                addNoteButton.show();


                //Запомнить выбранный хештег
                selectedHashtag = item;

            }


            //Статус: выбран хэштег
            state = NotesState.HASHTAGS;
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    };


    public void navSelectClear() {
        buttonNotes.setSelected(false);
        buttonDelete.setSelected(false);
    }
    public void navSelectNotes() {
        buttonNotes.setSelected(true);
        buttonDelete.setSelected(false);
    }
    public void navSelectDelete() {
        buttonNotes.setSelected(false);
        buttonDelete.setSelected(true);
    }




    //Callback для отслеживания событий ActionMode
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if(state == NotesState.NOTES || state == NotesState.HASHTAGS)
                mode.getMenuInflater().inflate(R.menu.notes_toolbar_action_mode, menu);
            if(state == NotesState.DELETE)
                mode.getMenuInflater().inflate(R.menu.notes_toolbar_action_mode_archive, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            //Заблокировать NavigationView
            //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            //Создать List на основе итератора
            Iterator<Note> iterator = mSelectionTracker.getSelection().iterator();
            List<Note> tempList = Lists.newArrayList(iterator);

            if(item.getItemId() == R.id.activity_notes_toolbar_archive){
                presenter.archiveNotesList(tempList);

                //Показать изменения
                adapter.deleteNotesFromList(tempList);
                //Выйти из режима выбора
                actionMode.finish();
            }

            if(item.getItemId() == R.id.activity_notes_toolbar_restore){
                presenter.restoreNotesList(tempList);

                //Показать изменения
                adapter.deleteNotesFromList(tempList);
                //Выйти из режима выбора
                actionMode.finish();
            }
            if(item.getItemId() == R.id.activity_notes_toolbar_delete){
                //
                presenter.deleteNotesList(tempList);
                //Показать изменения
                adapter.deleteNotesFromList(tempList);
                //Выйти из режима выбора
                actionMode.finish();

            }
            if(item.getItemId() == R.id.activity_notes_toolbar_color){

                if(dialog == null)
                {
                    //Ищем наш RecyclerView
                    @SuppressLint("InflateParams") View convertView = LayoutInflater.from(NotesActivity.this).inflate(R.layout.notes_list_alert_select_color, null);
                    RecyclerView colorList = convertView.findViewById(R.id.activity_notes_alert_color_list);
                    //Закидываем адаптер
                    NotesSelectColorAdapter notesSelectColorAdapter = new NotesSelectColorAdapter(notesSelectColorListener);
                    colorList.setAdapter(notesSelectColorAdapter);

                    FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getApplicationContext());
                    layoutManager.setFlexDirection(FlexDirection.ROW);
                    layoutManager.setJustifyContent(JustifyContent.FLEX_START);

                    colorList.setLayoutManager(layoutManager);
                    //Создаем окно
                    dialog = new MaterialAlertDialogBuilder(NotesActivity.this)
                            .setTitle(getString(R.string.activity_notes_change_color_title))
                            .setView(convertView)
                            .create();
                }
                dialog.show();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //Разблокировать NavigationView
            //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mSelectionTracker.clearSelection();
            actionMode = null;
        }
    };


    //Окно выбора цвета
    AlertDialog dialog;

    //Listener. Окно выбора цвета
    NotesSelectColorAdapter.NotesSelectColorListener notesSelectColorListener = new NotesSelectColorAdapter.NotesSelectColorListener() {
        @Override
        public void onItemClick(NoteColor.ItemColor item, ImageButton color, int adapterPosition) {
            Iterator<Note> iterator = mSelectionTracker.getSelection().iterator();
            List<Note> tempList = Lists.newArrayList(iterator);

            //Закинуть список в адаптер
            adapter.changeColorList(tempList, item);

            //Применить изменения
            presenter.changeColorNotesList(tempList, item);

            //Закрыть окно
            dialog.dismiss();

            //Выйти из режима выбора
            actionMode.finish();
        }
    };




    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
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

        int columns = 2;
        if(temp != null){
            columns = Integer.parseInt(temp);
        }
        mainNoteView.setLayoutManager(new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL));

        //Загрузить хэштеги в меню навигации
        presenter.loadHashtags();


        switch (state) {
            case NOTES:
                presenter.loadNotes();
                toolbar.setTitle(getString(R.string.navigation_view_notes));
                break;
            case DELETE:
                presenter.loadArchiveNotes();
                toolbar.setTitle(getString(R.string.navigation_view_archive));
                break;
            case HASHTAGS:
                if(selectedHashtag != null) {
                    //загрузить и выделить в панели
                    toolbar.setTitle(selectedHashtag.getName());
                    presenter.loadNotesWithHashtag(selectedHashtag.getId());
                    for (int i = 0; i < navHashtagsList.size(); i++) {
                        NavigationHashtag item = navHashtagsList.get(i);
                        if(item.getId().equals(selectedHashtag.getId())){
                            item.setSelected(true);
                            adapter.notifyItemChanged(i);
                        }

                    }
                }
                break;
        }


    }


    @Override
    public void showNotes(List<Note> notes) {
        adapter.replaceData(notes);
    }


    @Override
    //Заметка от Presenter, которую нужно отредактировать
    public void editNote(Note target) {
        Intent intent = new Intent(getApplicationContext(), NoteDetailActivity.class);
        intent.putExtra("note", target);
        intent.putExtra("from", "main_edit_note");
        startActivity(intent);
    }

    @Override
    public void applyHashtags(List<NavigationHashtag> hashtags) {
        if(hashtags.size() > 0) {
            nvHasHashtags.setVisibility(View.VISIBLE);
            nvNoHashtags.setVisibility(View.GONE);
        } else {
            nvHasHashtags.setVisibility(View.GONE);
            nvNoHashtags.setVisibility(View.VISIBLE);
        }
        navHashtagsList = hashtags;
        hashtagsAdapter.setList(hashtags);
    }


    public interface NoteListener {
        void onItemClick(Note target);
    }

    //Отступы для элементов RecyclerView
    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration
    {
        private final int space;
        SpacesItemDecoration(int space) {
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
