package info.androidhive.firebaseauthapp.food;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.ui.home.Frag2;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScreenSlidePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
import androidx.fragment.app.Fragment;
public class ScreenSlidePageFragment6 extends Fragment {

    private TextView goodbye;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_screen_slide_page6, container, false);
        init(view);

        goodbye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScreenSlidePageFragment6.super.getContext(), foodClassification.class));
            }
        });

        return view;
    }

    private void init(View view) {
        goodbye = (TextView)view.findViewById(R.id.goodbye);
    }
}
