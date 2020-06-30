package info.androidhive.firebaseauthapp.ui.social;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
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

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.adapter.PostAdapter;
import info.androidhive.firebaseauthapp.models.Item;
import info.androidhive.firebaseauthapp.models.PicturePost;
import info.androidhive.firebaseauthapp.models.TextPost;
import info.androidhive.firebaseauthapp.models.VideoPost;
import info.androidhive.firebaseauthapp.util.ScrollCalculatorHelper;

/**
 * Created by Belal on 1/23/2018.
 */

public class SocialFragment extends Fragment {


    List<Item> items = new ArrayList<>();
    RecyclerView mRecyclerView;
    boolean mFull = false;
    LinearLayoutManager layoutManager;
    ScrollCalculatorHelper scrollCalculatorHelper;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_social = inflater.inflate(R.layout.fragment_social, container, false);
        //INIT VIEWS
        init(fragment_social);

        addData();
        //限定范围为屏幕一半的上下偏移180
        int playTop = CommonUtil.getScreenHeight(fragment_social.getContext()) / 2 - CommonUtil.dip2px(fragment_social.getContext().getApplicationContext(), 200);
        int playBottom = CommonUtil.getScreenHeight(fragment_social.getContext()) / 2 + CommonUtil.dip2px(fragment_social.getContext().getApplicationContext(), 200);

        //自定播放帮助类

        scrollCalculatorHelper = new ScrollCalculatorHelper(R.id.detail_player, playTop, playBottom);
        layoutManager = new LinearLayoutManager(fragment_social.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        final PostAdapter adapter = new PostAdapter(items);
        mRecyclerView.setAdapter(adapter);
        /*如果 dx>0 则表示 右滑 ， dx<0 表示 左滑
        dy <0 表示 上滑， dy>0 表示下滑*/


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
                //这是滑动自动播放的代码
                if (!mFull) {
                    scrollCalculatorHelper.onScroll(recyclerView, firstVisibleItem, lastVisibleItem, lastVisibleItem - firstVisibleItem);
                }


            }

        });
        return fragment_social;
    }
    private void init(View v) {
        mRecyclerView=v.findViewById(R.id.recycler_view);
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


    private void addData() {
        VideoPost v3 = new VideoPost(R.drawable.medical_mask,"dicc7",R.drawable.deno,"http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4","demo","ppppppppppppppppppppppp");
        items.add(new Item(2,v3));
        TextPost t = new TextPost(R.drawable.medical_mask,"bob0","888","666");
        items.add(new Item(1,t));
        PicturePost p = new PicturePost(R.drawable.deno,R.drawable.medical_mask,"bob1","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p));
        PicturePost p2 = new PicturePost(R.drawable.deno,R.drawable.medical_mask,"bob2","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p2));
        VideoPost v1 = new VideoPost(R.drawable.medical_mask,"dicc4",R.drawable.deno,"http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4","demo","ppppppppppppppppppppppp");
        items.add(new Item(2,v1));
        PicturePost p3 = new PicturePost(R.drawable.deno,R.drawable.medical_mask,"bob3","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p3));
        PicturePost p4 = new PicturePost(R.drawable.deno,R.drawable.medical_mask,"bob4","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p4));
        VideoPost v2 = new VideoPost(R.drawable.medical_mask,"dicc7",R.drawable.deno,"http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4","demo","ppppppppppppppppppppppp");
        items.add(new Item(2,v2));
        PicturePost p5 = new PicturePost(R.drawable.deno,R.drawable.medical_mask,"bob5","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p5));

        PicturePost p6 = new PicturePost(R.drawable.deno,R.drawable.medical_mask,"bob6","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p6));
        PicturePost p7 = new PicturePost(R.drawable.deno,R.drawable.medical_mask,"bob7","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p7));
        PicturePost p8 = new PicturePost(R.drawable.deno,R.drawable.medical_mask,"bob8","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p8));
        PicturePost p9 = new PicturePost(R.drawable.deno,R.drawable.medical_mask,"bob9","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p9));
        PicturePost p10 = new PicturePost(R.drawable.deno,R.drawable.medical_mask,"bob10","big pp","s simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
        items.add(new Item(0,p10));


    }


}