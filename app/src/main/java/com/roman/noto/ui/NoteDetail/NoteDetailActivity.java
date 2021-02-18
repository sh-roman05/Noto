package com.roman.noto.ui.NoteDetail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.roman.noto.data.Note;
import com.roman.noto.R;
import com.roman.noto.data.repository.CacheRepository;
import com.roman.noto.data.repository.LocalRepository;
import com.roman.noto.ui.ChooseHashtags.ChooseHashtagsActivity;
import com.roman.noto.ui.Notes.NotesActivity;
import com.roman.noto.ui.Settings.SettingsActivity;
import com.roman.noto.util.AppExecutors;
import com.roman.noto.util.NoteColor;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NoteDetailActivity extends AppCompatActivity implements NoteDetailContract.View {

    static final String TAG = "NoteDetailActivity";

    private NoteDetailContract.Presenter presenter;
    private Note note;

    private CoordinatorLayout layout;
    Toolbar toolbar;
    private EditText title;
    private EditText text;
    private TextView lastChange;

    ConstraintLayout scrollView;

    //Хештеги
    ChipGroup hashtagsGroup;
    FrameLayout hashtagsContainer;

    //Была ли активити вызвана с shortcut
    boolean shortcut;

    //Заглушка для перекидывания выделения
    View empty;

    //Откуда пришли в NoteDetail
    String from;


    //Меню
    private NoteDetailDialogFragment bottomSheetDialogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        //Настройка Toolbar
        toolbar = (Toolbar) findViewById(R.id.activity_note_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Инициализация
        title = findViewById(R.id.activity_note_detail_title);
        text = findViewById(R.id.activity_note_detail_text);
        layout = findViewById(R.id.activity_note_detail_layout);
        lastChange = findViewById(R.id.activity_note_detail_last_change);
        empty = findViewById(R.id.activity_note_detail_empty);
        scrollView = findViewById(R.id.activity_note_detail_container);
        hashtagsGroup = findViewById(R.id.activity_note_detail_hashtags_chip_group);
        hashtagsContainer = findViewById(R.id.activity_note_detail_hashtags_group);
        bottomSheetDialogFragment = new NoteDetailDialogFragment(menuCallback);


        //повторное создание пустых AppExecutors, стоит убрать
        presenter = new NoteDetailPresenter(this, CacheRepository.getInstance(
                LocalRepository.getRepository(
                        new AppExecutors(), getApplicationContext()
                )
        ));

        //Откуда пришли
        from = getIntent().getStringExtra("from");












        hashtagsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooseHashtagsActivity.class);
                intent.putExtra("note", note);
                startActivity(intent);
            }
        });

        scrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //text
                text.post(new Runnable() {
                    @Override
                    public void run() {
                        if(isHardwareKeyboardAvailable(getApplicationContext())){
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInputFromWindow(text.getApplicationWindowToken(),InputMethodManager.SHOW_IMPLICIT, 0);
                        }
                        text.requestFocus();
                        text.setSelection(text.getText().length());
                    }
                });
            }
        });
    }

    //Проверить на наличие физической клавиатуры
    private boolean isHardwareKeyboardAvailable(Context context) {
        return context.getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS;
    }


    NoteDetailDialogFragment.BottomMenuCallback menuCallback = new NoteDetailDialogFragment.BottomMenuCallback() {
        @Override
        public void copy() {
            presenter.cloneNote(note);
        }

        @Override
        public void archive() {
            presenter.archiveNote(note);
        }

        @Override
        public void share() {
            //todo отправка заметки в другие приложения

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_TEXT, text.getText());
            startActivity(Intent.createChooser(share, "Share with"));
        }

        @Override
        public void selectColor(NoteColor.ItemColor color) {
            //Layout Background
            layout.setBackgroundColor(Color.parseColor(color.getColorBackground()));
            //Toolbar
            toolbar.setBackgroundColor(Color.parseColor(color.getColorPrimaryDark()));
            //Сохранить цвет
            note.setColor(color.getIndex());
        }

        @Override
        public void hashtag() {
            Intent intent = new Intent(getApplicationContext(), ChooseHashtagsActivity.class);
            intent.putExtra("note", note);
            startActivity(intent);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        if(from != null) {
            switch (from) {
                case "shortcut_new_note":
                    shortcut = true;
                    //Создать новую заметку
                    presenter.createNote();
                    break;
                case "main_edit_note":
                    shortcut = false;
                    //Редактировать
                    Note id = getIntent().getParcelableExtra("note");
                    presenter.loadNote(id);
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        saveNote();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    void saveNote()
    {
        note.setTitle(title.getText().toString());
        note.setText(text.getText().toString());
        note.setLastChange(new Date());
        presenter.saveNote(note);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(note != null)
            if(note.isArchive())
                getMenuInflater().inflate(R.menu.note_detail_toolbar_archive, menu);
            else
                getMenuInflater().inflate(R.menu.note_detail_toolbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(shortcut) {
                    Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
                    startActivity(intent);
                } else {
                    finish();
                }
                return true;
            //Восстановить заметку
            //Снять блокировку редактирования
            case R.id.activity_note_detail_toolbar_restore:
                presenter.restoreNote(note);
                return true;
            //Удалить окончательно заметку
            case R.id.activity_note_detail_toolbar_delete:
                presenter.deleteNote(note);
                return true;
            //Вызов панели
            case R.id.activity_note_detail_toolbar_bottom_panel:
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void noteView(Note note) {
        this.note = note;
        boolean archive = note.isArchive();

        if(archive) {
            //Блокировать
            title.setEnabled(false);
            text.setEnabled(false);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.note_detail_toolbar_archive);
        } else {
            //Разблокировать
            title.setEnabled(true);
            text.setEnabled(true);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.note_detail_toolbar);
        }

        //Меню, отметить выбранный цвет
        bottomSheetDialogFragment.setColor(note.getColor());

        //Добавить данные
        title.setText(note.getTitle());
        text.setText(note.getText());

        //Последнее изменение
        Date date = note.getLastChange();
        //Берем строку из ресурсов и форматируем дату
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.activity_note_detail_last_change));
            lastChange.setText(dateFormat.format(date));
        }catch (Exception ex){
            lastChange.setText(date.toString());
        }

        //Получить объект цвета
        NoteColor.ItemColor color = NoteColor.getInstance().getItemColor(note.getColor());
        //Layout Background
        layout.setBackgroundColor(Color.parseColor(color.getColorBackground()));
        //Toolbar
        toolbar.setBackgroundColor(Color.parseColor(color.getColorPrimaryDark()));


        //Получить выбранные хештеги
        presenter.getHashtags(note);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void returnToMain() {
        finish();
    }

    @Override
    public void hashtagsShow(List<String> hashtags) {
        try {
            hashtagsGroup.removeAllViews();
            for(String hashtag: hashtags){
                Chip chip = (Chip) View.inflate(new ContextThemeWrapper(this, R.style.AppTheme), R.layout.chip_view, null);
                chip.setId(ViewCompat.generateViewId());
                chip.setText(hashtag);
                hashtagsGroup.addView(chip);
            }
        } catch (Exception ex) { Log.d(TAG, ex.toString()); }
    }
}
