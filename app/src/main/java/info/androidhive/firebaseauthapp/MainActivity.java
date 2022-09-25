package info.androidhive.firebaseauthapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    private static final String TAG = "FACELOG";
    private FirebaseAuth mAuth;
    private ImageView mFacrbookIma;
    private static final int RC_SIGN_IN = 1 ;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView title;
    private Banner banner;
    private ArrayList<Integer> images;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION   //全螢幕顯示
                        | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);   //隱藏手機虛擬按鍵HOME/BACK/LIST按鍵

        RelativeLayout rl_bg = findViewById(R.id.rl_bg);
        title = findViewById(R.id.title);
        TextView button2 = findViewById(R.id.button2);
        mFacrbookIma = findViewById(R.id.facebookIma);
        ImageView mGoogleIma = findViewById(R.id.googleIma);
        banner = findViewById(R.id.banner);
        button = findViewById(R.id.button);

        banner.setVisibility(View.GONE);
        button.setVisibility(View.GONE);

        Calendar calendar= Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if(hour>=6 && hour<18){
            rl_bg.setBackgroundResource(R.drawable.morning_bg);
            title.setText("別一減肥就怕會失敗  \n  許多奇蹟我們相信才會存在。");
        }else{
            rl_bg.setBackgroundResource(R.drawable.night_bg);
            title.setText("證明自我的潛質：\n斷食能完成，什麼事完成不了？");
        }

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            if(firebaseAuth.getCurrentUser() != null){
                startActivity(new Intent(MainActivity.this, HeyYou.class));
            }
        };

        // 配置Google登錄
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleIma.setOnClickListener(v -> signIn());

        // 初始化Facebook登錄按鈕
        mCallbackManager = CallbackManager.Factory.create();

        mFacrbookIma.setOnClickListener(v -> {

            mFacrbookIma.setEnabled(false);

            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "facebook：成功" + loginResult);
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "facebook:取消");
                    Toast.makeText(MainActivity.this, "取消?", Toast.LENGTH_SHORT).show();
                }


                @Override
                public void onError(@NonNull FacebookException error) {
                    //出問題
                    Log.e(TAG, "facebook:錯誤", error);
                    // ...
                }
            });
        });

        button2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, EmailLogin.class)));
    }

    @Override
    public void onStart() {
        super.onStart();
        // 檢查用戶是否已登錄（非null）並相應地更新UI。

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI();
        }

        mAuth.addAuthStateListener(mAuthListener);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI() {
        Toast.makeText(MainActivity.this, "您已登錄", Toast.LENGTH_LONG).show();
        Intent accountIntent = new Intent(MainActivity.this, HeyYou.class);
        startActivity(accountIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 將活動結果傳遞回Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);


        // 從GoogleSignInApi.getSignInIntent（...）啟動Intent返回的結果；
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google登錄成功，已通過Firebase進行身份驗證
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google登錄失敗，請適當更新用戶界面
                Toast.makeText(this, "oopsi"+e, Toast.LENGTH_LONG).show();
                title.setText(e.toString());
                //Log.w(TAG, "Google登錄失敗", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 登錄成功，使用登錄用戶的信息更新用戶界面
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        assert user != null;
                        String email = user.getEmail();
                        String uid = user.getUid();
                        String username = user.getDisplayName();
                        String image = Objects.requireNonNull(user.getPhotoUrl()).toString();

                        DatabaseReference UsersRef;
                        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                        UsersRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    if(!dataSnapshot.hasChild("profileimage")){
                                        Log.e("show" , ""+dataSnapshot.hasChild("profileimage"));
                                        HashMap<Object,String> hashMap = new HashMap<>();
                                        hashMap.put("Email",email);
                                        hashMap.put("Uid",uid);
                                        hashMap.put("Username",username);
                                        hashMap.put("profileimage",image);

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference reference = database.getReference("Users");
                                        reference.child(uid).setValue(hashMap);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        // 如果登錄失敗，則向用戶顯示一條消息。
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(MainActivity.this, "驗證失敗"+task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                    updateUI();
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        assert user != null;
                        String email = user.getEmail();
                        String uid = user.getUid();
                        String username = user.getDisplayName();
                        String image = Objects.requireNonNull(user.getPhotoUrl()).toString();

                        DatabaseReference UsersRef;
                        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                        UsersRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    if(!dataSnapshot.hasChild("profileimage")){
                                        Log.e("show" , ""+dataSnapshot.hasChild("profileimage"));
                                        HashMap<Object,String> hashMap = new HashMap<>();
                                        hashMap.put("Email",email);
                                        hashMap.put("Uid",uid);
                                        hashMap.put("Username",username);
                                        hashMap.put("profileimage",image);

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference reference = database.getReference("Users");
                                        reference.child(uid).setValue(hashMap);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        // 如果登錄失敗，則向用戶顯示一條消息。
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(MainActivity.this, "驗證失敗Authentication failed."+task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                    mFacrbookIma.setEnabled(true);
                    updateUI();
                });
    }

    private void initView() {
        //設置樣式,默認為:banner.NOT_INDICATOR(不顯示指示器和標題)
        //可選樣式如下:
        //1. banner.CIRCLE_INDICATOR 顯示圓形指示器
        //2. banner.NUM_INDICATOR 顯示數字指示器
        //3. banner.NUM_INDICATOR_TITLE 顯示數字指示器和標題
        //4. banner.CIRCLE_INDICATOR_TITLE 顯示圓形指示器和標題
        //設置banner樣式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //設置圖片加載器
        banner.setImageLoader(new GlideImageLoader());
        //設置輪播樣式（沒有標題默認為右邊,有標題時默認左邊）
        //可選樣式:
        //banner.LEFT 指示器居左
        //banner.CENTER 指示器居中
        //banner.RIGHT 指示器居右
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //設置是否允許手動滑動輪播圖
        banner.setViewPagerIsScroll(true);
        //設置是否自動輪播（不設置則默認自動）
        banner.isAutoPlay(false);
        //設置輪播圖片間隔時間（不設置默認為2000）
        //banner.setDelayTime(1500);
        //設置圖片資源:可選圖片網址/資源文件，默認用Glide加載,也可自定義圖片的加載框架
        //所有設置參數方法都放在此方法之前執行
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setImages(images)
                .setOnBannerListener(position -> {
                    //Toast.makeText(MainActivity.this, "點擊" + (position + 1) + "張輪播圖", Toast.LENGTH_SHORT).show();
                })
                .start();

        button.setOnClickListener(v -> {
            banner.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        });
    }

    private void initData() {
        //設置圖片資源:url或本地資源
        images = new ArrayList<>();
        images.add(R.drawable.hello1);
        images.add(R.drawable.hello2);
        images.add(R.drawable.hello3);
        images.add(R.drawable.hello4);
    }

    /**
     * 網絡加載圖片
     * 使用了Glide圖片加載框架
     */
    public static class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context)
                    .load(path)
                    .into(imageView);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        String tutorialKey = "SOME_KEY";
        boolean firstTime = getPreferences(MODE_PRIVATE).getBoolean(tutorialKey, true);
        if (firstTime) {
            banner.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            initData();
            initView();
            getPreferences(MODE_PRIVATE).edit().putBoolean(tutorialKey, false).apply();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
