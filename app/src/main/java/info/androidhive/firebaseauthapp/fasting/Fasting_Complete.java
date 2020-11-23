package info.androidhive.firebaseauthapp.fasting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import info.androidhive.firebaseauthapp.HomeActivity;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.ui.home.Frag1;
import info.androidhive.firebaseauthapp.ui.home.HomeFragment;

public class Fasting_Complete extends AppCompatActivity {

    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fasting__complete);

        VideoView videoView = (VideoView) this.findViewById(R.id.videoView);
        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);

        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.testmovie));

        videoView.setMediaController(null);
        videoView.requestFocus();
        videoView.start();

        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Fasting_Complete.this, HomeActivity.class));
                finish();
            }
        });
    }

}
