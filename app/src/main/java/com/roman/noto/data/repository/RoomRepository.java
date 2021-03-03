package com.roman.noto.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.roman.noto.data.AppDatabase;
import com.roman.noto.data.Hashtag;
import com.roman.noto.data.Note;
import com.roman.noto.data.NoteDao;
import com.roman.noto.data.callback.DeleteArchiveNotesCallback;
import com.roman.noto.data.callback.DeleteNoteCallback;
import com.roman.noto.data.callback.GetFirstPositionCallback;
import com.roman.noto.data.callback.GetHashtagsCallback;
import com.roman.noto.data.callback.GetNoteCallback;
import com.roman.noto.data.callback.GetNotesWithHashtagCallback;
import com.roman.noto.data.callback.LoadNotesCallback;
import com.roman.noto.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomRepository implements Repository {

    //TODO нужно ли обрабатывать исключения (EmptyResultSetException)


    /*
    * Сейчас все изменения таблиц и полей фиксирую через изменение файла базы
    * В будущем разобраться с миграцией, что-бы не удалились данные пользователя
    */

    static final String TAG = "RoomRepository";
    private static RoomRepository INSTANCE = null;
    private String filename = "database18.db";
    private NoteDao dao;
    private AppDatabase db;
    private AppExecutors appExecutors;


    private RoomRepository(AppExecutors appExecutors, final Context context)
    {
        this.db =  Room.databaseBuilder(context, AppDatabase.class, filename).addCallback(new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }
        }).build();
        this.dao = db.noteDao();
        this.appExecutors = appExecutors;
    }


    public static RoomRepository getInstance(AppExecutors appExecutors, Context context)
    {
        if (INSTANCE == null) {
            synchronized (RoomRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RoomRepository(appExecutors, context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getFirstPosition(final GetFirstPositionCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    final int pos = dao.getFirstPosition();
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFirstPositionLoaded(pos);
                        }
                    });
                }
                catch (Exception ex)
                {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataNotAvailable();
                        }
                    });
                }
            }
        };
        appExecutors.diskIO().execute(runnable);
    }


    @Override
    public void clearAllTables() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                db.clearAllTables();
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateNote(final Note note) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.update(note);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void swapNotes(final Note fromPosition, final Note toPosition) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.insertAll(fromPosition, toPosition);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveNote(final Note note) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.insertAll(note);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void clearArchiveNotes(final DeleteArchiveNotesCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.deleteArchiveNotes();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onArchiveCleaned();
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getHashtags(final GetHashtagsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final ArrayList<Hashtag> hashtags = (ArrayList<Hashtag>) dao.getAllHashtags();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onHashtagsLoaded(hashtags);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteHashtag(final Hashtag hashtag) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.deleteHashtag(hashtag);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void addHashtag(final Hashtag newHashtag) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.addHashtag(newHashtag);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveHashtags(final List<Hashtag> hashtags) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.updateHashtags(hashtags);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteNote(final Note note, final DeleteNoteCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.delete(note);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onNoteDelete();
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateNotes(final List<Note> notes) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.updateList(notes);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteNotes(final List<Note> notes) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.deleteList(notes);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getNote(final String id, final GetNoteCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Note note = dao.getNoteById(id);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (note == null) {
                            callback.onDataNotAvailable();
                        } else {
                            callback.onNoteLoaded(note);
                        }
                    }
                });

            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getNotes(final LoadNotesCallback callback) {
        getAllNotes(callback);
    }

    @Override
    public void getArchivedNotes(final LoadNotesCallback callback) {
        getAllNotes(callback);
    }

    @Override
    public void getNotesWithHashtag(int id, GetNotesWithHashtagCallback callback) {
        //Не используется
        //todo: но можно сделать sql запрос
    }

    private void getAllNotes(final LoadNotesCallback callback)
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Note> list = dao.getAllNotes();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onNotesLoaded(list);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }



}

















