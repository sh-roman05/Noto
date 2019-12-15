package com.roman.noto.data.repository;

import com.roman.noto.data.Note;
import com.roman.noto.data.callback.DeleteArchiveNotesCallback;
import com.roman.noto.data.callback.DeleteNoteCallback;
import com.roman.noto.data.callback.GetFirstPositionCallback;
import com.roman.noto.data.callback.GetNoteCallback;
import com.roman.noto.data.callback.LoadNotesCallback;

public interface Repository {

    void saveNote(Note note);
    void swapNotes(Note fromPosition, Note toPosition);
    void getNote(String id, GetNoteCallback callback);


    //Получить не удаленные заметки
    void getNotes(final LoadNotesCallback callback);
    //Получить удаленные заметки
    void getArchivedNotes(final LoadNotesCallback callback);
    //Обновить данные в заметке
    void updateNote(Note note);

    //Получить первую позицию
    void getFirstPosition(final GetFirstPositionCallback callback);

    //Удалить все таблицы
    void clearAllTables();

    //Удалить архивные заметки
    void clearArchiveNotes(final DeleteArchiveNotesCallback callback);


    //Удалить заметку из базы
    void deleteNote(Note note, final DeleteNoteCallback callback);
}
