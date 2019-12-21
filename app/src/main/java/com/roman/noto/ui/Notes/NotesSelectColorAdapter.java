package com.roman.noto.ui.Notes;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.roman.noto.R;
import com.roman.noto.util.NoteColor;

import java.util.ArrayList;
import java.util.List;


public class NotesSelectColorAdapter extends RecyclerView.Adapter<NotesSelectColorAdapter.NotesSelectColorViewHolder> {
    private List<NoteColor.ItemColor> list = new ArrayList<>();
    private NotesSelectColorListener listener;

    NotesSelectColorAdapter(NotesSelectColorListener listener) {
        setList(NoteColor.getInstance().getList());
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotesSelectColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_list_view, null);
        return new NotesSelectColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesSelectColorViewHolder holder, int position) {
        NoteColor.ItemColor color = list.get(position);
        roundColorButton(holder.color, color);
    }

    public List<NoteColor.ItemColor> getList() {
        return list;
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    void replaceData(List<NoteColor.ItemColor> notes) {
        setList(notes);
        notifyDataSetChanged();
    }

    private void setList(List<NoteColor.ItemColor> notes) {
        this.list = notes;
    }

    class NotesSelectColorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageButton color;

        NotesSelectColorViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.color = (ImageButton) itemView.findViewById(R.id.ui_color_round_button);
        }

        @Override
        public void onClick(View v) {
            //Обрабатывать клик
            int adapterPosition = getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onItemClick(list.get(adapterPosition), NotesSelectColorViewHolder.this.color, adapterPosition);
            }

        }

    }

    //Нарисовать круглую кнопку
    private void roundColorButton(ImageButton button, NoteColor.ItemColor itemColor)
    {
        int strokeWidth = 3;
        int strokeColor = Color.parseColor("#424242");
        int fillColor = Color.parseColor(itemColor.getColorPrimary());

        GradientDrawable gD = new GradientDrawable();
        gD.setColor(fillColor);
        gD.setShape(GradientDrawable.OVAL);
        gD.setStroke(strokeWidth, strokeColor);
        button.setBackground(gD);
    }


    public interface NotesSelectColorListener {
        void onItemClick(NoteColor.ItemColor item, ImageButton color, int adapterPosition);
    }
}