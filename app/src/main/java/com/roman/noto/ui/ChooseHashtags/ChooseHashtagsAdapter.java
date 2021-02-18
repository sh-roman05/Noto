package com.roman.noto.ui.ChooseHashtags;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.common.base.Strings;
import com.roman.noto.R;

import java.util.ArrayList;
import java.util.List;


public class ChooseHashtagsAdapter extends RecyclerView.Adapter<ChooseHashtagsAdapter.CustomViewHolder>  {

    private List<ChooseHashtag> list = new ArrayList<>();

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_hashtag_item, null);
        return new CustomViewHolder(view);
    }

    public void setList(List<ChooseHashtag> list)
    {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<ChooseHashtag> getList(){
        return list;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        ChooseHashtag item = list.get(position);

        holder.selected.setChecked(item.isSelect());

        if(Strings.isNullOrEmpty(item.getName()))
            holder.name.setText("Нет названия");
        else
            holder.name.setText(item.getName());

    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView name;
        private final MaterialCheckBox selected;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.name = (TextView) itemView.findViewById(R.id.choose_hashtag_item_name);
            this.selected = (MaterialCheckBox) itemView.findViewById(R.id.choose_hashtag_item_check);

            selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        list.get(adapterPosition).setSelect(isChecked);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            //Обрабатывать клик
            int adapterPosition = getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                //listener.onItemClick(adapterPosition);
                selected.setChecked(!selected.isChecked());
            }
        }

    }
}
