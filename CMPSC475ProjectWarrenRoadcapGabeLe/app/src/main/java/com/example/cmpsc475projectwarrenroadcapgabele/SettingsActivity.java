package com.example.cmpsc475projectwarrenroadcapgabele;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean change = sharedPref.getBoolean("dark_theme_checkbox", true);
        changeTheme(change);
        setContentView(R.layout.activity_settings);
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            // Register the preference change listener
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Unregister the preference change listener
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("dark_theme_checkbox")) {
                boolean isChecked = sharedPreferences.getBoolean(key, false);
                changeTheme(isChecked);
            }
        }

        public void changeTheme(Boolean themeChanged) {
            if (themeChanged) {
                Toast.makeText(getActivity(), "setting theme dark", Toast.LENGTH_LONG).show();
                getActivity().setTheme(R.style.AppTheme_Dark);
            } else {
                Toast.makeText(getActivity(), "setting theme light", Toast.LENGTH_LONG).show();
                getActivity().setTheme(R.style.AppTheme);
            }
            Intent intent = new Intent(((SettingsActivity) getActivity()), MainActivity.class);
            startActivity(intent);
        }

    }

    public void changeTheme(Boolean themeChanged) {
        if (themeChanged) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }
    }

}