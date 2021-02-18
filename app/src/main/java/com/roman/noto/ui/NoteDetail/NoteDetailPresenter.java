package com.roman.noto.ui.NoteDetail;

import android.util.Log;

import com.roman.noto.data.callback.DeleteNoteCallback;
import com.roman.noto.data.callback.GetHashtagsCallback;
import com.roman.noto.data.callback.GetNoteCallback;
import com.roman.noto.data.Note;
import com.roman.noto.data.repository.Repository;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class NoteDetailPresenter implements NoteDetailContract.Presenter {

    static final String TAG = "NoteDetailPresenter";

    private Repository repository;
    private NoteDetailContract.View view;


    public NoteDetailPresenter(NoteDetailContract.View view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    //Использовать только id и запросить из репозитория
    @Override
    public void loadNote(Note note) {

        repository.getNote(note.getId(), new GetNoteCallback() {
            @Override
            public void onDataNotAvailable() {
                //
            }

            @Override
            public void onNoteLoaded(Note note) {
                view.noteView(note);
            }
        });
    }

    @Override
    public void saveNote(Note note) {
        repository.updateNote(note);
    }

    @Override
    public void archiveNote(Note note) {
        note.setArchive(true);
        repository.updateNote(note);
        view.returnToMain();
    }

    @Override
    public void cloneNote(Note note) {
        //Клонировать объект (Убрать двойное клонирование)
        //Todo отправить в самый вверх
        Note temp = new Note(note, UUID.randomUUID());
        repository.saveNote(temp);
        view.returnToMain();
    }

    @Override
    public void createNote() {
        Note newNote = new Note();
        repository.saveNote(newNote);
        view.noteView(newNote);
    }

    //Удалить заметку из базы
    @Override
    public void deleteNote(Note note) {
        repository.deleteNote(note, new DeleteNoteCallback() {
            @Override
            public void onDataNotAvailable() { }

            @Override
            public void onNoteDelete() {
                view.returnToMain();
            }
        });
    }

    //Сменить статус наметки на archive = false
    @Override
    public void restoreNote(Note note) {
        note.setArchive(false);
        repository.updateNote(note);
        view.noteView(note);
    }

    //Получить названия хэштегов по id сохраненных в заметке
    @Override
    public void getHashtags(final Note note) {
        repository.getHashtags(new GetHashtagsCallback() {
            @Override
            public void onDataNotAvailable() { }

            @Override
            public void onHashtagsLoaded(Map<Integer, String> object) {
                ArrayList<String> temp = new ArrayList<>();
                for (int hashId: note.getHashtags()) {
                    String hashStr = object.get(hashId);
                    if(hashStr != null) temp.add(hashStr);
                }
                view.hashtagsShow(temp);
            }
        });
    }
}
