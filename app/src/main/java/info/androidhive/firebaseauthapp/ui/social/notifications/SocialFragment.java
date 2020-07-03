package info.androidhive.firebaseauthapp.ui.social.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import javax.annotation.Nullable;

import info.androidhive.firebaseauthapp.R;

/**
 * Created by Belal on 1/23/2018.
 */

public class SocialFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_social = inflater.inflate(R.layout.fragment_social, container, false);
        //INIT VIEWS
        init(fragment_social);

        return fragment_social;
    }
    private void init(View v) {

    }
}