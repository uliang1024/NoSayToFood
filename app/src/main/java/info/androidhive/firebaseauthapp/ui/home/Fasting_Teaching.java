package info.androidhive.firebaseauthapp.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import info.androidhive.firebaseauthapp.R;

public class Fasting_Teaching extends AppCompatActivity {

    private TextView tv_leave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fasting__teaching);

        tv_leave = (TextView) findViewById(R.id.tv_leave);

        tv_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Fasting_Teaching.this, Frag1.class));
                finish();
            }
        });
    }
}