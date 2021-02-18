package com.roman.noto.ui.NoteDetail;

import com.roman.noto.data.Note;
import com.roman.noto.data.callback.GetHashtagsForAdapterCallback;

import java.util.List;

public interface NoteDetailContract {
    interface View {
        //Установить интерфейс заметки
        void noteView(Note note);
        //Завершить activity
        void returnToMain();
        //Показать выбранные хештеги в View
        void hashtagsShow(List<String> hashtags);
    }

    interface Presenter {
        void loadNote(Note id);
        void saveNote(Note note);
        void archiveNote(Note note);
        void cloneNote(Note note);
        void createNote();

        //Удалить заметку из базы
        void deleteNote(Note note);
        //Сменить статус наметки на archive = false
        void restoreNote(Note note);


        //Получить хештеги
        void getHashtags(Note note);
    }
}
