package com.roman.noto.ui.Notes;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
import com.google.common.base.Strings;
import com.roman.noto.NoteTouchHelperClass;
import com.roman.noto.R;
import com.roman.noto.data.Note;
import com.roman.noto.util.NoteColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> implements NoteTouchHelperClass.ItemTouchHelperAdapter {
    private List<Note> list = new ArrayList<>();
    private NotesActivity.NoteListener listener;
    private SelectionTracker<Note> mSelectionTracker;

    private NotesContract.Presenter presenter;

    NotesAdapter(List<Note> notes, NotesActivity.NoteListener listener, NotesContract.Presenter presenter) {
        setList(notes);
        this.listener = listener;
        this.presenter = presenter;
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
        presenter.swapNotes(list.get(fromPosition), list.get(toPosition));
        Collections.swap(list, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
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

        }

        ItemDetailsLookup.ItemDetails<Note> getItemDetails() {
            return new NoteItemDetails(getAdapterPosition(), list.get(getAdapterPosition()));
        }

        @Override
        public void onClick(View v) {
            //Обрабатывать клик
            int adapterPosition = getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onItemClick(list.get(adapterPosition));
            }
        }


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

            card.setCardBackgroundColor(Color.parseColor(NoteColor.getInstance().getItemColor(item.getColor()).getColorBackground()));

            selectOverlay.setActivated(isSelected);

        }

        @Override
        public boolean onLongClick(View v) {
            return true;
        }
    }




    //Добавил static

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
