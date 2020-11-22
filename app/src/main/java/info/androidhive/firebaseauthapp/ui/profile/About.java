package info.androidhive.firebaseauthapp.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.ui.dashboard.Food_Record;
import info.androidhive.firebaseauthapp.ui.dashboard.OneFoodRecord;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}