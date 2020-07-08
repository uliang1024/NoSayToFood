package info.androidhive.firebaseauthapp.ui.social;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.ui.home.Frag1;
import info.androidhive.firebaseauthapp.ui.home.Frag2;
import info.androidhive.firebaseauthapp.ui.home.HomeFragment;

public class SocialSectionPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.social_tab_text_1, R.string.social_tab_text_2};
    private final SocialFragment mContext;

    public SocialSectionPagerAdapter(SocialFragment context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new Frag_posting();
                break;
            case 1:
                fragment = new Frag_article();
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}
