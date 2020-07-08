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

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import info.androidhive.firebaseauthapp.PostingPageActivity;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.adapter.PicturePostAdapter;
import info.androidhive.firebaseauthapp.models.Item;
import info.androidhive.firebaseauthapp.models.PicturePost;
import info.androidhive.firebaseauthapp.models.TextPost;
import info.androidhive.firebaseauthapp.models.VideoPost;
import info.androidhive.firebaseauthapp.util.ScrollCalculatorHelper;

public class Frag_posting extends Fragment implements PicturePostAdapter.OnItemClickedListener {

    public static final String POSTING_TYPE="posting_type";
    public static final String POSTING_TITLE="posting_title";

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
        addData();

        int playTop = CommonUtil.getScreenHeight(mContext) / 2 - CommonUtil.dip2px(mContext, 200);
        int playBottom = CommonUtil.getScreenHeight(mContext) / 2 + CommonUtil.dip2px(mContext, 200);

        //自定播放帮助类

        scrollCalculatorHelper = new ScrollCalculatorHelper(R.id.video_item_player, playTop, playBottom);
        layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        final PicturePostAdapter adapter = new PicturePostAdapter(mContext,items);
        adapter.setOnItemClickedListener(this);
        //adapter.setOnItemClickedListener(this::onItemClicked);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int firstVisibleItem, lastVisibleItem,currentVisiblePos;
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
    }

    private void addData() {
        VideoPost v = new VideoPost(R.drawable.police,"dicc4",R.drawable.deno,"http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4","demo","ppppppppppppppppppppppp",3);
        items.add(new Item(2,v));
        TextPost t = new TextPost(R.drawable.police,"bob0","888","666");
        items.add(new Item(1,t));
        PicturePost p = new PicturePost(R.drawable.deno,R.drawable.police,"bob1","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p));
        PicturePost p2 = new PicturePost(R.drawable.deno,R.drawable.police,"bob2","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p2));
        VideoPost v1 = new VideoPost(R.drawable.police,"dicc4",R.drawable.deno,"http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4","demo","ppppppppppppppppppppppp",3);
        items.add(new Item(2,v1));
        PicturePost p3 = new PicturePost(R.drawable.deno,R.drawable.police,"bob3","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p3));
        PicturePost p4 = new PicturePost(R.drawable.deno,R.drawable.police,"bob4","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p4));
        VideoPost v2 = new VideoPost(R.drawable.police,"dicc7",R.drawable.deno,"http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4","demo","ppppppppppppppppppppppp", 6);
        items.add(new Item(2,v2));
        PicturePost p5 = new PicturePost(R.drawable.deno,R.drawable.police,"bob5","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p5));

        PicturePost p6 = new PicturePost(R.drawable.deno,R.drawable.police,"bob6","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p6));
        PicturePost p7 = new PicturePost(R.drawable.deno,R.drawable.police,"bob7","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p7));
        PicturePost p8 = new PicturePost(R.drawable.deno,R.drawable.police,"bob8","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p8));
        PicturePost p9 = new PicturePost(R.drawable.deno,R.drawable.police,"bob9","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p9));
        PicturePost p10 = new PicturePost(R.drawable.deno,R.drawable.police,"bob10","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p10));

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
            postingIntent.putExtra(POSTING_TITLE,picturePost.getTitle());

            startActivity(postingIntent);
        }
        else if (items.get(position).getObject() instanceof VideoPost){
            VideoPost videoPost = (VideoPost)items.get(position).getObject();
            postingIntent.putExtra(POSTING_TYPE,items.get(position).getType());
            postingIntent.putExtra(POSTING_TITLE,videoPost.getTitle());

            startActivity(postingIntent);
        }
    }
}