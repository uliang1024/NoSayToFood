package info.androidhive.firebaseauthapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.EdgeDetail;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.charts.SeriesLabel;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import info.androidhive.firebaseauthapp.FastRecordsActivity;
import info.androidhive.firebaseauthapp.FitnessActivity;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.RecordThis;
import info.androidhive.firebaseauthapp.SQLite.FastingPlan;
import info.androidhive.firebaseauthapp.fasting.FastingPlan1;
import info.androidhive.firebaseauthapp.fasting.FastingPlan2;
import info.androidhive.firebaseauthapp.fasting.FastingPlan3;
import info.androidhive.firebaseauthapp.fasting.FastingPlan4;
import info.androidhive.firebaseauthapp.fasting.Fasting_Complete;
import info.androidhive.firebaseauthapp.fasting.FirstFasting;
import info.androidhive.firebaseauthapp.food.foodClassification;
import me.itangqi.waveloadingview.WaveLoadingView;

import static android.content.Context.MODE_PRIVATE;

public class Frag1 extends Fragment implements View.OnClickListener {

    private Frag1TimeListener listener;

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
    private Button btn_check_fasting;
    //儲存Date
    ArrayList<Date> start_date = new ArrayList<>();
    ArrayList<Date> end_date = new ArrayList<>();
    //日期的index
    int index ;

    private ImageView img_notification;


    private HorizontalStepView horizontalStepView;

    private LinearLayout plan1,plan2,plan3,plan4;

    private TextView fastingplan;
    private Button end_fasting;

    private View fragment_frag1;

    private WaveLoadingView waveLoadingView1,waveLoadingView2,waveLoadingView3,waveLoadingView4,waveLoadingView5,waveLoadingView6,waveLoadingView7,waveLoadingView8,waveLoadingView9,waveLoadingView10;
    private Integer water=0,cc=0;
    private TextView tv_water,tv_drop,tv_add,tv_cc,title_water,progress;
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String STATUS = "status";
    private SharedPreferences sharedPreferences;
    private Button bt_eat;

    private ShowcaseView showcaseView;
    private int counter = 0;

    @Override
    public void onClick(View v) {
        switch (counter) {
            case 0:
                showcaseView.setContentTitle("更改斷食計畫");
                showcaseView.setContentText("當您選錯時段時，可以修改計畫ㄛ!");
                showcaseView.setShowcase(new ViewTarget(fastingplan), true);
                break;

            case 1:
                showcaseView.hide();

                fastingplan.setEnabled(true);

                break;
        }
        counter++;
    }

    public interface Frag1TimeListener{
        void onTimeChanged(ArrayList<Long> start_time,ArrayList<Long> end_time,ArrayList<Integer> off_day);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        myDb = new FastingPlan(Frag1.super.getContext());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if (user != null) {
            uid = user.getUid();
        }
        //cursor在FastingPlan中撈資料
        Cursor res = myDb.getAllData();
        while (res.moveToNext()) {
            if(uid.equals(res.getString(4))){
                //取得開始時間並且加入start_time
                start_time.add(res.getLong(1));
                //取得結束時間並且加入end_time
                end_time.add(res.getLong(2));
                //取得是否維修息日並加入off_day(休息日為0，段時日為1)
                off_day.add(res.getInt(3));
            }
        }


        //加入日期
        addTime();
        if(start_time.size()!=0){
            //如果start_time有值
            //填充斷食進行中的介面
            fragment_frag1 = inflater.inflate(R.layout.fragment_frag1, container, false);
            //INIT VIEWS
            init(fragment_frag1);
            //將開始日期及結束日期傳給HomeActivity
            listener.onTimeChanged(start_time,end_time,off_day);

            // 創造背景條
            decoView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                    .setRange(0, 100, 100)
                    .setInitialVisibility(false)
                    .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                    .setLineWidth(50f)
                    .build());
            //背景條特效
            decoView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                    //.setDelay(1000)
                    .setDuration(1000)
                    .build());

