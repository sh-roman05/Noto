package com.roman.noto.data.repository;

public class NavigationHashtag {

    private Integer id;
    private String name;
    private boolean selected;

    public NavigationHashtag(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.selected = false;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
