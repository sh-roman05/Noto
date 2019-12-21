package com.roman.noto.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.common.base.Strings;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "notes")
public class Note implements Parcelable {

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

    @Ignore
    protected Note(Parcel in) {
        id = in.readString();
        title = in.readString();
        text = in.readString();
        archive = in.readByte() != 0x00;
        color = in.readInt();
        fixed = in.readByte() != 0x00;
        position = in.readInt();
        long tmpLastChange = in.readLong();
        lastChange = tmpLastChange != -1 ? new Date(tmpLastChange) : null;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(text);
        dest.writeByte((byte) (archive ? 0x01 : 0x00));
        dest.writeInt(color);
        dest.writeByte((byte) (fixed ? 0x01 : 0x00));
        dest.writeInt(position);
        dest.writeLong(lastChange != null ? lastChange.getTime() : -1L);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
