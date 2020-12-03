package info.androidhive.firebaseauthapp.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import info.androidhive.firebaseauthapp.PostingActivity;
import info.androidhive.firebaseauthapp.R;

public class ManagerPage extends AppCompatActivity implements FragDelete.OnDeleteFragmentListener,
        FragUpdate.OnUpdateFragmentListener,FragSearch.OnSearchFragmentListener {

    private TextView tv_username;
    private ImageView img_search,img_update,img_delete,img_post;
    private FrameLayout fragmentContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_page);

        tv_username = findViewById(R.id.tv_username);
        img_delete = findViewById(R.id.img_delete);
        img_search = findViewById(R.id.img_search);
        img_update = findViewById(R.id.img_update);
        img_post = findViewById(R.id.img_post);
        fragmentContainer = findViewById(R.id.fragment_container_manager);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String uid = bundle.getString("uid");
        String name = bundle.getString("name");
        tv_username.setText("你好:"+name);

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragSearch fragSearch = FragSearch.newInstance();
                startTransition(fragSearch,"frag_search");
            }
        });

        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragDelete fragDelete = FragDelete.newInstance();
                startTransition(fragDelete,"frag_delete");
            }
        });

        img_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragUpdate fragUpdate = FragUpdate.newInstance();
                startTransition(fragUpdate,"frag_update");
            }
        });
        img_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startTransition(Fragment fragment,String tag){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right,R.anim.enter_from_right
                ,R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container_manager,fragment,tag).commit();
    }

    @Override
    public void onFragmentDeleteTouched(String sendBackText) {
        Toast.makeText(this, ""+sendBackText, Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Override
    public void onFragmentUpdateTouched(String sendBackText) {
        Toast.makeText(this, ""+sendBackText, Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Override
    public void onFragmentSearchTouched(String sendBackText) {
        Toast.makeText(this, ""+sendBackText, Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
}