package info.androidhive.firebaseauthapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FastTimerService extends Service {
    //跟HomeActivity裡面的notification channel 的 id 依樣
    private static final String CHANNEL_ID = "LemmeNo";
    ArrayList<Date> start_dates;
    ArrayList<Date> end_dates;

    ArrayList<Long> start_times;
    ArrayList<Long> end_times;

    ArrayList<Integer> off_day;

    NotificationManager notificationManager;
    NotificationCompat.Builder notification;

    RemoteViews collapsedView;
    int index ;
    private final Handler handler = new Handler();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("service created","created");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.e("service destroyed","destroyed");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<String> getStartDates = intent.getStringArrayListExtra("inputStartDates");
        ArrayList<String> getEndDates = intent.getStringArrayListExtra("inputEndDates");
        off_day = intent.getIntegerArrayListExtra("inputOffDates");

        start_times = new ArrayList<>();
        end_times = new ArrayList<>();
        for (int i=0;i<=getStartDates.size()-1;i++){
            start_times.add(Long.parseLong(getStartDates.get(i)));
            end_times.add(Long.parseLong(getEndDates.get(i)));
        }

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        start_dates = new ArrayList<>();
        end_dates = new ArrayList<>();
        for (int i=0;i<=getStartDates.size()-1;i++){
            Date date = new Date();
            date.setTime(start_times.get(i));
            start_dates.add(date);
            date.setTime(end_times.get(i));
            end_dates.add(date);
        }
        Log.e("service data",start_dates+"");
        Log.e("service data",end_dates+"");

        //notificationView = new RemoteViews.RemoteView(getPackageName(), R.layout.notification_layout);
        collapsedView = new RemoteViews(getPackageName(),
                R.layout.notification_layout);

        Intent navigationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(this,
                0, navigationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                //.setContentTitle("title")
                .setSmallIcon(R.drawable.ic_baseline_check_24)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCustomContentView(collapsedView)
                .setContentIntent(pendingIntent);

        collapsedView.setImageViewResource(R.id.notification_image,R.drawable.bells);
        collapsedView.setImageViewResource(R.id.notiication_go,R.drawable.ic_next);

        //將service推至前景執行，os才不會一下子就殺掉service
        notificationManager.notify(1,notification.build());
        startForeground(1,notification.build());
        handler.removeCallbacks(updateRunner);
        handler.postDelayed(updateRunner, 0);
        //假如工作完成，就讓service自己結束

        return START_REDELIVER_INTENT;
    }

    private final Runnable updateRunner = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this,1000);
            Date date = new Date();
            long now_time = date.getTime();
            if (date.after(end_dates.get(6))) index = 7;
            for (Date d : end_dates) {
                //尋找同日期
                if (compareSameDay(date, d)) {
                    //Log.e("now date","現在日期:"+sdf2.format(date)+" 抓到的時間:"+sdf2.format(d));
                    //比較時間先後
                    //若是現在時間比抓到的時間(end_date)後面
                    if (date.after(d)) {
                        index = end_dates.indexOf(d) + 1;
                        //Log.e("讀取:","休息時間:index="+index);
                    } else {
                        index = end_dates.indexOf(d);
                        //Log.e("讀取:","斷食時間:index ="+index);
                    }
                }
            }

            Date showDate2 = new Date();
            Date showDate3 = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //當還有下一筆資料時
            if (index<=end_times.size()-1){
                //當時間在第n個開始時間開始前
                //Log.e("index",index+"");
                if (off_day.get(index)==1){
                    //如果現在時間小於開始時間
                    if (now_time<start_times.get(index)){

                        if (index==0){
                            //第一天斷食開始前的休息日
                            long mytime = (start_times.get(index)-now_time);
                            showDate2.setTime(start_times.get(index));
                            showDate3.setTime(now_time);
                            //Log.e("service 準備","開始時間 :"+sdf.format(showDate2)+" 現在時間 :"+sdf.format(showDate3));
                            //Log.e("service距離斷時開始還有",getTimeLeft(mytime/1000));
                            //String status = "service 距離斷時開始還有\n"+getTimeLeft(mytime/1000)+"\n"+"在"+sdf.format(start_dates.get(index))+"開始斷食";
                            String status = "距離斷時開始還有"+getTimeLeft(mytime/1000);
                            collapsedView.setTextViewText(R.id.notification_status,"進食時間");
                            collapsedView.setTextViewText(R.id.notification_timeleft,status);
                            //notification.setContentText(status);
                            notificationManager.notify(1,notification.build());
                        }else{
                            //其它天的休息日
                            long mytime = (start_times.get(index)-now_time);
                            showDate2.setTime(start_times.get(index));
                            showDate3.setTime(now_time);
                            //String status = "距離斷時開始還有\n"+getTimeLeft(mytime/1000)+"\n"+"在"+sdf.format(start_dates.get(index))+"開始斷食";
                            String status = "距離斷時開始還有"+getTimeLeft(mytime/1000);
                            //Log.e("service準備","開始時間 :"+sdf.format(showDate2)+" 現在時間 :"+sdf.format(showDate3));
                            //notification.setContentText(status);
                            collapsedView.setTextViewText(R.id.notification_status,"進食時間");
                            collapsedView.setTextViewText(R.id.notification_timeleft,status);
                            notificationManager.notify(1,notification.build());

                        }
                    }
                    //時間介於第n個開始時間與第n個結束時間時
                    else if (now_time>=start_times.get(index)&&now_time<=end_times.get(index)){
                        //tv_percent.setText(""+getPercent(now_time,false));
                        //Log.e("service斷食中","開始時間 : "+sdf.format(start_dates.get(index))+" 剩餘時間 :"+(end_times.get(index)-now_time));
                        //String status = "距離斷食結束還有\n"+getTimeLeft((end_times.get(index)-now_time)/1000)+"\n"+"在"+sdf.format(end_dates.get(index))+"開始進食";
                        String status = "距離斷食結束還有"+getTimeLeft((end_times.get(index)-now_time)/1000);
                        collapsedView.setTextViewText(R.id.notification_status,"斷食時間");
                        collapsedView.setTextViewText(R.id.notification_timeleft,status);
                        notificationManager.notify(1,notification.build());
                    }
                }else {
                    //Log.e("service","斷食結束，現在維修昔日");
                    collapsedView.setTextViewText(R.id.notification_status,"斷食結束");
                    collapsedView.setTextViewText(R.id.notification_timeleft,"今日為休息日");
                }

            }else {
                //Log.e("service ",index+"此次斷食完成");
                collapsedView.setTextViewText(R.id.notification_status,"斷食完成");
                collapsedView.setTextViewText(R.id.notification_timeleft,"此次斷食完成");
            }
        }
    };
    public boolean compareSameDay(Date current ,Date compare){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(current);
        cal2.setTime(compare);

        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }
    public String getTimeLeft(long time){
        long hour = time/(60*60);
        long min = (time%(60*60))/(60);
        long sec = (time%(60*60))%(60);
        return String.format("%d小時 %d分 %d秒",hour,min,sec);
    }

}
