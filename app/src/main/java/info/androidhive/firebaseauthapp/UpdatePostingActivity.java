




package info.androidhive.firebaseauthapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
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
import com.gowtham.library.utils.TrimType;
import com.gowtham.library.utils.TrimVideo;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import info.androidhive.firebaseauthapp.ImageEdit.ImageEditActivity;
import info.androidhive.firebaseauthapp.models.Item;
import info.androidhive.firebaseauthapp.models.PicturePost;
import info.androidhive.firebaseauthapp.models.PicturePostGridImage;
import info.androidhive.firebaseauthapp.models.TextPost;
import info.androidhive.firebaseauthapp.models.VideoPost;
import info.androidhive.firebaseauthapp.util.SampleCoverVideo;

public class UpdatePostingActivity extends AppCompatActivity {
    //從裝置新增，上傳到firebase上的
    ArrayList<PicturePostGridImage> savedImageUrls = new ArrayList<>();
    //從FragUpdate上讀取下來的
    ArrayList<PicturePostGridImage> images = new ArrayList<>();
    int counter;
    private List<Uri> selectedUriList =new ArrayList<Uri>();
    //從FragUpdate上讀取下來的
    private String readedUri = "";
    //從裝置新增，上傳到firebase上的
    private Uri selectedUri;
    private ImageView img_user,media_image_plus,media_video_plus,img_remove_video,img_remove_image;
    private TextView tv_user;
    private EditText et_content;
    private GridLayout selected_photos_container;
    private Toolbar toolbar_posting;
    private RequestManager requestManager;
    private static final int SELECT_VIDEO = 1;
    private static final int IMAGE_EDIT = 2;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private SampleCoverVideo sampleCoverVideo;
    GSYVideoOptionBuilder gsyVideoOptionBuilder;
    View imageAddView,videoAddView;
    int position = 0;

    String getPostId ;
    String userId ;
    int postType;
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


        selected_photos_container.addView(imageAddView);
        selected_photos_container.addView(videoAddView);

