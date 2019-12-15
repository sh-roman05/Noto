package com.roman.noto.ui.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.roman.noto.R;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {
    static final String TAG = "SettingsFragment";

    private SettingsContract.Presenter presenter;

    SettingsFragment(SettingsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_settings, rootKey);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
        String temp = settings.getString("columns_list", "2");
    }



    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        /*if (preference.getKey().contains("columns_list"))
        {
            ListPreference list = (ListPreference)preference;
        }*/
        if (preference.getKey().contains("delete_base"))
        {
            presenter.clearDatabase();
        }
        return super.onPreferenceTreeClick(preference);
    }
}
