package info.androidhive.firebaseauthapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailEt,mPasswordEt;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        Button mLoginBtn = findViewById(R.id.loginBtn);
        TextView mHaveAccountTv = findViewById(R.id.have_accountTv);
        TextView mRecoverPassTv = findViewById(R.id.recoverPassTv);

        mLoginBtn.setOnClickListener(v -> {
            String email = mEmailEt.getText().toString();
            String passw = mPasswordEt.getText().toString();
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                mEmailEt.setError("不正確的電子郵件");
                mEmailEt.setFocusable(true);
            }else {
                loginUser(email,passw);
            }
        });
        mHaveAccountTv.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            finish();
        });

        mRecoverPassTv.setOnClickListener(v -> showRecoverPasswordDialog());

        progressDialog = new ProgressDialog(this);

    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("找回密碼");

        LinearLayout linearLayout = new LinearLayout(this);

        EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        builder.setPositiveButton("送出", (dialog, which) -> {
            String email = emailEt.getText().toString().trim();
            beginRecovery(email);
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void beginRecovery(String email) {
        progressDialog.setMessage("發送郵件Sending email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this,"郵件已發送Email sent",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(LoginActivity.this,"失敗Failed...",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                });
    }

    private void loginUser(String email, String passw) {
        progressDialog.setMessage("正在登入Logging In...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, passw)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        if(user.isEmailVerified()){
                            startActivity(new Intent(LoginActivity.this,HeyYou.class));
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "您未驗證電子信箱，請先驗證。",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "驗證失敗Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}