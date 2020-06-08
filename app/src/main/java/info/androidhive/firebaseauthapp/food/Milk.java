package info.androidhive.firebaseauthapp.food;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.Arrays;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.foodClassification;

public class Milk extends AppCompatActivity {


    private ExpandableListView expandableListView ;
    private TextView tv_what;
    private Bundle bundle = new Bundle();
    private String[] Name = {"全脂","低脂","脫脂"};
    private String[] Content = {"每份含蛋白質 8 公克，脂肪 8 公克，醣類有 12 公克，熱量 150 大卡",
            "每份含蛋白質 8 公克，脂肪 4 公克，醣類有 12 公克，熱量 120 大卡",
    "每份含蛋白質 8 公克，醣類有 12 公克，熱量 80 大卡"};
    //注意，字符数组不要写成{{"A1,A2,A3,A4"}, {"B1,B2,B3,B4，B5"}, {"C1,C2,C3,C4"}}
    private String[][] childs = {{"全脂奶","全脂奶粉","＊起司片"},
            {"低脂奶","低脂奶粉","優格(無糖)","酪乳(無糖)"},
            {"脫脂奶","脫脂奶粉"}};

    private String[] Name1 = new String[0];
    private String[] Amount1 = new String[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk);
        Intent intent = getIntent();
        Name1 = intent.getStringArrayExtra("Name");
        Amount1 = intent.getStringArrayExtra("Amount");

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListView.setAdapter(new Milk.MyExpandableListView());



        // Listview on child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.
                OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                what(v,groupPosition,childPosition);
                return false;
            }
        });
    }
    public void what(final View v ,final int groupPosition, final int childPosition) {
        final LayoutInflater inflater = LayoutInflater.from(Milk.this);
        final View view = inflater.inflate(R.layout.show_food, null);
        new AlertDialog.Builder(Milk.this)
                .setTitle("吃多少"+childs[groupPosition][childPosition])
                .setView(view)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) (view.findViewById(R.id.editTextfood));
                        tv_what =(TextView)v.findViewById(R.id.tv_what);
                        if(editText.getText().toString().matches("")) {

                            tv_what.setText("");

                            for(int i=0;i<Name1.length;i++){
                                if(Name1[i].equals(childs[groupPosition][childPosition])){
                                    //把最後一個元素替代指定的元素
                                    Name1[i]= Name1[Name1.length-1];
                                    Amount1[i]= Amount1[Amount1.length-1];
                                    //陣列縮容
                                    Name1 = Arrays.copyOf(Name1, Name1.length-1);
                                    Amount1 = Arrays.copyOf(Amount1, Amount1.length-1);
                                }
                            }
                        }else{
                            tv_what.setText(editText.getText().toString());
                            for(int i=0;i<Name1.length;i++){
                                if(childs[groupPosition][childPosition].equals(Name1[i])){
                                    //把最後一個元素替代指定的元素
                                    Name1[i]= Name1[Name1.length-1];
                                    Amount1[i]= Amount1[Amount1.length-1];
                                    //陣列縮容
                                    Name1 = Arrays.copyOf(Name1, Name1.length-1);
                                    Amount1 = Arrays.copyOf(Amount1, Amount1.length-1);
                                }
                            }
                            String[] Name2 = new String[Name1.length+1];
                            String[] Amount2 = new String[Name1.length+1];
                            //將原陣列的資料拷貝到新陣列
                            System.arraycopy(Name1, 0, Name2, 0, Name1.length);
                            System.arraycopy(Amount1, 0, Amount2, 0, Name1.length);
                            //將新元素放到dest陣列的末尾
                            Name2[Name1.length]=childs[groupPosition][childPosition];
                            Amount2[Name1.length]=editText.getText().toString();
                            //將src指向dest
                            Name1=Name2;
                            Amount1=Amount2;

                        }
                    }
                })
                .show();
    }
    //为ExpandableListView自定义适配器
    class MyExpandableListView extends BaseExpandableListAdapter {

        //返回一级列表的个数
        @Override
        public int getGroupCount() {
            return Name.length;
        }

        //返回每个二级列表的个数
        @Override
        public int getChildrenCount(int groupPosition) { //参数groupPosition表示第几个一级列表
            Log.d("smyhvae", "-->" + groupPosition);
            return childs[groupPosition].length;
        }

        //返回一级列表的单个item（返回的是对象）
        @Override
        public Object getGroup(int groupPosition) {
            return Name[groupPosition];
        }

        //返回二级列表中的单个item（返回的是对象）
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childs[groupPosition][childPosition];  //不要误写成groups[groupPosition][childPosition]
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
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.food_listview,null);
            }
            TextView textView5 = (TextView) convertView.findViewById(R.id.textView5);
            TextView textView6 = (TextView) convertView.findViewById(R.id.textView6);

            textView5.setText(Name[groupPosition]);
            textView6.setText(Content[groupPosition]);



            return convertView;
        }

        //【重要】填充二级列表
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_child, null);
            }
            TextView tv_child = (TextView) convertView.findViewById(R.id.tv_child);
            TextView tv_what = (TextView) convertView.findViewById(R.id.tv_what);

            //iv_child.setImageResource(resId);
            tv_child.setText(childs[groupPosition][childPosition]);

            //小鴨教導
            boolean found = false;

            for( int i = 0; i<Name1.length; i++ ){
                if (Name1[i].equals(childs[groupPosition][childPosition])) {
                    tv_what.setText(Amount1[i]);
                    found = true;
                    break;
                }
            }

            //小鴨教導
            if ( !found ) {
                tv_what.setText("");
            }

            return convertView;
        }

        //二级列表中的item是否能够被选中？可以改为true
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent it= new Intent(this,foodClassification.class);
            bundle.putStringArray("Name", Name1);
            bundle.putStringArray("Amount", Amount1);
            it.putExtras(bundle);
            startActivity(it);
            finish();
        }
        return true;
    }
}
