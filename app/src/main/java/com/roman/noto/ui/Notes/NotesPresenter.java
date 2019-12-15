package com.roman.noto.ui.Notes;

import android.util.Log;

import com.roman.noto.data.callback.DeleteArchiveNotesCallback;
import com.roman.noto.data.callback.LoadNotesCallback;
import com.roman.noto.data.Note;
import com.roman.noto.data.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class NotesPresenter implements NotesContract.Presenter
{
    static final String TAG = "NotesPresenter";

    private Repository repository;
    private NotesContract.View view;


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
    public void addNote() {
        Note newNote = new Note();
        repository.saveNote(newNote);
        view.editNote(newNote.getId());
    }

    @Override
    public void swapNotes(Note fromPosition, Note toPosition) {
        repository.swapNotes(fromPosition, toPosition);
    }

    @Override
    public void clickNote(String targetId) {
        view.editNote(targetId);
    }

    @Override
    public void archiveNote(Note note) {
        note.setArchive(true);
        repository.updateNote(note);
    }

    @Override
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


}
