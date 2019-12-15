package com.roman.noto.data.callback;

import com.roman.noto.data.Note;

public interface GetNoteCallback {
    void onDataNotAvailable();
    void onNoteLoaded(Note note);
}


