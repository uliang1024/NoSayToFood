package info.androidhive.firebaseauthapp.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.SQLite.DatabaseHelper;
import info.androidhive.firebaseauthapp.SQLite.PersonalInformation;

public class OneFoodRecord extends AppCompatActivity {

    DatabaseHelper myDb;
    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<Float> amount = new ArrayList<>();
    private float x1,x2,x3,x4,x5,x6;
    private TextView today_date,tv_milk,tv_fruit,tv_vegetables,tv_meet,tv_grain,tv_oil;

    private BarChart chart;
    float barWidth=0.3f;
    float barSpace=0f;
    float groupSpace=0.4f;

    PersonalInformation myDb2;
    private int user_exercise_level,age;
    private String gender;
    private float height,width;
    private double good_milk,good_fruit,good_vegetables,good_meet,good_grain,good_oil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_food_record);

        Bundle bundle = getIntent().getExtras();
        String datee = bundle.getString("datee").substring(6,17);

        myDb = new DatabaseHelper(this);
        Cursor res = myDb.getAllData();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if (user != null) {
            uid = user.getUid();
        }
        while (res.moveToNext()) {
            if(uid.equals(res.getString(4))){
                if(datee.equals(res.getString(1))){
                    name.add(res.getString(2));
                    amount.add(res.getFloat(3));
                }
            }
        }
        for(int i=0;i<name.size();i++){
            if(name.get(i).equals("全榖雜糧類")){
                x1=x1+amount.get(i);
            }else if(name.get(i).equals("豆魚蛋肉類")){
                x2=x2+amount.get(i);
            }else if(name.get(i).equals("乳品類")){
                x3=x3+amount.get(i);
            }else if(name.get(i).equals("蔬菜類")){
                x4=x4+amount.get(i);
            }else if(name.get(i).equals("水果類")){
                x5=x5+amount.get(i);
            }else{
                x6=x6+amount.get(i);
            }
        }

        tv_grain = (TextView)findViewById(R.id.tv_grain);
        tv_meet = (TextView)findViewById(R.id.tv_meet);
        tv_milk = (TextView)findViewById(R.id.tv_milk);
        tv_vegetables = (TextView)findViewById(R.id.tv_vegetables);
        tv_fruit = (TextView)findViewById(R.id.tv_fruit);
        tv_oil = (TextView)findViewById(R.id.tv_oil);
        today_date = (TextView)findViewById(R.id.today_date);
        today_date.setText(datee);

        tv_grain.setText(String.valueOf(x1));
        tv_meet.setText(String.valueOf(x2));
        tv_milk.setText(String.valueOf(x3));
        tv_vegetables.setText(String.valueOf(x4));
        tv_fruit.setText(String.valueOf(x5));
        tv_oil.setText(String.valueOf(x6));

        myDb2 = new PersonalInformation(this);
        Cursor res2 = myDb2.getAllData();
        while (res2.moveToNext()) {
            if(uid.equals(res2.getString(0))){
                user_exercise_level = res2.getInt(7);
                gender = res2.getString(1);
                age = res2.getInt(2);
                height = res2.getFloat(3);
                width = res2.getFloat(4);
            }
        }

        float bmr;
        if(gender.equals("男性")){
            bmr = (float)((10*width)+(6.25*height)-(5*age)+5);
        }else{
            bmr = (float)((10*width)+(6.25*height)-(5*age)-161);
        }
        float tdee;
        if(user_exercise_level == 0){
            tdee = (float)(1.2* bmr);
        }else if(user_exercise_level == 1){
            tdee = (float)(1.375* bmr);
        }else if(user_exercise_level == 2){
            tdee = (float)(1.55* bmr);
        }else if(user_exercise_level == 3){
            tdee = (float)(1.725* bmr);
        }else{
            tdee = (float)(1.9* bmr);
        }

        if(tdee >=2700){
            good_grain = 4;
            good_meet = 8;
            good_milk = 2;
            good_vegetables= 5;
            good_fruit=4;
            good_oil=7;
        }else if(tdee >=2500 && tdee <2700){
            good_grain = 4;
            good_meet = 7;
            good_milk = 1.5;
            good_vegetables= 5 ;
            good_fruit=4;
            good_oil=6;
        }else if(tdee>=2200  && tdee<2500 ){
            good_grain = 3.5;
            good_meet = 6;
            good_milk = 1.5;
            good_vegetables= 4 ;
            good_fruit=3.5;
            good_oil=5;
        }else if(tdee>=2000  && tdee <2200){
            good_grain = 3;
            good_meet = 6;
            good_milk = 1.5;
            good_vegetables= 4 ;
            good_fruit=3;
            good_oil=5;
        }else if(tdee>=1800  && tdee<2000 ){
            good_grain = 3;
            good_meet = 5;
            good_milk = 1.5;
            good_vegetables= 3;
            good_fruit=2 ;
            good_oil=4;
        }else if(tdee >=1500&& tdee <1800 ){
            good_grain = 2.5;
            good_meet = 4;
            good_milk = 1.5;
            good_vegetables= 3;
            good_fruit=2 ;
            good_oil=3;
        }else if(tdee <1500){
            good_grain = 1.5;
            good_meet = 3;
            good_milk = 1.5;
            good_vegetables= 3;
            good_fruit=2 ;
            good_oil=3;
        }

        chart = (BarChart)findViewById(R.id.barChart);
        chart.setDescription(null);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        int groupCount = 6;

        ArrayList xVals = new ArrayList();

        xVals.add("全榖雜糧");
        xVals.add("豆魚蛋肉");
        xVals.add("乳品");
        xVals.add("蔬菜");
        xVals.add("水果");
        xVals.add("油脂堅果種子");

        ArrayList yVals1 = new ArrayList();
        ArrayList yVals2 = new ArrayList();

        yVals1.add(new BarEntry(1, (float) x1));
        yVals2.add(new BarEntry(1, (float) good_grain));
        yVals1.add(new BarEntry(2, (float) x2));
        yVals2.add(new BarEntry(2, (float) good_meet));
        yVals1.add(new BarEntry(3, (float) x3));
        yVals2.add(new BarEntry(3, (float) good_milk));
        yVals1.add(new BarEntry(4, (float) x4));
        yVals2.add(new BarEntry(4, (float) good_vegetables));
        yVals1.add(new BarEntry(5, (float) x5));
        yVals2.add(new BarEntry(5, (float) good_fruit));
        yVals1.add(new BarEntry(6, (float) x6));
        yVals2.add(new BarEntry(6, (float) good_oil));

        BarDataSet set1, set2;
        set1 = new BarDataSet(yVals1, "當日");
        set1.setColor(Color.RED);
        set2 = new BarDataSet(yVals2, "標準");
        set2.setColor(Color.BLUE);
        BarData data = new BarData(set1, set2);
        data.setValueFormatter(new LargeValueFormatter());
        chart.setData(data);
        chart.getBarData().setBarWidth(barWidth);
        chart.getXAxis().setAxisMinimum(0);
        chart.getXAxis().setAxisMaximum(0 + chart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        chart.groupBars(0, groupSpace, barSpace);
        chart.getData().setHighlightEnabled(false);
        chart.invalidate();

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setYOffset(20f);
        l.setXOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        //X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(6);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
//Y-axis
        chart.getAxisRight().setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            startActivity(new Intent(OneFoodRecord.this,Food_Record.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}