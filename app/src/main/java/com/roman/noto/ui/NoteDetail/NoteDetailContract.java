package com.roman.noto.ui.NoteDetail;

import com.roman.noto.data.Note;

public interface NoteDetailContract {
    interface View {
        //Установить интерфейс заметки
        void noteView(Note note);
        //Завершить activity
        void returnToMain();
    }

    interface Presenter {
        void loadNoteById(String id);
        void saveNote(Note note);
        void archiveNote(Note note);
        void cloneNote(Note note);
        void createNote();

        //Удалить заметку из базы
        void deleteNote(Note note);
        //Сменить статус наметки на archive = false
        void restoreNote(Note note);
    }
}
