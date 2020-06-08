package info.androidhive.firebaseauthapp.ui.notifications;

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

public class NotificationsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_notifications = inflater.inflate(R.layout.fragment_notifications, container, false);
        //INIT VIEWS
        init(fragment_notifications);

        return fragment_notifications;
    }
    private void init(View v) {

    }
}