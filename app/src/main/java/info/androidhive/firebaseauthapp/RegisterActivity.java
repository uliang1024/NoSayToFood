package info.androidhive.firebaseauthapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText mNameEt,mEmailEt,mPasswordEt,mPassword2Et;

    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNameEt = findViewById(R.id.nameEt);
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mPassword2Et = findViewById(R.id.password2Et);
        Button mRegisterBtn = findViewById(R.id.registerBtn);
        TextView mHaveAccountTv = findViewById(R.id.have_accountTv);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("註冊用戶...");


        mRegisterBtn.setOnClickListener(v -> {
            String name = mNameEt.getText().toString().trim();
            String email = mEmailEt.getText().toString().trim();
            String password = mPasswordEt.getText().toString().trim();
            String password2 = mPassword2Et.getText().toString().trim();

            if(name.equals("")){
                mNameEt.setError("請輸入名稱");
                mNameEt.setFocusable(true);
            }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                mEmailEt.setError("請輸入正確的墊子郵件");
                mEmailEt.setFocusable(true);
            }else if(password.length()<6){
                mPasswordEt.setError("密碼長度至少6個字符");
                mPasswordEt.setFocusable(true);
            }else if(!password2.equals(password)){
                mPassword2Et.setError("密碼不一致,請重新輸入");
                mPassword2Et.setFocusable(true);
            }else{
                registerUser(name, email, password);
            }
        });
        mHaveAccountTv.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            finish();
        });
    }

    private void registerUser(String name,String email, String password) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        FirebaseUser user = mAuth.getCurrentUser();

                        String email1 = user.getEmail();
                        String uid = user.getUid();

                        HashMap<Object,String> hashMap = new HashMap<>();
                        hashMap.put("Email", email1);
                        hashMap.put("Uid",uid);
                        hashMap.put("Username",name);
                        hashMap.put("profileimage","");

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("Users");
                        reference.child(uid).setValue(hashMap);
                        user.sendEmailVerification()
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this,"註冊成功,請驗證你的信箱\n",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(RegisterActivity.this, task1.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }

                                });

                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,"驗證失敗Authentication failed",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}