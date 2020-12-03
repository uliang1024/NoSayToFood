package info.androidhive.firebaseauthapp.fasting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import info.androidhive.firebaseauthapp.HomeActivity;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.SQLite.FastingPlan;

public class FastingPlan4 extends AppCompatActivity implements View.OnClickListener {

    private TextView time1, time2, time3, time4, time5,time6,time7;
    private LinearLayout ll_day1, ll_day2, ll_day3, ll_day4, ll_day5, ll_day6, ll_day7;
    private String[] time_start = new String[7];
    private String[] time_end = new String[7];
    private long[] date_start = new long[7];
    private long[] date_end = new long[7];
    private PopupWindow popupWindow;
    private View workingAge_view4;
    private Integer hour=19, minute=30;
    private Button start_fasting;
    TimePicker picker;
    FastingPlan myDb;
    private ShowcaseView showcaseView;
    private int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fasting_plan4);
        View cv = getWindow().getDecorView();
        init(cv);
        myDb = new FastingPlan(this);
        //預設時間
        for(int i =0; i<7 ;i++){
            Date date=new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.DATE,i);
            calendar.set(Calendar.HOUR_OF_DAY, 19);
            calendar.set(Calendar.MINUTE, 30);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            date=calendar.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("EEHH:mm");
            time_start[i] = formatter.format(date);
            long diff1 = date.getTime();
            date_start[i]=diff1;

            Date date2=new Date();
            Calendar calendar2 = new GregorianCalendar();
            calendar2.setTime(date2);
            calendar2.add(Calendar.DATE,i);
            calendar2.set(Calendar.HOUR_OF_DAY, 19);
            calendar2.set(Calendar.MINUTE, 30);
            calendar2.set(Calendar.SECOND,0);
            calendar2.set(Calendar.MILLISECOND,0);
            calendar2.add(Calendar.HOUR_OF_DAY,18);
            date2 =calendar2.getTime();
            SimpleDateFormat formatter2 = new SimpleDateFormat("EEHH:mm");
            time_end[i] = formatter2.format(date2);
            long diff2 = date2.getTime();
            date_end[i]=diff2;
        }


        time1.setText(time_start[0]+"~"+time_end[0]);
        time2.setText(time_start[1]+"~"+time_end[1]);
        time3.setText(time_start[2]+"~"+time_end[2]);
        time4.setText(time_start[3]+"~"+time_end[3]);
        time5.setText(time_start[4]+"~"+time_end[4]);
        time6.setText(time_start[5]+"~"+time_end[5]);
        time7.setText(time_start[6]+"~"+time_end[6]);


        ll_day1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initNumberPicker4(0);
                showpopupWindow(view);
            }
        });
        ll_day2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initNumberPicker4(1);
                showpopupWindow(view);
            }
        });
        ll_day3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initNumberPicker4(2);
                showpopupWindow(view);
            }
        });
        ll_day4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initNumberPicker4(3);
                showpopupWindow(view);
            }
        });
        ll_day5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initNumberPicker4(4);
                showpopupWindow(view);
            }
        });
        ll_day6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initNumberPicker4(5);
                showpopupWindow(view);
            }
        });
        ll_day7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initNumberPicker4(6);
                showpopupWindow(view);
            }
        });

        start_fasting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mDatabase = database. getReference ();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Date date=new Date();//取時間
                long nowtime = date.getTime();
                String uid = null;
                if (user != null) {
                    uid = user.getUid();
                }
                Cursor res = myDb.getAllData();
                while (res.moveToNext()) {
                    if(uid.equals(res.getString(4))){
                        myDb.deleteData(uid);
                    }
                }
                for(int i =0; i<7 ;i++){

                    boolean isInserted = myDb.insertData(date_start[i],
                            date_end[i], 1,uid,nowtime,(i+1));
                    if(isInserted) {
                        Toast.makeText(FastingPlan4.this, i + "Data Inserted", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(FastingPlan4.this, i + "Data not Inserted", Toast.LENGTH_LONG).show();
                    }
                }

                startActivity(new Intent(FastingPlan4.this, HomeActivity.class));
                finish();
            }
        });
    }
    private void initNumberPicker4(final int i) {
        workingAge_view4 = LayoutInflater.from(this).inflate(R.layout.popupwindow4, null);
        picker=(TimePicker) workingAge_view4.findViewById(R.id.new_act_time_picker);
        Button showtime = (Button) workingAge_view4.findViewById(R.id.showtime);
        TextView breakfast = (TextView)workingAge_view4.findViewById(R.id.breakfast);
        TextView dinner = (TextView)workingAge_view4.findViewById(R.id.dinner);
        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    picker.setHour(17);
                    picker.setMinute(30);
                }
            }
        });
        dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    picker.setHour(14);
                    picker.setMinute(0);
                }
            }
        });
        picker.setIs24HourView(true);
        for(int x= 0 ;x<7;x++){
            String sb = time_start[i];
            String hour = sb.substring(2, 4);;
            String minute = sb.substring(5, 7);;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                picker.setHour(Integer.parseInt(hour));
                picker.setMinute(Integer.parseInt(minute));
            }
        }

        //設定時間
        showtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23 ){
                    hour = picker.getHour();
                    minute = picker.getMinute();
                }
                else{
                    hour = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                }
                Date date=new Date();//取時間
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                calendar.add(calendar.DATE,i);//把日期往前減少一天，若想把日期向後推一天則將負數改為正數
                calendar.set(Calendar.HOUR_OF_DAY, hour); //將hour改成開始的時間
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
                date=calendar.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("EEHH:mm");
                long diff1 = date.getTime();

                Date date2=new Date();//取時間
                Calendar calendar2 = new GregorianCalendar();
                calendar2.setTime(date2);
                calendar2.add(Calendar.DATE,i);//把日期往前減少一天，若想把日期向後推一天則將負數改為正數
                calendar2.set(Calendar.HOUR_OF_DAY, hour); //將hour改成開始的時間
                calendar2.set(Calendar.MINUTE, minute);
                calendar2.set(Calendar.SECOND,0);
                calendar2.set(Calendar.MILLISECOND,0);
                calendar2.add(Calendar.HOUR_OF_DAY,18);//把日期往前減少一天，若想把日期向後推一天則將負數改為正數
                date2 =calendar2.getTime();
                SimpleDateFormat formatter2 = new SimpleDateFormat("EEHH:mm");
                long diff2 = date2.getTime();
                String end = formatter2.format(date2);
                String start = formatter.format(date);

                try {
                    Date endTime=formatter.parse(end);
                    Date beginTime=formatter.parse(start);
                    if(i!=6){
                        Date beginTime1=formatter.parse(time_start[i+1]);
                        if(endTime.getTime()+30*60*1000>beginTime1.getTime()){
                            Toast.makeText(FastingPlan4.this, "離下一天的斷食時間太近了!請重新選擇時間", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    if(i!=0){
                        Date endTime1=formatter.parse(time_end[i-1]);
                        if(endTime1.getTime()+30*60*1000>beginTime.getTime()){
                            Toast.makeText(FastingPlan4.this, "離上一天的斷食時間太近了!請重新選擇時間", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    time_start[i] = formatter.format(date);;
                    time_end[i] = formatter2.format(date2);
                    date_start[i] = diff1;
                    date_end[i] = diff2;



                    if(i==0){
                        time1.setText(time_start[i]+"~"+time_end[i]);
                    }else if(i==1){
                        time2.setText(time_start[i]+"~"+time_end[i]);
                    }else if(i==2){
                        time3.setText(time_start[i]+"~"+time_end[i]);
                    }else if(i==3){
                        time4.setText(time_start[i]+"~"+time_end[i]);
                    }else if(i==4){
                        time5.setText(time_start[i]+"~"+time_end[i]);
                    }else if(i==5){
                        time6.setText(time_start[i]+"~"+time_end[i]);
                    }else{
                        time7.setText(time_start[i]+"~"+time_end[i]);
                    }
                    popupWindow.dismiss();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        });
    }
    @Override
    public void onClick(View v) {
        switch (counter) {
            case 0:
                showcaseView.setTarget(Target.NONE);
                showcaseView.setContentTitle("設定注意");
                showcaseView.setContentText("當您點擊斷食時間設定時，就無法修改段時天數。");
                showcaseView.setButtonText(getString(R.string.close));
                break;

            case 1:
                showcaseView.hide();

                ll_day1.setEnabled(true);
                ll_day2.setEnabled(true);
                ll_day3.setEnabled(true);
                ll_day4.setEnabled(true);
                ll_day5.setEnabled(true);
                ll_day6.setEnabled(true);
                ll_day7.setEnabled(true);

                break;
        }
        counter++;
    }
    @Override
    protected void onResume() {
        super.onResume();
        String tutorialKey = "SOME_KEY";
        Boolean firstTime = getPreferences(MODE_PRIVATE).getBoolean(tutorialKey, true);
        if (firstTime) {
            showcaseView = new ShowcaseView.Builder(this,true)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme3)
                    .setTarget(new ViewTarget(findViewById(R.id.ll_day1)))
                    .setContentTitle("斷食時間設定")
                    .setContentText("斷食天數中，您可以設定每一天斷食時段。")
                    .setOnClickListener(this)
                    .build();
            showcaseView.setButtonText(getString(R.string.next));

            ll_day1.setEnabled(false);
            ll_day2.setEnabled(false);
            ll_day3.setEnabled(false);
            ll_day4.setEnabled(false);
            ll_day5.setEnabled(false);
            ll_day6.setEnabled(false);
            ll_day7.setEnabled(false);
            getPreferences(MODE_PRIVATE).edit().putBoolean(tutorialKey, false).apply();
        }
    }
    private void showpopupWindow(View view) {
        // 强制隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        // 填充布局并设置弹出窗体的宽,高
        popupWindow = new PopupWindow(workingAge_view4,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置弹出窗体可点击
        popupWindow.setFocusable(true);
        // 设置弹出窗体动画效果
        popupWindow.setAnimationStyle(R.style.AnimBottom);
        // 触屏位置如果在选择框外面不銷毀
        popupWindow.setOutsideTouchable(false);
        // 设置弹出窗体的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.showAtLocation(workingAge_view4, Gravity.CENTER, 0, 0);

        // 设置背景透明度
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        // 添加窗口关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }

        });
    }
    private void init(View v) {
        time1 = (TextView) v.findViewById(R.id.time1);
        time2 = (TextView) v.findViewById(R.id.time2);
        time3 = (TextView) v.findViewById(R.id.time3);
        time4 = (TextView) v.findViewById(R.id.time4);
        time5 = (TextView) v.findViewById(R.id.time5);
        time6 = (TextView) v.findViewById(R.id.time6);
        time7 = (TextView) v.findViewById(R.id.time7);

        ll_day1 = (LinearLayout) v.findViewById(R.id.ll_day1);
        ll_day2 = (LinearLayout) v.findViewById(R.id.ll_day2);
        ll_day3 = (LinearLayout) v.findViewById(R.id.ll_day3);
        ll_day4 = (LinearLayout) v.findViewById(R.id.ll_day4);
        ll_day5 = (LinearLayout) v.findViewById(R.id.ll_day5);
        ll_day6 = (LinearLayout) v.findViewById(R.id.ll_day6);
        ll_day7 = (LinearLayout) v.findViewById(R.id.ll_day7);

        start_fasting= (Button)findViewById(R.id.start_fasting);
    }


}
