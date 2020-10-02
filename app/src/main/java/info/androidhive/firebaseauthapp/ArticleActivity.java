package info.androidhive.firebaseauthapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.htmlTextViewUtil.HtmlUtils;

public class ArticleActivity extends AppCompatActivity {
    float font_size = 18f;
    private TextView textView;
    private Toolbar article_toolbar;
    private static String HTML = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        textView = (TextView) findViewById(R.id.textview);
        article_toolbar = findViewById(R.id.article_toolbar);

        setSupportActionBar(article_toolbar);

        Bundle bundle = getIntent().getExtras();

        HTML = bundle.getString("content");
        Log.e("content get",""+HTML);
        textView.setText(HtmlUtils.getHtml(getApplicationContext(),textView,HTML));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.text_size,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.fonts_up:

                font_size+=2;
                textView.setTextSize(font_size);
                break;
            case R.id.fonts_down:
                font_size-=2;
                textView.setTextSize(font_size);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}