package com.roman.noto.ui.Notes;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roman.noto.R;
import com.roman.noto.data.Note;
import com.roman.noto.data.repository.NavigationHashtag;

import java.util.ArrayList;
import java.util.List;

public class NotesHashtagsAdapter extends RecyclerView.Adapter<NotesHashtagsAdapter.HashtagViewHolder> {

    static final String TAG = "NotesHashtagsAdapter";

    private List<NavigationHashtag> list = new ArrayList<>();

    //Listener
    private final NotesHashtagListener listener;

    public NotesHashtagsAdapter(NotesHashtagListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HashtagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_list_view, null);
        return new HashtagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HashtagViewHolder holder, int position) {
        NavigationHashtag item = list.get(position);
        holder.name.setText(item.getName());
        holder.itemView.setSelected(item.isSelected());
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }


    public void setList(List<NavigationHashtag> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<NavigationHashtag> getList(){
        return list;
    }


    class HashtagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        public HashtagViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            this.name = itemView.findViewById(R.id.navigation_list_view_name);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onItemClick(adapterPosition);
            }
        }
    }
}
