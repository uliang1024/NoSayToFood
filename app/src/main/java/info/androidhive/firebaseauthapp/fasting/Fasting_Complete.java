package info.androidhive.firebaseauthapp.fasting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import info.androidhive.firebaseauthapp.HomeActivity;
import info.androidhive.firebaseauthapp.R;

public class Fasting_Complete extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fasting__complete);

        VideoView videoView = this.findViewById(R.id.videoView);
        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);

        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.testmovie));

        videoView.setMediaController(null);
        videoView.requestFocus();
        videoView.start();

        Button back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            startActivity(new Intent(Fasting_Complete.this, HomeActivity.class));
            finish();
        });
    }

}
