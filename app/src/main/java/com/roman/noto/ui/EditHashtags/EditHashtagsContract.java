package com.roman.noto.ui.EditHashtags;

import com.roman.noto.data.Hashtag;
import com.roman.noto.ui.ChooseHashtags.ChooseHashtag;

import java.util.List;

public interface EditHashtagsContract {

    interface View {
        void showHashtags(List<Hashtag> hashtagList);
    }

    interface Presenter {
        //Загрузить хэштеги
        void loadHashtags();
        //Сохранить хештеги
        void saveHashtags(List<Hashtag> hashtags);
    }

}
