package com.roman.noto.ui.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;


import com.roman.noto.R;
import com.roman.noto.data.repository.CacheRepository;
import com.roman.noto.data.repository.LocalRepository;
import com.roman.noto.util.AppExecutors;

public class SettingsActivity extends AppCompatActivity implements SettingsContract.View {

    static final String TAG = "SettingsActivity";
    public SettingsContract.Presenter presenter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.activity_settings_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!= null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter = new SettingsPresenter(this, CacheRepository.getInstance(
                LocalRepository.getRepository(
                        new AppExecutors(), getApplicationContext()
                )
        ));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_settings_fragment, new SettingsFragment(presenter))
                .commit();
    }
}
