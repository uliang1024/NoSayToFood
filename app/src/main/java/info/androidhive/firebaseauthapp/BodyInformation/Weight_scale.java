package info.androidhive.firebaseauthapp.BodyInformation;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.SQLite.BodyRecord;
import info.androidhive.firebaseauthapp.SQLite.PersonalInformation;

public class Weight_scale extends AppCompatActivity {

    private TextView tv_KG,tv_BMI;
    private ArrayList<Float> KG = new ArrayList<>();
    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<Integer> ID = new ArrayList<>();
    private float height,width_data;
    private Button bt_KG;
    private String uid = null;
    PersonalInformation myDb;
    BodyRecord myDb2;
    private PopupWindow popupWindow;
    private View workingAge_view;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_weight_scale);

        tv_KG = (TextView)findViewById(R.id.tv_KG);
        tv_BMI = (TextView)findViewById(R.id.tv_BMI);
        bt_KG = (Button)findViewById(R.id.bt_KG);
        bt_KG.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                initNumberPicker3();

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
                // 触屏位置如果在选择框外面不銷毀
                popupWindow.setOutsideTouchable(true);
                // 设置弹出窗体的背景
                popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                popupWindow.showAtLocation(workingAge_view, Gravity.CENTER, 0, 0);

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
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            uid = user.getUid();
        }
        myDb = new PersonalInformation(this);
        Cursor res = myDb.getAllData();
        while (res.moveToNext()) {
            if(uid.equals(res.getString(0))){
                tv_KG.setText(String.valueOf(res.getFloat(5)));
                float bmi = res.getFloat(5)/((res.getFloat(4)/100)*(res.getFloat(4)/100));
                DecimalFormat df = new DecimalFormat("##0.0");
                tv_BMI.setText(String.valueOf(df.format(bmi)));
                height = res.getFloat(4);
            }
        }

        setTitle("ListViewMultiChartActivity");

        ListView lv = findViewById(R.id.listView1);

        ArrayList<ChartItem> list = new ArrayList<>();
        list.add(new LineChartItem(generateDataLine(1), getApplicationContext()));
        list.add(new LineChartItem2(generateDataLine2(2), getApplicationContext()));

        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);
    }
    private void initNumberPicker3() {
        workingAge_view = LayoutInflater.from(this).inflate(R.layout.show_yourkg2, null);

        final TextView date_picker = workingAge_view.findViewById(R.id.date_picker);
        Button kg_ok = workingAge_view.findViewById(R.id.kg_ok);
        final EditText editText = workingAge_view.findViewById(R.id.editText1);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DecimalFormat df = new DecimalFormat("##0.0");
        date_picker.setText(String.format("%02d", year)+"/"+String.format("%02d", month)+"/"+String.format("%02d", day));

        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog =new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String dateTime =String.format("%02d", year)+"/"+String.format("%02d", month+1)+"/"+String.format("%02d", day);
                        date_picker.setText(dateTime);
                    }
                }, year, month, day);
                //设置起始日期和结束日期
                DatePicker datePicker = dialog.getDatePicker();
                Date date = new Date();//当前时间
                long time = date.getTime();
                datePicker.setMaxDate(time);
                dialog.show();
            }
        });
        kg_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editText.getText().toString().matches("")) {

                }else{
                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                    Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                    String str = df.format(curDate);
                    if(date_picker.getText().toString().equals(str)){
                        tv_KG.setText(editText.getText().toString());
                    }
                    width_data = Float.parseFloat(tv_KG.getText().toString());
                    float profit = width_data/((height/100)*(height/100));
                    DecimalFormat df2 = new DecimalFormat("##0.0");
                    tv_BMI.setText(String.valueOf(df2.format(profit)));
                    width_data = Float.parseFloat(editText.getText().toString());
                    updateData(date_picker.getText().toString());
                    AddData(date_picker.getText().toString());


                }
                popupWindow.dismiss();
            }
        });
    }
    private void updateData(String x) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String str = df.format(curDate);
        if(x.equals(str)){
            boolean isupdated = false;
            isupdated = myDb.updateData(uid,
                    width_data);
        }

    }
    public  void AddData(String x) {
        if(date.indexOf(x) ==-1){
            boolean isInserted2 = myDb2.insertData(uid,width_data,x);
        }else{
            boolean isupdated = false;
            isupdated = myDb2.updateData(ID.get(date.indexOf(x)),
                    width_data);
        }
        KG = new ArrayList<>();
        date = new ArrayList<>();
        ID = new ArrayList<>();
        Cursor res = myDb2.getAllData();
        while (res.moveToNext()) {
            if(uid.equals(res.getString(1))){
                ID.add(res.getInt(0));
                KG.add(res.getFloat(2));
                date.add(res.getString(3));
            }
        }
        ListView lv = findViewById(R.id.listView1);

        ArrayList<ChartItem> list = new ArrayList<>();
        list.add(new LineChartItem(generateDataLine(1), getApplicationContext()));
        list.add(new LineChartItem2(generateDataLine2(2), getApplicationContext()));
        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);
    }

    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            //noinspection ConstantConditions
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            ChartItem ci = getItem(position);
            return ci != null ? ci.getItemType() : 0;
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }

    private LineData generateDataLine(int cnt) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if (user != null) {
            uid = user.getUid();
        }
        myDb2 = new BodyRecord(this);
        Cursor res = myDb2.getAllData();
        while (res.moveToNext()) {
            if(uid.equals(res.getString(1))){
                ID.add(res.getInt(0));
                KG.add(res.getFloat(2));
                date.add(res.getString(3));
            }
        }

        ArrayList<Entry> values1 = new ArrayList<>();


        for (int i = 0; i < 33; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,i-30);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String dateStr = sdf.format(calendar.getTime());
            for(int j = 0;j<date.size();j++){
                if(dateStr.equals(date.get(j))){
                    values1.add(new Entry(i, KG.get(j)));
                }
            }
        }

        LineDataSet d1 = new LineDataSet(values1, "體重變化");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setDrawValues(true);
        d1.setDrawFilled(true);
        d1.setCircleColor(Color.rgb(29, 77, 227));//圓點顏色
        d1.setCircleRadius(5);//圓點大小
        d1.setDrawCircleHole(false);//圓點為實心(預設空心)
        ArrayList<Entry> values2 = new ArrayList<>();

        float good_kg = (height/100)*(height/100)*22;
        for (int i = 0; i < 33; i++) {
            values2.add(new Entry(i, good_kg));
        }

        LineDataSet d2 = new LineDataSet(values2, "理想體重");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(false);
        d2.setDrawCircles(false);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);

        return new LineData(sets);
    }
    private LineData generateDataLine2(int cnt) {

        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i < 33; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,i-30);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String dateStr = sdf.format(calendar.getTime());
            for(int j = 0;j<date.size();j++){
                if(dateStr.equals(date.get(j))){
                    float bmi =  KG.get(j)/((height/100)*(height/100));
                    values1.add(new Entry(i,bmi));
                }
            }
        }

        LineDataSet d1 = new LineDataSet(values1, "BMI變化");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setDrawValues(true);
        d1.setDrawFilled(true);
        d1.setCircleColor(Color.rgb(29, 77, 227));//圓點顏色
        d1.setCircleRadius(5);//圓點大小
        d1.setDrawCircleHole(false);//圓點為實心(預設空心)

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);

        return new LineData(sets);
    }


}

