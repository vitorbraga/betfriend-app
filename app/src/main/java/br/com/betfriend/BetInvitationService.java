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
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.Bet;
import br.com.betfriend.model.JsonResponse;
import br.com.betfriend.model.Match;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

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
        }, 0, 1 * 40 * 1000);//5 Minutes (20 SEGUNDOS)
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
                .setEndpoint(getString(R.string.server_uri))
                .setConverter(new GsonConverter(gson)).build();

        final ServerApi api = restAdapter.create(ServerApi.class);

        api.getNewBetInvites("application/json", personId, new Callback<ArrayList<Bet>>() {

            @Override
            public void success(ArrayList<Bet> bets, Response response) {

                if (bets.size() > 0) {

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setContentTitle(getString(R.string.notification_content_title))
                                    .setContentText(getString(R.string.notification_content_text));

                    mBuilder.setAutoCancel(true);

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());

                    // Set vibration if allowed by user
                    boolean vibrate = prefs.getBoolean("key_notif_vibrate", true);
                    if(vibrate) {
                        mBuilder.setVibrate(new long[]{1000, 1000});
                    }

                    // Set notification sound if allowed by user
                    boolean notificationSound = prefs.getBoolean("key_notif_sound", true);
                    if(notificationSound) {
                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        mBuilder.setSound(alarmSound);
                    }

                    Intent resultIntent = new Intent(getApplicationContext(), BetInvitationsActivity.class);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplication());
                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(BetInvitationsActivity.class);
                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);

                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(100, mBuilder.build());

                    // Change notified status
                    api.invitationViewed(personId, new Callback<JsonResponse>() {

                        @Override
                        public void success(JsonResponse jsonResponse, Response response) {
                            Toast.makeText(BetInvitationService.this, "invitation viewed success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(BetInvitationService.this, "invitation viewed failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
//                    Toast.makeText(BetInvitationService.this, "sem novas apostas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(BetInvitationService.this, "failure 22", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
