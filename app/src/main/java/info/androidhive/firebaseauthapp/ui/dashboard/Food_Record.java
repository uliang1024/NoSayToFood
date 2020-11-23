package info.androidhive.firebaseauthapp.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import info.androidhive.firebaseauthapp.SQLite.DatabaseHelper;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.food.foodClassification;

public class Food_Record extends AppCompatActivity {
    DatabaseHelper myDb;
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private ArrayList<String> date = new ArrayList<>();

    private CalendarView calendarView;
    private Boolean exist = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food__record);

        listView = (ListView) findViewById(R.id.user_list);

        myDb = new DatabaseHelper(this);

        Cursor res = myDb.getAllData();

        if(res.getCount() == 0) {
            // show message
            //textView.setText("Error Nothing found");
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if (user != null) {
            uid = user.getUid();
        }
        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();

        while (res.moveToNext()) {
            if(uid.equals(res.getString(4))){
                date.add(res.getString(1));
            }
        }

        Set<String> set = new HashSet<String>(date);
        List<String> newList = new ArrayList<String>(set);

        for(int i=0;i<newList.size();i++){
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("Date", newList.get(i));
            items.add(item);
        }


        simpleAdapter = new SimpleAdapter(this,
                items, R.layout.today_food, new String[]{"Date"},
                new int[]{R.id.date});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                ListView listView = (ListView) arg0;
                Intent intent = new Intent();
                intent.setClass(Food_Record.this,OneFoodRecord.class);
                Bundle bundle = new Bundle();
                bundle.putString("datee",listView.getItemAtPosition(arg2).toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        calendarView = (CalendarView)findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String dateee =year+"年"+(month+1)+"月"+dayOfMonth+"日";
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
            }
        });
    }
}
