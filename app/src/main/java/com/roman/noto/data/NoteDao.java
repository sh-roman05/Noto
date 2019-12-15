package com.roman.noto.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    //Получить не удаленные заметки с учетом позиции
    @Query("SELECT * FROM notes WHERE archive = 0 ORDER BY position ASC")
    List<Note> getNonArchiveNotes();

    //Получить удаленные заметки с учетом позиции
    @Query("SELECT * FROM notes WHERE archive = 1 ORDER BY position ASC")
    List<Note> getArchiveNotes();

    //Получить все заметки с учетом позиции
    @Query("SELECT * FROM notes ORDER BY position ASC")
    List<Note> getAllNotes();

    //Получить первую позицию
    @Query("SELECT position FROM notes ORDER BY position ASC LIMIT 1")
    int getFirstPosition();

    //Добавить новые заметки
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Note... notes);

    @Delete
    void delete(Note note);

    @Update
    void update(Note note);

    //Удалить весь архив заметок
    @Query("DELETE FROM notes WHERE archive = 1")
    void deleteArchiveNotes();

    //Получить заметку по id
    @Query("SELECT * FROM notes WHERE id = :noteId")
    Note getNoteById(String noteId);

}
