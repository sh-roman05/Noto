package com.roman.noto.ui.NoteDetail;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.roman.noto.R;
import com.roman.noto.util.NoteColor;

import java.util.ArrayList;
import java.util.List;

public class NoteDetailDialogFragment extends BottomSheetDialogFragment
{
    static final String TAG = "NoteDetailDialogFragment";

    interface BottomMenuCallback
    {
        void copy();
        void archive();
        void selectColor(NoteColor.ItemColor color);
    }

    private BottomMenuCallback callback;
    private RecyclerView color_list;
    private int selectColor;

    NoteDetailDialogFragment(BottomMenuCallback callback) {
        this.callback = callback;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.note_detail_bottom_sheet, null);
        dialog.setContentView(contentView);


        LinearLayout action_delete = (LinearLayout) contentView.findViewById(R.id.activity_note_detail_action_delete);
        action_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callback.archive();
            }
        });
        LinearLayout action_copy = (LinearLayout) contentView.findViewById(R.id.activity_note_detail_action_copy);
        action_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callback.copy();
            }
        });


        color_list = (RecyclerView) contentView.findViewById(R.id.activity_note_detail_color_list);
        color_list.setAdapter(adapter);
        color_list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter.replaceData(NoteColor.getInstance().getList());

    }

    public void setColor(int color)
    {
        this.selectColor = color;
    }

    private ColorsAdapter adapter = new ColorsAdapter(new ArrayList<NoteColor.ItemColor>(), new ColorListener() {
        @Override
        public void onItemClick(NoteColor.ItemColor item, ImageButton color, int adapterPosition) {
            callback.selectColor(item);

            //Убрать выделение цвета
            for (int childCount = color_list.getChildCount(), i = 0; i < childCount; ++i) {
                final RecyclerView.ViewHolder holder = color_list.getChildViewHolder(color_list.getChildAt(i));
                ImageButton button = (ImageButton) holder.itemView.findViewById(R.id.ui_color_round_button);
                button.setImageDrawable(null);
            }

            //Выбрать текущий
            Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_done_grey, null);
            if (icon != null) color.setImageDrawable(icon);

            selectColor = adapterPosition;
        }
    });


    public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ColorViewHolder> {
        private List<NoteColor.ItemColor> list;
        private ColorListener listener;

        ColorsAdapter(List<NoteColor.ItemColor> notes, ColorListener listener) {
            setList(notes);
            this.listener = listener;
        }

        @NonNull
        @Override
        public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_list_view, null);
            return new ColorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
            NoteColor.ItemColor color = list.get(position);
            roundColorButton(holder.color, color, false);
            if(color.getIndex() == selectColor)
            {
                roundColorButton(holder.color, color, true);
            }
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

        class ColorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageButton color;

            ColorViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                this.color = (ImageButton) itemView.findViewById(R.id.ui_color_round_button);
            }

            @Override
            public void onClick(View v) {
                //Обрабатывать клик
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(list.get(adapterPosition), ColorViewHolder.this.color, adapterPosition);
                }

            }

        }
    }

    public void roundColorButton(ImageButton button, NoteColor.ItemColor itemColor, boolean select)
    {
        int strokeWidth = 3;
        int strokeColor = Color.parseColor("#424242");
        int fillColor = Color.parseColor(itemColor.getColorPrimary());

        GradientDrawable gD = new GradientDrawable();
        gD.setColor(fillColor);
        gD.setShape(GradientDrawable.OVAL);
        gD.setStroke(strokeWidth, strokeColor);
        button.setBackground(gD);

        if(select)
        {
            Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_done_grey, null);
            if (icon != null)
                button.setImageDrawable(icon);
        }
        else button.setImageDrawable(null);
    }


    public interface ColorListener {
        void onItemClick(NoteColor.ItemColor item, ImageButton color, int adapterPosition);
    }
}
