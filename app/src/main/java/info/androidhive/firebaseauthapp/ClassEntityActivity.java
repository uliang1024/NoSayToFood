package info.androidhive.firebaseauthapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import info.androidhive.firebaseauthapp.Fragments.RestFragment;
import info.androidhive.firebaseauthapp.Fragments.RestFragmentListener;
import info.androidhive.firebaseauthapp.classModels.ClassdataEntity;

public class ClassEntityActivity extends AppCompatActivity implements RestFragmentListener {

    ImageView action_image,btn_exit,btn_next,btn_back;
    TextView tv_action_name,tv_action_time;
    ImageButton btn_pause;
    int index = 0;
    ArrayList<ClassdataEntity> classes;

    private long initTime ;
    private long timeLeft ;
    CountDownTimer countDownTimer;
    private boolean isRunning;
    RestFragment restFragment;
    //private boolean needRun = false;
    String className;

    private static ClassEntityActivity activity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_entity);
        activity = this;
        Intent intent = getIntent();
        classes= intent.getExtras().getParcelableArrayList("class");


        Log.e("class", classes.size()+"");
        Log.e("class",classes.get(1).getMoveName());

        action_image = findViewById(R.id.action_image);
        btn_exit = findViewById(R.id.btn_exit);
        btn_next = findViewById(R.id.btn_next);
        btn_back = findViewById(R.id.btn_back);
        tv_action_name = findViewById(R.id.tv_action_name);
        tv_action_time = findViewById(R.id.tv_action_time);
        btn_pause = findViewById(R.id.btn_pause);

        show_action();

        btn_next.setOnClickListener(v -> {
            //如果還有下一項
            if (index<classes.size()-1){
                index+=1;
                //跳到下一頁時，如果timer還在運行，就停止
                if (countDownTimer!= null){
                    countDownTimer.cancel();
                }

                show_fragment();
            }
            //如果到底
            else{
                Toast.makeText(ClassEntityActivity.this, "已達最大值", Toast.LENGTH_SHORT).show();
            }


        });

        btn_back.setOnClickListener(v -> {
            if (index>0){
                index-=1;
                if (countDownTimer!= null){
                    countDownTimer.cancel();
                }

                show_fragment();
            }else{
                Toast.makeText(ClassEntityActivity.this, "已達最小值", Toast.LENGTH_SHORT).show();
            }


        });

        btn_pause.setOnClickListener(v -> {
            //如果還有項目
            if (index<classes.size()-1){
                //如果以次為單位
                if (classes.get(index).getUnit().equals("time")  ){
                    index+=1;
                    show_fragment();

                }
                //如果以秒為單位
                else{
                    if (isRunning){
                        pauseTimer();
                    }else{
                        startTimer();
                    }

                }
            }
            //如果到底了
            else {
                //如果是以秒為單位
                if (classes.get(index).getUnit().equals("second")){
                    if (isRunning){
                        pauseTimer();
                    }else{
                        startTimer();
                    }
                }else {
                    Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
                }
            }

        });



    }
    //處理計時器
    private void startTimer() {

        countDownTimer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                tv_action_time.setText((timeLeft/1000)+"秒");
            }

            @Override
            public void onFinish() {
                //結束計時後...
                isRunning = false;
                Toast.makeText(ClassEntityActivity.this, "finish", Toast.LENGTH_SHORT).show();
                //如果還有下一項目
                if (index<classes.size()-1){
                    index+=1;
                    //取消計時
                    countDownTimer.cancel();
                    //顯示下一項目
                    show_fragment();
                }
                //如果是最後一個課程
                else{
                    Toast.makeText(ClassEntityActivity.this, "教學結束", Toast.LENGTH_SHORT).show();
                }

            }
        }.start();
        isRunning = true;
        btn_pause.setImageResource(R.drawable.ic_baseline_pause_24);
    }

    //暫停計時器
    private void pauseTimer() {
        countDownTimer.cancel();
        isRunning = false;
        btn_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
    }

    private void show_fragment() {
        restFragment = RestFragment.getInstance();
        restFragment.setListener(this);
        restFragment.setCancelable(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        restFragment.show(fragmentManager,"");

    }

    private void show_action() {
        Glide.with(this).load(classes.get(index).getMoveImage()).into(action_image);
        tv_action_name.setText(classes.get(index).getMoveName());
        //如果以次為單位
        if (classes.get(index).getUnit().equals("time")){
            tv_action_time.setText(classes.get(index).getMoveTimes()+"次");
            btn_pause.setImageResource(R.drawable.ic_baseline_check_24);

        }
        //如果以秒為單位
        else{
            tv_action_time.setText(classes.get(index).getMoveTimes()+"秒");
            btn_pause.setImageResource(R.drawable.ic_baseline_pause_24);
            //重置時間為該課程的秒數
            initTime = classes.get(index).getMoveTimes()*1000;
            timeLeft = initTime;
            //開始計時
            startTimer();
        }


        if (index == 0){
            btn_back.setVisibility(View.INVISIBLE);
            btn_next.setVisibility(View.VISIBLE);
        }else if (index == classes.size()-1){
            btn_next.setVisibility(View.INVISIBLE);
            btn_back.setVisibility(View.VISIBLE);
        }else {
            btn_next.setVisibility(View.VISIBLE);
            btn_back.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onRestDone(boolean done) {
        if (done){
            restFragment.dismiss();
            show_action();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("確定離開課程??");
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

                //todo:回到主葉面後，在導航置運動介面
                Intent i = new Intent(ClassEntityActivity.this,HomeActivity.class);
                i.putExtra("id",2);
                startActivity(i);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}