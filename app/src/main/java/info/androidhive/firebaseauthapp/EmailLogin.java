package info.androidhive.firebaseauthapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EmailLogin extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        Button mRegisterBtn = findViewById(R.id.register_btn);
        Button mLoginBtn = findViewById(R.id.login_btn);

        mRegisterBtn.setOnClickListener(v -> startActivity(new Intent(EmailLogin.this, RegisterActivity.class)));

        mLoginBtn.setOnClickListener(v -> startActivity(new Intent(EmailLogin.this, LoginActivity.class)));
    }
}