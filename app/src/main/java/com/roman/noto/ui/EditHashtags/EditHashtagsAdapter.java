package com.roman.noto.ui.EditHashtags;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.roman.noto.R;
import com.roman.noto.RecyclerViewEmptySupport;
import com.roman.noto.data.Hashtag;
import com.roman.noto.ui.ChooseHashtags.ChooseHashtag;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class EditHashtagsAdapter extends RecyclerView.Adapter<EditHashtagsAdapter.CustomViewHolder> {

    //Проблема. При редактировании не прокрутки из-за android:windowSoftInputMode="adjustPan" в манифесте
    //Если поставить adjustResize, то на последних элементах слетает фокус. Можно решить ручной прокруткой.
    //Но, я не знаю куда крутить, клик получается позже перестройки layout.
    //addOnLayoutChangeListener и scrollToPosition

    static final String TAG = "EditHashtagsAdapter";

    private List<Hashtag> list = new ArrayList<>();
    Context context;
    EditHashtagsContract.Presenter presenter;
    InputMethodManager inputMethodManager;




    boolean keyboardIsOpen;

    public EditHashtagsAdapter(Context context, EditHashtagsContract.Presenter presenter) {
        this.context = context;
        this.presenter = presenter;
        inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        keyboardIsOpen = false;
    }

    public void setKeyboardStatus(boolean status) {
        keyboardIsOpen = status;
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
            itemView.setClickable(true);
            itemView.setFocusable(true);

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
                    }
                }
            });

            //Сохраняем все изменения в тексте
            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    saveCurrentText(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) { }
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
                            //Убрать фокус и клавиатуру
                            name.clearFocus();
                            if(keyboardIsOpen) hideKeyboard();
                            //
                            int position = getAbsoluteAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                Hashtag hashtag = list.get(position);
                                //Удалить в Repository
                                presenter.deleteHashtag(new Hashtag(hashtag.getId(), hashtag.getName()));
                                //Удалить в адаптере
                                list.remove(position);
                                notifyItemRemoved(position);
                            }
                        }
                    };
                    new MaterialAlertDialogBuilder(context)
                            .setTitle(context.getString(R.string.activity_edit_hashtags_alert_title))
                            .setMessage(context.getString(R.string.activity_edit_hashtags_alert_message))
                            .setPositiveButton(context.getString(R.string.activity_edit_hashtags_alert_delete_yes), alertCallback)
                            .setNegativeButton(context.getString(R.string.activity_edit_hashtags_alert_delete_no), null)
                            .show();
                }
            });

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name.clearFocus();
                    if(keyboardIsOpen) {
                        hideKeyboard();
                    }
                }
            });
        }






        private void hideKeyboard() {
            inputMethodManager.hideSoftInputFromWindow(name.getWindowToken(), 0);
        }

        private void saveCurrentText(String text) {
            int position = getAbsoluteAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                list.get(position).setName(text);
            }
        }

        //Проверить на наличие физической клавиатуры
        private boolean isHardwareKeyboardAvailable(Context context) {
            return context.getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS;
        }

        private void setFocusOnEditText() {
            name.requestFocus();
            name.setSelection(name.getText().length());

            if(!keyboardIsOpen){
                if(isHardwareKeyboardAvailable(context)){
                    if(inputMethodManager.isActive()){
                        inputMethodManager.toggleSoftInputFromWindow(name.getWindowToken(), InputMethodManager.SHOW_IMPLICIT, 0);
                    }
                }
            }


        }

        @Override
        public void onClick(View v) {
            setFocusOnEditText();
        }

    }

}
