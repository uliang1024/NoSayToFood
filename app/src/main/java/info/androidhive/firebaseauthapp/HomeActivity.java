package info.androidhive.firebaseauthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.HashMap;
import java.util.Map;

import info.androidhive.firebaseauthapp.first.HelloUser;

import info.androidhive.firebaseauthapp.ui.dashboard.DashboardFragment;
import info.androidhive.firebaseauthapp.ui.home.HomeFragment;
import info.androidhive.firebaseauthapp.ui.notifications.NotificationsFragment;
import info.androidhive.firebaseauthapp.ui.profile.ProfileFragment;
import info.androidhive.firebaseauthapp.ui.social.Frag_posting;
import info.androidhive.firebaseauthapp.ui.social.SocialFragment;

public class HomeActivity extends FragmentActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    BottomNavigationView navigation;
    private int counter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.e("run","執行onCreate");

        //loading the default fragment
        loadFragment(new HomeFragment());

        //getting bottom navigation view and attaching the listener
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

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
            Fragment fragment = new NotificationsFragment();
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
                fragment = new NotificationsFragment();
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

    @Override
    public void onBackPressed() {
        counter++;

        if(counter==2){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("確定關機??");
            builder.setCancelable(true);
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
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


}