            List<StepBean> sources =new ArrayList<>();
            Date date = new Date();
            int step_Index = 0;
            //找出現在進行到第幾天
            for (Date d:end_date){
                //尋找同日期
                if(compareSameDay(date,d)){
                    if (date.after(d)){
                        step_Index = end_date.indexOf(d)+1;
                        //Log.e("讀取:","休息時間:index="+index);
                    }else{
                        step_Index = end_date.indexOf(d);
                        //Log.e("讀取:","斷食時間:index ="+index);
                    }
                }
            }
            for(int i =0; i<7;i++){
                if(off_day.get(i)!=0){

                    if (i==step_Index){
                        //進行中的天數
                        Log.e("i",i+"進行中");
                        sources.add(new StepBean("第"+(i+1)+"天",0));
                    }else if (i<step_Index){
                        //已完成的天數
                        Log.e("i",i+"已完成");
                        sources.add(new StepBean("第"+(i+1)+"天",1));
                    }else {
                        //尚未完成的天數
                        Log.e("i",i+"未完成");
                        sources.add(new StepBean("第"+(i+1)+"天",-1));
                    }

                }
            }

            Log.e("start_time",start_time+"");
            Log.e("end_time",end_time+"");
            Log.e("off_day",off_day+"");
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

            handler.removeCallbacks(updateRunner);
            handler.postDelayed(updateRunner, 0);

            fastingplan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Frag1.super.getContext(), FirstFasting.class));
                }
            });

            Log.e("是否記錄此次斷食",""+isFastingRecord());
            //記錄此次斷食
            //只有在"段時與斷食之間的休息間格，及"前面一天是段時日"的休息日才會顯現button
            btn_check_fasting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //如果以記錄斷食
                    if (isFastingRecord()){
                        Toast.makeText(getContext(), "您已記錄本次斷食了噢", Toast.LENGTH_SHORT).show();
                        navigateToRecord(1);
                    }else{
                        //導至斷食完成的頁面，讓使ˇ用者記錄此次心得感想體重之類
                        saveFastingData(true);
                        navigateToRecord(0);
                    }

                }
            });

            bt_eat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Frag1.super.getContext(), foodClassification.class));
                }
            });

