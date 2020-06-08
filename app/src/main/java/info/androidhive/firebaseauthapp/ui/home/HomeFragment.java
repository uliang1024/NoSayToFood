package info.androidhive.firebaseauthapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import javax.annotation.Nullable;

import info.androidhive.firebaseauthapp.R;

/**
 * Created by Belal on 1/23/2018.
 */

public class HomeFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragment_home = inflater.inflate(R.layout.fragment_home, container, false);
        //INIT VIEWS
        init(fragment_home);
        SectionsPagerAdapter sectionsPagerAdapter;
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getChildFragmentManager());


        //INIT VIEWS
        init(fragment_home);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
        return fragment_home;
    }

    private void init(View v) {
        viewPager = v.findViewById(R.id.view_pager);
        tabs = v.findViewById(R.id.tabs);
    }

}