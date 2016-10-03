package br.com.betfriend;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.Bet;
import br.com.betfriend.model.JsonResponse;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class RefreshUserDataService extends Service {

    private Timer timer = new Timer();

    public RefreshUserDataService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendRequestToServer();   //Your code here
            }
        }, 0, 5 * 60 * 1000);//5 Minutes
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void sendRequestToServer() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication());
        final String personId = sharedPref.getString("PERSON_ID", "");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_API_BASE_URI)
                .setConverter(new GsonConverter(gson)).build();

        ServerApi api = restAdapter.create(ServerApi.class);

        api.getUserData(Constants.SERVER_KEY, "application/json", personId, new Callback<UserDataDTO>() {

            @Override
            public void success(UserDataDTO user, Response response) {
                Toast.makeText(RefreshUserDataService.this, "success 33", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(RefreshUserDataService.this, "failure 33", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
