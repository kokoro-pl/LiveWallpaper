package com.il.livewallpaper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.util.AttributeSet;
import android.util.Log;

@SuppressWarnings("ALL")
public class LiveWallpaperSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    ListPreference mListPreference1;
    CheckBoxPreference mCheckBox_h_o1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.live_wallpaper_settings);

        mListPreference1 = (ListPreference) findPreference("mListPreference1");
        mCheckBox_h_o1 = (CheckBoxPreference) findPreference("mCheckBox_h_o1");
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i("IL", "Key :- " + key);

        SharedPreferences customSharedPreference = getSharedPreferences(key, LiveWallpaperSettings.MODE_PRIVATE);
        SharedPreferences.Editor editor = customSharedPreference.edit();
        editor.putString("mListPreference1", mListPreference1.getValue());
        editor.putString("mCheckBox_h_o1", mCheckBox_h_o1.toString());
        editor.commit();

        Log.i("IL", "mListPreference1 :- " + mListPreference1.getValue());
        Log.i("IL", "mCheckBox_h_o1 :- " + mCheckBox_h_o1.toString());
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}