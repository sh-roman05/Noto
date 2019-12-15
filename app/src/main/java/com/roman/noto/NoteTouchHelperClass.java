package com.roman.noto;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.roman.noto.data.Note;
import com.roman.noto.ui.Notes.NotesActivity;

import java.util.List;

public class NoteTouchHelperClass extends ItemTouchHelper.Callback {
    private ItemTouchHelperAdapter adapter;
    public interface ItemTouchHelperAdapter {
        void onItemMoved(int fromPosition, int toPosition);
        void onItemRemoved(int position);
    }

    public NoteTouchHelperClass(ItemTouchHelperAdapter ad){
        adapter = ad;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int upFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

        //todo не самый лучший способ блокировки
        NotesActivity.NotesAdapter adapter2 = (NotesActivity.NotesAdapter)recyclerView.getAdapter();
        if (adapter2 != null) {
            List<Note> temp = adapter2.getList();
            if(temp.get(viewHolder.getAdapterPosition()).isArchive())
            {
                return makeMovementFlags(0, 0);

            }
        }


        return makeMovementFlags(upFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        adapter.onItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemRemoved(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
