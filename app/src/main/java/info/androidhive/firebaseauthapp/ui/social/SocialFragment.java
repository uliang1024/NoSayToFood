package info.androidhive.firebaseauthapp.ui.social;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


import info.androidhive.firebaseauthapp.R;


import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Belal on 1/23/2018.
 */

public class SocialFragment extends Fragment{

    FloatingActionButton fab_main,fab_post,fab_picture,fab_article;
    Float translateYAxis = 100f;
    Boolean menuOpen = false;
    OvershootInterpolator interpolator = new OvershootInterpolator();

    Context mContext;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_social = inflater.inflate(R.layout.fragment_social, container, false);

        //INIT VIEWS
        init(fragment_social);

        //showFab
        //shoeMenu();

        SocialSectionPagerAdapter sectionsPagerAdapter;
        sectionsPagerAdapter = new SocialSectionPagerAdapter(this, getChildFragmentManager());

        ((FragmentActivity) getActivity()).setActionBar(toolbar);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                switch (position){
                    case 0:
                        Log.e("im on page",""+position);
                        break;
                    case 1:
                        Log.e("im on another page",""+position);
                        break;
                }
            }
            //當頁面滑到完全展示在螢幕後 會觸發onPageSelected
            @Override
            public void onPageSelected(int position) {
                Log.e("selected",""+position);
//                switch (position){
//                    case 0:
//                        fab_main.show();
//                        fab_article.hide();
//                        break;
//                    case 1:
//                        closeMenu();
//                        fab_main.hide();
//                        fab_article.show();
//                        break;
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return fragment_social;
    }

//    private void shoeMenu() {
//
//        fab_post.setAlpha(0f);
//        fab_picture.setAlpha(0f);
//
//
//        fab_picture.setTranslationY(translateYAxis);
//        fab_post.setTranslationY(translateYAxis);
//
//        fab_main.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(menuOpen){
//                    closeMenu();
//                }else{
//                    openMenu();
//                }
//            }
//        });
//
//        fab_post.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext, "fab_post clicked" , Toast.LENGTH_SHORT).show();
//
//
//            }
//        });
//        fab_picture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext, "fab_picture clicked" , Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void openMenu() {
//        menuOpen=!menuOpen;
//        fab_post.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(500).start();
//        fab_picture.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(500).start();
//    }
//
//    private void closeMenu() {
//        menuOpen=!menuOpen;
//        fab_post.animate().translationY(translateYAxis).alpha(0f).setInterpolator(interpolator).setDuration(500).start();
//        fab_picture.animate().translationY(translateYAxis).alpha(0f).setInterpolator(interpolator).setDuration(500).start();
//    }

//    Intent intent = new Intent(mContext, PostingActivity.class);
//    startActivity(intent);

    private void init(View v) {
        mContext = v.getContext();
//        fab_article = v.findViewById(R.id.fab_article);
//        fab_main = v.findViewById(R.id.fab_main);
//        fab_post = v.findViewById(R.id.fab_post);
//        fab_picture = v.findViewById(R.id.fab_picture);
        viewPager = v.findViewById(R.id.social_view_pager);
        tabLayout = v.findViewById(R.id.tab_layout);
        toolbar = v.findViewById(R.id.tool_bar);
    }


}