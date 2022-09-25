package info.androidhive.firebaseauthapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import info.androidhive.firebaseauthapp.adapter.ImageSwipePagerAdapter;
//import ua.zabelnikov.swipelayout.layout.listener.OnLayoutSwipedListener;
//public class ImageViewPagerActivity extends AppCompatActivity implements OnLayoutSwipedListener {
public class ImageViewPagerActivity extends AppCompatActivity {

//    private ImageSwipePagerAdapter imageSwipePagerAdapter;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_pager);


        viewPager = findViewById(R.id.viewPager);
        Intent i = getIntent();
//        ArrayList<String> imageList = i.getStringArrayListExtra("images");
        int position = i.getIntExtra("position",0);
//        setUpPagerAdapter(imageList);

//        viewPager.setAdapter(imageSwipePagerAdapter);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

//    //在此接收ArrayList<String>相片
//    private void setUpPagerAdapter(ArrayList<String> imageList) {
//
//        Log.e("imageList","run success");
//        Log.e("imageList",imageList.toString());
//
//        imageSwipePagerAdapter = new ImageSwipePagerAdapter(this,imageList);
//        imageSwipePagerAdapter.setOnLayoutSwipedListener(this);
//
//    }
//
//    @Override
//    public void onLayoutSwiped() {
//        finish();
//        this.overridePendingTransition(R.anim.activity_fade_in,R.anim.activity_fade_out);
//    }
}