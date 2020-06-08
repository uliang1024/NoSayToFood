package info.androidhive.firebaseauthapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import java.util.List;

import info.androidhive.firebaseauthapp.SQLite.BodyRecord;
import info.androidhive.firebaseauthapp.SQLite.PersonalInformation;

public class Weight_scale extends AppCompatActivity {

    private TextView tv_KG,tv_BMI;
    private ArrayList<Float> KG = new ArrayList<>();
    private ArrayList<String> date = new ArrayList<>();
    private float height;
    PersonalInformation myDb;
    BodyRecord myDb2;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_weight_scale);

        tv_KG = (TextView)findViewById(R.id.tv_KG);
        tv_BMI = (TextView)findViewById(R.id.tv_BMI);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
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
                KG.add(res.getFloat(2));
                date.add(res.getString(3));
            }
        }

        ArrayList<Entry> values1 = new ArrayList<>();


        for (int i = 0; i < 7; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,i-5);
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
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(true);

        ArrayList<Entry> values2 = new ArrayList<>();

        float good_kg = (height/100)*(height/100)*22;
        for (int i = 0; i < 7; i++) {
            values2.add(new Entry(i, good_kg));
        }

        LineDataSet d2 = new LineDataSet(values2, "理想體重");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);

        return new LineData(sets);
    }


}

