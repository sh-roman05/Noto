package com.roman.noto.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.roman.noto.data.AppDatabase;
import com.roman.noto.data.Note;
import com.roman.noto.data.NoteDao;
import com.roman.noto.data.callback.DeleteArchiveNotesCallback;
import com.roman.noto.data.callback.DeleteNoteCallback;
import com.roman.noto.data.callback.GetFirstPositionCallback;
import com.roman.noto.data.callback.GetNoteCallback;
import com.roman.noto.data.callback.LoadNotesCallback;
import com.roman.noto.util.AppExecutors;

import java.util.List;

public class RoomRepository implements Repository {

    //TODO нужно ли обрабатывать исключения (EmptyResultSetException)

    static final String TAG = "RoomRepository";
    private static RoomRepository INSTANCE = null;
    private String filename = "database12.db";
    private NoteDao dao;
    private AppDatabase db;
    private AppExecutors appExecutors;


    private RoomRepository(AppExecutors appExecutors, Context context)
    {
        this.db =  Room.databaseBuilder(context, AppDatabase.class, filename).build();
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

    private void getAllNotes(final LoadNotesCallback callback)
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Note> list = dao.getAllNotes();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (list.isEmpty()) {
                            callback.onDataNotAvailable();
                        } else {
                            callback.onNotesLoaded(list);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

}
