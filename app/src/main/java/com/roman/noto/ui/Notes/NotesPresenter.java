package com.roman.noto.ui.Notes;

import android.util.Log;


import com.roman.noto.data.callback.DeleteArchiveNotesCallback;
import com.roman.noto.data.callback.GetHashtagsCallback;
import com.roman.noto.data.callback.GetHashtagsForAdapterCallback;
import com.roman.noto.data.callback.GetNotesWithHashtagCallback;
import com.roman.noto.data.callback.LoadNotesCallback;
import com.roman.noto.data.Note;
import com.roman.noto.data.repository.NavigationHashtag;
import com.roman.noto.data.repository.Repository;
import com.roman.noto.ui.ChooseHashtags.ChooseHashtag;
import com.roman.noto.util.NoteColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class NotesPresenter implements NotesContract.Presenter
{
    static final String TAG = "NotesPresenter";

    private final Repository repository;
    private final NotesContract.View view;


    NotesPresenter(NotesContract.View view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void loadNotes() {
        repository.getNotes(new LoadNotesCallback() {
            @Override
            public void onDataNotAvailable() { }

            @Override
            public void onNotesLoaded(List<Note> notes) {
                view.showNotes(notes);
            }
        });
    }

    @Override
    public void loadArchiveNotes() {
        repository.getArchivedNotes(new LoadNotesCallback() {
            @Override
            public void onDataNotAvailable() { }

            @Override
            public void onNotesLoaded(List<Note> notes) {
                view.showNotes(notes);
            }
        });
    }

    @Override
    public void addEmptyNote() {
        Note newNote = new Note();
        repository.saveNote(newNote);
        view.editNote(newNote);
    }

    @Override
    public void swapNotes(Note fromPosition, Note toPosition) {
        repository.swapNotes(fromPosition, toPosition);
    }

    @Override
    public void clickNote(Note target) {
        view.editNote(target);
    }

    @Override
    public void archiveNote(Note note) {
        note.setArchive(true);
        repository.updateNote(note);
    }




    @Override
    //Удалить все архивированные заметки
    public void clearArchiveNotes() {
        repository.clearArchiveNotes(new DeleteArchiveNotesCallback() {
            @Override
            public void onDataNotAvailable() { }

            @Override
            public void onArchiveCleaned() {
                view.showNotes(new ArrayList<Note>());
            }
        });
    }

    @Override
    //Восстановить заметки по списку
    public void restoreNotesList(List<Note> notes) {
        for (Note note: notes)
            note.setArchive(false);
        //отправить дальше
        repository.updateNotes(notes);
    }

    @Override
    //Удалить заметки по списку
    public void deleteNotesList(List<Note> notes) {
        repository.deleteNotes(notes);
    }

    @Override
    //Архивировать заметки по списку
    public void archiveNotesList(List<Note> notes) {
        for (Note note: notes)
            note.setArchive(true);
        //отправить дальше
        repository.updateNotes(notes);
    }


    @Override
    //Изменить цвета заметок по списку
    public void changeColorNotesList(List<Note> notes, NoteColor.ItemColor item) {
        for (Note note: notes)
            note.setColor(item.getIndex());
        //отправить дальше
        repository.updateNotes(notes);
    }

    //Получить хештеги
    @Override
    public void getHashtagsForAdapter(final GetHashtagsForAdapterCallback callback) {
        repository.getHashtags(new GetHashtagsCallback() {
            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }

            @Override
            public void onHashtagsLoaded(Map<Integer, String> object) {
                callback.onHashtagsLoaded(object);
            }
        });
    }

    @Override
    public void loadHashtags() {
        repository.getHashtags(new GetHashtagsCallback() {
            @Override
            public void onDataNotAvailable() { }

            @Override
            public void onHashtagsLoaded(Map<Integer, String> object) {
                List<NavigationHashtag> hashtagList = hashtagsMapToList(object);
                view.applyHashtags(hashtagList);
            }
        });
    }

    @Override
    public void loadNotesWithHashtag(int hashtagId) {
        //
        repository.getNotesWithHashtag(hashtagId, new GetNotesWithHashtagCallback() {
            @Override
            public void onDataNotAvailable() {
                //
            }

            @Override
            public void onNotesLoaded(List<Note> notes) {
                view.showNotes(notes);
            }
        });
    }

    @Override
    public void addEmptyNoteWithHashtag(NavigationHashtag selectedHashtag) {
        //Создаем пустую заметку и добавляем хештег
        Note newNote = new Note();
        if(newNote.getHashtags() == null)
            newNote.setHashtags(new HashSet<Integer>());
        if(selectedHashtag != null)
            newNote.getHashtags().add(selectedHashtag.getId());
        repository.saveNote(newNote);
        view.editNote(newNote);
    }

    private List<NavigationHashtag> hashtagsMapToList(Map<Integer, String> hashtags) {
        List<NavigationHashtag> temp = new ArrayList<>();
        //Получаем список всех id
        List<Integer> list = new ArrayList<Integer>(hashtags.keySet());
        for (Integer id: list) {
            temp.add(new NavigationHashtag(id, hashtags.get(id)));
        }
        return temp;
    }


}
