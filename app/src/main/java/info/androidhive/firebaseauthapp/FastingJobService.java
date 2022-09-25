package info.androidhive.firebaseauthapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import info.androidhive.firebaseauthapp.Notification.AlarmReciever;

public class FastingJobService extends JobService {

    private boolean jobCancelled = false;
    int counter = 0;
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e("onStartJob","job service started");
        doBackGround(params);
        return false;
    }

    private void doBackGround(JobParameters params) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true){
//                    counter+=1;
//                    Log.e("run","run: "+counter);
//                    if (jobCancelled){
//                        return;
//                    }
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                //Log.e("run","run: finished");
//                //jobFinished(params,false);
//            }
//        }).start();
        Calendar cal = Calendar.getInstance(); //取得時間
        cal.set(Calendar.HOUR_OF_DAY,9);
        cal.set(Calendar.MINUTE,35);
        add_alarm(this, cal);
    }
    public void add_alarm(Context context, Calendar cal) {

        Toast.makeText(context,"有執行",Toast.LENGTH_SHORT).show();


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReciever.class);
        intent.putExtra("type",10001);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
    }
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e("onStopJob","job service stopped");
        jobCancelled = true;
        //boolean means if we want to reschedule our job or not
        return true;
    }
}
