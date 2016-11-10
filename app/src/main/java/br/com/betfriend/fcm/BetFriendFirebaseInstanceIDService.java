package br.com.betfriend.fcm;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.JsonResponse;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BetFriendFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FCMInstanceID";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "New refreshed token: " + refreshedToken);

        // Registering to ALL topic
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {

        // Send new token to server, if user is registered and logged

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String personId = sharedPref.getString("PERSON_ID", null);

        if (personId != null) {

            // User is registered and logged
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Constants.SERVER_API_BASE_URI).build();

            ServerApi api = restAdapter.create(ServerApi.class);

            api.updateFcmToken(Constants.SERVER_KEY, personId, token, new Callback<JsonResponse>() {

                @Override
                public void success(JsonResponse jsonResponse, Response response) {
                    // Token updated
                }

                @Override
                public void failure(RetrofitError error) {
                    // Some error has occurred
                }
            });
        }
    }
}