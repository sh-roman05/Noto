package com.roman.noto.ui.ChooseHashtags;

import com.roman.noto.data.Hashtag;

public class ChooseHashtag extends Hashtag {

    private boolean select;

    public ChooseHashtag(Integer id, String name, boolean select) {
        super(id, name);
        this.select = select;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
