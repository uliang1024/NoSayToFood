package info.androidhive.firebaseauthapp.ui.recipe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.RecipeActivity;
import info.androidhive.firebaseauthapp.adapter.RecipeAdapter;
import info.androidhive.firebaseauthapp.models.Recipe;

/**
 * Created by Belal on 1/23/2018.
 */

public class RecipeFragment extends Fragment implements RecipeAdapter.RecipeClickedListener {
    private LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    private DatabaseReference myRef;
    private Context mContext;
    ArrayList<Recipe> recipes = new ArrayList<>();
    private RecipeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_notifications = inflater.inflate(R.layout.fragment_notifications, container, false);
        //INIT VIEWS
        myRef = FirebaseDatabase.getInstance().getReference();
        recyclerView = fragment_notifications.findViewById(R.id.recycler_recipe);
        mContext = getContext();
        add_Data(new DataListener() {
            @Override
            public void onReceiveData(boolean dataLoadComplete) {
                //Log.e("load data complete",""+recipes.size()+"，"+recipes.toString()+"，"+recipes.get(0).getRecipename());
                adapter.isShimmer = false;
                adapter.notifyDataSetChanged();
            }
        });

        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecipeAdapter(recipes,mContext,this);
        recyclerView.setAdapter(adapter);


        return fragment_notifications;
    }

    private void add_Data(DataListener listener) {

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("recipe")){

                    HashMap<String,Object> loadedItems = (HashMap<String,Object>) dataSnapshot.child("recipe").getValue();
                    if (loadedItems == null||loadedItems.size() == 0){
                        Log.e("no data","no data read");
                        return;
                    }
                    int loadCounter = 0;
                    recipes.clear();
                    for(DataSnapshot recipeSnapShot:dataSnapshot.child("recipe").getChildren()){
                        Recipe recipe = recipeSnapShot.getValue(Recipe.class);
                        recipes.add(recipe);
                        loadCounter++;
                    }

                    if (loadCounter == loadedItems.size()){
                        //Log.e("load data complete",""+recipes.size()+"，"+recipes.toString());
                        listener.onReceiveData(true);
                    }

                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onRecipeClicked(int position) {
        Log.e("clicked ","on position "+position);
        Intent intent = new Intent(mContext, RecipeActivity.class);
        Bundle bundle = new Bundle();
        //bundle.putParcelableArrayList("content",(ArrayList<? extends Parcelable>) recipes.get(position).getSteps());
        bundle.putString("content",recipes.get(position).getContent());
        bundle.putString("image",recipes.get(position).getImage());
        bundle.putString("ingredient",recipes.get(position).getIngredient());
        bundle.putString("preview",recipes.get(position).getPreview());
        bundle.putString("time",recipes.get(position).getTime());
        bundle.putString("title",recipes.get(position).getTitle());
        intent.putExtras(bundle);
        startActivity(intent);
    }


    interface DataListener{
        void onReceiveData(boolean dataLoadComplete);
    }


}