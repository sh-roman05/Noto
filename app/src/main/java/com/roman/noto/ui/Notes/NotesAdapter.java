package com.roman.noto.ui.Notes;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.common.base.Strings;
import com.roman.noto.NoteTouchHelperClass;
import com.roman.noto.R;
import com.roman.noto.data.Note;
import com.roman.noto.data.callback.GetHashtagsForAdapterCallback;
import com.roman.noto.util.NoteColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> implements NoteTouchHelperClass.ItemTouchHelperAdapter {


    //Строка "+ еще"
    String moreString;


    static final String TAG = "NotesAdapter";


    private List<Note> list = new ArrayList<>();
    private NotesActivity.NoteListener listener;
    private SelectionTracker<Note> mSelectionTracker;

    private NotesContract.Presenter presenter;

    //Нужно хранить актуальную версию
    Map<Integer, String> hashtags;

    NotesAdapter(List<Note> notes, NotesActivity.NoteListener listener, NotesContract.Presenter presenter, String moreString) {
        setList(notes);
        this.listener = listener;
        this.presenter = presenter;
        this.moreString = moreString;


        //Запросить список хештегов
        presenter.getHashtagsForAdapter(new GetHashtagsForAdapterCallback() {
            @Override
            public void onDataNotAvailable() {
                hashtags = new HashMap<>();
            }

            @Override
            public void onHashtagsLoaded(Map<Integer, String> object) {
                hashtags = object;
            }
        });

    }

    //Список элементов, у которых нужно изменить цвет
    void changeColorList(List<Note> changeList, NoteColor.ItemColor color) {
        for (Note item: changeList) {
            int pos = list.indexOf(item);
            list.get(pos).setColor(color.getIndex());
            notifyItemChanged(pos);
        }
    }

    //Список элементов, которые нужно убрать
    void deleteNotesFromList(List<Note> notesList) {
        for (Note item: notesList) {
            int pos = list.indexOf(item);
            list.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).hashCode();
    }


    void setSelectionTracker(SelectionTracker<Note> mSelectionTracker) {
        this.mSelectionTracker = mSelectionTracker;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_view, null);
        return new NoteViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note item = list.get(position);
        boolean isSelected = false;
        if (mSelectionTracker != null) {
            if (mSelectionTracker.isSelected(item)) {
                isSelected = true;
            }
        }
        holder.bind(position, isSelected, item);
    }

    public List<Note> getList() {
        return list;
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    void replaceData(List<Note> notes) {
        setList(notes);
        notifyDataSetChanged();
    }

    private void setList(List<Note> notes) {
        this.list.clear();
        this.list.addAll(notes);
    }


    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        Log.d(TAG, "onItemMoved: from:" + fromPosition + " to:" + toPosition);
        presenter.swapNotes(list.get(fromPosition), list.get(toPosition));
        notifyItemMoved(fromPosition, toPosition);
        Collections.swap(list, fromPosition, toPosition);
        //При перемещении заметки, отключить режим выделения, если был включен
        if(mSelectionTracker.hasSelection()) mSelectionTracker.clearSelection();
    }

    @Override
    public void onItemRemoved(int position) {
        Note item = list.get(position);
        presenter.archiveNote(item);
        //Удалить из адаптера
        list.remove(position);
        notifyItemRemoved(position);
    }


    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        View root;
        TextView title;
        TextView text;
        MaterialCardView card;
        View selectOverlay;
        View selectRound;
        Chip chip1, chip2, chip3;

        NoteViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);
            this.title = itemView.findViewById(R.id.notes_list_view_title);
            this.text = itemView.findViewById(R.id.notes_list_view_text);
            this.card = itemView.findViewById(R.id.notes_list_card_view);
            this.selectOverlay = itemView.findViewById(R.id.notes_list_card_view_select_overlay);
            this.selectRound = itemView.findViewById(R.id.notes_list_card_view_select_round);
            this.chip1 = itemView.findViewById(R.id.notes_list_view_chip1);
            this.chip2 = itemView.findViewById(R.id.notes_list_view_chip2);
            this.chip3 = itemView.findViewById(R.id.notes_list_view_chip3);
        }

        ItemDetailsLookup.ItemDetails<Note> getItemDetails() {
            return new NoteItemDetails(getAdapterPosition(), list.get(getAdapterPosition()));
        }

        @Override
        public void onClick(View v) {
            //Обрабатывать клик
            int adapterPosition = getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {

                if (mSelectionTracker != null && !mSelectionTracker.hasSelection())
                    listener.onItemClick(list.get(adapterPosition));


            }
        }


        @SuppressLint("SetTextI18n")
        void bind(int position, boolean isSelected, Note item)
        {
            if(Strings.isNullOrEmpty(item.getTitle())) {
                title.setVisibility(View.GONE);
            } else {
                title.setVisibility(View.VISIBLE);
                title.setText(item.getTitle());
            }
            if(Strings.isNullOrEmpty(item.getText()))
                text.setVisibility(View.GONE);
            else {
                text.setVisibility(View.VISIBLE);
                text.setText(item.getText());
            }

            //Применить цвет заметки
            card.setCardBackgroundColor(Color.parseColor(NoteColor.getInstance().getItemColor(item.getColor()).getColorBackground()));

            //Получаем список id прикрепленных хештегов
            HashSet<Integer> hashId = item.getHashtags();

            ArrayList<String> hashName = new ArrayList<>();
            Iterator iter =  hashId.iterator();
            while (iter.hasNext())
            {
                String name = hashtags.get((Integer) iter.next());
                if(name != null) hashName.add(name);
            }


            //hashName - тут строки которые можно показать
            chip1.setVisibility(View.GONE);
            chip2.setVisibility(View.GONE);
            chip3.setVisibility(View.GONE);


            if(hashName.size() == 1) {
                chip1.setText(hashName.get(0));
                chip1.setVisibility(View.VISIBLE);
            } else if(hashName.size() == 2){
                chip1.setText(hashName.get(0));
                chip2.setText(hashName.get(1));
                chip1.setVisibility(View.VISIBLE);
                chip2.setVisibility(View.VISIBLE);
            } else if(hashName.size() == 3){
                chip1.setText(hashName.get(0));
                chip2.setText(hashName.get(1));
                chip3.setText(hashName.get(2));
                chip1.setVisibility(View.VISIBLE);
                chip2.setVisibility(View.VISIBLE);
                chip3.setVisibility(View.VISIBLE);
            } else if(hashName.size() > 3) {
                chip1.setText(hashName.get(0));
                chip2.setText(hashName.get(1));
                chip3.setText("+ " + moreString + " " + (hashName.size() - 2));
                chip1.setVisibility(View.VISIBLE);
                chip2.setVisibility(View.VISIBLE);
                chip3.setVisibility(View.VISIBLE);
            }

            selectOverlay.setActivated(isSelected);
        }

        @Override
        public boolean onLongClick(View v) {
            return true;
        }
    }


    //ItemKeyProvider
    public static class NoteItemKeyProvider extends ItemKeyProvider<Note> {

        private List<Note> items;

        NoteItemKeyProvider(int scope, List<Note> items) {
            super(scope);
            this.items = items;
        }

        @Nullable
        @Override
        public Note getKey(int position) {
            return items.get(position);
        }

        @Override
        public int getPosition(@NonNull Note key) {
            return items.indexOf(key);
        }
    }
    //ItemDetailsLookup.ItemDetails
    public static class NoteItemDetails extends ItemDetailsLookup.ItemDetails<Note> {

        private int position;
        private Note item;

        NoteItemDetails(int position, Note item) {
            this.position = position;
            this.item = item;
        }

        @Override
        public int getPosition() {
            return position;
        }

        @Nullable
        @Override
        public Note getSelectionKey() {
            return item;
        }
    }
    //ItemDetailsLookup
    public static class NoteItemDetailsLookup extends ItemDetailsLookup<Note> {

        private final RecyclerView mRecyclerView;

        NoteItemDetailsLookup(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
        }

        @Nullable
        @Override
        public ItemDetails<Note> getItemDetails(@NonNull MotionEvent e) {
            View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (view != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
                if (holder instanceof NotesAdapter.NoteViewHolder) {
                    return ((NotesAdapter.NoteViewHolder) holder).getItemDetails();
                }
            }
            return null;
        }
    }
}
