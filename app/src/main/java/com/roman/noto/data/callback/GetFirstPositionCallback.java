package com.roman.noto.data.callback;

public interface GetFirstPositionCallback {
    void onDataNotAvailable();
    void onFirstPositionLoaded(int position);
}
