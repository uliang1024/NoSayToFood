




package info.androidhive.firebaseauthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import java.util.UUID;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import gun0912.tedbottompicker.TedBottomPicker;
import info.androidhive.firebaseauthapp.ImageEdit.ImageEditActivity;
import info.androidhive.firebaseauthapp.models.Comments;
import info.androidhive.firebaseauthapp.models.PicturePost;
import info.androidhive.firebaseauthapp.models.PicturePostGridImage;
import info.androidhive.firebaseauthapp.models.TextPost;
import info.androidhive.firebaseauthapp.models.VideoPost;
import info.androidhive.firebaseauthapp.ui.social.Frag_posting;
import info.androidhive.firebaseauthapp.util.SampleCoverVideo;

import static info.androidhive.firebaseauthapp.util.Constants.DESCRIPTION;
import static info.androidhive.firebaseauthapp.util.Constants.ITEM_IMAGES;
import static info.androidhive.firebaseauthapp.util.Constants.POST_TYPE;
import static info.androidhive.firebaseauthapp.util.Constants.USER_AVATAR;
import static info.androidhive.firebaseauthapp.util.Constants.USER_NAME;
import static info.androidhive.firebaseauthapp.util.Constants.VIDEO_THUMBNAIL;
import static info.androidhive.firebaseauthapp.util.Constants.VIDEO_URL;

public class PostingActivity extends AppCompatActivity {
    ArrayList<PicturePostGridImage> savedImageUrls = new ArrayList<>();
    int counter;
    private List<Uri> selectedUriList =new ArrayList<Uri>();
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
    int position = 0;

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
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        //設置ActionBar
        setUpActionBar(toolbar_posting);
        //設定edittext多行
        et_content.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        et_content.setSingleLine(false);

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
        //設定使用者姓名頭像
        Glide.with(getApplicationContext()).load(firebaseAuth.getCurrentUser().getPhotoUrl().toString()).into(img_user);
        tv_user.setText(firebaseAuth.getCurrentUser().getDisplayName());

