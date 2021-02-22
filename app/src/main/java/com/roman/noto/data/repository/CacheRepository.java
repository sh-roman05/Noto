package com.roman.noto.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.roman.noto.data.Note;
import com.roman.noto.data.callback.DeleteArchiveNotesCallback;
import com.roman.noto.data.callback.DeleteNoteCallback;
import com.roman.noto.data.callback.GetFirstPositionCallback;
import com.roman.noto.data.callback.GetHashtagsCallback;
import com.roman.noto.data.callback.GetNoteCallback;
import com.roman.noto.data.callback.GetNotesWithHashtagCallback;
import com.roman.noto.data.callback.LoadNotesCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



//Кэширующий слой
public class CacheRepository implements Repository
{
    //TODO нужен ли переход на PriorityQueue
    static final String TAG = "CacheRepository";
    private static CacheRepository INSTANCE = null;
    //Ссылка на реальный репозиторий
    private final Repository repository;

    private CacheRepository(Repository repository) {
        this.repository = repository;
    }

    //todo переделать singlton, doublechecking
    public static Repository getInstance(Repository repository) {
        if (INSTANCE == null) {
            INSTANCE = new CacheRepository(repository);
        }
        return INSTANCE;
    }

    private Map<String, Note> cachedNotes;
    Integer position = null;

    private void createCasheIsNull() {
        if (cachedNotes == null)
            cachedNotes = new LinkedHashMap<>();
    }

    class SortByPosition implements Comparator<Note>
    {
        public int compare(Note a, Note b)
        {
            return a.getPosition() - b.getPosition();
        }
    }

    private void saveNoteWithPosition(int pos, Note note)
    {
        Note temp = new Note(note);
        temp.setPosition(pos);
        repository.saveNote(temp);
        cachedNotes.put(temp.getId(), temp);
    }

    @Override
    public void swapNotes(Note fromPosition, Note toPosition) {
        int fromPos = fromPosition.getPosition();
        int toPos = toPosition.getPosition();
        fromPosition.setPosition(toPos);
        toPosition.setPosition(fromPos);
        cachedNotes.put(fromPosition.getId(), new Note(fromPosition));
        cachedNotes.put(toPosition.getId(), new Note(toPosition));
        repository.swapNotes(fromPosition, toPosition);
    }

    public Note getNoteById(String id) {
        if (cachedNotes == null || cachedNotes.isEmpty()) {
            return null;
        } else {
            return cachedNotes.get(id);
        }
    }

    @Override
    //Обновить данные в заметке
    public void updateNote(Note note) {
        //Клонировать объект
        Note temp = new Note(note);
        //Создать кеш, если данных нет
        createCasheIsNull();
        //Нужно ли сохранять или обновлять
        Note casheNote = cachedNotes.get(temp.getId());
        if (casheNote == null) {
            //Этой заметки нет. Значит просто сохраняем.

            //todo: position может быть null. нужно и его запрашивать тогда.
            //Так как заметка новая, отправляем ее вверх.
            if(position != null) temp.setPosition(--position);

            cachedNotes.put(temp.getId(), temp);
            //Внести изменения в базу
            repository.saveNote(temp);
        } else {
            //Заметка существует. Сравнить и обновить если надо.
            //Проверять в будущем
            //if(!casheNote.isEqual(temp))

            Log.d(TAG, "updateNote, size: " + temp.getHashtags().size());

            //Обновить в кеше
            cachedNotes.put(temp.getId(), temp);
            //Обновить в базе
            repository.updateNote(temp);
        }
    }



    @Override
    public void getFirstPosition(GetFirstPositionCallback callback) {
        repository.getFirstPosition(callback);
    }

    @Override
    public void clearAllTables() {
        //Очистить кэш
        cachedNotes = new LinkedHashMap<>();
        position = null;
        //Очистить базу
        repository.clearAllTables();
    }

