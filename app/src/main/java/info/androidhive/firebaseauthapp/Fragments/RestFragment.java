package info.androidhive.firebaseauthapp.Fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import info.androidhive.firebaseauthapp.R;


public class RestFragment extends DialogFragment{

    public static RestFragment instance;
    RestFragmentListener listener;
    private Button btn_rest;
    private TextView tv_rest_time;
    private long initTime = 25000;
    private long timeLeft ;
    CountDownTimer countDownTimer;
    private ProgressBar progressBar;
    private int progress ;
    private boolean isRunning;

    public RestFragment() {
        // Required empty public constructor
    }

    public static RestFragment getInstance() {
        if (instance == null){
            instance = new RestFragment();
        }
        return instance;
    }

    public void setListener(RestFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View itemView =  inflater.inflate(R.layout.fragment_rest, container, false);

        btn_rest = itemView.findViewById(R.id.btn_rest);
        tv_rest_time = itemView.findViewById(R.id.tv_rest_time);
        progressBar = itemView.findViewById(R.id.progressBar_rest);
        progressBar.setMax((int)(initTime/1000));
        startCount();




        btn_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onRestDone(true);
                if (countDownTimer!= null){
                    countDownTimer.cancel();
                }
            }
        });

        return itemView;


    }

    private void startCount() {
        timeLeft = initTime;
        progress = 0;
        progressBar.setProgress(0);
        countDownTimer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                tv_rest_time.setText((timeLeft/1000)+"秒");
                if (progress<=24){
                    progress=(int)(25-(millisUntilFinished/1000));
                    Log.e("progress",progress+"");

                    progressBar.setProgress(progress,true);
                    //startAnim();
                }
            }

            @Override
            public void onFinish() {
                //isRunning = false;
                listener.onRestDone(true);
                countDownTimer.cancel();
            }
        }.start();
        //isRunning = true;
    }

    private  void startAnim(){
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progress,progress+1);
        animation.setDuration(1000); // 0.5 second
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

}