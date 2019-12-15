package com.roman.noto.ui.About;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.roman.noto.BuildConfig;
import com.roman.noto.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AboutFragment extends PreferenceFragmentCompat {

    static final String TAG = "AboutFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_about, rootKey);

        Preference app_version = findPreference( "app_version" );
        Preference build_time = findPreference( "build_time" );
        //Дата сборки
        Date buildDate = new Date(BuildConfig.TIMESTAMP);
        //Берем строку из ресурсов и форматируем дату
        try {
            SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            dateFormat.applyPattern(getString(R.string.activity_about_build_time_format));
            build_time.setSummary(dateFormat.format(buildDate));
        }catch (Exception ex){
            build_time.setSummary(buildDate.toString());
        }

        //Версия
        String version = BuildConfig.VERSION_NAME;
        app_version.setSummary(version);
    }

}
