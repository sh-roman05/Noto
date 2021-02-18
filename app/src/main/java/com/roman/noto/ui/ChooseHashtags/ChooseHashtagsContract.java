package com.roman.noto.ui.ChooseHashtags;

import com.roman.noto.data.Note;

import java.util.List;

public interface ChooseHashtagsContract {
    interface View {
        void hashtagShow(List<ChooseHashtag> hashtagList);
    }

    interface Presenter {
        void selectHashtagsShow(Note note);
        void saveSelectHashtags(Note note, List<ChooseHashtag> hashtagList);
    }
}
