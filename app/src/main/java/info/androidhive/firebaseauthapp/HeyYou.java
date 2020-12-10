package info.androidhive.firebaseauthapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import info.androidhive.firebaseauthapp.SQLite.PersonalInformation;
import info.androidhive.firebaseauthapp.first.HelloUser;

public class HeyYou extends AppCompatActivity {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference mDatabase = database. getReference ();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    PersonalInformation myDb;
    private final ArrayList<String> user_id = new ArrayList<>();
    private int find=0;
    FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    FirebaseUser firebaseUser =firebaseAuth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hey_you);

        if(firebaseUser != null && firebaseUser.isEmailVerified()){
            startActivity(new Intent(HeyYou.this, MainActivity.class));
        }

         String uid = user.getUid();
        mDatabase.child("Users").child(uid).child("Uid").setValue(uid);

        myDb = new PersonalInformation(this);
        Cursor res = myDb.getAllData();
        while (res.moveToNext()) {
            user_id.add(res.getString(0));
        }
        for(int i =0;i<user_id.size();i++){
            if(uid.equals(user_id.get(i))){
                find = 1;
                Intent intent = new Intent(HeyYou.this, HomeActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
        if(find==0){
            Intent intent = new Intent(HeyYou.this, HelloUser.class);
            startActivity(intent);
            finish();
        }
    }
}
