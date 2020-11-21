package info.androidhive.firebaseauthapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.EdgeDetail;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.util.ArrayList;
import java.util.Date;

import info.androidhive.firebaseauthapp.SQLite.FastRecord;
import info.androidhive.firebaseauthapp.adapter.FastRecordAdapter;
import info.androidhive.firebaseauthapp.models.fastRecords;


public class FastRecordsActivity extends AppCompatActivity implements FastRecordAdapter.RecordClickedListener {
    private TextView tv_daycount,tv_hourcount,tv_no_data;
    private RecyclerView recycler_fast_record;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid ;
    FastRecord myDb3;
    private ArrayList<fastRecords> fast_records = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_records);

        tv_daycount = findViewById(R.id.tv_daycount);
        tv_hourcount = findViewById(R.id.tv_hourcount);
        tv_no_data = findViewById(R.id.tv_no_data);
        recycler_fast_record = findViewById(R.id.recycler_fast_record);
        myDb3 = new FastRecord(this);
        uid = user.getUid();

        readData();
        if (fast_records.size()==0){
            tv_no_data.setVisibility(View.VISIBLE);
        }else{
            tv_no_data.setVisibility(View.GONE);
        }
        int days=0;
        int hours=0;
        int mins=0;
        for (fastRecords f:fast_records){
            days+=1;

            long time = (f.getEndTime()-f.getStartTime())/1000;
            long hour = time/(60*60);
            long min = (time%(60*60))/(60);

            hours+=(int)hour ;
            mins+=(int)min;
            Log.e("time",hours+" hours "+mins+" mins");
        }
        if (mins>=60){
            hours+=mins/60;
            mins = mins%60;
        }

        tv_daycount.setText(days+" 天");
        tv_hourcount.setText(hours+" 小時 "+ mins+" 分");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_fast_record.setLayoutManager(layoutManager);
        FastRecordAdapter adapter = new FastRecordAdapter(this,fast_records,this);
        recycler_fast_record.setAdapter(adapter);

    }

    private void readData() {
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


    @Override
    public void onRecordClicked(int position) {
        Toast.makeText(this, "u clicked"+position, Toast.LENGTH_SHORT).show();

        long startDate = fast_records.get(position).getStartTime();
        long endDate = fast_records.get(position).getStartTime();
        int status = 3;
        int emoji = fast_records.get(position).getEmoji();
        Intent intent = new Intent(this,RecordThis.class);
        Bundle bundle = new Bundle();
        bundle.putLong("startdate",startDate);
        bundle.putLong("enddate",endDate);
        //0代表未紀錄，要記錄 1代表已記，3代表已經過了可以修改的時候，只能查看
        bundle.putInt("status",status);
        bundle.putInt("emoji",emoji);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}