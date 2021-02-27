package com.roman.noto.ui.Settings;

import com.roman.noto.data.repository.Repository;

public class SettingsPresenter implements SettingsContract.Presenter {
    static final String TAG = "SettingsPresenter";

    private final Repository repository;
    private final SettingsContract.View view;

    public SettingsPresenter(SettingsContract.View view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void clearDatabase() {
        repository.clearAllTables();
    }
}
