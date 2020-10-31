package info.androidhive.firebaseauthapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.firebaseauthapp.SQLite.DatabaseHelper;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.food.Fruit;
import info.androidhive.firebaseauthapp.food.foodClassification;


public class Frag2 extends Fragment {
    DatabaseHelper myDb;
    private ExpandableListView user_list ;
    private ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
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

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String str = formatter.format(curDate);

        int meal1 = 0;
        list.add(new ArrayList<String>());
        list.get(meal1).add(String.valueOf(new ArrayList<String>()));
        while (res.moveToNext()) {
            if(uid.equals(res.getString(4))){
                if(res.getString(1).substring(0, 11).equals(str)){
                    if(res.getInt(5)==(meal1+1)){
                        list.get(meal1).add(res.getString(2));
                    }else {
                        meal1+=1;
                        list.add(new ArrayList<String>());
                        list.get(meal1).add(res.getString(2));
                    }
                }
            }
        }
        user_list.setAdapter(new Frag2.MyExpandableListView());

        // Listview on child click listener
        user_list.setOnChildClickListener(new ExpandableListView.
                OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });

        bt_eat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Frag2.super.getContext(), foodClassification.class));
            }
        });

        return fragment_frag2;
    }

    @SuppressLint("WrongViewCast")
    private void init(View v) {
        user_list = (ExpandableListView) v.findViewById(R.id.user_list);
        bt_eat = (Button)v.findViewById(R.id.bt_eat);
    }
    //为ExpandableListView自定义适配器
    class MyExpandableListView extends BaseExpandableListAdapter {

        //返回一级列表的个数
        @Override
        public int getGroupCount() {
            return list.size();
        }

        //返回每个二级列表的个数
        @Override
        public int getChildrenCount(int groupPosition) { //参数groupPosition表示第几个一级列表
            Log.d("smyhvae", "-->" + groupPosition);
            return list.get(groupPosition).size();
        }

        //返回一级列表的单个item（返回的是对象）
        @Override
        public Object getGroup(int groupPosition) {
            return list.get(groupPosition);
        }

        //返回二级列表中的单个item（返回的是对象）
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return list.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //每个item的id是否是固定？一般为true
        @Override
        public boolean hasStableIds() {
            return false;
        }

        //【重要】填充一级列表
        @SuppressLint("SetTextI18n")
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.today_food_listview,null);
            }
            TextView meal = (TextView) convertView.findViewById(R.id.meal);

            meal.setText("第"+ (groupPosition + 1) +"餐");



            return convertView;
        }

        //【重要】填充二级列表
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.today_food, null);
            }
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView amount = (TextView) convertView.findViewById(R.id.amount);

            name.setText(list.get(groupPosition).get(childPosition));

            return convertView;
        }

        //二级列表中的item是否能够被选中？可以改为true
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
