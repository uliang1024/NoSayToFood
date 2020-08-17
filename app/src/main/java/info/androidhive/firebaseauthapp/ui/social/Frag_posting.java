package info.androidhive.firebaseauthapp.ui.social;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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

import static info.androidhive.firebaseauthapp.util.Constants.DESCRIPTION;
import static info.androidhive.firebaseauthapp.util.Constants.ITEM_IMAGES;
import static info.androidhive.firebaseauthapp.util.Constants.POST_TYPE;
import static info.androidhive.firebaseauthapp.util.Constants.USER_AVATAR;
import static info.androidhive.firebaseauthapp.util.Constants.USER_NAME;
import static info.androidhive.firebaseauthapp.util.Constants.VIDEO_THUMBNAIL;
import static info.androidhive.firebaseauthapp.util.Constants.VIDEO_URL;

import info.androidhive.firebaseauthapp.util.Constants;

public class Frag_posting extends Fragment implements PicturePostAdapter.OnItemClickedListener  {

    public static final String POSTING_TYPE="posting_type";
    public static final String POSTING_TITLE="posting_title";
    int currentOffset = 0;

    private DatabaseReference myRef;

    //圖片gridview的item
    ArrayList<PicturePostGridImage> Pathitems = new ArrayList<>();
    //綜合的item
    List<Item> items;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView mRecyclerView;
    boolean mFull = false;
    LinearLayoutManager layoutManager;
    ScrollCalculatorHelper scrollCalculatorHelper;
    Context mContext;
    PicturePostAdapter adapter;
    PicturePostAdapter.OnItemClickedListener clickedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_social = inflater.inflate(R.layout.social_frag_posting, container, false);

        //INIT VIEWS
        init(fragment_social);
        ClearAll();
        addData();



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ClearAll();
                addData();

                swipeRefreshLayout.setRefreshing(false);
            }
        });
        clickedListener = this::onItemClicked;


        //自定播放帮助类
        int playTop = CommonUtil.getScreenHeight(mContext) / 2 - CommonUtil.dip2px(mContext, 200);
        int playBottom = CommonUtil.getScreenHeight(mContext) / 2 + CommonUtil.dip2px(mContext, 200);
        scrollCalculatorHelper = new ScrollCalculatorHelper(R.id.video_item_player, playTop, playBottom);

        //============================================================
        layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
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
//                Log.e("playing",""+position);
//                Log.e("firstplay",""+firstVisibleItem);
//                Log.e("lastplay",""+lastVisibleItem);
                //这是滑动自动播放的代码
                if (!mFull) {
                    scrollCalculatorHelper.onScroll(recyclerView, firstVisibleItem, lastVisibleItem, lastVisibleItem - firstVisibleItem);
                }

                if (GSYVideoManager.instance().getPlayPosition() >= 0) {
                    //当前播放的位置

                    //对应的播放列表TAG
                    if ( (position < firstVisibleItem || position > lastVisibleItem)) {
                        GSYVideoManager.releaseAllVideos();
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
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh);
        items = new ArrayList<>();
        mRecyclerView = v.findViewById(R.id.recycler_view);
        mContext = v.getContext();
        myRef = FirebaseDatabase.getInstance().getReference();
    }

    private void addData() {

        myRef.child("posting").addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearAll();
                //Toast.makeText(mContext, "count"+count , Toast.LENGTH_SHORT).show();
                int mMaxDisplay_Size = 0;
                int mTotal_Size = 0;
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren()){

                    if(postSnapShot.hasChild(POST_TYPE)){
                        int type = Integer.parseInt(postSnapShot.child(POST_TYPE).getValue().toString());
                        if(type ==0){
                            PicturePost p = postSnapShot.getValue(PicturePost.class);
                            items.add(new Item(0,p));
                        }
                        if(type==1){

                            TextPost t = postSnapShot.getValue(TextPost.class);
                            items.add(new Item(1,t));
                        }
                        else if(type==2){
                            VideoPost v = postSnapShot.getValue(VideoPost.class);

                            items.add(new Item(2,v));
                        }
                        adapter = new PicturePostAdapter(mContext,items);
                        adapter.setOnItemClickedListener(clickedListener);
                        adapter.notifyDataSetChanged(); //notify
                        mRecyclerView.setAdapter(adapter);
                    }


                    else{
                        Toast.makeText(mContext, "no..." , Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

            startActivity(postingIntent);
        }
    }

//參考code
//    private void preparePictureData() {
//
//        //創建一arraylist儲存 ItemImage
//        ArrayList<PicturePostGridImage> mPathitems = new ArrayList<>();
//        boolean isCol2Avail = false;
//
//        Pathitems.clear();
//        for (int i = 0;i<3;i++){
//            //創建一個itemImage=====================================
//            PicturePostGridImage i1 = new PicturePostGridImage(Image1);
//            //int colSpan = Math.random() < 0.2f ? 2 : 1;
//            int colSpan = 2;
//            int rowSpan = colSpan;
//
//            i1.setColumnSpan(1);
//            i1.setRowSpan(1);
//            i1.setPosition( currentOffset + i);
//            //=====================================================
//            Pathitems.add(i1);
//
//        }
//        mMaxDisplay_Size = Pathitems.size();
//        //依據使用者的照片數量來決定mMaxDisplay_Size 數字
//        //如果是1張的話mMaxDisplay_Size=1(展示1張照片)
//        //如果是2張的話mMaxDisplay_Size=2(展示2張照片)
//        //如果是3~5張的話mMaxDisplay_Size=4(展示4張照片)
//        //如果是6~n張的話mMaxDisplay_Size=6(展示6張照片)
//        if (mMaxDisplay_Size>=4&&mMaxDisplay_Size<6){
//            mMaxDisplay_Size =4;
//        }
//        if (mMaxDisplay_Size>=6){
//            mMaxDisplay_Size = 6;
//        }
//
//        for(int i = 0; i < mMaxDisplay_Size;i++)
//        {
//            mPathitems.add(Pathitems.get(i));
//        }
//        PicturePost p = new PicturePost();
//        p.setDescription("i posted a image");
//        p.setTitle("check mate");
//        p.setImages(mPathitems);
//        p.setUser_name("hey");
//        p.setUser_avatar("https://firebasestorage.googleapis.com/v0/b/storagetest-dfeb6.appspot.com/o/eyes%2F8.jpg?alt=media&token=f1d63bbb-85db-488e-bd64-a23347140ab7");
//
//        items.add(new Item(0,p));
//        currentOffset += mPathitems.size();
//
//    }

}