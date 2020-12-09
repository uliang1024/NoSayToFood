package info.androidhive.firebaseauthapp.ui.dashboard;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.SQLite.DatabaseHelper;

public class Food_Record extends AppCompatActivity {
    DatabaseHelper myDb;
    private final ArrayList<String> date = new ArrayList<>();

    private Boolean exist = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food__record);

        ListView listView = findViewById(R.id.user_list);

        myDb = new DatabaseHelper(this);

        Cursor res = myDb.getAllData();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if (user != null) {
            uid = user.getUid();
        }
        List<Map<String, Object>> items = new ArrayList<>();

        while (res.moveToNext()) {
            assert uid != null;
            if(uid.equals(res.getString(4))){
                date.add(res.getString(1));
            }
        }

        Set<String> set = new HashSet<>(date);
        List<String> newList = new ArrayList<>(set);

        for(int i=0;i<newList.size();i++){
            Map<String, Object> item = new HashMap<>();
            item.put("Date", newList.get(i));
            items.add(item);
        }


        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                items, R.layout.today_food, new String[]{"Date"},
                new int[]{R.id.date});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            // TODO Auto-generated method stub
            ListView listView1 = (ListView) arg0;
            Intent intent = new Intent();
            intent.setClass(Food_Record.this,OneFoodRecord.class);
            Bundle bundle = new Bundle();
            bundle.putString("datee", listView1.getItemAtPosition(arg2).toString());
            intent.putExtras(bundle);
            startActivity(intent);
        });

        CalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String st_year,st_month,st_dayOfMonth;
            st_year = String.valueOf(year);
            if((month+1)<10){
                st_month = "0"+ (month + 1);
            }else{
                st_month = String.valueOf(month+1);
            }
            if((dayOfMonth)<10){
                st_dayOfMonth = "0"+ (dayOfMonth);
            }else{
                st_dayOfMonth = String.valueOf(dayOfMonth);
            }
            String dateee =st_year+"年"+st_month+"月"+st_dayOfMonth+"日";
           for (int i=0;i<date.size();i++){
               if(dateee.equals(date.get(i))){
                   exist=true;
                   Intent intent = new Intent();
                   intent.setClass(Food_Record.this,OneFoodRecord.class);
                   Bundle bundle = new Bundle();
                   bundle.putString("datee","000000"+dateee+"0");
                   intent.putExtras(bundle);
                   startActivity(intent);
                   finish();
                   break;
               }
           }
            if(!exist){
                Toast.makeText(Food_Record.this,dateee+"沒有任何紀錄",Toast.LENGTH_LONG).show();
            }
        });
    }
}
