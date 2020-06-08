package info.androidhive.firebaseauthapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.firebaseauthapp.DatabaseHelper;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.fasting.FirstFasting;
import info.androidhive.firebaseauthapp.foodClassification;


public class Frag2 extends Fragment {
    DatabaseHelper myDb;
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private Button bt_eat;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_frag2 = inflater.inflate(R.layout.fragment_frag2, container, false);
        init(fragment_frag2);

        myDb = new DatabaseHelper(Frag2.super.getContext());
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
        item1.put("date", "新增時間");
        item1.put("Name", "食物名稱(熱量攝取量)");
        item1.put("Amount", "份量");
        items.add(item1);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String str = formatter.format(curDate);

        while (res.moveToNext()) {
            if(uid.equals(res.getString(4))){
                if(res.getString(1).substring(0, 11).equals(str)){
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("Name", res.getString(1));
                    item.put("date", res.getString(2));
                    item.put("Amount", res.getString(3));
                    items.add(item);
                }
            }
        }

        simpleAdapter = new SimpleAdapter(Frag2.super.getContext(),
                items, R.layout.today_food, new String[]{"Name","date", "Amount"},
                new int[]{R.id.name, R.id.date, R.id.amount});
        listView.setAdapter(simpleAdapter);

        bt_eat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Frag2.super.getContext(), foodClassification.class));
            }
        });

        return fragment_frag2;
    }

    private void init(View v) {
        listView = (ListView) v.findViewById(R.id.user_list);
        bt_eat = (Button)v.findViewById(R.id.bt_eat);
    }

}
