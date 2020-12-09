package info.androidhive.firebaseauthapp.fasting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import info.androidhive.firebaseauthapp.R;

public class FirstFasting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_fasting);

        LinearLayout plan1 = findViewById(R.id.plan1);
        LinearLayout plan2 = findViewById(R.id.plan2);
        LinearLayout plan3 = findViewById(R.id.plan3);
        LinearLayout plan4 = findViewById(R.id.plan4);

        plan1.setOnClickListener(view -> {
            startActivity(new Intent(FirstFasting.this, FastingPlan1.class));
            finish();
        });
        plan2.setOnClickListener(view -> {
            startActivity(new Intent(FirstFasting.this, FastingPlan2.class));
            finish();
        });
        plan3.setOnClickListener(view -> {
            startActivity(new Intent(FirstFasting.this, FastingPlan3.class));
            finish();
        });
        plan4.setOnClickListener(view -> {
            startActivity(new Intent(FirstFasting.this, FastingPlan4.class));
            finish();
        });

    }
}
