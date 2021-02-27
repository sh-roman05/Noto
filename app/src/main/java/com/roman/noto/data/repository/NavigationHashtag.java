package com.roman.noto.data.repository;

import com.roman.noto.data.Hashtag;

public class NavigationHashtag extends Hashtag {

    private boolean selected;

    public NavigationHashtag(Integer id, String name) {
        super(id, name);
        this.selected = false;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
