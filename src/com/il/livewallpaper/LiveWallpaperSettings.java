package com.il.livewallpaper;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class LiveWallpaperSettings extends PreferenceActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.live_wallpaper_settings);
    }
    
    public void SetCheckBoxValue(String myCheckBoxVal)
    {
        //  dsf
    }
}