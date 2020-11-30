package info.androidhive.firebaseauthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;

import info.androidhive.firebaseauthapp.ui.dashboard.DashboardFragment;
import info.androidhive.firebaseauthapp.ui.home.Frag1;
import info.androidhive.firebaseauthapp.ui.home.HomeFragment;
import info.androidhive.firebaseauthapp.ui.notifications.RecipeFragment;
import info.androidhive.firebaseauthapp.ui.profile.ProfileFragment;
import info.androidhive.firebaseauthapp.ui.social.SocialFragment;

public class HomeActivity extends FragmentActivity implements BottomNavigationView.OnNavigationItemSelectedListener , Frag1.Frag1TimeListener {

    BottomNavigationView navigation;
    private int counter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Stetho.initializeWithDefaults(this);
        Log.e("run","執行onCreate");

        //loading the default fragment
        loadFragment(new HomeFragment());

        //getting bottom navigation view and attaching the listener
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        //設定提醒
        Calendar cal = Calendar.getInstance(); //取得時間
        cal.add(Calendar.SECOND, 10);


        //add_alarm(this, cal);
//        createNotificationChannel();
//        StopService();
//        StartService();
        createNotificationChannel();
        cancelJob();
        scheduleJob();
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        Log.e("run","執行onRestart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //判斷(由各個Fragment)傳送過來的intent所挾帶的值為多少
        Log.e("run","執行onResume");
        Intent intent = getIntent();
        int id = intent.getIntExtra("id",0);
        Log.e("run","執行onResume，id = "+id);
        //1的話，為PostingActivity上傳完貼文後的傳值
        if(id == 1){
            Fragment fragment = new SocialFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container,fragment);
            transaction.commit();
            //將導向到SocialFragment
            Log.e("run","執行onResume，id = "+id);
            //將ButtomVavigation的指標設到navigation_social
            navigation.setSelectedItemId(R.id.navigation_social);
            //重設intent夾帶的值
            intent.putExtra("id", 0);
        }if(id == 2){
            Fragment fragment = new RecipeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container,fragment);
            transaction.commit();
            //將導向到SocialFragment
            Log.e("run","執行onResume，id = "+id);
            //將ButtomVavigation的指標設到navigation_social
            navigation.setSelectedItemId(R.id.navigation_notifications);
            //重設intent夾帶的值
            intent.putExtra("id", 0);
        }
    }

    public void scheduleJob(){
        ComponentName componentName = new ComponentName(this,FastingJobService.class);
        JobInfo info = new JobInfo.Builder(123,componentName)
                .setPersisted(true)
                .setOverrideDeadline(0)
                .build();

        JobScheduler scheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS){
            Log.e("job scheduled","success");
        }else {
            Log.e("job scheduled","failed");
        }
    }

    public void cancelJob(){
        JobScheduler scheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.e("job canceled","job has been canceled");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;

            case R.id.navigation_dashboard:
                fragment = new DashboardFragment();
                break;

            case R.id.navigation_notifications:
                fragment = new RecipeFragment();
                break;
            case R.id.navigation_social:
                fragment = new SocialFragment();
                break;
            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

//    public void add_alarm(Context context, Calendar cal) {
//
//        Toast.makeText(context,"有執行",Toast.LENGTH_SHORT).show();
//        createNotificationChannel();
//
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, AlarmReciever.class);
//        intent.putExtra("type",10001);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),1000*60*3, pendingIntent);
//    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = "斷時進程";
            String description = "channel for fasting progress";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel= new NotificationChannel("LemmeNo",name,importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(R.color.colorPrimary);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            //NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


            int importance_repeat_notification = NotificationManager.IMPORTANCE_HIGH;
            CharSequence name2 = "斷時進程2";
            String description2 = "channel for fasting progress2";
            NotificationChannel channe2= new NotificationChannel("channe2",name2,importance_repeat_notification);
            channe2.setDescription(description2);
            channe2.enableLights(true);
            channe2.enableVibration(true);
            channe2.setLightColor(R.color.colorPrimary);
            channe2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager2 = getSystemService(NotificationManager.class);
            notificationManager2.createNotificationChannel(channe2);
        }
    }
    //開始前景執行
    public void StartService(ArrayList<String> start_date,ArrayList<String> end_date,ArrayList<Integer> off_day){
        Intent serviceIntent = new Intent(this,FastTimerService.class);
        serviceIntent.putStringArrayListExtra("inputStartDates",start_date);
        serviceIntent.putStringArrayListExtra("inputEndDates",end_date);
        serviceIntent.putIntegerArrayListExtra("inputOffDates",off_day);
        ContextCompat.startForegroundService(this,serviceIntent);
    }
    //停止前景執行
    public void StopService(){
        Intent serviceIntent = new Intent(this,FastTimerService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        Log.e("destroyed","destroyed");
        //StopService();
        //Toast.makeText(this, "destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        counter++;

        if(counter==2){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("離開app");
            builder.setCancelable(true);
            builder.setNegativeButton("我想再看看", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("(揮淚狠心離去)", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addCategory(Intent.CATEGORY_HOME);
                    startActivity(i);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            counter =0;
        }else{
            Toast.makeText(this, "確定退出嗎?", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return super.onKeyDown(keyCode, event);
        }
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_MEDIA_PLAY:
//                Toast.makeText(this, "KEYCODE_MEDIA_PLAY", Toast.LENGTH_SHORT).show();
//                AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                amanager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND,AudioManager.ADJUST_RAISE);
//                return true;
//        }

//        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
//            new AlertDialog.Builder(HomeActivity.this)
//                    .setTitle("確認視窗")
//                    .setMessage("確定要結束應用程式嗎?")
//                    .setPositiveButton("確定",
//                            new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    Intent i = new Intent(Intent.ACTION_MAIN);
//                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    i.addCategory(Intent.CATEGORY_HOME);
//                                    startActivity(i);
//                                }
//                            })
//                    .setNegativeButton("取消",
//                            new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    // TODO Auto-generated method stub
//
//                                }
//                            }).show();
//        }
        return true;
    }


    @Override
    public void onTimeChanged(ArrayList<Long> start_time, ArrayList<Long> end_time ,ArrayList<Integer> off_day) {

        if (start_time!= null && end_time!=null){
            Log.e("getTimeFromHome",start_time+"");
            Log.e("getTimeFromHome",end_time+"");
            ArrayList<String>start_date = new ArrayList<>();
            ArrayList<String>end_date = new ArrayList<>();
            for (long start:start_time){
                start_date.add(String.valueOf(start));
            }
            for (long end:end_time){
                end_date.add(String.valueOf(end));
            }
            //createNotificationChannel();
            StopService();
            StartService(start_date,end_date,off_day);
        }
        else{
            Log.e("get0Data","u havent choose a fasting plan");
        }

    }
}
