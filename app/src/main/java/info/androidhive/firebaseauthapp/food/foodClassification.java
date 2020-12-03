package info.androidhive.firebaseauthapp.food;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.firebaseauthapp.HomeActivity;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.SQLite.DatabaseHelper;
import info.androidhive.firebaseauthapp.SQLite.PersonalInformation;
import info.androidhive.firebaseauthapp.first.HelloUser;
import info.androidhive.firebaseauthapp.ui.home.HomeFragment;

public class foodClassification extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private Handler handler = new Handler();
    private SimpleAdapter simpleAdapter;
    private ArrayList<String> Name = new ArrayList<String>();
    private ArrayList<Double> Amount = new ArrayList<Double>();
    private Button bt_milk,bt_fruit,bt_vegetables,bt_meet,bt_grain,bt_oil,button;
    ArrayList<Integer> meal = new ArrayList<Integer>();
    DatabaseHelper myDb;

    PersonalInformation myDb2;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = null;

    private int user_exercise_level,age;
    private String gender;
    private float height,width;

    private double bmr,tdee;
    private String good_milk,good_fruit,good_vegetables,good_meet,good_grain,good_oil;
    private Button bt_recommend;
    private Button bt_what;

    private ShowcaseView showcaseView;
    private int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_classification);
        myDb = new DatabaseHelper(this);

        uid = user.getUid();
        myDb2 = new PersonalInformation(this);
        Cursor res = myDb2.getAllData();
        while (res.moveToNext()) {
            if(uid.equals(res.getString(0))){
                user_exercise_level = res.getInt(7);
                gender = res.getString(1);
                age = res.getInt(2);
                height = res.getFloat(3);
                width = res.getFloat(4);
            }
        }
        if(gender.equals("男性")){
            bmr = (float)((10*width)+(6.25*height)-(5*age)+5);
        }else{
            bmr = (float)((10*width)+(6.25*height)-(5*age)-161);
        }
        if(user_exercise_level == 0){
            tdee = (float)(1.2*bmr);
        }else if(user_exercise_level == 1){
            tdee = (float)(1.375*bmr);
        }else if(user_exercise_level == 2){
            tdee = (float)(1.55*bmr);
        }else if(user_exercise_level == 3){
            tdee = (float)(1.725*bmr);
        }else{
            tdee = (float)(1.9*bmr);
        }

        if(tdee>=2700){
            good_grain = "4";
            good_meet = "8";
            good_milk = "2杯 (480 ml)";
            good_vegetables= "5 份";
            good_fruit="4 份";
            good_oil="7 茶匙油 ( 約 35 公克 ) + 1 份堅果種子";
        }else if(tdee >=2500 && tdee<2700){
            good_grain = "4";
            good_meet = "7";
            good_milk = "1.5杯 (360 ml)";
            good_vegetables= "5 份";
            good_fruit="4 份";
            good_oil="6 茶匙油 ( 約 35 公克 ) + 1 份堅果種子";
        }else if(tdee>=2200  && tdee<2500){
            good_grain = "3.5";
            good_meet = "6";
            good_milk = "1.5杯 (360 ml)";
            good_vegetables= "4 份";
            good_fruit="3.5 份";
            good_oil="5 茶匙油 ( 約 35 公克 ) + 1 份堅果種子";
        }else if(tdee>=2000  && tdee<2200){
            good_grain = "3";
            good_meet = "6";
            good_milk = "1.5杯 (360 ml)";
            good_vegetables= "4 份";
            good_fruit="3 份";
            good_oil="5 茶匙油 ( 約 35 公克 ) + 1 份堅果種子";
        }else if(tdee>=1800  && tdee<2000){
            good_grain = "3";
            good_meet = "5";
            good_milk = "1.5杯 (360 ml)";
            good_vegetables= "3 份";
            good_fruit="2 份";
            good_oil="4 茶匙油 ( 約 35 公克 ) + 1 份堅果種子";
        }else if(tdee >=1500 && tdee<1800){
            good_grain = "2.5";
            good_meet = "4( 青少年高鈣豆製品至少占 1.3 份 )";
            good_milk = "1.5杯 (360 ml)";
            good_vegetables= "3 份";
            good_fruit="2 份";
            good_oil="3 茶匙油 ( 約 35 公克 ) + 1 份堅果種子";
        }else{
            good_grain = "1.5";
            good_meet = "3( 高鈣豆製品至少占 1 份 )";
            good_milk = "1.5杯 (360 ml)";
            good_vegetables= "3 份，至少 1.5 份為深色蔬菜";
            good_fruit="2 份";
            good_oil="3 茶匙油 ( 約 35 公克 ) + 1 份堅果種子";
        }

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

        bt_recommend= (Button)findViewById(R.id.bt_recommend);
        bt_what = (Button)findViewById(R.id.bt_what);

        bt_recommend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(foodClassification.this)
                        .setTitle("六大類飲食建議份數")
                        .setMessage("全榖雜糧類 ( 碗 )"+good_grain+"碗\n"+
                                "豆魚蛋肉類 ( 份 )"+ good_meet+"份\n"+
                                "乳品類 ( 杯 )"+good_milk+"杯\n"+
                                "蔬菜類 ( 份 )"+good_vegetables+"份\n"+
                                    "水果類 ( 份 )"+good_fruit+"份\n"+
                                "油脂與堅果種子類 ( 份 )" +good_oil+"份")
                        .setPositiveButton("了解", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNeutralButton("問題?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(foodClassification.this,ScreenSlidePagerActivity.class));
                            }
                        })

                        .show();
            }
        });
        bt_what.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(foodClassification.this,ScreenSlidePagerActivity.class));
            }
        });

        bt_oil.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(foodClassification.this);
                final View view = inflater.inflate(R.layout.show_yourkg, null);
                new AlertDialog.Builder(foodClassification.this)
                        .setTitle("油脂與堅果種子類吃多少(份)?")
                        .setView(view)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) (view.findViewById(R.id.editText1));
                                if(editText.getText().toString().matches("")) {

                                }else{
                                    if(Name.contains("油脂與堅果種子類")){
                                        int index = Name.indexOf("油脂與堅果種子類");
                                        Amount.set(index,Amount.get(index)+Double.parseDouble(editText.getText().toString()));
                                    }else{
                                        Name.add("油脂與堅果種子類");
                                        Amount.add(Double.parseDouble(editText.getText().toString()));
                                    }

                                }
                            }
                        })
                        .show();
            }
        });
        bt_milk.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(foodClassification.this);
                final View view = inflater.inflate(R.layout.show_yourkg, null);
                new AlertDialog.Builder(foodClassification.this)
                        .setTitle("乳品類吃多少(杯)?")
                        .setView(view)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) (view.findViewById(R.id.editText1));
                                if(editText.getText().toString().matches("")) {

                                }else{
                                    if(Name.contains("乳品類")){
                                        int index = Name.indexOf("乳品類");
                                        Amount.set(index,Amount.get(index)+Double.parseDouble(editText.getText().toString()));
                                    }else{
                                        Name.add("乳品類");
                                        Amount.add(Double.parseDouble(editText.getText().toString()));
                                    }

                                }
                            }
                        })
                        .show();
            }
        });
        bt_meet.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(foodClassification.this);
                final View view = inflater.inflate(R.layout.show_yourkg, null);
                new AlertDialog.Builder(foodClassification.this)
                        .setTitle("豆魚蛋肉類吃多少(份)?")
                        .setView(view)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) (view.findViewById(R.id.editText1));
                                if(editText.getText().toString().matches("")) {

                                }else{
                                    if(Name.contains("豆魚蛋肉類")){
                                        int index = Name.indexOf("豆魚蛋肉類");
                                        Amount.set(index,Amount.get(index)+Double.parseDouble(editText.getText().toString()));
                                    }else{
                                        Name.add("豆魚蛋肉類");
                                        Amount.add(Double.parseDouble(editText.getText().toString()));
                                    }

                                }
                            }
                        })
                        .show();
            }
        });
        bt_vegetables.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(foodClassification.this);
                final View view = inflater.inflate(R.layout.show_yourkg, null);
                new AlertDialog.Builder(foodClassification.this)
                        .setTitle("蔬菜類吃多少(份)?")
                        .setView(view)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) (view.findViewById(R.id.editText1));
                                if(editText.getText().toString().matches("")) {

                                }else{
                                    if(Name.contains("蔬菜類")){
                                        int index = Name.indexOf("蔬菜類");
                                        Amount.set(index,Amount.get(index)+Double.parseDouble(editText.getText().toString()));
                                    }else{
                                        Name.add("蔬菜類");
                                        Amount.add(Double.parseDouble(editText.getText().toString()));
                                    }
                                }
                            }
                        })
                        .show();
            }
        });
        bt_fruit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(foodClassification.this);
                final View view = inflater.inflate(R.layout.show_yourkg, null);
                new AlertDialog.Builder(foodClassification.this)
                        .setTitle("水果類吃多少(杯)?")
                        .setView(view)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) (view.findViewById(R.id.editText1));
                                if(editText.getText().toString().matches("")) {

                                }else{
                                    if(Name.contains("水果類")){
                                        int index = Name.indexOf("水果類");
                                        Amount.set(index,Amount.get(index)+Double.parseDouble(editText.getText().toString()));
                                    }else{
                                        Name.add("水果類");
                                        Amount.add(Double.parseDouble(editText.getText().toString()));
                                    }
                                }
                            }
                        })
                        .show();
            }
        });
        bt_grain.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(foodClassification.this);
                final View view = inflater.inflate(R.layout.show_yourkg, null);
                new AlertDialog.Builder(foodClassification.this)
                        .setTitle("全榖雜糧類吃了幾碗?")
                        .setView(view)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) (view.findViewById(R.id.editText1));
                                if(editText.getText().toString().matches("")) {

                                }else{
                                    if(Name.contains("全榖雜糧類")){
                                        int index = Name.indexOf("全榖雜糧類");
                                        Amount.set(index,Amount.get(index)+Double.parseDouble(editText.getText().toString()));
                                    }else{
                                        Name.add("全榖雜糧類");
                                        Amount.add(Double.parseDouble(editText.getText().toString()));
                                    }
                                }
                            }
                        })
                        .show();
            }
        });
        AddData();

    }
    @Override
    public void onClick(View v) {
        switch (counter) {
            case 0:
                showcaseView.setContentTitle("問題?六大類食物代換份量");
                showcaseView.setContentText("了解每單位換算之份量");
                showcaseView.setShowcase(new ViewTarget(bt_what), true);
                break;
            case 1:
                showcaseView.setContentTitle("選擇食物種類");
                showcaseView.setContentText("輸入食物種類份量");
                showcaseView.setShowcase(new ViewTarget(bt_milk), true);
                break;

            case 2:
                showcaseView.setContentTitle("完成紀錄");
                showcaseView.setContentText("儲存您今日一餐的紀錄");
                showcaseView.setShowcase(new ViewTarget(button), true);
                break;

            case 3:
                showcaseView.hide();

                break;
        }
        counter++;
    }
    @Override
    protected void onResume() {
        super.onResume();
        String tutorialKey = "SOME_KEY";
        Boolean firstTime = getPreferences(MODE_PRIVATE).getBoolean(tutorialKey, true);
        if (firstTime) {
            showcaseView = new ShowcaseView.Builder(this,true)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme3)
                    .setTarget(new ViewTarget(findViewById(R.id.bt_recommend)))
                    .setContentTitle("推薦")
                    .setContentText("依照您的身體數據，推薦您最適合的六大類飲食建議份數")
                    .setOnClickListener(this)
                    .build();
            showcaseView.setButtonText(getString(R.string.next));

            getPreferences(MODE_PRIVATE).edit().putBoolean(tutorialKey, false).apply();
        }
    }

    public  void AddData() {
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Name.size()<=0){
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
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy年MM月dd日");
                                    Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                                    String str = formatter2.format(curDate);
                                    Cursor res = myDb.getAllData();
                                    while (res.moveToNext()) {
                                        if(uid.equals(res.getString(4))){
                                            if(res.getString(1).substring(0, 11).equals(str)){
                                                meal.add(res.getInt(5));
                                            }
                                        }
                                    }
                                    int highest = 0;
                                    if (meal.size()> 0) {
                                        highest = meal.get(0);

                                        for (int s = 1; s < meal.size(); s++) {
                                            int curValue = meal.get(s);
                                            if (curValue > highest) {
                                                highest = curValue;
                                            }
                                        }
                                    }
                                    boolean isInserted = false;



                                    for(int i=0; i<Name.size();i++){

                                        isInserted = myDb.insertData(Name.get(i),
                                                str,
                                                Amount.get(i),
                                                uid,
                                                highest+1);
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

            listView = (ListView)findViewById(R.id.listView);
            List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
            for (int i = 0; i < Name.size(); i++) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("Name", Name.get(i));
                item.put("Amount", Amount.get(i));
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