//            img_notification.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(getContext(),RecordThis.class));
//                }
//            });
            //結束斷食紐按下
            end_fasting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    final View v = inflater.inflate(R.layout.end_fasting_alert, null);
                    new AlertDialog.Builder(getContext())
                            .setView(v)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //導向到斷食完成的activity (Fasting_Complete)
                                    startActivity(new Intent(Frag1.super.getContext(), Fasting_Complete.class));

                                    Cursor res = myDb.getAllData();
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    String uid = null;
                                    if (user != null) {
                                        //取得使用者的id
                                        uid = user.getUid();
                                    }
                                    while (res.moveToNext()) {
                                        //刪除該使用者id的資料
                                        if(uid.equals(res.getString(4))){
                                            myDb.deleteData(uid);
                                        }
                                    }
                                }
                            })
                            .show();


                }
            });
        }else{
            //如果start_time無值
            //填充選擇斷食計畫的介面
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
                    startActivity(new Intent(Frag1.super.getContext(), FastingPlan2.class));
                }
            });
            plan3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Frag1.super.getContext(), FastingPlan3.class));
                }
            });
            plan4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Frag1.super.getContext(), FastingPlan4.class));
                }
            });
        }

        return fragment_frag1;
    }
    @Override
    public void onResume() {
        super.onResume();
        String tutorialKey = "SOME_KEY";
        Boolean firstTime = this.getActivity().getPreferences(MODE_PRIVATE).getBoolean(tutorialKey, true);
        if (firstTime) {
            showcaseView = new ShowcaseView.Builder(Frag1.super.getActivity(),true)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme3)
                    .setTarget(new ViewTarget(fragment_frag1.findViewById(R.id.dynamicArcView)))
                    .setContentTitle("進度條")
                    .setContentText("清楚了解目前段時狀況")
                    .setOnClickListener(this)
                    .build();
            showcaseView.setButtonText(getString(R.string.next));
            fastingplan.setEnabled(false);
            this.getActivity().getPreferences(MODE_PRIVATE).edit().putBoolean(tutorialKey, false).apply();
        }
    }
    private void navigateToRecord(int status) {

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        int target_day = 0;
        //如果現在的時間在結束時間之後
        if (date.after(end_date.get(end_date.size()-1))){
            Log.e("oops","something wrong");
            return;
        }

        for (int i = 0;i<=end_date.size();i++){
            if (df.format(end_date.get(i)).equals(df.format(date))){
                target_day= i;
                break;

            }
        }

        Log.e("target day","index"+target_day);
        Log.e("today day","today"+date);


        long startdate = (start_date.get(target_day).getTime());
        long enddate = (end_date.get(target_day).getTime());


        Intent intent = new Intent(getContext(),RecordThis.class);
        Bundle bundle = new Bundle();
        bundle.putLong("startdate",startdate);
        bundle.putLong("enddate",enddate);
        //0代表未紀錄，要記錄 1代表已記，要查看
        bundle.putInt("status",status);
        bundle.putInt("emoji",100);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    //只有在"段時與斷食之間的休息間格，及"前面一天是段時日"的休息日才會顯現button
    private void saveFastingData(boolean isRecorded) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STATUS,isRecorded);
        editor.apply();
        Log.e("紀錄狀態",""+isRecorded);
    }

    private boolean isFastingRecord(){
        boolean isRecorded = sharedPreferences.getBoolean(STATUS,false);
        Log.e("已記錄狀態",""+isRecorded);
        return isRecorded;
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

    private void init(View v) {
        sharedPreferences = v.getContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        bt_eat = v.findViewById(R.id.bt_eat);
        btn_check_fasting = v.findViewById(R.id.btn_check_fasting);
        progress = v.findViewById(R.id.tv_progress);
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
        //img_notification = v.findViewById(R.id.iv_bells);
    }
    private void init2(View v) {
        plan1 = (LinearLayout)v.findViewById(R.id.plan1);
        plan2 = (LinearLayout)v.findViewById(R.id.plan2);
        plan3 = (LinearLayout)v.findViewById(R.id.plan3);
        plan4 = (LinearLayout)v.findViewById(R.id.plan4);

    }



    private final Runnable updateRunner = new Runnable() {
        @Override
        public void run() {

            handler.postDelayed(this,1000);

            Date date=new Date();
            long now_time =  date.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //如果今天的日期超過最後一個end_date(使用者閒置過久)
            if (date.after(end_date.get(6))) index = 7;

            for (Date d:end_date){
                //尋找同日期
                if(compareSameDay(date,d)){
                    //Log.e("now date","現在日期:"+sdf2.format(date)+" 抓到的時間:"+sdf2.format(d));
                    //比較時間先後
                    //若是現在時間比抓到的時間(end_date)後面
                    if (date.after(d)){
                        index = end_date.indexOf(d)+1;
                        //Log.e("讀取:","休息時間:index="+index);
                    }else{
                        index = end_date.indexOf(d);
                        //Log.e("讀取:","斷食時間:index ="+index);
                    }
                }
            }


            Date showDate2 = new Date();
            Date showDate3 = new Date();
            //當還有下一筆資料時
            if (index<=end_time.size()-1){
                //當時間在第n個開始時間開始前
                Log.e("index",index+"");
                if (off_day.get(index)==1){
                    //如果現在時間小於開始時間
                    if (now_time<start_time.get(index)){

                        if (index==0){
                            //第一天斷食開始前的休息日
                            long mytime = (start_time.get(index)-now_time);
                            showDate2.setTime(start_time.get(index));
                            showDate3.setTime(now_time);
                            //Log.e("準備","開始時間 :"+sdf.format(showDate2)+" 現在時間 :"+sdf.format(showDate3));
                            //Log.e("目前進度",getPercent(now_time,true));
                            status.setText("距離斷時開始還有");
                            textPercentage.setText(getTimeLeft(mytime/1000));
                            start.setText("在"+sdf.format(start_date.get(index))+"開始斷食");
                            //listener.onTimeChanged("距離斷時開始還有"+getTimeLeft(mytime/1000));
                            btn_check_fasting.setVisibility(View.GONE);
                            bt_eat.setVisibility(View.GONE);
                            //在這進行progressbar
                        }else{
                            //其它天的休息日
                            long mytime = (start_time.get(index)-now_time);
                            showDate2.setTime(start_time.get(index));
                            showDate3.setTime(now_time);

                            //Log.e("準備","開始時間 :"+sdf.format(showDate2)+" 現在時間 :"+sdf.format(showDate3));
                            //Log.e("準備","開始時間 :"+start_time.get(index)+" 現在時間 :"+now_time+" 相差時間"+mytime);
                            //Log.e("目前進度",Float.parseFloat(getPercent(now_time,true))+"");
                            status.setText("距離斷時開始還有");
                            textPercentage.setText(getTimeLeft(mytime/1000));
                            start.setText("在"+sdf.format(start_date.get(index))+"開始斷食");
                            progress.setText("目前進度"+Math.round(Float.parseFloat(getPercent(now_time,true))*100)+"%");
                            //listener.onTimeChanged("距離斷時開始還有"+getTimeLeft(mytime/1000));
                            add_progress(Float.parseFloat(getPercent(now_time,true)));
                            btn_check_fasting.setVisibility(View.VISIBLE);
                            bt_eat.setVisibility(View.VISIBLE);

                            //在這進行progressbar
                        }
                    }
                    //時間介於第n個開始時間與第n個結束時間時
                    else if (now_time>=start_time.get(index)&&now_time<=end_time.get(index)){
                        //tv_percent.setText(""+getPercent(now_time,false));
                        //Log.e("斷食中","開始時間 : "+sdf.format(start_time.get(index))+" 剩餘時間 :"+(end_time.get(index)-now_time));
                        //Log.e("目前進度",Float.parseFloat(getPercent(now_time,false))+"");
                        status.setText("距離斷食結束還有");
                        textPercentage.setText(getTimeLeft((end_time.get(index)-now_time)/1000));
                        start.setText("在"+sdf.format(end_date.get(index))+"開始進食");
                        progress.setText("目前進度"+Math.round(Float.parseFloat(getPercent(now_time,false))*100)+"%");
                        //listener.onTimeChanged("距離斷食結束還有"+getTimeLeft((end_time.get(index)-now_time)/1000));
                        add_progress(Float.parseFloat(getPercent(now_time,false)));
                        //開啟一輪新的斷食要重置為為記錄此次斷食
                        saveFastingData(false);
                        btn_check_fasting.setVisibility(View.GONE);
                        bt_eat.setVisibility(View.GONE);
                    }
                }else
                  //off_day.get(index == 0)
                 {
                    ArrayList<Integer> off_days_indexs =new ArrayList<>();
                    //抓到 "斷食後的休息日" 在休息日陣列中的索引直
                    for (int i = 0;i<off_day.size();i++){
                        //1.要是休息日(0) 2.不能是第一筆(i-1>=0) 3.他的前一筆必須為段時日(off_day.get(i-1)==1)
                        if (off_day.get(i) == 0  && i-1>=0 && off_day.get(i-1)==1){
                            off_days_indexs .add(i);
                        }
                    }
                    //如果今天是"斷食後的休息日"，就開放它記錄段時
                    if (off_days_indexs.contains(index)){
                        status.setText("斷食結束");
                        textPercentage.setText("接下來維休息日");
                        start.setText("");
                        progress.setText("");
                        btn_check_fasting.setVisibility(View.VISIBLE);
                    }
                    //如果今天不是"斷食後的休息日"，就不開放它記錄段時
                    else{
                        status.setText("斷食結束");
                        textPercentage.setText("接下來維休息日");
                        start.setText("");
                        progress.setText("");
                        btn_check_fasting.setVisibility(View.GONE);
                    }

                }

            }else {
                //Log.e("index",index+"");
                status.setText("恭喜您完成此次斷食");
                textPercentage.setText("結束");
                start.setText("");
                progress.setText("");
                btn_check_fasting.setVisibility(View.INVISIBLE);
            }

        }
    };
    //添加數值在背景條
    public void add_progress(float progress){
        //設定數值樣式
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setInitialVisibility(false)
                .setLineWidth(60f)
                .addEdgeDetail(new EdgeDetail(EdgeDetail.EdgeType.EDGE_INNER, Color.parseColor("#22000000"), 0.4f))
                //.setSeriesLabel(new SeriesLabel.Builder("Percent %.0f%%").build())
                .setInterpolator(new BounceInterpolator())
                .setShowPointWhenEmpty(true)
                .setCapRounded(true)
                //.setInset(new PointF(32f, 32f))
                .setDrawAsPoint(false)
                .setSpinClockwise(true)
                .setSpinDuration(1000)
                .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                .build();

        series1Index = decoView.addSeries(seriesItem1);
        decoView.addEvent(new DecoEvent.Builder(Math.round((progress*100))).setIndex(series1Index).setDuration(1500).build());
    }

    //加入時間
    private void addTime() {
//        List<Long> startTime = Arrays.asList(1603567800000L, 1603654200000L, 1603740600000L, 1603827000000L, 1603913400000L, 1603971000000L, 1604057400000L);
//        List<Long> endTime = Arrays.asList(1603625400000L, 1603711800000L, 1603798200000L, 1603884600000L, 1603971000000L, 1604086200000L, 1604172600000L);
        for (long d :start_time){
            Date date = new Date();
            date.setTime(d);
            start_date.add(date);
        }
        for (long d :end_time){
            Date date = new Date();
            date.setTime(d);
            end_date.add(date);
        }
    }
    //獲取相差的時數
    public String getTimeLeft(long time){
        long hour = time/(60*60);
        long min = (time%(60*60))/(60);
        long sec = (time%(60*60))%(60);
        return String.format("%d小時 %d分 %d秒",hour,min,sec);
    }
    //比較兩日日期是否一致
    public boolean compareSameDay(Date current ,Date compare){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(current);
        cal2.setTime(compare);
        boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

        return sameDay;
    }
    //取得當前進時斷食進度的百分底
    public String getPercent(long now_time,boolean isRest){
        DecimalFormat df1 = new DecimalFormat("");
        //如果是休息時間
        if (isRest){
            long startTime = start_time.get(index);
            long endTime = end_time.get(index-1);
            long total = startTime-endTime;

            double restTimeLeft =  ((startTime-now_time)/(double)(total));
            //Log.e("percent",1-restTimeLeft+"");
            //休息時間是越來越少
            //所以得用1-x才能表現出目前休息進度
            String percent = String.format("%.2f", (1-restTimeLeft));

            return percent;
        }else {
            //如果是斷食時間
            long startTime = start_time.get(index);
            long endTime = end_time.get(index);
            long total = endTime-startTime;
            double fastTimeLeft =  (now_time-startTime)/(double)(total);
            //Log.e("percent",(now_time-startTime)+"，"+total);
            //Log.e("percent",fastTimeLeft+"");
            String percent =String.format("%.2f", fastTimeLeft);
            return percent;
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof  Frag1TimeListener){
            listener=(Frag1TimeListener)context;
        }else {
            throw new RuntimeException(context.toString()+"must implement Frag1TimeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
