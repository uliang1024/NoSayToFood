package info.androidhive.firebaseauthapp.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import javax.annotation.Nullable;

import info.androidhive.firebaseauthapp.FitnessClassActivity;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.adapter.ClassAdapter;
import info.androidhive.firebaseauthapp.classModels.FitClass;

/**
 * Created by Belal on 1/23/2018.
 */

public class NotificationsFragment extends Fragment {
    RecyclerView gridView;
    private DatabaseReference myRef;
    String[] numbers = {"在家輕鬆動","啞鈴鍊肌","重量訓練","有氧瑜珈","暖身操"};
    int [] pics ={R.drawable.exercise,R.drawable.gym,R.drawable.gym2,R.drawable.healthy,R.drawable.workout};
    ArrayList<FitClass> classes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_notifications = inflater.inflate(R.layout.fragment_notifications, container, false);
        Context context = fragment_notifications.getContext();
        //INIT VIEWS
        init(fragment_notifications);

        myRef.child("fitness").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearAll();
                for(DataSnapshot fitSnapShop:dataSnapshot.getChildren()){
                    if(fitSnapShop.hasChild("classImage")){
                        FitClass fitClass = fitSnapShop.getValue(FitClass.class);
                        Log.e("className",""+fitClass.getClassName());
                        Log.e("classImage",""+fitClass.getClassImage());
                        classes.add(fitClass);

                    }else{
                        Log.e("classImage","no such class");
                    }
                    ClassAdapter adapter = new ClassAdapter(context,classes);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(context,2, GridLayoutManager.VERTICAL,false);
                    gridView.setLayoutManager(gridLayoutManager);
                    gridView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        return fragment_notifications;
    }

    public void ClearAll(){
        if (classes != null){
            classes.clear();
        }
        classes = new ArrayList<>();
    }
    private void init(View v) {
        myRef = FirebaseDatabase.getInstance().getReference();
        gridView = v.findViewById(R.id.grid_view);
    }



}