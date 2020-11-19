package info.androidhive.firebaseauthapp.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import info.androidhive.firebaseauthapp.FastRecordsActivity;
import info.androidhive.firebaseauthapp.MainActivity;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.SQLite.PersonalInformation;

public class PersonalProfile extends AppCompatActivity {

    PersonalInformation myDb;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = null;

    private int user_exercise_level,age;
    private String gender;
    private float height,width;

    private TextView tv_gender,tv_age,tv_height,tv_width,tv_exercise;
    String[] exercise = {"久坐","輕量活動","中度活動量","高度活動量","非常高度活動量"};

    private Button bt_setting;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalprofile);



        tv_gender = (TextView)findViewById(R.id.tv_gender);
        tv_age = (TextView)findViewById(R.id.tv_age);
        tv_height = (TextView)findViewById(R.id.tv_height);
        tv_width = (TextView)findViewById(R.id.tv_width);
        tv_exercise = (TextView)findViewById(R.id.tv_exercise);
        bt_setting = (Button)findViewById(R.id.bt_setting);

        uid = user.getUid();
        myDb = new PersonalInformation(this);
        Cursor res = myDb.getAllData();
        while (res.moveToNext()) {
            if(uid.equals(res.getString(0))){
                user_exercise_level = res.getInt(8);
                gender = res.getString(2);
                age = res.getInt(3);
                height = res.getFloat(4);
                width = res.getFloat(5);
            }
        }
        tv_gender.setText(gender);
        tv_age.setText(String.valueOf(age));
        tv_exercise.setText(exercise[user_exercise_level]);
        tv_height.setText(String.valueOf(height));
        tv_width.setText(String.valueOf(width));

        bt_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accountIntent = new Intent(PersonalProfile.this, UserEdit.class);
                startActivity(accountIntent);
                finish();
            }
        });

    }
}