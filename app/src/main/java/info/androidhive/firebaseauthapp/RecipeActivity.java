package info.androidhive.firebaseauthapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import java.util.ArrayList;

import info.androidhive.firebaseauthapp.adapter.RecipeStepPagerAdapter;
import info.androidhive.firebaseauthapp.classModels.ClassdataEntity;
import info.androidhive.firebaseauthapp.htmlTextViewUtil.HtmlUtils;
import info.androidhive.firebaseauthapp.models.StepsEntity;

public class RecipeActivity extends AppCompatActivity {
    float font_size = 18f;
    private TextView textView;
    private Toolbar recipe_toolbar;
    private static String HTML = "";
    ArrayList<StepsEntity> steps;
    HorizontalInfiniteCycleViewPager recipe_view_pager;

    private TextView tv_title,tv_content,tv_ingredient;
    private ImageView img_recipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String title = bundle.getString("title");
        String content = bundle.getString("content");
        String image = bundle.getString("image");
        String time = bundle.getString("time");
        String ingredient = bundle.getString("ingredient");

        tv_content = findViewById(R.id.tv_content);
        tv_title = findViewById(R.id.tv_title);
        tv_ingredient = findViewById(R.id.tv_ingredient);
        img_recipe = findViewById(R.id.img_recipe);

        Glide.with(this)
            .load(image).into(img_recipe);
        tv_content.setText(content);
        tv_ingredient.setText(ingredient);
        tv_title.setText(title);
//        Intent intent = getIntent();
//        steps= intent.getExtras().getParcelableArrayList("content");
//        recipe_view_pager = findViewById(R.id.recipe_view_pager);
//
//        Log.e("get steps",""+steps.get(0).getDescription()+""+steps.get(0).getStepimage()+""+steps.get(0).getStepname());
//
//        RecipeStepPagerAdapter adapter = new RecipeStepPagerAdapter(this,steps);
//        recipe_view_pager.setAdapter(adapter);


//        textView = (TextView) findViewById(R.id.textview_recipe);
//        recipe_toolbar = findViewById(R.id.recipe_toolbar);
//
//        setSupportActionBar(recipe_toolbar);
//
//        Bundle bundle = getIntent().getExtras();
//
//        HTML = bundle.getString("content");
//        Log.e("content get",""+HTML);
//        textView.setText(HtmlUtils.getHtml(getApplicationContext(),textView,HTML));

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.text_size,menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//
//            case R.id.fonts_up:
//
//                font_size+=2;
//                textView.setTextSize(font_size);
//                break;
//            case R.id.fonts_down:
//                font_size-=2;
//                textView.setTextSize(font_size);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}