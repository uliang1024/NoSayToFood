package info.androidhive.firebaseauthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import info.androidhive.firebaseauthapp.adapter.ClassAdapter;
import info.androidhive.firebaseauthapp.classModels.FitClass;

public class FitnessActivity extends AppCompatActivity {
    RecyclerView gridView;
    private DatabaseReference myRef;
    ArrayList<FitClass> classes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness);

        myRef = FirebaseDatabase.getInstance().getReference();
        gridView = findViewById(R.id.grid_view);

        ClassAdapter adapter = new ClassAdapter(this,classes);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false);
        gridView.setLayoutManager(gridLayoutManager);
        gridView.setAdapter(adapter);


        myRef.child("fitness").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                classes.clear();
                for(DataSnapshot fitSnapShop:dataSnapshot.getChildren()){
                    if(fitSnapShop.hasChild("classImage")){
                        FitClass fitClass = fitSnapShop.getValue(FitClass.class);
                        Log.e("className",""+fitClass.getClassName());
                        Log.e("classImage",""+fitClass.getClassImage());
                        classes.add(fitClass);

                    }else{
                        Log.e("classImage","no such class");
                    }

                }
                adapter.isShimmer = false;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}