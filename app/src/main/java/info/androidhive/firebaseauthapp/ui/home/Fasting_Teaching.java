package info.androidhive.firebaseauthapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import info.androidhive.firebaseauthapp.R;

public class Fasting_Teaching extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fasting__teaching);

        TextView tv_leave = findViewById(R.id.tv_leave);

        tv_leave.setOnClickListener(v -> {
            //startActivity(new Intent(Fasting_Teaching.this, Frag1.class));
            finish();
        });
    }
}