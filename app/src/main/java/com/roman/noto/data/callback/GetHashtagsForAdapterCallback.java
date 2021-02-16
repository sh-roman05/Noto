package com.roman.noto.data.callback;

import java.util.Map;

public interface GetHashtagsForAdapterCallback {
    void onDataNotAvailable();
    void onHashtagsLoaded(Map<Integer, String> object);
}
