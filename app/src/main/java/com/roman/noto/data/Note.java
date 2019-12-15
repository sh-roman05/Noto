package com.roman.noto.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.common.base.Strings;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "notes")
public class Note {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "archive")
    private boolean archive;

    @ColumnInfo(name = "color")
    private int color;

    @ColumnInfo(name = "fixed")
    private boolean fixed;

    @ColumnInfo(name = "position")
    private int position;

    @ColumnInfo(name = "lastChange")
    private Date lastChange;



    public Note() {
        this(UUID.randomUUID().toString(), "", "", false, 0, false, 0, new Date());
    }

    @Ignore
    public Note(Note note)
    {
        this(note.getId(), note.getTitle(), note.getText(), note.isArchive(), note.getColor(), note.isFixed(), note.getPosition(), (Date) note.getLastChange().clone());
    }

    @Ignore
    public Note(Note note, UUID uuid)
    {
        this(uuid.toString(), note.getTitle(), note.getText(), note.isArchive(), note.getColor(), note.isFixed(), note.getPosition(), (Date) note.getLastChange().clone());
    }

    @Ignore
    public Note(@NonNull String id, String title, String text, boolean archive, int color, boolean fixed, int position, Date lastChange) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.archive = archive;
        this.color = color;
        this.fixed = fixed;
        this.position = position;
        this.lastChange = lastChange;
    }


    //Так же наличие картинок/звукозаписей
    public boolean isEmpty() {
        return (
                Strings.isNullOrEmpty(title) && Strings.isNullOrEmpty(text)
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
