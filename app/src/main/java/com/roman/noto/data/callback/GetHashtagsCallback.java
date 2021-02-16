package com.roman.noto.data.callback;

import com.roman.noto.data.Note;

import java.util.Map;

public interface GetHashtagsCallback {
    void onDataNotAvailable();
    void onHashtagsLoaded(Map<Integer, String> object);
}
