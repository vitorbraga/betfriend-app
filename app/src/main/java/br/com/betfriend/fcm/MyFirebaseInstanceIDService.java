package br.com.betfriend.fcm;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.JsonResponse;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {

        // Send new token to server, if user is registered and logged

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String personId = sharedPref.getString("PERSON_ID", null);


        if(personId != null) {

            // user is registered and logged

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