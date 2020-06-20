package info.androidhive.firebaseauthapp.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.firebaseauthapp.SQLite.DatabaseHelper;
import info.androidhive.firebaseauthapp.R;

public class Food_Record extends AppCompatActivity {
    DatabaseHelper myDb;
    private ListView listView;
    private SimpleAdapter simpleAdapter;
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
        Map<String, Object> item1 = new HashMap<String, Object>();
        item1.put("Name", "食物名稱(熱量攝取量)");
        item1.put("Amount", "份量");
        items.add(item1);

        while (res.moveToNext()) {
            if(uid.equals(res.getString(4))){
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("Name", res.getString(2));
                    item.put("Amount", res.getString(3));
                    items.add(item);
            }
        }

        simpleAdapter = new SimpleAdapter(this,
                items, R.layout.today_food, new String[]{"Name","date", "Amount"},
                new int[]{R.id.name, R.id.amount});
        listView.setAdapter(simpleAdapter);
    }
}
