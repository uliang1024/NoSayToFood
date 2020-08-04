




package info.androidhive.firebaseauthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import info.androidhive.firebaseauthapp.util.SampleCoverVideo;

public class PostingActivity extends AppCompatActivity {
    private List<Uri> selectedUriList;
    private Uri selectedUri;
    private ImageView img_user,media_image_plus,media_video_plus,img_remove_video,img_remove_image;
    private TextView tv_user;
    private EditText et_content;
    private GridLayout selected_photos_container;
    private Toolbar toolbar_posting;
    private RequestManager requestManager;
    private static final int SELECT_VIDEO = 1;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private SampleCoverVideo sampleCoverVideo;
    GSYVideoOptionBuilder gsyVideoOptionBuilder;
    View imageAddView,videoAddView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);


        img_user = findViewById(R.id.img_user);
        tv_user = findViewById(R.id.tv_user);
        et_content = findViewById(R.id.et_content);
        selected_photos_container = findViewById(R.id.selected_photos_container);
        toolbar_posting = findViewById(R.id.toolbar_posting);
        //加入相片的按鈕
        imageAddView = LayoutInflater.from(this).inflate(R.layout.image_item_add, null);
        media_image_plus = imageAddView.findViewById(R.id.media_image_plus);
        //加入影片的按鈕
        videoAddView = LayoutInflater.from(this).inflate(R.layout.video_item_add, null);
        media_video_plus = videoAddView.findViewById(R.id.media_video_plus);
        //相片的item

        //firebase
        storageReference = FirebaseStorage.getInstance().getReference("posted_video");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        //設置ActionBar
        setUpActionBar(toolbar_posting);

        //得知螢幕的寬度以計算出一排可以放多少個相片
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        // 屏幕寬度（像素）
        int tot_widthPixels = metric.widthPixels;

        //將100dp 轉化為像素單位
        int widthPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        //40dp的padding空間
        int paddingSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        //計算得知螢幕寬度可容下4張相片,保險起見先預留一張圖片的空間當緩衝
        Log.e("ted", "tot+width"+(tot_widthPixels));
        Log.e("ted", "width"+widthPixel);
        Log.e("ted", "width"+paddingSpace);
        int count = (tot_widthPixels-paddingSpace)/widthPixel;
        Log.e("ted", "item available "+count);

        //設置一行gridLayout可放3張相片
        selected_photos_container.setColumnCount(count);

        //"增加圖片"按鈕================================
        requestManager = Glide.with(this);
        media_image_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //處理增加相片的方法
                setUpPhotoActivity();
            }
        });
        //"增加影片"按鈕================================
        media_video_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("video/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, SELECT_VIDEO);
            }
        });


        selected_photos_container.addView(imageAddView);
        selected_photos_container.addView(videoAddView);
    }

    @ Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                selectedUri = data.getData();
                try {
                    if (selectedUri == null) {
                        Log.e("TED","selected video path = null!");
                        finish();
                    } else {

                        //Uri uri = Uri.fromFile(new File(selectedVideoPath));
                        Log.e("TED","selected video path ="+selectedUri);
                        loadVideo();
                        //UploadVideo();
                    }
                } catch (Exception e) {
                    //#debug
                    e.printStackTrace();
                }
            }
        }

    }
    //取得檔案格式
    private String getVideoExt(Uri uri){
        ContentResolver contentResolver= getContentResolver();
        MimeTypeMap mimeTypeMap =   MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void loadVideo(){
        View loadVideo = LayoutInflater.from(this).inflate(R.layout.video_item, null);
        img_remove_video = loadVideo.findViewById(R.id.img_remove_video);
        sampleCoverVideo = loadVideo.findViewById(R.id.video_player);
        gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
        gsyVideoOptionBuilder
                .setIsTouchWiget(false)
                .setUrl(selectedUri.toString())
                .setCacheWithPlay(true)
                .setRotateViewAuto(true)
                .setRotateWithSystem(true)
                .setLockLand(false)
                .setShowFullAnimation(true)
                .setNeedLockFull(false)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        if (!sampleCoverVideo.isIfCurrentIsFullscreen()) {
                            //静音
                            GSYVideoManager.instance().setNeedMute(false);
                        }

                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        //全屏不静音
                        GSYVideoManager.instance().setNeedMute(false);
                    }

                    @Override
                    public void onEnterFullscreen(String url, Object... objects) {
                        super.onEnterFullscreen(url, objects);
                        GSYVideoManager.instance().setNeedMute(false);
                        sampleCoverVideo.getCurrentPlayer().getTitleTextView().setText((String)objects[0]);
                    }
                }).build(sampleCoverVideo);
        //设置全屏按键功能
        sampleCoverVideo.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolveFullBtn(sampleCoverVideo);
            }
        });
        //移除 "增加影片" "增加相片"
        selected_photos_container.removeView(imageAddView);
        selected_photos_container.removeView(videoAddView);
        selected_photos_container.addView(loadVideo);

        img_remove_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新加入 "增加影片" "增加相片"
                selected_photos_container.removeView(loadVideo);
                selected_photos_container.addView(imageAddView);
                selected_photos_container.addView(videoAddView);
                //將 videourl 重置
                selectedUri = null;
            }
        });

    }
    private void UploadVideo(){
        if(selectedUri!= null){
            StorageReference reference = storageReference.child(firebaseAuth.getCurrentUser().getUid()).child(System.currentTimeMillis()+"."+getVideoExt(selectedUri));
            reference.putFile(selectedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.e("TED","your url is : "+url);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("TED","upload failed : "+e);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostingActivity.this, "upload "+selectedUri.toString()+"fail", Toast.LENGTH_SHORT).show();
                    Log.e("TED","upload failed : "+e);
                }
            });

        }
    }

    ////處理增加相片的方法
    private void setUpPhotoActivity() {

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

                TedBottomPicker.with(PostingActivity.this)
                        .setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                        //.setPeekHeight(1500)
                        .showTitle(false)
                        .setCompleteButtonText("完成")
                        .setEmptySelectionText("還沒選擇任何圖片誒")
                        .setSelectedUriList(selectedUriList)
                        .showMultiImage(uriList -> {
                            //將我們所選的uri給予selectedUriList
                            selectedUriList = uriList;
                            //將uri串列展示在螢幕上的方法
                            //此時已經按下Done按鈕離開image picker
                            showUriList(uriList);
                        });
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(PostingActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        checkPermission(permissionlistener);
    }
    private void checkPermission(PermissionListener permissionlistener) {
        TedPermission.with(PostingActivity.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    //此時已經按下Done按鈕離開image picker
    private void showUriList(List<Uri> uriList) {
        //先將所有的view移除
        selected_photos_container.removeAllViews();
        selected_photos_container.setVisibility(View.VISIBLE);
        //將uri串列一一取出
        for (Uri uri : uriList) {
            //初始化容器
            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            ImageView thumbnail = imageHolder.findViewById(R.id.media_image);
            //將uri裝入容器
            requestManager
                    .load(uri.toString())
                    .apply(new RequestOptions().centerCrop())
                    .into(thumbnail);
            //將容器添加到gridLayout
            selected_photos_container.addView(imageHolder);
        }

        media_image_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPhotoActivity();
            }
        });

        //再將"增加圖片"按鈕添加到gridLayout
        selected_photos_container.addView(imageAddView);

        //如果沒有相片
        Log.e("ted", "total size: "+uriList.size());
        if(uriList.size()==0){
            //再將"增加影片"按鈕添加到gridLayout
            selected_photos_container.addView(videoAddView);
        }
    }
    private void setUpActionBar(Toolbar toolbar_posting) {
        setSupportActionBar(toolbar_posting);
        getSupportActionBar().setTitle("post something");
        toolbar_posting.setNavigationIcon(R.drawable.ic_back);
        toolbar_posting.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void resolveFullBtn(final StandardGSYVideoPlayer standardGSYVideoPlayer) {
        standardGSYVideoPlayer.startWindowFullscreen(this, true, true);
    }

}