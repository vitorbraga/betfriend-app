package br.com.betfriend;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.Match;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BetInvitationService extends Service {

    private Timer timer = new Timer();

    public BetInvitationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendRequestToServer();   //Your code here
            }
        }, 0, 1 * 20 * 1000);//5 Minutes (20 SEGUNDOS)
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void sendRequestToServer() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.server_uri)).build();

        ServerApi api = restAdapter.create(ServerApi.class);

        api.getMatches("application/json", new Callback<ArrayList<Match>>() {

            @Override
            public void success(ArrayList<Match> matches, Response response) {

                Toast.makeText(BetInvitationService.this, "success", Toast.LENGTH_SHORT).show();
//                NotificationCompat.Builder mBuilder =
//                        new NotificationCompat.Builder(getApplicationContext())
//                                .setContentTitle("My notification")
//                                .setContentText("Hello World!");
//                mBuilder.notify();
                Log.d("BetInvitationService", "success HTTP");
            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(BetInvitationService.this, "failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
