package com.roman.noto.ui.ChooseHashtags;

import android.util.Log;

import com.roman.noto.data.Note;
import com.roman.noto.data.callback.GetHashtagsCallback;
import com.roman.noto.data.repository.Repository;
import com.roman.noto.ui.About.AboutContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChooseHashtagsPresenter implements ChooseHashtagsContract.Presenter {

    static final String TAG = "ChooseHashtagsPresenter";

    private Repository repository;
    private ChooseHashtagsContract.View view;

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
            public void onHashtagsLoaded(Map<Integer, String> object) {
                //
                List<ChooseHashtag> hashtagList = hashtagsMapToList(object);
                for (ChooseHashtag tag: hashtagList) {
                    for (Integer selectInt: note.getHashtags()) {
                        if(tag.getId().equals(selectInt)){
                            tag.setSelect(true);
                            break;
                        }
                    }
                }
                //
                view.hashtagShow(hashtagList);
            }
        });


    }

    @Override
    public void saveSelectHashtags(Note note, List<ChooseHashtag> hashtagList) {

        ArrayList<Integer> hashList = new ArrayList<>();
        for (ChooseHashtag tag: hashtagList){
            if(tag.isSelect()){
                hashList.add(tag.getId());
            }
        }
        note.setHashtags(hashList);

        Log.d(TAG, "saveSelectHashtags, size: " + note.getHashtags().size());

        repository.updateNote(note);
    }


    private List<ChooseHashtag> hashtagsMapToList(Map<Integer, String> hashtags) {
        List<ChooseHashtag> temp = new ArrayList<>();
        //Получаем список всех id
        List<Integer> list = new ArrayList<Integer>(hashtags.keySet());
        for (Integer id: list) {
            temp.add(new ChooseHashtag(id, hashtags.get(id), false));
        }
        return temp;
    }
}
