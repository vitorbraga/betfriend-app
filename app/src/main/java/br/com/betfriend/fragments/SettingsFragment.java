package br.com.betfriend.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import br.com.betfriend.R;
import br.com.betfriend.SignInActivity;
import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.JsonResponse;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

                new AlertDialog.Builder(getActivity())
                        .setTitle(getActivity().getString(R.string.disconnect_dialog_title))
                        .setMessage(getActivity().getString(R.string.disconnect_dialog_description))
                        .setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                mProgressBar.setVisibility(View.VISIBLE);

                                Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                                                        new ResultCallback<Status>() {
                                                            @Override
                                                            public void onResult(Status status) {

                                                                // Clear all shared preferences
                                                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                                                settings.edit().clear().commit();

                                                                Intent intent = new Intent(getActivity(), SignInActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                getActivity().startActivity(intent);
                                            }
                                        });
                            }
                        })
                        .setNegativeButton(getActivity().getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();

                return false;
            }
        });

        Preference removeAccountPref = findPreference("remove_account");
        removeAccountPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                new AlertDialog.Builder(getActivity())
                        .setTitle(getActivity().getString(R.string.remove_account_dialog_title))
                        .setMessage(getActivity().getString(R.string.remove_account_dialog_description))
                        .setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                mProgressBar.setVisibility(View.VISIBLE);

                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

                                String personId = prefs.getString("PERSON_ID", "");

                                RestAdapter restAdapter = new RestAdapter.Builder()
                                        .setEndpoint(Constants.SERVER_API_BASE_URI).build();

                                ServerApi api = restAdapter.create(ServerApi.class);

                                api.removeAccount(Constants.SERVER_KEY, personId, new Callback<JsonResponse>() {

                                    @Override
                                    public void success(JsonResponse json, Response response) {

                                        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                                                new ResultCallback<Status>() {
                                                    @Override
                                                    public void onResult(Status status) {

                                                        // Clear all shared preferences
                                                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                                        settings.edit().clear().commit();

                                                        Intent intent = new Intent(getActivity(), SignInActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        getActivity().startActivity(intent);
                                                    }
                                                });

                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                    }
                                });

                            }
                        })
                        .setNegativeButton(getActivity().getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                return false;
            }
        });

        getActivity().setTheme(R.style.PreferencesTheme);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.drawer_settings));
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

        if(key.equals("key_visible")) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            boolean visible = prefs.getBoolean("key_visible", true);
            String personId = prefs.getString("PERSON_ID", "");

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Constants.SERVER_API_BASE_URI).build();

            ServerApi api = restAdapter.create(ServerApi.class);

            api.setBetVisibility(Constants.SERVER_KEY, personId, visible, new Callback<JsonResponse>() {

                @Override
                public void success(JsonResponse json, Response response) {
                }

                @Override
                public void failure(RetrofitError error) {
                }
            });

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}


