package info.androidhive.firebaseauthapp.fasting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.first.HelloUser;

public class FirstFasting extends AppCompatActivity {

    private LinearLayout plan1,plan2,plan3,plan4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_fasting);

        plan1 = (LinearLayout)findViewById(R.id.plan1);
        plan2 = (LinearLayout)findViewById(R.id.plan2);
        plan3 = (LinearLayout)findViewById(R.id.plan3);
        plan4 = (LinearLayout)findViewById(R.id.plan4);

        plan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstFasting.this, FastingPlan1.class));
                finish();
            }
        });
        plan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstFasting.this, FastingPlan1.class));
                finish();
            }
        });
        plan3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstFasting.this, FastingPlan1.class));
                finish();
            }
        });
        plan4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstFasting.this, FastingPlan1.class));
                finish();
            }
        });

    }
}
