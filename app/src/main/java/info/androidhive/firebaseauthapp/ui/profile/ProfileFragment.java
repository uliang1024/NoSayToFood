package info.androidhive.firebaseauthapp.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.annotation.Nullable;

import info.androidhive.firebaseauthapp.MainActivity;
import info.androidhive.firebaseauthapp.R;

/**
 * Created by Belal on 1/23/2018.
 */

public class ProfileFragment extends Fragment {
    private static final String KEY_USERNAME = "Username";
    private static final String KEY_EMAIL = "Email";
   // private Button mlogoutBtn;
    private FirebaseAuth mAuth;
    private TextView welconeText;
    private ImageView imageView;
    private LinearLayout ll_logout,ll_editor;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @SuppressLint("StaticFieldLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_profile = inflater.inflate(R.layout.fragment_profile, container, false);
        init(fragment_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            welconeText.setText(name);

            //建立一個AsyncTask執行緒進行圖片讀取動作，並帶入圖片連結網址路徑
            new AsyncTask<String, Void, Bitmap>()
            {
                @Override
                protected Bitmap doInBackground(String... params)
                {
                    String url = params[0];
                    return getBitmapFromURL(url);
                }

                @Override
                protected void onPostExecute(Bitmap result)
                {
                    imageView. setImageBitmap (result);
                    super.onPostExecute(result);
                }
            }.execute(String.valueOf(photoUrl));

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }

        mAuth = FirebaseAuth.getInstance();

        ll_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileFragment.super.getContext(), PersonalProfile.class));
            }
        });

        ll_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                // Google 登出
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignIn.getClient(ProfileFragment.super.getContext(), gso).signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProfileFragment.super.getContext(), "SingOut", Toast.LENGTH_LONG).show();
                        Intent accountIntent = new Intent(ProfileFragment.super.getContext(), MainActivity.class);
                        startActivity(accountIntent);
                        getActivity().finish();
                    }
                });
            }
        });

        return fragment_profile;
    }

    //讀取網路圖片，型態為Bitmap
    private static Bitmap getBitmapFromURL(String imageUrl)
    {
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            updateUI();
        }
    }

    private void updateUI() {
        Toast.makeText(ProfileFragment.super.getContext(), "You're logged out", Toast.LENGTH_LONG).show();

        Intent accountIntent = new Intent(ProfileFragment.super.getContext(), MainActivity.class);
        startActivity(accountIntent);
        getActivity().finish();
    }



    private void init(View v) {
        welconeText = (TextView)v.findViewById(R.id.welconeText);
        imageView = (ImageView)v.findViewById(R.id.imageView);
        ll_logout = (LinearLayout)v.findViewById(R.id.ll_logout);
        ll_editor = (LinearLayout)v.findViewById(R.id.ll_editor);
    }



}