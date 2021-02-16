package com.roman.noto.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.common.base.Strings;

import java.util.ArrayList;
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

    @ColumnInfo(name = "hashtag")
    private ArrayList<Integer> hashtags = new ArrayList<Integer>();


    //Создание чистой заметки
    public Note() {
        this(UUID.randomUUID().toString(), "", "", false, 0, false, 0, new Date(), new ArrayList<Integer>());
    }

    //Полное клонирование заметки
    @Ignore
    public Note(Note note)
    {
        this(note.getId(), note.getTitle(), note.getText(), note.isArchive(), note.getColor(), note.isFixed(), note.getPosition(), (Date) note.getLastChange().clone(), (ArrayList<Integer>) note.getHashtags().clone());
    }

    //Полное клонирование заметки с новым id
    @Ignore
    public Note(Note note, UUID uuid)
    {
        this(uuid.toString(), note.getTitle(), note.getText(), note.isArchive(), note.getColor(), note.isFixed(), note.getPosition(), (Date) note.getLastChange().clone(), (ArrayList<Integer>) note.getHashtags().clone());
    }

    @Ignore
    public Note(@NonNull String id, String title, String text, boolean archive, int color, boolean fixed, int position, Date lastChange, ArrayList<Integer> hashtags) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.archive = archive;
        this.color = color;
        this.fixed = fixed;
        this.position = position;
        this.lastChange = lastChange;
        this.hashtags = hashtags;
    }

    //todo: обновить при добавлении новых полей
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
        if (in.readByte() == 0x01) {
            hashtags = new ArrayList<Integer>();
            in.readList(hashtags, Integer.class.getClassLoader());
        } else {
            hashtags = null;
        }
    }

    //todo: обновить при добавлении новых полей
    //todo: сравнение всех хештегов
    //Эквивалентны ли по содержанию
    public boolean isEqual(Note note) {
        boolean temp = true;
        if (!title.equals(note.title)) temp = false;
        if (!text.equals(note.text)) temp = false;
        if (archive != note.archive) temp = false;
        if (color != note.color) temp = false;
        if (position != note.position) temp = false;
        if (hashtags.size() != note.hashtags.size()) temp = false;
        return temp;
    }


    //todo: обновить при добавлении новых полей
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

    public ArrayList<Integer> getHashtags() {
        return hashtags;
    }

    public void setHashtags(ArrayList<Integer> hashtags) {
        this.hashtags = hashtags;
    }

    //todo: обновить при добавлении новых полей
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
        if (hashtags == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(hashtags);
        }
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
