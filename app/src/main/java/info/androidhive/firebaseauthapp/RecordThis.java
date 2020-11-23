package info.androidhive.firebaseauthapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.EdgeDetail;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import info.androidhive.firebaseauthapp.SQLite.BodyRecord;
import info.androidhive.firebaseauthapp.SQLite.FastRecord;
import info.androidhive.firebaseauthapp.SQLite.PersonalInformation;
import info.androidhive.firebaseauthapp.first.HelloUser;
import info.androidhive.firebaseauthapp.models.fastRecords;


public class RecordThis extends AppCompatActivity {
    private TextView tv_endtime,tv_time_span,fasting_time ;
    private ImageView img_show_weight,img_show_height,img_show_waist,img_show_fat;
    private TextView record_show_weight,record_show_height,record_show_waist,record_show_fat;
    private DecoView decoView;
    private TextView go_food_record;
    private Button btn_record;
    private LinearLayout lv_easy ,lv_soso,lv_hard;
    private TextView tv_easy,tv_soso,tv_hard;
    private ImageView img_easy,img_soso,img_hard;


    private int emoji = 0;
    private int series1Index;
    private float weight =0,height =0,waist =0,fat =0;
    PersonalInformation myDb;
    BodyRecord myDb2;
    FastRecord myDb3;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid ;
    //arrayList用來存從myDb2讀下來的資料
    private ArrayList<Float> KGs = new ArrayList<>();
    private ArrayList<Float> Heights = new ArrayList<>();
    private ArrayList<Float> Waists = new ArrayList<>();
    private ArrayList<Float> Body_fats = new ArrayList<>();
    private ArrayList<String> Dates = new ArrayList<>();
    private ArrayList<Integer> IDs = new ArrayList<>();
    //arrayList用來存從myDb3讀下來的資料
    private ArrayList<fastRecords> fast_records = new ArrayList<>();

