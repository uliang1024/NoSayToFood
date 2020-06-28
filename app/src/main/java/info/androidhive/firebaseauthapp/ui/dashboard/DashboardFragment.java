package info.androidhive.firebaseauthapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import javax.annotation.Nullable;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.BodyInformation.Weight_scale;

/**
 * Created by Belal on 1/23/2018.
 */

public class DashboardFragment extends Fragment {
    private LinearLayout show2,show;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_dashboard = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //INIT VIEWS
        init(fragment_dashboard);
        show2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accountIntent = new Intent(DashboardFragment.super.getContext(), Weight_scale.class);
                startActivity(accountIntent);
            }
        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accountIntent = new Intent(DashboardFragment.super.getContext(), Food_Record.class);
                startActivity(accountIntent);
            }
        });
        return fragment_dashboard;
    }
    private void init(View v) {
        show2 = (LinearLayout)v.findViewById(R.id.show2);
        show = (LinearLayout)v.findViewById(R.id.show);
    }
}