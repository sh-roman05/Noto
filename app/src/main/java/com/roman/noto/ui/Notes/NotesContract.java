package com.roman.noto.ui.Notes;

import com.roman.noto.data.Note;
import com.roman.noto.data.callback.GetHashtagsForAdapterCallback;
import com.roman.noto.util.NoteColor;

import java.util.List;

public interface NotesContract {

    interface View {
        void showNotes(List<Note> notes);
        void editNote(Note target);


    }

    interface Presenter {
        void loadNotes();
        void loadArchiveNotes();
        void addNote();
        void swapNotes(Note fromPosition, Note toPosition);
        void clickNote(Note target);
        void archiveNote(Note note);
        //void deleteNote(Note note);

        //Удалить все архивированные заметки
        void clearArchiveNotes();
        //Восстановить заметки по списку
        void restoreNotesList(List<Note> notes);
        //Удалить заметки по списку
        void deleteNotesList(List<Note> notes);
        //Архивировать заметки по списку
        void archiveNotesList(List<Note> notes);
        //Изменить цвета заметок по списку
        void changeColorNotesList(List<Note> notes, NoteColor.ItemColor item);

        //Получить хештеги для адаптера
        void getHashtagsForAdapter(final GetHashtagsForAdapterCallback callback);
    }
}
