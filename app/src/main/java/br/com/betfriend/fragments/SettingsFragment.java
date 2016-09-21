package br.com.betfriend.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import br.com.betfriend.R;
import br.com.betfriend.SignInActivity;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;

    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.main_progressbar);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean visible = prefs.getBoolean("key_visible", true);
        boolean notificationVibration = prefs.getBoolean("key_notif_vibrate", true);
        boolean notificationSound = prefs.getBoolean("key_notif_sound", true);

        prefs.edit().putBoolean("key_visible", visible).apply();
        prefs.edit().putBoolean("key_notif_vibrate", notificationVibration).apply();
        prefs.edit().putBoolean("key_notif_sound", notificationSound).apply();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile().requestIdToken(getString(R.string.server_client_id)).build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();

        Preference disconnectAccountPref = findPreference("disconnect_account");
        disconnectAccountPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                mProgressBar.setVisibility(View.VISIBLE);

                Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {

                                Intent intent = new Intent(getActivity(), SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getActivity().startActivity(intent);
                            }
                        });

                return false;
            }
        });

        Preference removeAccountPref = findPreference("remove_account");
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}


