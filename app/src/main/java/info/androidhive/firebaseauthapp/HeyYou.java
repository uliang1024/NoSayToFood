package info.androidhive.firebaseauthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import info.androidhive.firebaseauthapp.SQLite.PersonalInformation;
import info.androidhive.firebaseauthapp.first.HelloUser;

public class HeyYou extends AppCompatActivity {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = database. getReference ();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    PersonalInformation myDb;
    private ArrayList<String> user_id = new ArrayList<>();
    private int find=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hey_you);

         String uid = user.getUid();
         String name = user.getDisplayName();
         String email = user.getEmail();
        mDatabase.child("Users").child(uid).child("Username").setValue(name);
        mDatabase.child("Users").child(uid).child("Email").setValue(email);
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
