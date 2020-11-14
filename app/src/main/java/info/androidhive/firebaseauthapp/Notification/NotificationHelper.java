package info.androidhive.firebaseauthapp.Notification;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import info.androidhive.firebaseauthapp.HomeActivity;
import info.androidhive.firebaseauthapp.R;


public class NotificationHelper
        extends ContextWrapper {
    public static final String channel1ID = "channel1ID";
    public static final String channel1Name = "channel 1";
    public static final String channel2ID = "channel2ID";
    public static final String channel2Name = "channel 2";
    private NotificationManager mManager;

    public NotificationHelper(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT >= 26) {
            this.createChannel();
        }
    }

    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(channel1ID, (CharSequence)channel1Name, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(R.color.colorPrimary);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        this.getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationCompat.Builder getChanne2_1Notification(String string2, String string3) {
        return new NotificationCompat.Builder(this.getApplicationContext(), channel2ID).setContentTitle((CharSequence)string2).setContentText((CharSequence)string3).setSmallIcon(2131099735);
    }

    public NotificationCompat.Builder getChannel_1Notification() {
        PendingIntent pendingIntent = PendingIntent.getActivity((Context)this, 1, (Intent)new Intent((Context)this, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(this.getApplicationContext(), channel1ID)
                .setContentTitle((CharSequence)"Alarm!")
                .setContentText((CharSequence)"POWER CONNECT")
                .setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_baseline_check_24)
                .setAutoCancel(true);
    }

    public NotificationCompat.Builder getChannel_3Notification() {
        PendingIntent pendingIntent = PendingIntent.getActivity((Context)this, (int)2, (Intent)new Intent((Context)this, HomeActivity.class), (int)134217728);
        return new NotificationCompat.Builder(this.getApplicationContext(), channel1ID).setContentTitle((CharSequence)"Alarm!").setContentText((CharSequence)"POWER DISCONNECT").setContentIntent(pendingIntent).setSmallIcon(2131099735).setAutoCancel(true);
    }

    public NotificationCompat.Builder getChannel_4Notification() {
        PendingIntent pendingIntent = PendingIntent.getActivity((Context)this, (int)2, (Intent)new Intent((Context)this, HomeActivity.class), (int)134217728);
        return new NotificationCompat.Builder(this.getApplicationContext(), channel1ID).setContentTitle((CharSequence)"Alarm!").setContentText((CharSequence)"BOOT UP!").setContentIntent(pendingIntent).setSmallIcon(2131099735).setAutoCancel(true);
    }

    public NotificationCompat.Builder getChannel_5Notification() {
        PendingIntent pendingIntent = PendingIntent.getActivity((Context)this, (int)2, (Intent)new Intent((Context)this, HomeActivity.class), (int)134217728);
        return new NotificationCompat.Builder(this.getApplicationContext(), channel1ID).setContentTitle((CharSequence)"Alarm!").setContentText((CharSequence)"LOCKED BOOT UP!").setContentIntent(pendingIntent).setSmallIcon(2131099735).setAutoCancel(true);
    }

    public NotificationManager getManager() {
        if (this.mManager == null) {
            this.mManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return this.mManager;
    }
}