        //如果有接收到有人傳進來資料(基本上是從Frag_Update)
        if (getIntent().getExtras()!= null){

            if (getIntent().getExtras().getParcelable("post") instanceof PicturePost){
                Bundle bundle = getIntent().getExtras();
                PicturePost p = bundle.getParcelable("post");
                images = bundle.getParcelableArrayList("images");
                dealPicPost(p);

            }else if (getIntent().getParcelableExtra("post") instanceof TextPost){

                Bundle bundle = getIntent().getExtras();
                TextPost t = bundle.getParcelable("post");
                dealTextPost(t);
                Log.e("text",t.getPostID());
            }else if (getIntent().getParcelableExtra("post") instanceof VideoPost){
                Bundle bundle = getIntent().getExtras();
                VideoPost v = bundle.getParcelable("post");
                dealVideoPost(v);

            }

        }




    }

    private void dealTextPost(TextPost textPost) {
        Log.e("user get",textPost.getUser_name()+"");
        Log.e("postId get",textPost.getPostID()+"");

        Glide.with(getApplicationContext()).load(textPost.getUser_avatar()).into(img_user);
        tv_user.setText(textPost.getUser_name());
        et_content.setText(textPost.getDescription());
        getPostId = textPost.getPostID();
        userId = textPost.getUser_ID();
        postType = textPost.getPost_type();
        Log.e("author id get textPost",textPost.getUser_ID()+"");
        Log.e("posttype get textPost",textPost.getPost_type()+"");

        selected_photos_container.removeAllViews();

    }

    //post type 2 = video, 0=images 1=text

    private void dealVideoPost(VideoPost videoPost) {
        Log.e("user get",videoPost.getUser_name()+"");
        Log.e("postId get",videoPost.getPostID()+"");

        Glide.with(getApplicationContext()).load(videoPost.getUser_avatar()).into(img_user);
        tv_user.setText(videoPost.getUser_name());
        et_content.setText(videoPost.getDescription());
        getPostId = videoPost.getPostID();
        userId = videoPost.getUser_ID();
        postType = videoPost.getPost_type();
        Log.e("author id get videopost",videoPost.getUser_ID()+"");
        Log.e(" get url",videoPost.getVideo_url()+"");
        Log.e("posttype get videopost",videoPost.getPost_type()+"");

        readedUri = videoPost.getVideo_url();
        //將毒入的網址給gsyvideoplayer
        loadVideo();
    }

    //更新貼文 功能
    private void dealPicPost(PicturePost picturePost ) {

        Log.e("uris1",picturePost.getUser_name()+"");
        Log.e("uris2",images.size()+"");
        //初始化容器

        //設定使用者姓名頭像
        Glide.with(getApplicationContext()).load(picturePost.getUser_avatar()).into(img_user);
        tv_user.setText(picturePost.getUser_name());
        et_content.setText(picturePost.getDescription());
        getPostId = picturePost.getPostID();
        userId = picturePost.getUser_ID();
        postType = picturePost.getPost_type();
        Log.e("author id get picturepost",picturePost.getUser_ID()+"");
        Log.e("posttype get picturepost",picturePost.getPost_type()+"");

        //將容器添加到gridLayout
        selected_photos_container.removeAllViews();
        selected_photos_container.setVisibility(View.VISIBLE);

        addReadList();

        selected_photos_container.addView(imageAddView);


    }
    //添加讀取進來的 相片
    private void addReadList() {

        for (PicturePostGridImage image: images){
            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item_update, null);
            ImageView thumbnail = imageHolder.findViewById(R.id.media_image_update);

            Glide.with(this)
                    .load(image.getImagePath())
                    .centerCrop()
                    .into(thumbnail);
            selected_photos_container.addView(imageHolder);
        }

        handleDelete();

    }
    //處理點擊(處理使用者新增新相片前的點擊，使用者新增相片後就交由showUriList來處理點擊)
    private void handleDelete(){
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i=0; i<selected_photos_container.getChildCount(); i++){
            indexes.add(i);
        }

        int itemCount = selected_photos_container.getChildCount();
        Log.e("selected_photos_container.getChildCount()",""+itemCount);

        for (int i= 0; i < itemCount; i++) {

            FrameLayout container = (FrameLayout) selected_photos_container.getChildAt(i);
            int finalI = i;
            container.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    LayoutInflater inflater = LayoutInflater.from(UpdatePostingActivity.this);
                    final View v = inflater.inflate(R.layout.delete_image_alert, null);
                    new AlertDialog.Builder(UpdatePostingActivity.this)
                            .setTitle("刪除貼文")
                            .setView(v)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e("removed","at position"+finalI);

                                    images.remove(indexes.indexOf(finalI));
                                    selected_photos_container.removeView(selected_photos_container.getChildAt(indexes.indexOf(finalI)));
                                    indexes.remove(indexes.indexOf(finalI));
                                    Log.e("indexes",indexes.toString());
                                    Log.e("readList size",images.size()+"");
                                    Log.e("to removed index size",indexes.indexOf(finalI)+"");
//
//                                        Log.e("readlists",readList.size()+"");
                                    if (images.size()==0){
                                        Log.e("empty","readList empty");
                                        selected_photos_container.removeAllViews();
                                        selected_photos_container.addView(imageAddView);
                                    }
                                }
                            })
                            .show();
                }
            });
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

        String video_url;
        //如果讀下來的值不為空，video_url = readedUri
        if (!readedUri.equals("")){
            video_url = readedUri;
        }
        //如果讀下來的值為空(表示使用者是撿完影片回來的)，video_url = selectedUri
        else {
            video_url= selectedUri.toString();
        }
        gsyVideoOptionBuilder
                .setIsTouchWiget(false)
                .setUrl(video_url)
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
        sampleCoverVideo.startPlayLogic();
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
                //重新加入 "增加影片"
                LayoutInflater inflater = LayoutInflater.from(UpdatePostingActivity.this);
                final View view = inflater.inflate(R.layout.delete_video_alert, null);
                new AlertDialog.Builder(UpdatePostingActivity.this)
                        .setTitle("刪除影片")
                        .setView(view)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                selected_photos_container.removeView(loadVideo);
                                //selected_photos_container.addView(imageAddView);
                                selected_photos_container.addView(videoAddView);
                                //將 videourl 重置
                                readedUri = "";
                                selectedUri = null;
                                GSYVideoManager.releaseAllVideos();

                            }
                        })
                        .show();

            }
        });

    }


    ////處理增加相片的方法
    private void setUpPhotoActivity() {

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

                TedBottomPicker.with(UpdatePostingActivity.this)
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
                Toast.makeText(UpdatePostingActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        checkPermission(permissionlistener);
    }
    private void checkPermission(PermissionListener permissionlistener) {
        TedPermission.with(UpdatePostingActivity.this)
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

        addReadList();
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

        selected_photos_container.addView(imageAddView);

        //如果沒有相片
        Log.e("ted", "total size: "+uriList.size());
        if(uriList.size()==0&& images.size()==0){
            //再將"增加影片"按鈕添加到gridLayout
            selected_photos_container.addView(videoAddView);
        }else{
            //處理新增新相片後的點擊
            loadClickOptions(uriList);

        }
    }
    //處理新增新相片後的點擊
    private void loadClickOptions(List<Uri> uriList) {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i=0; i<selected_photos_container.getChildCount(); i++){
            indexes.add(i);
        }
        int itemCount = selected_photos_container.getChildCount();
        for (int i= 0; i < itemCount; i++) {
            FrameLayout container = (FrameLayout) selected_photos_container.getChildAt(i);
            int finalI = i;
            container.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Log.e("u clicked ",""+selected_photos_container.indexOfChild(container));

                    //如果點擊的 位置在images之外，代表是新增的，點擊進入editImagePage
                    if (selected_photos_container.indexOfChild(container)>=images.size()){
                        Log.e("image size",""+images.size());
                        //Toast.makeText(UpdatePostingActivity.this, "u clicked "+selected_photos_container.indexOfChild(container), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(UpdatePostingActivity.this, "image uri is"+ uriList.get(finalI-images.size()).toString(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(UpdatePostingActivity.this, "final = "+finalI+" image position= "+ (finalI-images.size()), Toast.LENGTH_SHORT).show();

                        // your click code here
                        Intent intent = new Intent(UpdatePostingActivity.this, ImageEditActivity.class);
                        intent.putExtra("position",(finalI-images.size()));
                        intent.setData(uriList.get((finalI-images.size())));
                        startActivityForResult(intent,IMAGE_EDIT);
                    }
                    //如果點擊的 位置在images之內，代表是讀取的，點擊確定刪除
                    else {
                        LayoutInflater inflater = LayoutInflater.from(UpdatePostingActivity.this);
                        final View v = inflater.inflate(R.layout.delete_image_alert, null);
                        new AlertDialog.Builder(UpdatePostingActivity.this)
                                .setTitle("刪除貼文2")
                                .setView(v)
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.e("removed","at position"+finalI);

                                        images.remove(indexes.indexOf(finalI));
                                        selected_photos_container.removeView(selected_photos_container.getChildAt(indexes.indexOf(finalI)));
                                        indexes.remove(indexes.indexOf(finalI));

                                        Log.e("readList size",images.size()+"");
                                        //將images移除後，再呼叫一次，好讓finalI重新讀取
                                        loadClickOptions(uriList);

                                        if (images.size()==0 && uriList.size() == 0){
                                            Log.e("empty","readList empty");
                                            selected_photos_container.removeAllViews();
                                            selected_photos_container.addView(imageAddView);
                                        }
                                    }
                                })
                                .show();
                    }

                }
            });
        }

    }

    @ Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TrimVideo.VIDEO_TRIMMER_REQ_CODE && data != null){
            selectedUri = Uri.parse(TrimVideo.getTrimmedVideoPath(data));
            Log.e("trimmed video path", "Trimmed path:: " + selectedUri);
            readedUri = "";
            loadVideo();
        }
        if (resultCode == RESULT_OK) {
            Log.e("request is",""+requestCode);


            if (requestCode == SELECT_VIDEO) {

                try {
                    if (data.getData() == null) {
                        Log.e("TED","selected video path = null!");
                        finish();
                    } else {

                        //Uri uri = Uri.fromFile(new File(selectedVideoPath));
                        Log.e("TED","selected video path ="+data.getData());
                        openTrimActivity(String.valueOf(data.getData()));
                        //

                    }
                } catch (Exception e) {
                    //#debug
                    e.printStackTrace();
                }
            }
            if (requestCode ==IMAGE_EDIT){
                int position = data.getIntExtra("position",-1);
                String uri = data.getStringExtra("uri");
                Log.e("get result","uri:"+uri+"posittion:"+position);

                selectedUriList.set(position,Uri.parse(uri));
                showUriList(selectedUriList);
                //reloadUriList();
            }
        }

    }

    private void openTrimActivity(String data) {
        TrimVideo.activity(data)
                .setTrimType(TrimType.MIN_MAX_DURATION)
                .setMinToMax(1, 1200)
                .start(this);
    }



    private void setUpActionBar(Toolbar toolbar_posting) {
        setSupportActionBar(toolbar_posting);
        getSupportActionBar().setTitle("post something");
        toolbar_posting.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
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

            //檢查更新後的貼文種類是否和先前的貼文種類依樣(問同學)
//            if (postType == 0){
//                if ((selectedUriList.size()+images.size())==0){
//                    LayoutInflater inflater = LayoutInflater.from(UpdatePostingActivity.this);
//                    final View v = inflater.inflate(R.layout.delete_post_alert, null);
//                    new AlertDialog.Builder(UpdatePostingActivity.this)
//                            .setTitle("尚未選擇照片")
//                            .setView(v)
//                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //不要執行接下來的動作
//                                }
//                            })
//
//                            .show();
//                }
//            }

//            Toast.makeText(PostingActivity.this, "UR url list is"+selectedUriList+"\n"+"UR video list is"+selectedUri, Toast.LENGTH_SHORT).show();
            if ((selectedUriList.size()+images.size())==0 && selectedUri==null && readedUri.equals("")){
                //String uuid = UUID.randomUUID().toString();


                HashMap<String,Object> hashMap = new HashMap();
                hashMap.put("description",et_content.getText().toString());
                hashMap.put("toUpdate",0);
                databaseReference.child("posting").child(getPostId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdatePostingActivity.this, "update success", Toast.LENGTH_SHORT).show();
                        startIntent();
                    }
                });


            }
            //picturepost
            else if((selectedUriList.size()+images.size())!=0 && selectedUri==null && readedUri.equals("") )
            {

                //如果有新的照片
                if (selectedUriList.size()>0){
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("upload 0/"+selectedUriList.size());
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
//                StorageReference reference = storageReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("posted_images").child(System.currentTimeMillis()+"");
                    for(Uri uri :selectedUriList){
                        Log.e("get downUrls ",uri.toString());
                        StorageReference reference = storageReference.child("users").child(userId).child("posted_images").child(System.currentTimeMillis()+"");
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
                }
                //如果沒有新的照片要上傳
                else{
                    uploadImages(null);
                }


            }
            //videopost
            else {
                //如果使用者有選取影片，且毒入的url==""
                if (selectedUri!=null && readedUri.equals("")){
                    UploadVideo();
                }
                //如果使用者有沒選取影片，且毒入的url不等於""
                else if (selectedUri==null && !readedUri.equals("")){
                    updateVideos(readedUri,null);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadImages(ProgressDialog progressDialog) {
        //上傳時要將 savedImageUrls 和 images(update傳來的) 一起上傳
        Log.e("images size" ,"savedImageUrls size"+savedImageUrls.size() +" ，images size"+images.size() );
        savedImageUrls.addAll(images);
        Log.e("merged size" ,""+savedImageUrls.size());
        //在 getPostId 的貼文上更新edittext 及新的圖片集


        HashMap<String,Object> hashMap = new HashMap();
        hashMap.put("description",et_content.getText().toString());
        hashMap.put("images",savedImageUrls);
        hashMap.put("toUpdate",0);
        databaseReference.child("posting").child(getPostId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdatePostingActivity.this, "update success", Toast.LENGTH_SHORT).show();
                if (progressDialog!= null){
                    progressDialog.dismiss();
                }

                startIntent();
            }
        });



    }



    private void UploadVideo(){
        if(selectedUri!= null && readedUri.equals("")){
            Log.e("user id",userId);
            StorageReference reference = storageReference.child("users").child(userId).child("posted_video").child(System.currentTimeMillis()+"."+getVideoExt(selectedUri));
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("upload 0%");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            UploadTask uploadTask = reference.putFile(Uri.parse("file://"+selectedUri));

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
                            updateVideos(url,progressDialog);
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

    private void updateVideos(String videourl,ProgressDialog dialog){
//        String pushId = databaseReference.push().getKey();
//        VideoPost v = new VideoPost();
//        v.setUser_name(firebaseAuth.getCurrentUser().getDisplayName());
//        v.setUser_avatar(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
//        v.setDescription(et_content.getText().toString());
//        v.setThumbnail_img("https://firebasestorage.googleapis.com/v0/b/storagetest-dfeb6.appspot.com/o/eyes%2F2.jpg?alt=media&token=254289ea-59ac-4d4c-80dd-f3720864af41");
//        v.setVideo_url(videourl);
//        v.setPost_type(2);
//        v.setPostID(firebaseAuth.getCurrentUser().getUid());
//        v.setPostID(pushId);
//        v.setPostTime(System.currentTimeMillis());
//        v.setToUpdate(0);
//        v.setToDelete(0);
        Log.e("video_url",videourl);
        HashMap<String,Object> hashMap = new HashMap();
        hashMap.put("description",et_content.getText().toString());
        hashMap.put("video_url",videourl);
        hashMap.put("toUpdate",0);

        databaseReference.child("posting").child(getPostId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (dialog!= null){
                    dialog.dismiss();
                }
                startIntent();
            }
        });



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