package com.roman.noto.data.callback;

import com.roman.noto.data.Hashtag;

import java.util.List;
import java.util.Map;

public interface GetHashtagsForAdapterCallback {
    void onDataNotAvailable();
    void onHashtagsLoaded(List<Hashtag> object);
}
