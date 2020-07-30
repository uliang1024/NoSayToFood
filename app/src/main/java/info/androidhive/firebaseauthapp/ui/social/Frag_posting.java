package info.androidhive.firebaseauthapp.ui.social;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import info.androidhive.firebaseauthapp.PostingPageActivity;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.adapter.PicturePostAdapter;
import info.androidhive.firebaseauthapp.models.Item;
import info.androidhive.firebaseauthapp.models.PicturePost;
import info.androidhive.firebaseauthapp.models.PicturePostGridImage;
import info.androidhive.firebaseauthapp.models.TextPost;
import info.androidhive.firebaseauthapp.models.VideoPost;
import info.androidhive.firebaseauthapp.util.ScrollCalculatorHelper;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Frag_posting extends Fragment implements PicturePostAdapter.OnItemClickedListener {

    public static final String POSTING_TYPE="posting_type";
    public static final String POSTING_TITLE="posting_title";
    //recyclerview讀取此地資料
    public String Image1 = "https://firebasestorage.googleapis.com/v0/b/storagetest-dfeb6.appspot.com/o/eyes%2F5.jpg?alt=media&token=d471627d-35d1-4e43-a8e8-57c0e9857833";
    int currentOffset = 0;
    //決定一個cardview中有多少張相片可見
    int mMaxDisplay_Size = 0;
    int mTotal_Size = 0;
    private DatabaseReference myRef;

    //圖片gridview的item
    ArrayList<PicturePostGridImage> Pathitems = new ArrayList<>();
    //綜合的item
    List<Item> items = new ArrayList<>();

    RecyclerView mRecyclerView;
    boolean mFull = false;
    LinearLayoutManager layoutManager;
    ScrollCalculatorHelper scrollCalculatorHelper;
    Context mContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_social = inflater.inflate(R.layout.social_frag_posting, container, false);

        //INIT VIEWS
        init(fragment_social);
        ClearAll();
        //addData();

        for(int k = 0; k < 100;k++) {
            //prepareMovieData(k);
            addData(k);
        }

        int playTop = CommonUtil.getScreenHeight(mContext) / 2 - CommonUtil.dip2px(mContext, 200);
        int playBottom = CommonUtil.getScreenHeight(mContext) / 2 + CommonUtil.dip2px(mContext, 200);

        //自定播放帮助类

        scrollCalculatorHelper = new ScrollCalculatorHelper(R.id.video_item_player, playTop, playBottom);
        layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        //mTotal_Size是image總數取決於有幾個PicturePostGridImage被加到裡面
        mTotal_Size = Pathitems.size();
        //max_display是一個cardview所展示的image數，mTotal_Size是image總數
        final PicturePostAdapter adapter = new PicturePostAdapter(mContext,items,mMaxDisplay_Size,mTotal_Size);

        adapter.setOnItemClickedListener(this);

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int firstVisibleItem, lastVisibleItem;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                scrollCalculatorHelper.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem   = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int position = GSYVideoManager.instance().getPlayPosition();
                Log.e("playing",""+position);
                Log.e("firstplay",""+firstVisibleItem);
                Log.e("lastplay",""+lastVisibleItem);
                //这是滑动自动播放的代码
                if (!mFull) {
                    scrollCalculatorHelper.onScroll(recyclerView, firstVisibleItem, lastVisibleItem, lastVisibleItem - firstVisibleItem);
                }

                if (GSYVideoManager.instance().getPlayPosition() >= 0) {
                    //当前播放的位置

                    //对应的播放列表TAG
                    if ( (position < firstVisibleItem || position > lastVisibleItem)) {
                        GSYVideoManager.onPause();
                        adapter.notifyItemChanged(position);

                    }
                }
            }

        });


        return fragment_social;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation != ActivityInfo.SCREEN_ORIENTATION_USER) {
            mFull = false;
        } else {
            mFull = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        GSYVideoManager.onResume(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }

    private void init(View v) {
        mRecyclerView = v.findViewById(R.id.recycler_view);
        mContext = v.getContext();
        myRef = FirebaseDatabase.getInstance().getReference();
    }

    private void addData(int k) {

        Random ran = new Random();
        int type =ran.nextInt(3);

        switch (type){
            case 0:
                prepareMovieData(k);
            case 1:
                TextPost t = new TextPost("https://firebasestorage.googleapis.com/v0/b/storagetest-dfeb6.appspot.com/o/eyes%2F8.jpg?alt=media&token=f1d63bbb-85db-488e-bd64-a23347140ab7","bob","Lorem Ipsum","is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was po");
                items.add(new Item(1,t));
            case 2:
                VideoPost v = new VideoPost("https://firebasestorage.googleapis.com/v0/b/storagetest-dfeb6.appspot.com/o/eyes%2F9.jpg?alt=media&token=5464b71f-bc4f-4896-bc37-a77e4e90ae22","peter","https://firebasestorage.googleapis.com/v0/b/storagetest-dfeb6.appspot.com/o/eyes%2F6.jpg?alt=media&token=9f59d48a-8023-457a-b99e-ccfc5bd1f29d","http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4","demo","is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was po",3);
                items.add(new Item(2,v));
        }
    }

    public void ClearAll(){
        if (items != null){
            items.clear();
        }
        items = new ArrayList<>();
    }

    @Override
    public void onItemClicked(int position) {
        Intent postingIntent = new Intent(getActivity(), PostingPageActivity.class);
        if (items.get(position).getObject() instanceof TextPost){
            TextPost textPost = (TextPost)items.get(position).getObject();
            postingIntent.putExtra(POSTING_TYPE,items.get(position).getType());
            postingIntent.putExtra(POSTING_TITLE,textPost.getTitle());

            startActivity(postingIntent);
        }
        else if (items.get(position).getObject() instanceof PicturePost){
            PicturePost picturePost = (PicturePost)items.get(position).getObject();
            postingIntent.putExtra(POSTING_TYPE,items.get(position).getType());


            startActivity(postingIntent);
        }
        else if (items.get(position).getObject() instanceof VideoPost){
            VideoPost videoPost = (VideoPost)items.get(position).getObject();
            postingIntent.putExtra(POSTING_TYPE,items.get(position).getType());
            postingIntent.putExtra(POSTING_TITLE,videoPost.getTitle());

            startActivity(postingIntent);
        }
    }
    private void prepareMovieData(int k) {

        //創建一arraylist儲存 ItemImage
        ArrayList<PicturePostGridImage> mPathitems = new ArrayList<>();
        boolean isCol2Avail = false;

//        //創建一個itemImage=====================================
//        PicturePostGridImage i1 = new PicturePostGridImage(1,Image1,Image1);
//        //int colSpan1 = Math.random() < 0.2f ? 2 : 1;
//        int colSpan1 = 1;
//        int rowSpan1 = colSpan1;
//        if(colSpan1 == 2 && !isCol2Avail)
//            isCol2Avail = true;
//        else if(colSpan1 == 2 && isCol2Avail)
//            colSpan1 = 1;
//
//        i1.setColumnSpan(colSpan1);
//        i1.setRowSpan(rowSpan1);
//        i1.setPosition( currentOffset + 0);
//        //=====================================================
//
//
//        PicturePostGridImage i2 = new PicturePostGridImage(2,Image2,Image2);
//        //int colSpan2 = Math.random() < 0.2f ? 2 : 1;
//        int colSpan2 = 1;
//        if(colSpan2 == 2 && !isCol2Avail)
//            isCol2Avail = true;
//        else if(colSpan2 == 2 && isCol2Avail)
//            colSpan2 = 1;
//
//        int rowSpan2 = colSpan2;
//        i2.setColumnSpan(colSpan2);
//        i2.setRowSpan(rowSpan2);
//        i2.setPosition( currentOffset + 1);
//
//
//        PicturePostGridImage i3 = new PicturePostGridImage(3,Image3,Image3);
//        //int colSpan3 = Math.random() < 0.2f ? 2 : 1;
//        int colSpan3 = 2;
//        if(colSpan3 == 2 && !isCol2Avail)
//            isCol2Avail = true;
//        else if(colSpan3 == 2 && isCol2Avail)
//            colSpan3 = 1;
//
//        int rowSpan3 = colSpan3;
//        i3.setColumnSpan(colSpan3);
//        i3.setRowSpan(rowSpan3);
//        i3.setPosition( currentOffset + 2);
//
//        PicturePostGridImage i4 = new PicturePostGridImage(4,Image4,Image4);
//        int colSpan4 = Math.random() < 0.2f ? 2 : 1;
//        if(colSpan4 == 2 && !isCol2Avail)
//            isCol2Avail = true;
//        else if(colSpan4 == 2 && isCol2Avail)
//            colSpan4 = 1;
//
//        int rowSpan4 = colSpan4;
//        i4.setColumnSpan(colSpan4);
//        i4.setRowSpan(rowSpan4);
//        i4.setPosition( currentOffset + 3);

        Pathitems.clear();

//        Pathitems.add(i1);
//        Pathitems.add(i2);
//        Pathitems.add(i3);
//        Pathitems.add(i4);


        Random ran = new Random();
        int type =ran.nextInt(5);
        for (int i = 0;i<5;i++){
            //創建一個itemImage=====================================
            PicturePostGridImage i1 = new PicturePostGridImage(i+1,Image1,Image1);
            //int colSpan = Math.random() < 0.2f ? 2 : 1;
            int colSpan = 2;
            int rowSpan = colSpan;

            i1.setColumnSpan(1);
            i1.setRowSpan(1);
            i1.setPosition( currentOffset + i);
            //=====================================================
            Pathitems.add(i1);

        }
        mMaxDisplay_Size = Pathitems.size();
        //依據使用者的照片數量來決定mMaxDisplay_Size 數字
        //如果是1張的話mMaxDisplay_Size=1(展示1張照片)
        //如果是2張的話mMaxDisplay_Size=2(展示2張照片)
        //如果是3~n張的話mMaxDisplay_Size=3(展示3張照片)
        if (mMaxDisplay_Size>=4&&mMaxDisplay_Size<6){
            mMaxDisplay_Size =4;
        }
        if (mMaxDisplay_Size>=6){
            mMaxDisplay_Size = 6;
        }

        for(int i = 0; i < mMaxDisplay_Size;i++)
        {
            mPathitems.add(Pathitems.get(i));
        }

        PicturePost item = new PicturePost(k,mPathitems,"https://firebasestorage.googleapis.com/v0/b/storagetest-dfeb6.appspot.com/o/eyes%2F7.jpg?alt=media&token=6d0bbae6-7bbc-451a-9164-a188fcc614e5","frank","i posted a picture!","is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown prin");
        items.add(new Item(0,item));
        currentOffset += mPathitems.size();

    }

}