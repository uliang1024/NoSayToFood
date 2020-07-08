package info.androidhive.firebaseauthapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import static info.androidhive.firebaseauthapp.ui.social.Frag_posting.POSTING_TITLE;
import static info.androidhive.firebaseauthapp.ui.social.Frag_posting.POSTING_TYPE;


public class PostingPageActivity extends AppCompatActivity {
    private TextView postingType,postingTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_page);

        postingType = findViewById(R.id.tv_posting_type);
        postingTitle = findViewById(R.id.tv_posting_title);
        Intent intent = getIntent();
        int posting_type = intent.getIntExtra(POSTING_TYPE,999);
        String posting_title = intent.getStringExtra(POSTING_TITLE);

        postingType.setText(String.valueOf(posting_type));
        postingTitle.setText(posting_title);
    }
}