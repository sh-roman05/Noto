package com.roman.noto.ui.ChooseHashtags;

import android.util.Log;

import com.roman.noto.data.Hashtag;
import com.roman.noto.data.Note;
import com.roman.noto.data.callback.GetHashtagsCallback;
import com.roman.noto.data.repository.Repository;
import com.roman.noto.ui.About.AboutContract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChooseHashtagsPresenter implements ChooseHashtagsContract.Presenter {

    static final String TAG = "ChooseHashtagsPresenter";

    private final Repository repository;
    private final ChooseHashtagsContract.View view;

    ChooseHashtagsPresenter(ChooseHashtagsContract.View view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }


    //Получить список хештегов и отметить выделенные
    @Override
    public void selectHashtagsShow(final Note note) {
        repository.getHashtags(new GetHashtagsCallback() {
            @Override
            public void onDataNotAvailable() { }

            @Override
            public void onHashtagsLoaded(List<Hashtag> object) {

                List<ChooseHashtag> hashtagList = new ArrayList<>();
                for (Hashtag hashtag: object) {
                    hashtagList.add(new ChooseHashtag(hashtag.getId(), hashtag.getName(), false));
                }

                for (ChooseHashtag tag: hashtagList) {
                    for (Integer selectInt: note.getHashtags()) {
                        if(tag.getId().equals(selectInt)){
                            tag.setSelect(true);
                            break;
                        }
                    }
                }
                view.hashtagShow(hashtagList);
            }
        });


    }

    @Override
    public void saveSelectHashtags(Note note, List<ChooseHashtag> hashtagList) {

        HashSet<Integer> hashList = new HashSet<>();
        for (ChooseHashtag tag: hashtagList){
            if(tag.isSelect()){
                hashList.add(tag.getId());
            }
        }
        note.setHashtags(hashList);

        repository.updateNote(note);
    }

    @Override
    public void addNewHashtag(String name) {
        //Создать хештег и отправить на сохранение
        Hashtag newHashtag = new Hashtag(generateUniqueId(), name);
        repository.addHashtag(newHashtag);
    }

    private static int generateUniqueId() {
        UUID uuid = UUID.randomUUID();
        return Math.abs(uuid.hashCode());
    }

}
