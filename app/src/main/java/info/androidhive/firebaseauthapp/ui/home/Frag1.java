package info.androidhive.firebaseauthapp.ui.home;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.SQLite.FastingPlan;
import info.androidhive.firebaseauthapp.fasting.FastingPlan1;
import info.androidhive.firebaseauthapp.fasting.Fasting_Complete;
import info.androidhive.firebaseauthapp.fasting.FirstFasting;
import me.itangqi.waveloadingview.WaveLoadingView;

public class Frag1 extends Fragment {

    FastingPlan myDb;
    private Handler handler = new Handler();
    private int series1Index;
    private DecoView decoView;
    private SeriesItem seriesItem;
    private TextView textPercentage,status,start;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ArrayList<Long> start_time = new ArrayList<>();
    private ArrayList<Long> end_time = new ArrayList<>();
    private ArrayList<Integer> off_day = new ArrayList<>();
    private HorizontalStepView horizontalStepView;

    private LinearLayout plan1,plan2,plan3,plan4;

    private TextView fastingplan;
    private Button end_fasting;

    private View fragment_frag1;

    private WaveLoadingView waveLoadingView1,waveLoadingView2,waveLoadingView3,waveLoadingView4,waveLoadingView5,waveLoadingView6,waveLoadingView7,waveLoadingView8,waveLoadingView9,waveLoadingView10;
    private Integer water=0,cc=0;
    private TextView tv_water,tv_drop,tv_add,tv_cc,title_water;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        myDb = new FastingPlan(Frag1.super.getContext());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if (user != null) {
            uid = user.getUid();
        }
        Cursor res = myDb.getAllData();
        while (res.moveToNext()) {
            if(uid.equals(res.getString(4))){
                start_time.add(res.getLong(1));
                end_time.add(res.getLong(2));
                off_day.add(res.getInt(3));
            }
        }
        if(start_time.size()!=0){
            fragment_frag1 = inflater.inflate(R.layout.fragment_frag1, container, false);
            //INIT VIEWS
            init(fragment_frag1);
            seriesItem = new SeriesItem.Builder(Color.parseColor("#E5E7E9"))
                    .setRange(0, 1, 1)
                    .build();
            series1Index = decoView.addSeries(seriesItem);
            Date date=new Date();
            long now_time =  date.getTime();
            for(int i =0; i<7;i++){
                if(now_time>=start_time.get(i)&&now_time<end_time.get(i)&& off_day.get(i)==1){
                    seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
                            .setRange(start_time.get(i), end_time.get(i), now_time)
                            .build();
                    series1Index = decoView.addSeries(seriesItem);
                    status.setText("目前為斷食時間");
                    seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
                        @Override
                        public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                            float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                            textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
                        }

                        @Override
                        public void onSeriesItemDisplayProgress(float percentComplete) {

                        }
                    });
                    break;
                }else{
                    status.setText("進食時間喔!");
                }
            }

            List<StepBean> sources =new ArrayList<>();
            for(int i =0; i<7;i++){
                if(off_day.get(i)!=0){
                    if(now_time>=end_time.get(i)){
                        sources.add(new StepBean("斷食"+(i+1)+"天",1));
                    }else if(now_time>=start_time.get(i)&&now_time<end_time.get(i)){
                        sources.add(new StepBean("斷食"+(i+1)+"天",0));
                    }else{
                        sources.add(new StepBean("斷食"+(i+1)+"天",-1));
                    }
                }
            }
            horizontalStepView.setStepViewTexts(sources)
                    .setTextSize(12)
                    .setStepsViewIndicatorCompletedLineColor(Color.parseColor("#FFFF00"))
                    .setStepViewComplectedTextColor(Color.parseColor("#000000"))
                    .setStepViewUnComplectedTextColor(Color.parseColor("#424949"))
                    .setStepsViewIndicatorUnCompletedLineColor(Color.parseColor("#424949"))
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(Frag1.super.getContext(),R.drawable.fasting_completed))
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(Frag1.super.getContext(),R.drawable.fasting_break))
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(Frag1.super.getContext(),R.drawable.fasting_not_completed));

            water();

            handler.removeCallbacks(updateTimer);
            handler.postDelayed(updateTimer, 0);

            fastingplan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Frag1.super.getContext(), FirstFasting.class));
                }
            });
            end_fasting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Frag1.super.getContext(), Fasting_Complete.class));

                    Cursor res = myDb.getAllData();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = null;
                    if (user != null) {
                        uid = user.getUid();
                    }
                    while (res.moveToNext()) {
                        if(uid.equals(res.getString(4))){
                            myDb.deleteData(uid);
                        }
                    }
                }
            });
        }else{
            fragment_frag1 = inflater.inflate(R.layout.fragment_frag1_fasting_plan, container, false);
            init2(fragment_frag1);

            plan1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Frag1.super.getContext(), FastingPlan1.class));
                }
            });
            plan2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Frag1.super.getContext(), FastingPlan1.class));
                }
            });
            plan3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Frag1.super.getContext(), FastingPlan1.class));
                }
            });
            plan4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Frag1.super.getContext(), FastingPlan1.class));
                }
            });
        }

        return fragment_frag1;
    }

    private void water() {
        waveLoadingView1.setProgressValue(0);
        waveLoadingView2.setProgressValue(0);
        waveLoadingView3.setProgressValue(0);
        waveLoadingView4.setProgressValue(0);
        waveLoadingView5.setProgressValue(0);
        waveLoadingView6.setProgressValue(0);
        waveLoadingView7.setProgressValue(0);
        waveLoadingView8.setProgressValue(0);
        waveLoadingView9.setProgressValue(0);
        waveLoadingView10.setProgressValue(0);

        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                water+=1;
                cc+=200;
                ccc();
                tv_water.setText(String.valueOf(water));
                tv_cc.setText(cc+" cc");
            }
        });
        tv_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(water==0){
                    water = 0;
                    cc = 0;
                    ccc();
                    tv_water.setText(String.valueOf(water));
                    tv_cc.setText(cc+" cc");

                }else{
                    water-=1;
                    cc-=200;
                    ccc();
                    tv_water.setText(String.valueOf(water));
                    tv_cc.setText(cc+" cc");

                }
            }
        });

    }

    private void ccc() {
        if(water==0){
            waveLoadingView1.setProgressValue(0);
            waveLoadingView2.setProgressValue(0);
            waveLoadingView3.setProgressValue(0);
            waveLoadingView4.setProgressValue(0);
            waveLoadingView5.setProgressValue(0);
            waveLoadingView6.setProgressValue(0);
            waveLoadingView7.setProgressValue(0);
            waveLoadingView8.setProgressValue(0);
            waveLoadingView9.setProgressValue(0);
            waveLoadingView10.setProgressValue(0);
            title_water.setText("不會說你今天沒喝水吧?");
        }else if(water==1){
            waveLoadingView1.setProgressValue(80);
            waveLoadingView2.setProgressValue(0);
            waveLoadingView3.setProgressValue(0);
            waveLoadingView4.setProgressValue(0);
            waveLoadingView5.setProgressValue(0);
            waveLoadingView6.setProgressValue(0);
            waveLoadingView7.setProgressValue(0);
            waveLoadingView8.setProgressValue(0);
            waveLoadingView9.setProgressValue(0);
            waveLoadingView10.setProgressValue(0);
            title_water.setText("很好多喝點水");
        }else if(water==2){
            waveLoadingView1.setProgressValue(80);
            waveLoadingView2.setProgressValue(80);
            waveLoadingView3.setProgressValue(0);
            waveLoadingView4.setProgressValue(0);
            waveLoadingView5.setProgressValue(0);
            waveLoadingView6.setProgressValue(0);
            waveLoadingView7.setProgressValue(0);
            waveLoadingView8.setProgressValue(0);
            waveLoadingView9.setProgressValue(0);
            waveLoadingView10.setProgressValue(0);
        }else if(water==3){
            waveLoadingView1.setProgressValue(80);
            waveLoadingView2.setProgressValue(80);
            waveLoadingView3.setProgressValue(80);
            waveLoadingView4.setProgressValue(0);
            waveLoadingView5.setProgressValue(0);
            waveLoadingView6.setProgressValue(0);
            waveLoadingView7.setProgressValue(0);
            waveLoadingView8.setProgressValue(0);
            waveLoadingView9.setProgressValue(0);
            waveLoadingView10.setProgressValue(0);
        }else if(water==4){
            waveLoadingView1.setProgressValue(80);
            waveLoadingView2.setProgressValue(80);
            waveLoadingView3.setProgressValue(80);
            waveLoadingView4.setProgressValue(80);
            waveLoadingView5.setProgressValue(0);
            waveLoadingView6.setProgressValue(0);
            waveLoadingView7.setProgressValue(0);
            waveLoadingView8.setProgressValue(0);
            waveLoadingView9.setProgressValue(0);
            waveLoadingView10.setProgressValue(0);
        }else if(water==5){
            waveLoadingView1.setProgressValue(80);
            waveLoadingView2.setProgressValue(80);
            waveLoadingView3.setProgressValue(80);
            waveLoadingView4.setProgressValue(80);
            waveLoadingView5.setProgressValue(80);
            waveLoadingView6.setProgressValue(0);
            waveLoadingView7.setProgressValue(0);
            waveLoadingView8.setProgressValue(0);
            waveLoadingView9.setProgressValue(0);
            waveLoadingView10.setProgressValue(0);
        }else if(water==6){
            waveLoadingView1.setProgressValue(80);
            waveLoadingView2.setProgressValue(80);
            waveLoadingView3.setProgressValue(80);
            waveLoadingView4.setProgressValue(80);
            waveLoadingView5.setProgressValue(80);
            waveLoadingView6.setProgressValue(80);
            waveLoadingView7.setProgressValue(0);
            waveLoadingView8.setProgressValue(0);
            waveLoadingView9.setProgressValue(0);
            waveLoadingView10.setProgressValue(0);
        }else if(water==7){
            waveLoadingView1.setProgressValue(80);
            waveLoadingView2.setProgressValue(80);
            waveLoadingView3.setProgressValue(80);
            waveLoadingView4.setProgressValue(80);
            waveLoadingView5.setProgressValue(80);
            waveLoadingView6.setProgressValue(80);
            waveLoadingView7.setProgressValue(80);
            waveLoadingView8.setProgressValue(0);
            waveLoadingView9.setProgressValue(0);
            waveLoadingView10.setProgressValue(0);
        }else if(water==8){
            waveLoadingView1.setProgressValue(80);
            waveLoadingView2.setProgressValue(80);
            waveLoadingView3.setProgressValue(80);
            waveLoadingView4.setProgressValue(80);
            waveLoadingView5.setProgressValue(80);
            waveLoadingView6.setProgressValue(80);
            waveLoadingView7.setProgressValue(80);
            waveLoadingView8.setProgressValue(80);
            waveLoadingView9.setProgressValue(0);
            waveLoadingView10.setProgressValue(0);
        }else if(water== 9){
            waveLoadingView1.setProgressValue(80);
            waveLoadingView2.setProgressValue(80);
            waveLoadingView3.setProgressValue(80);
            waveLoadingView4.setProgressValue(80);
            waveLoadingView5.setProgressValue(80);
            waveLoadingView6.setProgressValue(80);
            waveLoadingView7.setProgressValue(80);
            waveLoadingView8.setProgressValue(80);
            waveLoadingView9.setProgressValue(80);
            waveLoadingView10.setProgressValue(0);
            title_water.setText("很好喝蠻多水的!");
        }else if(water== 10){
            waveLoadingView1.setProgressValue(80);
            waveLoadingView2.setProgressValue(80);
            waveLoadingView3.setProgressValue(80);
            waveLoadingView4.setProgressValue(80);
            waveLoadingView5.setProgressValue(80);
            waveLoadingView6.setProgressValue(80);
            waveLoadingView7.setProgressValue(80);
            waveLoadingView8.setProgressValue(80);
            waveLoadingView9.setProgressValue(80);
            waveLoadingView10.setProgressValue(80);
            title_water.setText("很好喝夠多水了!");
        }else{
            waveLoadingView1.setProgressValue(80);
            waveLoadingView2.setProgressValue(80);
            waveLoadingView3.setProgressValue(80);
            waveLoadingView4.setProgressValue(80);
            waveLoadingView5.setProgressValue(80);
            waveLoadingView6.setProgressValue(80);
            waveLoadingView7.setProgressValue(80);
            waveLoadingView8.setProgressValue(80);
            waveLoadingView9.setProgressValue(80);
            waveLoadingView10.setProgressValue(80);
            title_water.setText("不好啦!喝太多會中毒啦!");
        }
    }

    private void drawing() {

        Date date=new Date();//取時間
        long now_time =  date.getTime();//這樣得到的差值是微秒級別
        for(int i =0; i<7;i++){
            if(now_time>=start_time.get(i)&&now_time<end_time.get(i)&& off_day.get(i)==1){
                seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
                        .setRange(start_time.get(i), end_time.get(i), now_time)
                        .build();
                series1Index = decoView.addSeries(seriesItem);
                status.setText("目前為斷食時間");

                break;
            }else{
                status.setText("進食時間喔!");
            }
        }
    }

    private void init(View v) {
        textPercentage = (TextView) v.findViewById(R.id.textPercentage);
        decoView = (DecoView) v.findViewById(R.id.dynamicArcView);
        status = (TextView) v.findViewById(R.id.status);
        start = (TextView) v.findViewById(R.id.start);
        horizontalStepView = (HorizontalStepView)v.findViewById(R.id.horizontalStepView);
        plan1 = (LinearLayout)v.findViewById(R.id.plan1);
        plan2 = (LinearLayout)v.findViewById(R.id.plan2);
        plan3 = (LinearLayout)v.findViewById(R.id.plan3);
        plan4 = (LinearLayout)v.findViewById(R.id.plan4);
        end_fasting = (Button)v.findViewById(R.id.end_fasting);
        fastingplan = (TextView)v.findViewById(R.id.fastingplan);
        waveLoadingView1 = (WaveLoadingView)v.findViewById(R.id.waveLoadingView1);
        waveLoadingView2 = (WaveLoadingView)v.findViewById(R.id.waveLoadingView2);
        waveLoadingView3 = (WaveLoadingView)v.findViewById(R.id.waveLoadingView3);
        waveLoadingView4 = (WaveLoadingView)v.findViewById(R.id.waveLoadingView4);
        waveLoadingView5 = (WaveLoadingView)v.findViewById(R.id.waveLoadingView5);
        waveLoadingView6 = (WaveLoadingView)v.findViewById(R.id.waveLoadingView6);
        waveLoadingView7 = (WaveLoadingView)v.findViewById(R.id.waveLoadingView7);
        waveLoadingView8 = (WaveLoadingView)v.findViewById(R.id.waveLoadingView8);
        waveLoadingView9 = (WaveLoadingView)v.findViewById(R.id.waveLoadingView9);
        waveLoadingView10 = (WaveLoadingView)v.findViewById(R.id.waveLoadingView10);
        tv_add = (TextView)v.findViewById(R.id.tv_add);
        tv_drop = (TextView)v.findViewById(R.id.tv_drop);
        tv_water = (TextView)v.findViewById(R.id.tv_water);
        tv_cc = (TextView)v.findViewById(R.id.tv_cc);
        title_water = (TextView)v.findViewById(R.id.title_water);

    }
    private void init2(View v) {
        plan1 = (LinearLayout)v.findViewById(R.id.plan1);
        plan2 = (LinearLayout)v.findViewById(R.id.plan2);
        plan3 = (LinearLayout)v.findViewById(R.id.plan3);
        plan4 = (LinearLayout)v.findViewById(R.id.plan4);

    }

    private final Runnable updateTimer = new Runnable() {
        public void run() {
            handler.postDelayed(this, 0);  //**

            Date date=new Date();
            long now_time =  date.getTime();

            for(int i =0; i<7;i++){

                if(now_time<start_time.get(0)){
                    status.setText("進食時間喔!");
                    SimpleDateFormat sdf= new SimpleDateFormat("HH:mm:ss");
                    java.util.Date date1 = new Date(start_time.get(0) - now_time - 8*60*60*1000);
                    java.util.Date start_date = new Date(start_time.get(0));
                    String str = sdf.format(date1);
                    String str2 = sdf.format(start_date);
                    textPercentage.setText(str);
                    start.setText("在"+str2+"開始斷食");
                }else if(now_time<start_time.get(i) && now_time>=end_time.get(i-1)){
                    status.setText("進食時間喔!");
                    SimpleDateFormat sdf= new SimpleDateFormat("HH:mm:ss");
                    java.util.Date date1 = new Date(start_time.get(i) - now_time - 8*60*60*1000);
                    java.util.Date start_date = new Date(start_time.get(i));
                    String str = sdf.format(date1);
                    String str2 = sdf.format(start_date);
                    textPercentage.setText(str);
                    start.setText("在"+str2+"開始斷食");
                }

                if(now_time>=start_time.get(i)&&now_time<=(start_time.get(i)+1000)&& off_day.get(i)==1){
                    drawing();
                }

                if(now_time>start_time.get(i)&&now_time<end_time.get(i)&& off_day.get(i)==1){
                    decoView.addEvent(new DecoEvent.Builder(now_time)
                    .setIndex(series1Index)
                    .build());
                    String percent = String.format("%.0f%%",(((float)now_time-(float)start_time.get(i))/((float)end_time.get(i)-(float)start_time.get(i)))* 100f);
                    textPercentage.setText(percent);

                    SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");
                    java.util.Date start_date = new Date(end_time.get(i));
                    String str2 = sdf.format(start_date);
                    start.setText("在"+str2+"斷食結束");

                    break;
                }

            }
        }
    };
}