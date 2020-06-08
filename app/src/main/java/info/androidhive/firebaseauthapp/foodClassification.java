package info.androidhive.firebaseauthapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.firebaseauthapp.food.Fruit;
import info.androidhive.firebaseauthapp.food.Grain;
import info.androidhive.firebaseauthapp.food.Meat;
import info.androidhive.firebaseauthapp.food.Milk;
import info.androidhive.firebaseauthapp.food.SugarSaltOil;
import info.androidhive.firebaseauthapp.food.Vegetables;
import info.androidhive.firebaseauthapp.ui.home.Frag1;
import info.androidhive.firebaseauthapp.ui.home.HomeFragment;

public class foodClassification extends AppCompatActivity {

    private ListView listView;
    private Handler handler = new Handler();
    private SimpleAdapter simpleAdapter;
    private String[] Name = {"食物名稱(熱量攝取量)"};
    private String[] Amount = {"份量"};
    private Button bt_milk,bt_fruit,bt_vegetables,bt_meet,bt_grain,bt_oil,button;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_classification);
        myDb = new DatabaseHelper(this);

        listView = (ListView)findViewById(R.id.listView);

        handler.removeCallbacks(updateTimer);//設定定時要執行的方法
        handler.postDelayed(updateTimer, 0);//設定Delay的時間

        bt_milk =(Button)findViewById(R.id.bt_milk);
        bt_fruit =(Button)findViewById(R.id.bt_fruit);
        bt_vegetables =(Button)findViewById(R.id.bt_vegetables);
        bt_meet =(Button)findViewById(R.id.bt_meet);
        bt_grain =(Button)findViewById(R.id.bt_grain);
        bt_oil =(Button)findViewById(R.id.bt_oil);

        button =(Button)findViewById(R.id.button);

        bt_oil.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent it= new Intent(foodClassification.this,SugarSaltOil.class);
                bundle.putStringArray("Name", Name);
                bundle.putStringArray("Amount", Amount);
                it.putExtras(bundle);
                startActivity(it);
            }
        });
        bt_milk.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent it= new Intent(foodClassification.this,Milk.class);
                bundle.putStringArray("Name", Name);
                bundle.putStringArray("Amount", Amount);
                it.putExtras(bundle);
                startActivity(it);
            }
        });
        bt_meet.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent it= new Intent(foodClassification.this,Meat.class);
                bundle.putStringArray("Name", Name);
                bundle.putStringArray("Amount", Amount);
                it.putExtras(bundle);
                startActivity(it);
            }
        });
        bt_vegetables.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent it= new Intent(foodClassification.this,Vegetables.class);
                bundle.putStringArray("Name", Name);
                bundle.putStringArray("Amount", Amount);
                it.putExtras(bundle);
                startActivity(it);
            }
        });
        bt_fruit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent it= new Intent(foodClassification.this,Fruit.class);
                bundle.putStringArray("Name", Name);
                bundle.putStringArray("Amount", Amount);
                it.putExtras(bundle);
                startActivity(it);
            }
        });
        bt_grain.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent it= new Intent(foodClassification.this,Grain.class);
                bundle.putStringArray("Name", Name);
                bundle.putStringArray("Amount", Amount);
                it.putExtras(bundle);
                startActivity(it);
            }
        });
        AddData();

    }

    public  void AddData() {
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Name.length<=1){
                    Toast.makeText(foodClassification.this,"您還沒有新增任何食物",Toast.LENGTH_LONG).show();
                }else{
                new AlertDialog.Builder(foodClassification.this)
                        .setTitle("你確定完成您的菜單了嗎?")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = null;
                                if (user != null) {
                                    uid = user.getUid();
                                }

                                boolean isInserted = false;
                                for(int i=1; i<Name.length;i++){
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                                    Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                                    String str = formatter.format(curDate);
                                    isInserted = myDb.insertData(Name[i],
                                            str,
                                            Amount[i],
                                            uid);
                                }
                                if(isInserted)
                                    Toast.makeText(foodClassification.this,"Data Inserted",Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(foodClassification.this,"Data not Inserted",Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(foodClassification.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton("cancel",null).create()
                        .show();
                }
            }
        });
    }

    private Runnable updateTimer = new Runnable() {
        public void run() {
            handler.postDelayed(this, 0);
            Intent intent = getIntent();
            if(intent.getStringArrayExtra("Name") != null){
                Name = intent.getStringArrayExtra("Name");
                Amount = intent.getStringArrayExtra("Amount");
            }



                listView = (ListView)findViewById(R.id.listView);
                List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
                for (int i = 0; i < Name.length; i++) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("Name", Name[i]);
                    item.put("Amount", Amount[i]);
                    items.add(item);
                }
                simpleAdapter = new SimpleAdapter(foodClassification.this,
                        items, R.layout.item_child, new String[]{"Name", "Amount"},
                        new int[]{R.id.tv_child, R.id.tv_what});
                listView.setAdapter(simpleAdapter);
            }

    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in Action Bar clicked; go home
                Intent intent = new Intent(this, HomeFragment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

}
