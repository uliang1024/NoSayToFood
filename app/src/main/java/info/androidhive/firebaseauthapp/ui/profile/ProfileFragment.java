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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import info.androidhive.firebaseauthapp.MainActivity;
import info.androidhive.firebaseauthapp.R;

/**
 * Created by Belal on 1/23/2018.
 */

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private TextView welconeText;
    private ImageView imageView;
    private LinearLayout ll_logout,ll_editor,ll_about,ll_share;
    private CircleImageView userface;
    String  currentUserID;

    @SuppressLint("StaticFieldLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_profile = inflater.inflate(R.layout.fragment_profile, container, false);
        init(fragment_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String fullname = dataSnapshot.child("Username").getValue().toString();
                    String image = dataSnapshot.child("profileimage").getValue().toString();
                    welconeText.setText(fullname);
                    Picasso.get().load(image).placeholder(R.drawable.com_facebook_profile_picture_blank_square).into(userface);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "該斷食了吧!");
                shareIntent.setType("text/jpeg");
                startActivity(Intent.createChooser(shareIntent, "讓更多人知道我們"));
            }
        });
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
        ll_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accountIntent = new Intent(ProfileFragment.super.getContext(), About.class);
                startActivity(accountIntent);
            }
        });
        return fragment_profile;
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
        userface = (CircleImageView)v.findViewById(R.id.userface);
        ll_about = (LinearLayout)v.findViewById(R.id.ll_about);
        ll_share = (LinearLayout)v.findViewById(R.id.ll_share);
    }



}