    @Override
    public void clearArchiveNotes(final DeleteArchiveNotesCallback callback) {
        //Удаляем из кеша
        Iterator<Map.Entry<String, Note>> it = cachedNotes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Note> entry = it.next();
            if (entry.getValue().isArchive()) {
                it.remove();
            }
        }
        //Удаляем из базы
        repository.clearArchiveNotes(new DeleteArchiveNotesCallback() {
            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }

            @Override
            public void onArchiveCleaned() {
                callback.onArchiveCleaned();
            }
        });
    }

    //todo хеш теги кеш временный
    @Override
    public void getHashtags(GetHashtagsCallback callback) {
        HashMap<Integer, String> hashtags;
        hashtags = new HashMap<>();
        hashtags.put(0, "Учеба");
        hashtags.put(1, "Работа");
        hashtags.put(2, "Полезное");
        hashtags.put(3, "Купить");
        hashtags.put(4, "Посмотреть");
        hashtags.put(5, "Важное");
        callback.onHashtagsLoaded(hashtags);
    }

    @Override
    public void deleteNote(Note note, final DeleteNoteCallback callback) {
        //Клонировать объект
        Note temp = new Note(note);
        cachedNotes.remove(temp.getId());

        repository.deleteNote(temp, new DeleteNoteCallback() {
            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }

            @Override
            public void onNoteDelete() {
                callback.onNoteDelete();
            }
        });
    }

    @Override
    public void updateNotes(List<Note> notes) {

        for (Note note: notes) {
            cachedNotes.put(note.getId(), note);
        }

        //Закинуть в кэш
        repository.updateNotes(notes);
    }

    @Override
    public void deleteNotes(List<Note> notes) {
        for (Note note: notes)
            cachedNotes.remove(note.getId());
        repository.deleteNotes(notes);
    }


    private void refreshCache(List<Note> tasks) {
        createCasheIsNull();
        cachedNotes.clear();
        for (Note task : tasks)
            cachedNotes.put(task.getId(), task);
    }

    @Override
    public void getNote(String id, final GetNoteCallback callback) {
        Note cachedNote = getNoteById(id);

        if (cachedNote != null) {
            callback.onNoteLoaded(cachedNote);
            return;
        }

        repository.getNote(id, new GetNoteCallback() {
            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }

            @Override
            public void onNoteLoaded(Note note) {
                createCasheIsNull();
                cachedNotes.put(note.getId(), note);
                callback.onNoteLoaded(note);
            }
        });
    }

    //Сохранение новой заметки
    @Override
    public void saveNote(@NonNull final Note note) {
        createCasheIsNull();
        if(position == null)
        {
            //Запросить позицию
            getFirstPosition(new GetFirstPositionCallback() {
                @Override
                public void onDataNotAvailable() {
                    saveNoteWithPosition(0, note);
                }

                @Override
                public void onFirstPositionLoaded(int position) {
                    saveNoteWithPosition(position, note);
                }
            });
        } else {
            Note temp = new Note(note);
            temp.setPosition(--position);
            repository.saveNote(temp);
            cachedNotes.put(temp.getId(), temp);
        }
    }

    public void getAllNotes(final LoadNotesCallback callback, final boolean archive)
    {
        //Если нет кеша, то запрашиваем у Room данные
        if (cachedNotes == null) {
            repository.getNotes(new LoadNotesCallback() {
                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }
                @Override
                public void onNotesLoaded(List<Note> notes) {

                    if(!notes.isEmpty() && position == null)
                        position = notes.get(0).getPosition();

                    refreshCache(notes);
                    ArrayList<Note> temp = new ArrayList<>();

                    Iterator<Map.Entry<String, Note>> it = cachedNotes.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Note> entry = it.next();
                        if(archive) {
                            if (entry.getValue().isArchive())
                                temp.add(entry.getValue());
                        }else {
                            if (!entry.getValue().isArchive())
                                temp.add(entry.getValue());
                        }

                    }
                    //Готово
                    callback.onNotesLoaded(temp);
                }
            });
        }else
        {
            ArrayList<Note> temp = new ArrayList<>();
            //Работа этого кода предполагает наличие кэша
            Iterator<Map.Entry<String, Note>> it = cachedNotes.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Note> entry = it.next();
                if(archive) {
                    if (entry.getValue().isArchive())
                        temp.add(entry.getValue());
                }else {
                    if (!entry.getValue().isArchive())
                        temp.add(entry.getValue());
                }
            }
            //Отсортировать по позиции
            Collections.sort(temp, new SortByPosition());
            //Готово
            callback.onNotesLoaded(temp);
        }
    }


    @Override
    public void getNotes(final LoadNotesCallback callback) {
        getAllNotes(callback, false);
    }


    @Override
    public void getArchivedNotes(final LoadNotesCallback callback) {
        getAllNotes(callback, true);
    }


    //Получить заметки содержащие выбранный хештег
    @Override
    public void getNotesWithHashtag(final int id, final GetNotesWithHashtagCallback callback) {
        //Если нет кеша, то запрашиваем у Room данные
        if (cachedNotes == null) {
            repository.getNotes(new LoadNotesCallback() {
                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }
                @Override
                public void onNotesLoaded(List<Note> notes) {

                    if(!notes.isEmpty() && position == null)
                        position = notes.get(0).getPosition();

                    refreshCache(notes);
                    ArrayList<Note> temp = new ArrayList<>();

                    Iterator<Map.Entry<String, Note>> it = cachedNotes.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Note> entry = it.next();

                        Note item = entry.getValue();
                        for (int hashId: item.getHashtags()){
                            if(hashId == id){
                                temp.add(item);
                                break;
                            }
                        }

                    }
                    //Готово
                    callback.onNotesLoaded(temp);
                }
            });
        }else
        {
            ArrayList<Note> temp = new ArrayList<>();
            //Работа этого кода предполагает наличие кэша
            Iterator<Map.Entry<String, Note>> it = cachedNotes.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Note> entry = it.next();

                Note item = entry.getValue();
                for (int hashId: item.getHashtags()){
                    if(hashId == id){
                        temp.add(item);
                        break;
                    }
                }
            }
            //Отсортировать по позиции
            Collections.sort(temp, new SortByPosition());
            //Готово
            callback.onNotesLoaded(temp);
        }
    }

}
