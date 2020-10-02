package info.androidhive.firebaseauthapp.ui.social;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import info.androidhive.firebaseauthapp.ArticleActivity;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.adapter.ArticleAdapter;
import info.androidhive.firebaseauthapp.models.Article;
import info.androidhive.firebaseauthapp.models.Item;
import info.androidhive.firebaseauthapp.models.PicturePost;
import info.androidhive.firebaseauthapp.models.TextPost;
import info.androidhive.firebaseauthapp.models.VideoPost;

import static info.androidhive.firebaseauthapp.util.Constants.POST_TYPE;

public class Frag_article extends Fragment implements ArticleAdapter.ArticleClickedListener {

    private LinearLayoutManager layoutManager;
    private RecyclerView recycler_article;
    private DatabaseReference myRef;
    List<Article> articles = new ArrayList<>();
    private Context mContext;
    private ArticleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_article = inflater.inflate(R.layout.social_frag_article, container, false);
        recycler_article = fragment_article.findViewById(R.id.recycler_article);
        myRef = FirebaseDatabase.getInstance().getReference();
        mContext = getContext();

        addData(new DataListener() {
            @Override
            public void onReceiveData(boolean dataLoadComplete) {
                adapter.notifyDataSetChanged();
            }
        });
        layoutManager = new LinearLayoutManager(mContext);
        recycler_article.setLayoutManager(layoutManager);
        adapter = new ArticleAdapter(articles,mContext,this);
        recycler_article.setAdapter(adapter);
        return fragment_article;
    }

    private void addData(DataListener listener) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("article")){

                    HashMap<String,Object> loadedItems = (HashMap<String,Object>) dataSnapshot.child("article").getValue();
                    if (loadedItems == null||loadedItems.size() == 0){
                        Log.e("no data","no data read");
                        return;
                    }
                    int loadCounter = 0;
                    articles.clear();
                    for(DataSnapshot articleSnapShot:dataSnapshot.child("article").getChildren()){
                        Article article = articleSnapShot.getValue(Article.class);
                        articles.add(article);
                        loadCounter++;
                    }

                    if (loadCounter == loadedItems.size()){
                        Log.e("load data complete",""+articles.size()+"，"+articles.toString());
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
    public void onArticleClicked(int position) {
        Log.e("clicked ","on position "+position);
        Intent intent = new Intent(mContext, ArticleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("content",articles.get(position).getContent());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    interface DataListener{
        void onReceiveData(boolean dataLoadComplete);
    }

}