    // TODO: 2020/11/20 到休息時間時記得把這改成讀入的時間
    long startdate = 0;
    long enddate = 0;
    int status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_this);
        //初始化
        init();

        //獲取 intent 和 bundle
        if (getIntent().getExtras()!= null){
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            startdate = bundle.getLong("startdate");
            enddate = bundle.getLong("enddate");
            status = bundle.getInt("status");
            emoji = bundle.getInt("emoji");
        }

        //將資料從身體數值表讀下來，用於身高體重等數值的顯示
        readData();
        DecimalFormat fdf = new DecimalFormat("###0.##");
        record_show_weight.setText(fdf.format(KGs.get(KGs.size()-1))+" kg");
        record_show_height.setText(fdf.format(Heights.get(Heights.size()-1))+" cm");
        record_show_waist.setText(fdf.format(Waists.get(Waists.size()-1))+" cm");
        record_show_fat.setText(fdf.format(Body_fats.get(Body_fats.size()-1))+" %");


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
        //設定數值樣式
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setInitialVisibility(false)
                .setLineWidth(60f)
                .addEdgeDetail(new EdgeDetail(EdgeDetail.EdgeType.EDGE_INNER, Color.parseColor("#22000000"), 0.4f))
                //.setSeriesLabel(new SeriesLabel.Builder("Percent %.0f%%").build())
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setShowPointWhenEmpty(true)
                .setCapRounded(true)
                //.setInset(new PointF(32f, 32f))
                .setDrawAsPoint(false)
                .setSpinClockwise(true)
                .setSpinDuration(1000)
                .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                .build();

        series1Index = decoView.addSeries(seriesItem1);
        decoView.addEvent(new DecoEvent.Builder(Math.round(100)).setIndex(series1Index).setDuration(1500).build());

        //如果傳入狀態是3(從斷食紀錄表來的)
        if (status==3){
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Drawable drawable_dark = ContextCompat.getDrawable(getApplicationContext(), R.drawable.food_button_rounde_dark);

            Date startDate = new Date();
            startDate.setTime(startdate);
            Date endDate = new Date();
            endDate.setTime(enddate);
            Log.e("emoji", ""+emoji);
            switch(emoji){
                case 0:
                    YoYo.with(Techniques.Pulse)
                            .duration(700)
                            .repeat(1)
                            .playOn(findViewById(R.id.img_easy));
                    tv_easy.setTextColor(Color.WHITE);
                    tv_soso.setTextColor(Color.GRAY);
                    tv_hard.setTextColor(Color.GRAY);
                    lv_easy.setBackground(drawable_dark);
                    lv_soso.setBackground(null);
                    lv_hard.setBackground(null);
                    break;
                case 1:
                    YoYo.with(Techniques.Pulse)
                            .duration(700)
                            .repeat(1)
                            .playOn(findViewById(R.id.img_soso));
                    tv_easy.setTextColor(Color.GRAY);
                    tv_soso.setTextColor(Color.WHITE);
                    tv_hard.setTextColor(Color.GRAY);
                    lv_easy.setBackground(null);
                    lv_soso.setBackground(drawable_dark);
                    lv_hard.setBackground(null);
                    break;
                case 2:
                    YoYo.with(Techniques.Pulse)
                            .duration(700)
                            .repeat(1)
                            .playOn(findViewById(R.id.img_hard));
                    tv_easy.setTextColor(Color.GRAY);
                    tv_soso.setTextColor(Color.GRAY);
                    tv_hard.setTextColor(Color.WHITE);
                    lv_easy.setBackground(null);
                    lv_soso.setBackground(null);
                    lv_hard.setBackground(drawable_dark);
                    break;
            }

            tv_time_span.setText(df.format(startDate)+" \n到 \n"+df.format(endDate));
            btn_record.setVisibility(View.GONE);
        }else {
            YoYo.with(Techniques.FlipInX)
                    .delay(500)
                    .duration(700)
                    .repeat(0)
                    .playOn(findViewById(R.id.img_easy));

            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            tv_endtime.setText(df.format(enddate));
            tv_time_span.setText(df.format(startdate)+" 到 "+df.format(enddate));

            img_show_weight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(RecordThis.this);
                    final View view = inflater.inflate(R.layout.show_yourkg, null);

                    new AlertDialog.Builder(RecordThis.this)
                            .setTitle("請輸入你的體重")
                            .setView(view)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText editText = (EditText) (view.findViewById(R.id.editText1));
                                    DecimalFormat fdf = new DecimalFormat("###0.##");

                                    if(editText.getText().toString().equals("")|| Float.parseFloat(editText.getText().toString())==0) {
                                        Toast.makeText(RecordThis.this, "你還沒輸入數值喔", Toast.LENGTH_SHORT).show();
                                    }else{
                                        float data = Float.parseFloat(editText.getText().toString());
                                        record_show_weight.setText(fdf.format(data)+" kg");
                                        weight = data;
                                    }
                                }
                            })
                            .show();
                }
            });
            img_show_height.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(RecordThis.this);
                    final View view = inflater.inflate(R.layout.show_yourkg, null);

                    new AlertDialog.Builder(RecordThis.this)
                            .setTitle("請輸入你的身高")
                            .setView(view)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText editText = (EditText) (view.findViewById(R.id.editText1));
                                    DecimalFormat fdf = new DecimalFormat("###0.##");
                                    if(editText.getText().toString().equals("")|| Float.parseFloat(editText.getText().toString())==0) {
                                        Toast.makeText(RecordThis.this, "你還沒輸入數值喔", Toast.LENGTH_SHORT).show();
                                    }else{
                                        float data = Float.parseFloat(editText.getText().toString());
                                        record_show_height.setText(fdf.format(data)+" cm");
                                        height = data;
                                    }
                                }
                            })
                            .show();
                }
            });
            img_show_waist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(RecordThis.this);
                    final View view = inflater.inflate(R.layout.show_yourkg, null);

                    new AlertDialog.Builder(RecordThis.this)
                            .setTitle("請輸入你的腰圍")
                            .setView(view)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText editText = (EditText) (view.findViewById(R.id.editText1));
                                    DecimalFormat fdf = new DecimalFormat("###0.##");
                                    if(editText.getText().toString().equals("")|| Float.parseFloat(editText.getText().toString())==0) {
                                        Toast.makeText(RecordThis.this, "你還沒輸入數值喔", Toast.LENGTH_SHORT).show();
                                    }else{
                                        float data = Float.parseFloat(editText.getText().toString());
                                        record_show_waist.setText(fdf.format(data)+" cm");
                                        waist = data;
                                    }
                                }
                            })
                            .show();
                }
            });
            img_show_fat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(RecordThis.this);
                    final View view = inflater.inflate(R.layout.show_yourkg, null);

                    new AlertDialog.Builder(RecordThis.this)
                            .setTitle("請輸入你的體脂")
                            .setView(view)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText editText = (EditText) (view.findViewById(R.id.editText1));
                                    DecimalFormat fdf = new DecimalFormat("###0.##");
                                    if(editText.getText().toString().equals("")|| Float.parseFloat(editText.getText().toString())==0) {
                                        Toast.makeText(RecordThis.this, "你還沒輸入數值喔", Toast.LENGTH_SHORT).show();
                                    }else{
                                        float data = Float.parseFloat(editText.getText().toString());
                                        record_show_fat.setText(fdf.format(data)+" %");
                                        fat = data;
                                    }
                                }
                            })
                            .show();
                }
            });

            lv_easy.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    emoji = 0;
                    Drawable drawable_dark = ContextCompat.getDrawable(getApplicationContext(), R.drawable.food_button_rounde_dark);

                    YoYo.with(Techniques.FlipInX)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.img_easy));
                    tv_easy.setTextColor(Color.WHITE);
                    tv_soso.setTextColor(Color.GRAY);
                    tv_hard.setTextColor(Color.GRAY);
                    lv_easy.setBackground(drawable_dark);
                    lv_soso.setBackground(null);
                    lv_hard.setBackground(null);

                }
            });

            lv_soso.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    emoji = 1;
                    Drawable drawable_dark = ContextCompat.getDrawable(getApplicationContext(), R.drawable.food_button_rounde_dark);
                    YoYo.with(Techniques.FlipInX)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.img_soso));
                    tv_easy.setTextColor(Color.GRAY);
                    tv_soso.setTextColor(Color.WHITE);
                    tv_hard.setTextColor(Color.GRAY);
                    lv_easy.setBackground(null);
                    lv_soso.setBackground(drawable_dark);
                    lv_hard.setBackground(null);
                }
            });

            lv_hard.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    emoji = 2;
                    Drawable drawable_dark = ContextCompat.getDrawable(getApplicationContext(), R.drawable.food_button_rounde_dark);
                    YoYo.with(Techniques.FlipInX)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.img_hard));
                    tv_easy.setTextColor(Color.GRAY);
                    tv_soso.setTextColor(Color.GRAY);
                    tv_hard.setTextColor(Color.WHITE);

                    lv_easy.setBackground(null);
                    lv_soso.setBackground(null);
                    lv_hard.setBackground(drawable_dark);
                }
            });

            btn_record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        AddBodyData();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    addFastingData();
                }
            });
        }

    }

    private void addFastingData() {
        Date date = new Date();
        //用來輸入timestamp值
        long timestamp = date.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        readData();
        int index = 0;
        for (fastRecords r :fast_records){
            Date get_date = new Date();
            get_date.setTime(r.getStartTime());
            //如果在fast_records找到等於傳入時間的日期
            if (df.format(get_date).equals(df.format(startdate))){
                //取得該筆資料的index
                index = fast_records.indexOf(r);
                break;
            }
        }

        Log.e("get",""+index);
        //如果資料表沒有值，就新增
        if (fast_records.size()==0){
            boolean insertRecord = myDb3.insertData(uid,startdate,enddate,emoji,timestamp);
            Log.e("fast record","inserted:"+insertRecord);
            return;
        }

        Date getrecordDate = new Date();
        getrecordDate.setTime(fast_records.get(index).getStartTime());
        Date readDate = new Date();
        readDate.setTime(startdate);
        //找不到與傳入日期同一天的資料，就新增，反之，更新
        if (!df.format(getrecordDate).equals(df.format(readDate))){
            boolean insertRecord = myDb3.insertData(uid,startdate,enddate,emoji,timestamp);
            Log.e("fast record","inserted:"+insertRecord);
        }else{
            //更新不用更新timestamp，timestamp用來排序
            //timestamp根據使用者輸入的當下決定
            boolean updateRecord = myDb3.updateFastData(fast_records.get(index).getId(),startdate,enddate,emoji);
            Log.e("fast record","updated:"+updateRecord);
        }


    }

    //新增&更新資料
    private void AddBodyData() throws ParseException {
        Date date = new Date();
        //如果資料不存在
        //取得的日期字串轉換成Date
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String now_date = df.format(date);
        long timestamp = date.getTime();
        //如果該日期沒有資料
        Log.e("get date:",now_date);
        if(Dates.indexOf(now_date) ==-1){
            Log.e("dates",Dates.get(Dates.size()-1)+"");
            boolean isInserted2 = myDb2.insertData(uid,weight,height,waist,fat,now_date,timestamp);
            Log.e("insert:",""+isInserted2);
        }
        //如果資料存在
        else{
            boolean isupdated = false;
            isupdated = myDb2.updateAllData(IDs.get(Dates.indexOf(now_date)), weight,height,waist,fat,timestamp);
            Log.e("update","updated:"+isupdated);
        }

        boolean updateBodyData = myDb.updateBodyData(uid,
                height,
                weight,
                waist,
                fat);

        Log.e("update personal profile body data",""+updateBodyData);

    }
    public void init(){
        tv_endtime = findViewById(R.id.tv_endtime);
        tv_time_span = findViewById(R.id.tv_time_span);
        fasting_time = findViewById(R.id.fasting_time);
        img_show_weight = findViewById(R.id.img_show_weight);
        img_show_height = findViewById(R.id.img_show_height);
        img_show_waist = findViewById(R.id.img_show_waist);
        img_show_fat = findViewById(R.id.img_show_fat);
        record_show_weight = findViewById(R.id.record_show_weight);
        record_show_height = findViewById(R.id.record_show_height);
        record_show_waist = findViewById(R.id.record_show_waist);
        record_show_fat = findViewById(R.id.record_show_fat);
        decoView = findViewById(R.id.fasting_progress_record);
        go_food_record = findViewById(R.id.go_food_record);
        btn_record = findViewById(R.id.btn_record);
        uid = user.getUid();
        lv_easy = findViewById(R.id.lv_easy);
        lv_soso = findViewById(R.id.lv_soso);
        lv_hard = findViewById(R.id.lv_hard);
        tv_easy = findViewById(R.id.tv_easy);
        tv_soso = findViewById(R.id.tv_soso);
        tv_hard = findViewById(R.id.tv_hard);
        img_easy = findViewById(R.id.img_easy);
        img_soso = findViewById(R.id.img_soso);
        img_hard = findViewById(R.id.img_hard);

        myDb = new PersonalInformation(this);
        myDb2 = new BodyRecord(this);
        myDb3 = new FastRecord(this);
    }
    public void getValue(int type) {

    }

    public void readData(){
        Cursor res2 = myDb2.getAllData();
        if (res2.getCount()<=0){
            Log.e("no data found","no data in res2");

        }else {
            while (res2.moveToNext()) {
                if(uid.equals(res2.getString(1))){
                    IDs.add(res2.getInt(0));
                    KGs.add(res2.getFloat(2));
                    Heights.add(res2.getFloat(3));
                    Waists.add(res2.getFloat(4));
                    Body_fats.add(res2.getFloat(5));
                    Dates.add(res2.getString(6));


                    Log.e("get data ","IDs.size="+IDs.size()+",KGs.size="+KGs.size()+",Dates.size="+Dates.size());
                    Log.e("dates.get(0)",Dates.get(0));
                }
            }
            //取得最新一筆資料
            weight = KGs.get(KGs.size()-1);
            height = Heights.get(Heights.size()-1);
            waist = Waists.get(Waists.size()-1);
            fat = Body_fats.get(Body_fats.size()-1);
        }

        Cursor res = myDb3.getAllData();
        if (res.getCount()<=0){
            Log.e("no data found","no data in myDb3");

        }else {
            while (res.moveToNext()) {
                if(uid.equals(res.getString(1))){
                    fastRecords record = new fastRecords();
                    record.setId(res.getInt(0));
                    record.setUid(res.getString(1));
                    record.setStartTime(res.getLong(2));
                    record.setEndTime(res.getLong(3));
                    record.setEmoji(res.getInt(4));
                    record.setTimeStamp(res.getLong(5));

                    fast_records.add(record);
                }
            }
            Log.e("get record", ""+fast_records.size());
        }
    }

    public void writeData(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        long ts = date.getTime();

        String today = df.format(date);

    }
}