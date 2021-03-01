package com.roman.noto.ui.EditHashtags;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.roman.noto.R;
import com.roman.noto.data.Hashtag;
import com.roman.noto.ui.ChooseHashtags.ChooseHashtag;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class EditHashtagsAdapter extends RecyclerView.Adapter<EditHashtagsAdapter.CustomViewHolder> {

    private List<Hashtag> list = new ArrayList<>();
    Context context;

    public EditHashtagsAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<Hashtag> list)
    {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<Hashtag> getList(){
        return list;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_hashtag, null);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Hashtag item = list.get(position);
        holder.name.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View itemView, deleteButton, saveButton, hashtagButton, editButton;
        EditText name;

        public CustomViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemView.setOnClickListener(this);
            this.name = itemView.findViewById(R.id.item_edit_hashtag_name);
            deleteButton = itemView.findViewById(R.id.item_edit_hashtag_button_delete);
            saveButton = itemView.findViewById(R.id.item_edit_hashtag_button_save);
            hashtagButton = itemView.findViewById(R.id.item_edit_hashtag_button_hashtag);
            editButton = itemView.findViewById(R.id.item_edit_hashtag_button_edit);

            name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        deleteButton.setVisibility(View.VISIBLE);
                        saveButton.setVisibility(View.VISIBLE);
                        hashtagButton.setVisibility(View.INVISIBLE);
                        editButton.setVisibility(View.INVISIBLE);
                    } else {
                        deleteButton.setVisibility(View.INVISIBLE);
                        saveButton.setVisibility(View.INVISIBLE);
                        hashtagButton.setVisibility(View.VISIBLE);
                        editButton.setVisibility(View.VISIBLE);
                        saveCurrentText();
                    }
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFocusOnEditText();
                }
            });


            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Удалить хештег после предупреждения
                    DialogInterface.OnClickListener alertCallback = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int position = getAbsoluteAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                list.remove(position);
                                notifyItemRemoved(position);
                            }
                        }
                    };
                    new MaterialAlertDialogBuilder(context)
                            .setTitle(context.getString(R.string.activity_settings_alert_delete_title))
                            .setMessage(context.getString(R.string.activity_settings_alert_delete_message))
                            .setPositiveButton(context.getString(R.string.activity_settings_alert_delete_yes), alertCallback)
                            .setNegativeButton(context.getString(R.string.activity_settings_alert_delete_no), null)
                            .show();
                }
            });

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name.clearFocus();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
                }
            });
        }

        private void saveCurrentText() {
            int position = getAbsoluteAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                list.get(position).setName(name.getText().toString());
            }
        }

        //Проверить на наличие физической клавиатуры
        private boolean isHardwareKeyboardAvailable(Context context) {
            return context.getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS;
        }

        private void setFocusOnEditText() {
            if(isHardwareKeyboardAvailable(context)){
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(name.getApplicationWindowToken(), InputMethodManager.SHOW_IMPLICIT, 0);
            }
            name.requestFocus();
            name.setSelection(name.getText().length());
        }

        @Override
        public void onClick(View v) {
            setFocusOnEditText();
        }

    }

}
