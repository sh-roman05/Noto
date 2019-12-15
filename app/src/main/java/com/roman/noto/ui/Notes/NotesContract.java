package com.roman.noto.ui.Notes;

import com.roman.noto.data.Note;

import java.util.List;

public interface NotesContract {

    interface View {
        void showNotes(List<Note> notes);
        void editNote(String targetId);
    }

    interface Presenter {
        void loadNotes();
        void loadArchiveNotes();
        void addNote();
        void swapNotes(Note fromPosition, Note toPosition);
        void clickNote(String targetId);
        void archiveNote(Note note);
        //void deleteNote(Note note);
        void clearArchiveNotes();
    }
}
