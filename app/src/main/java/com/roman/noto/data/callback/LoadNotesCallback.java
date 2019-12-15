package com.roman.noto.data.callback;

import com.roman.noto.data.Note;

import java.util.List;

public interface LoadNotesCallback {
    void onDataNotAvailable();
    void onNotesLoaded(List<Note> notes);
}
