package info.androidhive.firebaseauthapp.first;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.SQLite.BodyRecord;
import info.androidhive.firebaseauthapp.SQLite.PersonalInformation;
import info.androidhive.firebaseauthapp.fasting.FirstFasting;

public class HelloUser extends AppCompatActivity {


    private Button submit_workingAge,btn_cancel,submit_workingAge2,btn_cancel2;
    private PopupWindow popupWindow;
    private NumberPicker numberPicker,numberPicker2;
    private View workingAge_view,workingAge_view2,workingAge_view3;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid;
    String[] gender = {"男性","女性"};
    //0 = 久坐, 1=輕量活動 , 2=中度活動量, 3=高度活動量,4 = 非常高度活動量
    private int exercise_level;
    String[] exercise = {"久坐","輕量活動","中度活動量","高度活動量","非常高度活動量"};
    String[] BMI = {"","過輕","目前","過重",""};
    String[] fat = {"","目前","肥胖"};
    private TextView tv_gender,tv_height,tv_width,tv_age,tv_waistline,tv_exercise,tv_fat;
    PersonalInformation myDb;
    BodyRecord myDb2;
    private String gender_data;

    private Integer age_data;
    private float height_data,width_data,waistline_data,fat_data;
    private float profit;
    private BarChart chart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_user);

        myDb = new PersonalInformation(this);
        myDb2 = new BodyRecord(this);
        LinearLayout rl_gender = findViewById(R.id.rl_gender);
        LinearLayout rl_years = findViewById(R.id.rl_years);
        tv_gender = findViewById(R.id.tv_gender);
        tv_age = findViewById(R.id.tv_age);
        tv_height = findViewById(R.id.tv_height);
        tv_width = findViewById(R.id.tv_width);
        tv_waistline = findViewById(R.id.tv_waistline);
        tv_exercise = findViewById(R.id.tv_exercise);
        tv_fat = findViewById(R.id.tv_fat);
        Button btn_ok = findViewById(R.id.btn_ok);
        if (user != null) {
            uid = user.getUid();
        }
        initNumberPicker();
        initNumberPicker2();



        rl_gender.setOnClickListener(view -> {
            // 设置初始值
            numberPicker.setValue(0);

            // 强制隐藏键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            // 填充布局并设置弹出窗体的宽,高
            popupWindow = new PopupWindow(workingAge_view,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // 设置弹出窗体可点击
            popupWindow.setFocusable(true);
            // 设置弹出窗体动画效果
            popupWindow.setAnimationStyle(R.style.AnimBottom);
            // 触屏位置如果在选择框外面则销毁弹出框
            popupWindow.setOutsideTouchable(true);
            // 设置弹出窗体的背景
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.showAtLocation(workingAge_view,
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

            // 设置背景透明度
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.5f;
            getWindow().setAttributes(lp);

            // 添加窗口关闭事件
            popupWindow.setOnDismissListener(() -> {
                WindowManager.LayoutParams lp1 = getWindow().getAttributes();
                lp1.alpha = 1f;
                getWindow().setAttributes(lp1);
            });
        });

        // 确定服务年限
        submit_workingAge.setOnClickListener(v -> {
            tv_gender.setText(gender[numberPicker.getValue()]);
            popupWindow.dismiss();
        });
        submit_workingAge2.setOnClickListener(v -> {
            tv_exercise.setText(exercise[numberPicker2.getValue()]);
            popupWindow.dismiss();
        });
        //設定初始的顯示日期
        rl_years.setOnClickListener(v -> new DatePickerDialog(HelloUser.this, (view, year, monthofYear, dayOfMonth) -> {
            String strDate = year + "-" + monthofYear + "-" + dayOfMonth;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date birthDay = null;
            try {
                birthDay = sdf.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int birth = countAge(birthDay);
            if (birth<0) {
                Toast.makeText(getApplicationContext(), "生日輸入有誤", Toast.LENGTH_SHORT).show();
                tv_age.setText("");
            } else {
                tv_age.setText(String.valueOf(birth));
            }

        }, 2000, 0, 1).show());

        btn_cancel.setOnClickListener(view -> popupWindow.dismiss());
        btn_cancel2.setOnClickListener(view -> popupWindow.dismiss());
        btn_ok.setOnClickListener(view -> {
            if(tv_gender.getText().toString().equals("")){
                Toast.makeText(HelloUser.this, "請輸入「性別」", Toast.LENGTH_LONG).show();
            }else if(tv_age.getText().toString().equals("")){
                Toast.makeText(HelloUser.this, "請輸入「年齡」", Toast.LENGTH_LONG).show();
            }else if(tv_height.getText().toString().equals("")){
                Toast.makeText(HelloUser.this, "請輸入「身高」", Toast.LENGTH_LONG).show();
            }else if(tv_width.getText().toString().equals("")){
                Toast.makeText(HelloUser.this, "請輸入「體重」", Toast.LENGTH_LONG).show();
            }else if(tv_waistline.getText().toString().equals("")){
                Toast.makeText(HelloUser.this, "請輸入「腰圍」", Toast.LENGTH_LONG).show();
            }else if(tv_exercise.getText().toString().equals("")){
                Toast.makeText(HelloUser.this, "請輸入「每日活動量」", Toast.LENGTH_LONG).show();
            }else {
                gender_data = tv_gender.getText().toString();
                age_data = Integer.parseInt(tv_age.getText().toString());
                height_data = Float.parseFloat(tv_height.getText().toString());
                width_data = Float.parseFloat(tv_width.getText().toString());
                waistline_data = Float.parseFloat(tv_waistline.getText().toString());
                tv_fat.getText();
                if (!tv_fat.getText().toString().isEmpty() && !tv_fat.getText().toString().equals("null")) {
                    fat_data = Float.parseFloat(tv_fat.getText().toString());
                }

                exercise_level = numberPicker2.getValue();
                profit = width_data/((height_data/100)*(height_data/100));
                AddData();

                initNumberPicker3();

                // 强制隐藏键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // 填充布局并设置弹出窗体的宽,高
                popupWindow = new PopupWindow(workingAge_view3,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // 设置弹出窗体可点击
                popupWindow.setFocusable(true);
                // 设置弹出窗体动画效果
                popupWindow.setAnimationStyle(R.style.AnimBottom);
                // 触屏位置如果在选择框外面不銷毀
                popupWindow.setOutsideTouchable(true);
                // 设置弹出窗体的背景
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupWindow.showAtLocation(workingAge_view3,Gravity.CENTER, 0, 0);

                // 设置背景透明度
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.5f;
                getWindow().setAttributes(lp);

            }
        });

    }
    public  void AddData() {
        boolean isInserted;
        isInserted = myDb.insertData(uid,
                gender_data,
                age_data,
                height_data,
                width_data,
                waistline_data,
                fat_data,
                exercise_level);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間

        String str = formatter.format(curDate);
        myDb2.insertData(uid,width_data,height_data,waistline_data,fat_data,str,curDate.getTime());
        //Log.e("body data inserted :",isInserted2+"ID:"+uid+"weight:"+width_data+"height:"+height_data+"waist:"+waistline_data+"fat data:"+fat_data+"date:"+str);
        if(isInserted){
            Toast.makeText(HelloUser.this,"Data Inserted",Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(HelloUser.this, "Data not Inserted", Toast.LENGTH_LONG).show();
        }
    }
    //根據生日計算年齡
    private int countAge(Date birthDay) {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            } else {
                age--;
            }
        }
        return age;

    }
    public void height(View view) {
        LayoutInflater inflater = LayoutInflater.from(HelloUser.this);
        final View v = inflater.inflate(R.layout.show_yourkg, null);
        new AlertDialog.Builder(HelloUser.this)
                .setTitle("請輸入你的身高")
                .setView(v)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) (v.findViewById(R.id.editText1));
                        if(editText.getText().toString().matches("")) {
                            tv_height.setText("");
                        }
                        //身高必須>56 或 <300
                        else if (Float.parseFloat(editText.getText().toString().trim())<56 || Float.parseFloat(editText.getText().toString().trim())>300){
                            Toast.makeText(HelloUser.this, "輸入值為無效的數字", Toast.LENGTH_SHORT).show();
                            tv_height.setText("");
                        }
                        else{
                            tv_height.setText(editText.getText().toString());
                        }
                    }
                })
                .show();
    }
    public void waistline(View view) {
        LayoutInflater inflater = LayoutInflater.from(HelloUser.this);
        final View v = inflater.inflate(R.layout.show_yourkg, null);
        new AlertDialog.Builder(HelloUser.this)
                .setTitle("請輸入你的腰圍")
                .setView(v)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) (v.findViewById(R.id.editText1));
                        if(editText.getText().toString().matches("")) {
                            Toast.makeText(HelloUser.this, "輸入不得為0", Toast.LENGTH_SHORT).show();
                            tv_waistline.setText("");
                        }
                        //腰圍必須>34.7 或 <310
                        else if (Float.parseFloat(editText.getText().toString().trim())<34.7 || Float.parseFloat(editText.getText().toString().trim())>310){
                            Toast.makeText(HelloUser.this, "輸入值為無效的數字", Toast.LENGTH_SHORT).show();
                            tv_waistline.setText("");
                        }
                        else{
                            tv_waistline.setText(editText.getText().toString());
                        }
                    }
                })
                .show();
    }
    public void fat(View view) {
        LayoutInflater inflater = LayoutInflater.from(HelloUser.this);
        final View v = inflater.inflate(R.layout.show_yourkg, null);
        new AlertDialog.Builder(HelloUser.this)
                .setTitle("請輸入你的體脂率")
                .setView(v)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) (v.findViewById(R.id.editText1));
                        if(editText.getText().toString().matches("")) {
                            tv_fat.setText("");
                        }
                        //體脂必須>2 或 <74
                        else if (Float.parseFloat(editText.getText().toString().trim())<2 || Float.parseFloat(editText.getText().toString().trim())>74){
                            Toast.makeText(HelloUser.this, "輸入值為無效的數字", Toast.LENGTH_SHORT).show();
                            tv_fat.setText("");
                        }
                        else{
                            tv_fat.setText(editText.getText().toString());
                        }
                    }
                })
                .show();
    }
    public void exercise(final View view) {
        LayoutInflater inflater = LayoutInflater.from(HelloUser.this);
        final View v = inflater.inflate(R.layout.daily_activity_show, null);
        new AlertDialog.Builder(HelloUser.this)
                .setTitle("活動量表")
                .setView(v)
                .setPositiveButton("了解", (dialog, which) -> {
                    // 设置初始值
                    numberPicker2.setValue(0);

                    // 强制隐藏键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    // 填充布局并设置弹出窗体的宽,高
                    popupWindow = new PopupWindow(workingAge_view2,
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    // 设置弹出窗体可点击
                    popupWindow.setFocusable(true);
                    // 设置弹出窗体动画效果
                    popupWindow.setAnimationStyle(R.style.AnimBottom);
                    // 触屏位置如果在选择框外面则销毁弹出框
                    popupWindow.setOutsideTouchable(false);
                    // 设置弹出窗体的背景
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popupWindow.showAtLocation(workingAge_view2,
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                    // 设置背景透明度
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 0.5f;
                    getWindow().setAttributes(lp);

                    // 添加窗口关闭事件
                    popupWindow.setOnDismissListener(() -> {
                        WindowManager.LayoutParams lp1 = getWindow().getAttributes();
                        lp1.alpha = 1f;
                        getWindow().setAttributes(lp1);
                    });
                })
                .show();
    }
    public void width(View view) {
        LayoutInflater inflater = LayoutInflater.from(HelloUser.this);
        final View v = inflater.inflate(R.layout.show_yourkg, null);
        new AlertDialog.Builder(HelloUser.this)
                .setTitle("請輸入你的體重")
                .setView(v)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) (v.findViewById(R.id.editText1));
                        if(editText.getText().toString().matches("")) {
                            tv_width.setText("");
                        }
                        //體重必須>5.9 或 <635
                        else if (Float.parseFloat(editText.getText().toString().trim())<6 || Float.parseFloat(editText.getText().toString().trim())>635){
                            Toast.makeText(HelloUser.this, "輸入值為無效的數字", Toast.LENGTH_SHORT).show();
                            tv_width.setText("");
                        }
                        else{
                            tv_width.setText(editText.getText().toString());
                        }
                    }
                })
                .show();
    }

    /**
     * 初始化滚动框布局
     */
    @SuppressLint("InflateParams")
    private void initNumberPicker() {
        workingAge_view = LayoutInflater.from(this).inflate(R.layout.popupwindow, null);
        btn_cancel = workingAge_view.findViewById(R.id.btn_cancel);
        submit_workingAge = workingAge_view.findViewById(R.id.submit_workingAge);
        numberPicker = workingAge_view.findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(gender.length-1);
        numberPicker.setMinValue(0);
        numberPicker.setDisplayedValues(gender);
        numberPicker.setFocusable(false);
        numberPicker.setFocusableInTouchMode(false);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setNumberPickerDividerColor(numberPicker);
    }
    @SuppressLint("InflateParams")
    private void initNumberPicker2() {
        workingAge_view2 = LayoutInflater.from(this).inflate(R.layout.popupwindow2, null);
        btn_cancel2 = workingAge_view2.findViewById(R.id.btn_cancel2);
        submit_workingAge2 = workingAge_view2.findViewById(R.id.submit_workingAge2);
        numberPicker2 = workingAge_view2.findViewById(R.id.numberPicker2);
        numberPicker2.setMaxValue(exercise.length-1);
        numberPicker2.setMinValue(0);
        numberPicker2.setDisplayedValues(exercise);
        numberPicker2.setFocusable(false);
        numberPicker2.setFocusableInTouchMode(false);
        numberPicker2.setWrapSelectorWheel(false);
        numberPicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setNumberPickerDividerColor(numberPicker2);
    }
    @SuppressLint({"InflateParams", "SetTextI18n"})
    private void initNumberPicker3() {
        workingAge_view3 = LayoutInflater.from(this).inflate(R.layout.popupwindow3, null);
        TextView tv_bmi = workingAge_view3.findViewById(R.id.tv_bmi);
        TextView tv_bmr = workingAge_view3.findViewById(R.id.tv_bmr);
        TextView tv_fat = workingAge_view3.findViewById(R.id.tv_fat);
        TextView tv_what = workingAge_view3.findViewById(R.id.tv_what);
        TextView tv_okder = workingAge_view3.findViewById(R.id.tv_okder);
        Button bt_go = workingAge_view3.findViewById(R.id.bt_go);
        BarChart chart = workingAge_view3.findViewById(R.id.chart1);
        chart2 = workingAge_view3.findViewById(R.id.chart2);
        tv_bmi.setText("BMI:"+profit);
        if(gender_data.equals("男性")){
            float BMR_man= (float) ((10*width_data)+(6.25*height_data)-(5*age_data)+5);
            tv_bmr.setText("BMR(基礎代謝率):"+BMR_man);
        }else{
            float BMR_woman=(float) ((10*width_data)+(6.25*height_data)-(5*age_data)-161);
            tv_bmr.setText("BMR(基礎代謝率):"+BMR_woman);
        }
        if(fat_data==0){
            tv_fat.setVisibility(View.GONE);
            chart2.setVisibility(View.GONE);
        }else{
            tv_fat.setText("體脂率:"+fat_data);
            fat_chart();
        }

        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        YAxis yAxis = chart.getAxisLeft();
        YAxis yAxis2 = chart.getAxisRight();
        xAxis.setLabelCount(4);
        yAxis.setEnabled(false);
        yAxis2.setEnabled(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter((value, axis) -> {
            if(value>0.0&&value<4){
                return BMI[(int) value];
            }else {
                return "" ;
            }
        });

        chart.getAxisLeft().setDrawGridLines(false);
        // add a nice and smooth animation
        chart.animateY(1500);

        chart.getLegend().setEnabled(false);
        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();
        ArrayList<BarEntry> values3 = new ArrayList<>();
        values1.add(new BarEntry(1,18.5f));
        values2.add(new BarEntry(2, profit));
        values3.add(new BarEntry(3, 24));

        BarDataSet set1, set2, set3;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) chart.getData().getDataSetByIndex(1);
            set3 = (BarDataSet) chart.getData().getDataSetByIndex(2);
            set1.setValues(values1);
            set2.setValues(values2);
            set3.setValues(values3);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values1, "Company A");
            set1.setColor(Color.rgb(104, 241, 175));
            set2 = new BarDataSet(values2, "Company B");
            set2.setColor(Color.rgb(164, 228, 251));
            set3 = new BarDataSet(values3, "Company C");
            set3.setColor(Color.rgb(242, 247, 158));

            BarData data = new BarData(set1, set2, set3);
            data.setValueFormatter(new LargeValueFormatter());
            chart.setData(data);
            chart.setFitBars(true);
        }

        chart.invalidate();
        if(profit>=24){
            tv_what.setText("體態評斷為:過重");
            tv_okder.setText("你這樣看起來有點胖喔!");
        }else if (profit<=18.5){
            tv_what.setText("體態評斷為:過輕");
            tv_okder.setText("太瘦了啦!吃多一點");
        }else{
            tv_what.setText("體態評斷為:正常");
            tv_okder.setText("你的身材很正常很棒喔!");
        }
        bt_go.setOnClickListener(view -> {
            startActivity(new Intent(HelloUser.this, FirstFasting.class));
            finish();
        });
    }
    private void fat_chart() {
        chart2.getDescription().setEnabled(false);
        chart2.setMaxVisibleValueCount(60);
        chart2.setPinchZoom(false);
        chart2.setDrawBarShadow(false);
        chart2.setDrawGridBackground(false);

        XAxis xAxis2 = chart2.getXAxis();
        YAxis yAxis2 = chart2.getAxisLeft();
        YAxis yAxis3 = chart2.getAxisRight();
        xAxis2.setLabelCount(2);
        yAxis2.setEnabled(false);
        yAxis3.setEnabled(false);
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setDrawGridLines(false);
        xAxis2.setValueFormatter((value, axis) -> {
            if(value>0.0){
                return fat[(int) value];
            }else {
                return "" ;
            }
        });

        chart2.getAxisLeft().setDrawGridLines(false);
        // add a nice and smooth animation
        chart2.animateY(1500);

        chart2.getLegend().setEnabled(false);
        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();
        values1.add(new BarEntry(1,fat_data));
        if(gender_data.equals("男性")){
            values2.add(new BarEntry(2,25));
        }else {
            values2.add(new BarEntry(2,30));
        }


        BarDataSet set1, set2;

        if (chart2.getData() != null &&
                chart2.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart2.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) chart2.getData().getDataSetByIndex(1);
            set1.setValues(values1);
            set2.setValues(values2);
            chart2.getData().notifyDataChanged();
            chart2.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values1, "Company A");
            set1.setColor(Color.rgb(104, 241, 175));
            set2 = new BarDataSet(values2, "Company B");
            set2.setColor(Color.rgb(164, 228, 251));

            BarData data = new BarData(set1, set2);
            data.setValueFormatter(new LargeValueFormatter());
            chart2.setData(data);
            chart2.setFitBars(true);
        }

        chart2.invalidate();

    }
    /**
     * 自定义滚动框分隔线颜色
     */
    private void setNumberPickerDividerColor(NumberPicker number) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(number, new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimaryDark)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    public void years(View view) {
    }

}
