package com.roman.noto.data.callback;

import com.roman.noto.data.Hashtag;
import com.roman.noto.data.Note;

import java.util.List;
import java.util.Map;

public interface GetHashtagsCallback {
    void onDataNotAvailable();
    void onHashtagsLoaded(List<Hashtag> hashtags);
}
