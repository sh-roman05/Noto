package com.roman.noto.ui.ChooseHashtags;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.roman.noto.R;
import com.roman.noto.data.Note;

public class ChooseHashtagsActivity extends AppCompatActivity implements ChooseHashtagsContract.View {

    static final String TAG = "ChooseHashtagsActivity";

    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_hashtags);

        note = getIntent().getParcelableExtra("note");


        //получить хештеги все


    }
}