        selected_photos_container.addView(imageAddView);
        selected_photos_container.addView(videoAddView);



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
                GSYVideoManager.releaseAllVideos();
            }
        });

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

        //再將"增加圖片"按鈕添加到gridLayout
        selected_photos_container.addView(imageAddView);

        //如果沒有相片
        Log.e("ted", "total size: "+uriList.size());
        if(uriList.size()==0){
            //再將"增加影片"按鈕添加到gridLayout
            selected_photos_container.addView(videoAddView);
        }else{
            int itemCount = selected_photos_container.getChildCount();
            for (int i= 0; i < itemCount; i++) {
                FrameLayout container = (FrameLayout) selected_photos_container.getChildAt(i);
                int finalI = i;
                container.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        Toast.makeText(PostingActivity.this, "image uri is"+ uriList.get(finalI).toString(), Toast.LENGTH_SHORT).show();
                        // your click code here
                        Intent intent = new Intent(PostingActivity.this, ImageEditActivity.class);
                        intent.putExtra("position",finalI);
                        intent.setData(uriList.get(finalI));
                        startActivityForResult(intent,2);
                    }
                });
            }
        }
    }

    @ Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.e("request is",""+requestCode);
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
            if (requestCode ==2){
                int position = data.getIntExtra("position",-1);
                String uri = data.getStringExtra("uri");
                Log.e("get result","uri:"+uri+"posittion:"+position);

                selectedUriList.set(position,Uri.parse(uri));

                reloadUriList();
            }
        }

    }

    private void reloadUriList() {
        selected_photos_container.removeAllViews();
        for (Uri uri : selectedUriList) {
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

        //再將"增加圖片"按鈕添加到gridLayout
        selected_photos_container.addView(imageAddView);
        if(selectedUriList.size()==0){
            //再將"增加影片"按鈕添加到gridLayout
            selected_photos_container.addView(videoAddView);
        }else{
            int itemCount = selected_photos_container.getChildCount();
            for (int i= 0; i < itemCount; i++) {
                FrameLayout container = (FrameLayout) selected_photos_container.getChildAt(i);
                int finalI = i;
                container.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        Toast.makeText(PostingActivity.this, "image uri is"+ selectedUriList.get(finalI).toString(), Toast.LENGTH_SHORT).show();
                        // your click code here
                        Intent intent = new Intent(PostingActivity.this, ImageEditActivity.class);
                        intent.putExtra("position",finalI);
                        intent.setData(selectedUriList.get(finalI));
                        startActivityForResult(intent,2);
                    }
                });
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_action,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        int img_count = 1;
        if (id==R.id.btn_post){
            //按下發布紐，如果selectedUriList.size = 0,selectedUri = null為 textpost

//            Toast.makeText(PostingActivity.this, "UR url list is"+selectedUriList+"\n"+"UR video list is"+selectedUri, Toast.LENGTH_SHORT).show();
            if (selectedUriList.size()==0 &&selectedUri ==null){
                //String uuid = UUID.randomUUID().toString();
                String pushId = databaseReference.push().getKey();
                TextPost t = new TextPost();

                t.setUser_name(firebaseAuth.getCurrentUser().getDisplayName());
                t.setUser_avatar(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                t.setDescription(et_content.getText().toString());
                t.setPost_type(1);
                t.setPostID(pushId);
                t.setPostTime(System.currentTimeMillis());
                t.setUser_ID(firebaseAuth.getCurrentUser().getUid());

                databaseReference.child("posting").child(pushId).setValue(t).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            startIntent();
                        }else{
                            Toast.makeText(getApplicationContext(),"something wrong",Toast.LENGTH_SHORT).show();
                        }
                    }
                });





            }else if(selectedUriList.size()!=0&&selectedUri ==null)
            //picturepost
            {
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("upload 0/"+selectedUriList.size());
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
//                StorageReference reference = storageReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("posted_images").child(System.currentTimeMillis()+"");
                for(Uri uri :selectedUriList){
                    Log.e("get downUrls ",uri.toString());
                    StorageReference reference = storageReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("posted_images").child(System.currentTimeMillis()+"");
                     reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        //只要完成了一輪loop，無論task是否成功都要++
                                        counter++;
                                        progressDialog.setMessage("upload "+counter+"/"+selectedUriList.size());
                                        if(task.isSuccessful()){

                                            String myurl = task.getResult().toString();
                                            PicturePostGridImage imageItem = new PicturePostGridImage();
                                            imageItem.setImagePath(myurl);
                                            Log.e("get downUrls ",myurl);
                                            //將我們拿到的url存到廣域的arraylist : savedImageUrls中
                                            savedImageUrls.add(imageItem);

                                        }else{
                                            //失敗了必須將檔案刪掉
                                            reference.delete();
                                            Log.e("get downUrls error","something wrong");
                                        }
                                        //如果loop次數達到selectedUriList.size()
                                        if(counter==selectedUriList.size()){
                                            //呼叫
                                            uploadImages(progressDialog);
                                        }
                                    }
                                });
                            }else{
                                //只要完成了一輪loop，無論task是否成功都要++
                                counter++;
                                Log.e("upload files error","something wrong");
                            }
                        }
                    });
                }

            }else{
                UploadVideo();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadImages(ProgressDialog progressDialog) {
        String pushId = databaseReference.push().getKey();
        PicturePost p = new PicturePost();

        p.setImages(savedImageUrls);
        p.setUser_name(firebaseAuth.getCurrentUser().getDisplayName());
        p.setUser_avatar(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
        p.setDescription(et_content.getText().toString());
        p.setPostID(pushId);
        p.setPostTime(System.currentTimeMillis());
        p.setUser_ID(firebaseAuth.getCurrentUser().getUid());
        p.setPost_type(0);
//        Comments comments = new Comments();
//        comments.setComment("hi");
//        comments.setCommentTime(System.currentTimeMillis());
//        comments.setUser_avatar(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
//        comments.setUser_Id(firebaseAuth.getCurrentUser().getUid());
//        comments.setUser_name(firebaseAuth.getCurrentUser().getDisplayName());
//
//        ArrayList<Comments> commentList = new ArrayList<>();
//        commentList.add(comments);
//
//        p.setComments(commentList);

        Log.e("upload images","upload complete");

        databaseReference.child("posting").child(pushId).setValue(p);
        progressDialog.dismiss();
        startIntent();

    }



    private void UploadVideo(){
        if(selectedUri!= null){
            StorageReference reference = storageReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("posted_video").child(System.currentTimeMillis()+"."+getVideoExt(selectedUri));
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("upload 0%");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            UploadTask uploadTask = reference.putFile(selectedUri);

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("upload "+progress+"%");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            String pushId = databaseReference.push().getKey();
                            VideoPost v = new VideoPost();
                            v.setUser_name(firebaseAuth.getCurrentUser().getDisplayName());
                            v.setUser_avatar(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                            v.setDescription(et_content.getText().toString());
                            v.setThumbnail_img("https://firebasestorage.googleapis.com/v0/b/storagetest-dfeb6.appspot.com/o/eyes%2F2.jpg?alt=media&token=254289ea-59ac-4d4c-80dd-f3720864af41");
                            v.setVideo_url(url);
                            v.setPost_type(2);
                            v.setPostID(firebaseAuth.getCurrentUser().getUid());
                            v.setPostID(pushId);
                            v.setPostTime(System.currentTimeMillis());

                            databaseReference.child("posting").child(pushId).setValue(v);
                            progressDialog.dismiss();
                            startIntent();
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

                }
            });


        }
    }

    private void resolveFullBtn(final StandardGSYVideoPlayer standardGSYVideoPlayer) {
        standardGSYVideoPlayer.startWindowFullscreen(this, true, true);
    }

    private  void startIntent(){
        //上船完成，重新導向到HomeActivity
        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
        intent.putExtra("id", 1);
        startActivity(intent);
    }

}