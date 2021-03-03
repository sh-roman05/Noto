package com.roman.noto.data.repository;

import com.roman.noto.data.Hashtag;
import com.roman.noto.data.Note;
import com.roman.noto.data.callback.DeleteArchiveNotesCallback;
import com.roman.noto.data.callback.DeleteNoteCallback;
import com.roman.noto.data.callback.GetFirstPositionCallback;
import com.roman.noto.data.callback.GetHashtagsCallback;
import com.roman.noto.data.callback.GetNoteCallback;
import com.roman.noto.data.callback.GetNotesWithHashtagCallback;
import com.roman.noto.data.callback.LoadNotesCallback;

import java.util.List;

public interface Repository {

    //Получить первую позицию
    void getFirstPosition(final GetFirstPositionCallback callback);
    //Удалить все таблицы
    void clearAllTables();

    //Обновить данные в заметке
    void updateNote(Note note);
    //Удалить заметку из базы
    void deleteNote(Note note, final DeleteNoteCallback callback);
    //Получить заметку по id
    void getNote(String id, GetNoteCallback callback);
    //Сохранить заметку
    void saveNote(Note note);
    //Поменять заметки местами
    void swapNotes(Note fromPosition, Note toPosition);

    //Обновить заметки из списка
    void updateNotes(List<Note> notes);
    //Удалить заметки по списку
    void deleteNotes(List<Note> notes);
    //Получить не удаленные заметки
    void getNotes(final LoadNotesCallback callback);
    //Получить удаленные заметки
    void getArchivedNotes(final LoadNotesCallback callback);
    //Получить заметки содержащие выбранный хештег
    void getNotesWithHashtag(int id, final GetNotesWithHashtagCallback callback);
    //Удалить архивные заметки
    void clearArchiveNotes(final DeleteArchiveNotesCallback callback);

    //Получить все хештеги
    void getHashtags(final GetHashtagsCallback callback);
    //Удалить хештег
    void deleteHashtag(Hashtag hashtag);
    //Добавить хештег
    void addHashtag(Hashtag newHashtag);
    //Сохранить хештеги
    void saveHashtags(List<Hashtag> hashtags);
}
