package br.com.betfriend.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import br.com.betfriend.MainActivity;
import br.com.betfriend.R;
import br.com.betfriend.utils.FcmMessagesEnum;

public class BetFriendFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "New message received.");

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage.getData().get("code"));
        }
    }

    private void sendNotification(String code) {

        String title = getString(R.string.app_name), messageBody = getString(R.string.app_name);

        Integer codeInt = Integer.parseInt(code);
        FcmMessagesEnum fme = FcmMessagesEnum.get(codeInt);

        if(fme != null) {
            title = getString(fme.title());
            messageBody = getString(fme.messageBody());
        }

        Intent intent = new Intent(this, MainActivity.class);
        if (code.equals("101")) {
            // New Bet invite
            intent.putExtra("MENU_FRAGMENT", R.id.nav_invites);
        } else if(code.equals("102")) {
            // Bet accepted
            intent.putExtra("MENU_FRAGMENT", R.id.nav_history);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_white)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(messageBody)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
