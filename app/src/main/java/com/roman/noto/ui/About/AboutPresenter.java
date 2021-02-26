package com.roman.noto.ui.About;

import com.roman.noto.data.repository.Repository;

public class AboutPresenter implements AboutContract.Presenter {
    static final String TAG = "AboutPresenter";

    private final Repository repository;
    private final AboutContract.View view;

    AboutPresenter(AboutContract.View view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }
}
