package info.androidhive.firebaseauthapp.ui.social;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.Collections;
import java.util.HashMap;
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
    List<Item> items = new ArrayList<>();;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView mRecyclerView;
    boolean mFull = false;
    LinearLayoutManager layoutManager;
    ScrollCalculatorHelper scrollCalculatorHelper;
    Context mContext;
    PicturePostAdapter adapter;
    PicturePostAdapter.OnItemClickedListener clickedListener;
    TextView tv_noData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_social = inflater.inflate(R.layout.social_frag_posting, container, false);

        //INIT VIEWS
        init(fragment_social);
        items.clear();
        //讀取資料
        addData(new DataListener() {
            @Override
            public void onReceiveData(boolean dataLoadComplete) {
                //如果接收資料成功
                if (dataLoadComplete){
                    //先暫停兩秒等待shimmer動畫效果播出
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.isShimmer = false;
                            adapter.notifyDataSetChanged();
                        }
                    },300);
                }
                Log.e("dataload","ok");
            }
        });
        //下滑刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                items.clear();
                adapter.notifyDataSetChanged();
                addData(new DataListener() {
                    //如果接收資料成功
                    @Override
                    public void onReceiveData(boolean dataLoadComplete) {
                        if (dataLoadComplete){
                            adapter.isShimmer = false;
                            adapter.notifyDataSetChanged();
                        }
                        Log.e("dataload","ok");
                    }
                });

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

        adapter = new PicturePostAdapter(mContext,items);
        adapter.setOnItemClickedListener(clickedListener);
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
        tv_noData = v.findViewById(R.id.tv_noData);
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh);
        items = new ArrayList<>();
        mRecyclerView = v.findViewById(R.id.recycler_view);
        mContext = v.getContext();
        myRef = FirebaseDatabase.getInstance().getReference();
    }

    private void addData(DataListener dataListener) {


        myRef.addListenerForSingleValueEvent (new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("posting")){
                    //取得共幾筆資料
                    HashMap<String,Object> loadedItems = (HashMap<String,Object>) dataSnapshot.child("posting").getValue();

                    if (loadedItems == null||loadedItems.size() == 0){
                        return;
                    }
                    Log.e("loaded list size",loadedItems.size()+"");
                    int loadCounter= 0;

                    items.clear();
                    //Toast.makeText(mContext, "count"+count , Toast.LENGTH_SHORT).show();
                    for(DataSnapshot postSnapShot:dataSnapshot.child("posting").getChildren()){

                        if(postSnapShot.hasChild(POST_TYPE)){
                            int type = Integer.parseInt(postSnapShot.child(POST_TYPE).getValue().toString());
                            if(type ==0){
                                PicturePost p = postSnapShot.getValue(PicturePost.class);
                                items.add(new Item(0,p));
                                loadCounter++;
                            }
                            if(type==1){

                                TextPost t = postSnapShot.getValue(TextPost.class);
                                items.add(new Item(1,t));
                                loadCounter++;
                            }
                            else if(type==2){
                                VideoPost v = postSnapShot.getValue(VideoPost.class);
                                items.add(new Item(2,v));
                                loadCounter++;
                            }


                        }
                        else{
                            Toast.makeText(mContext, "no..." , Toast.LENGTH_SHORT).show();
                        }
                        //adapter.notifyDataSetChanged(); //notify

                    }

                    if (loadCounter == loadedItems.size()){
                        Collections.reverse(items);
                        dataListener.onReceiveData(true);
                    }
                }else{
                    adapter.isShimmer = false;
                    adapter.notifyDataSetChanged();
                    tv_noData.setVisibility(View.VISIBLE);
                    Toast.makeText(mContext, "這個app太邊緣了，目前沒有任何貼文，真的很抱歉", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

//    public void ClearAll(){
//        if (items != null){
//            items.clear();
//        }
//        items = new ArrayList<>();
//    }

    @Override
    public void onItemClicked(int position) {
        Intent postingIntent = new Intent(getActivity(), PostingPageActivity.class);
        if (items.get(position).getObject() instanceof TextPost){
            TextPost textPost = (TextPost)items.get(position).getObject();
//            postingIntent.putExtra(POSTING_TYPE,items.get(position).getType());
//
//            startActivity(postingIntent);
        }
        else if (items.get(position).getObject() instanceof PicturePost){
            PicturePost picturePost = (PicturePost)items.get(position).getObject();
//            postingIntent.putExtra(POSTING_TYPE,items.get(position).getType());
//
//
//            startActivity(postingIntent);
        }
        else if (items.get(position).getObject() instanceof VideoPost){
            VideoPost videoPost = (VideoPost)items.get(position).getObject();
//            postingIntent.putExtra(POSTING_TYPE,items.get(position).getType());
//
//            startActivity(postingIntent);
        }
    }
    interface DataListener{
        void onReceiveData(boolean dataLoadComplete);
    }
}