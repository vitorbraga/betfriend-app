package br.com.betfriend.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import br.com.betfriend.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean visible = prefs.getBoolean("key_visible", true);
        boolean notificationVibration = prefs.getBoolean("key_notif_vibrate", true);
        boolean notificationSound = prefs.getBoolean("key_notif_sound", true);

        prefs.edit().putBoolean("key_visible", visible).apply();
        prefs.edit().putBoolean("key_notif_vibrate", notificationVibration).apply();
        prefs.edit().putBoolean("key_notif_sound", notificationSound).apply();

        Preference disconnectAccountPref= findPreference("disconnect_account");
        disconnectAccountPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String key = preference.getKey();
                Log.d("key:", key);
                Toast.makeText(getActivity(), "disconnect", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        Preference removeAccountPref= findPreference("remove_account");
        removeAccountPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String key = preference.getKey();
                Log.d("key:", key);
                Toast.makeText(getActivity(), "remove", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        getActivity().setTheme(R.style.PreferencesTheme);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("key:", key);
    }
}


