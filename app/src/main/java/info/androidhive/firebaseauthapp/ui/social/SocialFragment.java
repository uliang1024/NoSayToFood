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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import info.androidhive.firebaseauthapp.MainActivity;
import info.androidhive.firebaseauthapp.PostingPageActivity;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.adapter.PicturePostAdapter;
import info.androidhive.firebaseauthapp.models.Item;
import info.androidhive.firebaseauthapp.models.PicturePost;
import info.androidhive.firebaseauthapp.models.TextPost;
import info.androidhive.firebaseauthapp.models.VideoPost;
import info.androidhive.firebaseauthapp.ui.home.SectionsPagerAdapter;
import info.androidhive.firebaseauthapp.util.ScrollCalculatorHelper;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Belal on 1/23/2018.
 */

public class SocialFragment extends Fragment{
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_social = inflater.inflate(R.layout.fragment_social, container, false);

        //INIT VIEWS
        init(fragment_social);
        SocialSectionPagerAdapter sectionsPagerAdapter;
        sectionsPagerAdapter = new SocialSectionPagerAdapter(this, getChildFragmentManager());

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        return fragment_social;
    }



    private void init(View v) {
        viewPager = v.findViewById(R.id.social_view_pager);
        tabLayout = v.findViewById(R.id.tab_layout);
        toolbar = v.findViewById(R.id.tool_bar);
    }


}