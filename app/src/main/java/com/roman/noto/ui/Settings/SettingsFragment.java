package com.roman.noto.ui.Settings;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.roman.noto.R;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {
    static final String TAG = "SettingsFragment";

    private final SettingsContract.Presenter presenter;

    SettingsFragment(SettingsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_settings, rootKey);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(requireContext());
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
            DialogInterface.OnClickListener alertCallback = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    presenter.clearDatabase();
                }
            };
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.activity_settings_alert_delete_title))
                    .setMessage(getString(R.string.activity_settings_alert_delete_message))
                    .setPositiveButton(getString(R.string.activity_settings_alert_delete_yes), alertCallback)
                    .setNegativeButton(getString(R.string.activity_settings_alert_delete_no), null)
                    .show();
        }
        return super.onPreferenceTreeClick(preference);
    }
}
