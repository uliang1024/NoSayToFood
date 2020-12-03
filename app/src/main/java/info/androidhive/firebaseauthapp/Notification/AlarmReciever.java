package info.androidhive.firebaseauthapp.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

import info.androidhive.firebaseauthapp.HomeActivity;
import info.androidhive.firebaseauthapp.MainActivity;
import info.androidhive.firebaseauthapp.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int i = intent.getIntExtra("type",0);
        Log.e("got it","ok"+i);

        if (i==10001){
            Log.e("ok","ok");

            Intent repeatintent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, repeatintent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"channe2")
                    .setSmallIcon(R.drawable.ic_baseline_check_24)
                    .setContentTitle("哈囉")
                    .setContentText("持之以恆，方能成功")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat notificationManager= NotificationManagerCompat.from(context);
            notificationManager.notify(200,builder.build());
        }

    }
}
