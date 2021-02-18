package com.roman.noto.ui.ChooseHashtags;

public class ChooseHashtag {
    private Integer id;
    private String name;
    private boolean select;

    public ChooseHashtag(Integer id, String name, boolean select) {
        this.id = id;
        this.name = name;
        this.select = select;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
