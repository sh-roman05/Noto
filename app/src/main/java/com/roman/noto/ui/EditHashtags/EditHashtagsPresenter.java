package com.roman.noto.ui.EditHashtags;

import com.roman.noto.data.Hashtag;
import com.roman.noto.data.callback.GetHashtagsCallback;
import com.roman.noto.data.repository.Repository;

import java.util.List;

public class EditHashtagsPresenter implements EditHashtagsContract.Presenter {

    static final String TAG = "EditHashtagsPresenter";

    private final Repository repository;
    private final EditHashtagsContract.View view;

    public EditHashtagsPresenter(EditHashtagsContract.View view, Repository repository) {
        this.repository = repository;
        this.view = view;
    }

    @Override
    public void loadHashtags() {

        repository.getHashtags(new GetHashtagsCallback() {
            @Override
            public void onDataNotAvailable() {
                //
            }

            @Override
            public void onHashtagsLoaded(List<Hashtag> hashtags) {
                view.showHashtags(hashtags);
            }
        });

    }

    @Override
    public void saveHashtags(List<Hashtag> hashtags) {

        repository.saveHashtags(hashtags);

    }
}
