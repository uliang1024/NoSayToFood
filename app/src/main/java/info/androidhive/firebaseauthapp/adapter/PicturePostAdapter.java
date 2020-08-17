package info.androidhive.firebaseauthapp.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.models.Item;
import info.androidhive.firebaseauthapp.models.PicturePost;
import info.androidhive.firebaseauthapp.models.PicturePostGridImage;
import info.androidhive.firebaseauthapp.models.TextPost;
import info.androidhive.firebaseauthapp.models.VideoPost;
import info.androidhive.firebaseauthapp.util.SampleCoverVideo;
import info.androidhive.firebaseauthapp.util.SpacesItemDecoration;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

//Adapter
public class PicturePostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static String TAG = "RecyclerView2List";
    //recyclerView將要展示的元素
    private List<Item> items;
    //requestManager管理Glide的實體
    //private RequestManager requestManager;
    private Context context = null;
    private GSYVideoHelper smallVideoHelper;

    private  OnItemClickedListener mListener;
    private int AnimId = R.anim.left_to_right;
    //先寫一個interface OnItemClickedListener
    public interface OnItemClickedListener{
        void onItemClicked(int position);
    }

    //稍後將會在主程式呼叫此方法，並傳入一個 OnItemClickedListener
    public void setOnItemClickedListener(OnItemClickedListener listener){
        mListener = listener;
    }

    private GSYVideoHelper.GSYVideoHelperBuilder gsySmallVideoHelperBuilder;



    public PicturePostAdapter(Context context, List<Item> items ) {
        this.items = items;
        this.context = context;

    }

    @NonNull
    @Override
    //在紫創造一個viewHolder
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //types are :0=pic ,1=text ,2=video
        //填充xml到相應的viewHolder
        if (viewType ==0){
            return  new pictuerPostViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.item_container_picpost
                            ,parent
                            ,false
                    )
            );
        }else if (viewType ==1){
            return  new TextPostViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.item_container_textpost
                            ,parent
                            ,false
                    )
            );
        }else{

            View v = LayoutInflater.from(context).inflate(R.layout.item_container_videopost, parent, false);
            final RecyclerView.ViewHolder holder = new RecyclerItemNormalHolder(context, v);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //如果第n個位置傳過來的getItemViewType是0
        if(getItemViewType(position)==0){
            //picturePost代表在items裡面第n個位置的物件，並將其轉型為PicturePost
            PicturePost picturePost = (PicturePost)items.get(position).getObject();
            ((pictuerPostViewHolder)holder).set_picPost_Content(picturePost,position ,holder);
        }
        //如果第n個位置傳過來的getItemViewType是1
        else if(getItemViewType(position)==1){
            //textPost代表在items裡面第n個位置的物件，並將其轉型為TextPost
            TextPost textPost = (TextPost)items.get(position).getObject();
            ((TextPostViewHolder)holder).set_textPost_Content(textPost);
        }else{

            //創造一個holder
            RecyclerItemNormalHolder recyclerItemViewHolder = (RecyclerItemNormalHolder) holder;
            //將holder設定一個RecyclerBaseAdapter
            //recyclerItemViewHolder.setRecyclerBaseAdapter(this);
            VideoPost videoPost = (VideoPost)items.get(position).getObject();
            recyclerItemViewHolder.onBind(position, videoPost);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }



    //===========================ViewHolders===============================//
    public class pictuerPostViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView_picpost;
        public ViewPager image_slider;
        private ImageView img_user;
        private TextView tv_username_pic, tv_img_count;
        private ExpandableTextView tv_des_picPost;
        //在建構子內宣告
        public pictuerPostViewHolder(@NonNull View itemView) {
            super(itemView);

            image_slider = itemView.findViewById(R.id.view_pager);
            //設置橫向有幾個元素展出



            cardView_picpost = itemView.findViewById(R.id.cardview_picpost);
            img_user = itemView.findViewById(R.id.img_user_picpost);
            tv_username_pic = itemView.findViewById(R.id.tv_user_picpost);
            tv_img_count = itemView.findViewById(R.id.tv_img_count);
            tv_des_picPost = itemView.findViewById(R.id.expand_text_view);
//            cardView_picpost.setAnimation(AnimationUtils.loadAnimation(context,AnimId));
             itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if(mListener!= null){
                         //在此捕捉一個點及動作，然後藉由interface傳到activity
                        int position = getAbsoluteAdapterPosition();

                        if(position!= RecyclerView.NO_POSITION){
                            mListener.onItemClicked(position);
                        }
                     }
                 }
             });
        }
        void set_picPost_Content(PicturePost p,int position ,RecyclerView.ViewHolder holder){

            Glide.with(context).load(p.getUser_avatar()).centerCrop().into(img_user);
            tv_username_pic.setText(p.getUser_name());
            tv_des_picPost.setText(p.getDescription());
            if (p.getImages().size() ==1){
                tv_img_count.setVisibility(View.GONE);
            }else {
                tv_img_count.setText("1/"+p.getImages().size());
            }
            ArrayList<String> imageList = new ArrayList<>();
            for(PicturePostGridImage image:p.getImages()){
                imageList.add(image.getImagePath());
            }

            ImagePagerAdapter adapter = new ImagePagerAdapter(context,imageList);

            image_slider.setAdapter(adapter);
            image_slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    Log.e("viewPager","on page "+position);
                    tv_img_count.setText((position+1)+"/"+p.getImages().size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }
    }

    public class TextPostViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView_textpost;
        private ImageView img_user_text;
        private TextView tv_username_text, tv_textPost;
        private ExpandableTextView tv_des_textPost;
        public TextPostViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView_textpost =itemView.findViewById(R.id.cardview_textpost);
            img_user_text = itemView.findViewById(R.id.img_user_textpost);
            tv_username_text = itemView.findViewById(R.id.tv_user_textpost);
            tv_des_textPost = itemView.findViewById(R.id.expand_text_view_textpost);
            //            cardView_textpost.setAnimation(AnimationUtils.loadAnimation(context,AnimId));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!= null){
                        //在此捕捉一個點及動作，然後藉由interface傳到activity
                        int position = getAbsoluteAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            mListener.onItemClicked(position);
                        }
                    }
                }
            });
        }

        void set_textPost_Content(TextPost t) {
            Glide.with(context).load(t.getUser_avatar()).centerCrop().into(img_user_text);

            tv_username_text.setText(t.getUser_name());
            tv_des_textPost.setText(t.getDescription());
        }

    }


    public class RecyclerItemNormalHolder extends RecyclerItemBaseHolder {

        public final static String TAG = "RecyclerView2List";
        protected Context context = null;

        @BindView(R.id.video_item_player)
        SampleCoverVideo gsyVideoPlayer;
        private CardView cardView_videopost;
        ImageView imageView;
        private ImageView img_user_video;
        private TextView tv_username_video, tv_videoPost;
        GSYVideoOptionBuilder gsyVideoOptionBuilder;
        private ExpandableTextView tv_des_videoPost;
        public RecyclerItemNormalHolder(Context context, View v) {
            super(v);
            this.context = context;
            ButterKnife.bind(this, v);
            imageView = new ImageView(context);
            cardView_videopost =v.findViewById(R.id.cardview_videopost);
            img_user_video = itemView.findViewById(R.id.img_user_videopost);
            tv_username_video = itemView.findViewById(R.id.tv_user_videopost);
            tv_des_videoPost = itemView.findViewById(R.id.expand_text_view_videopost);
            gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
//            cardView_videopost.setAnimation(AnimationUtils.loadAnimation(context,AnimId));
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
//                    Toast.makeText(context,String.valueOf(position),Toast.LENGTH_SHORT);
                    if(mListener!= null){
                        //在此捕捉一個點及動作，然後藉由interface傳到activity

                        if(position!= RecyclerView.NO_POSITION){
                            mListener.onItemClicked(position);
                        }
                    }

                }
            });
        }

        public void onBind(final int position, VideoPost v) {

            Glide.with(context).load(v.getUser_avatar()).centerCrop().into(img_user_video);

            tv_username_video.setText(v.getUser_name());
            tv_des_videoPost.setText(v.getDescription());
            Glide.with(context).load(v.getThumbnail_img()).centerCrop().into(imageView);

            Map<String, String> header = new HashMap<>();
            header.put("ee", "33");
            //使用exo內核
            PlayerFactory.setPlayManager(Exo2PlayerManager.class);

            //使用exo緩存方式
            CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
            //防止错位，离开释放
            //gsyVideoPlayer.initUIState();
            gsyVideoOptionBuilder

                    .setIsTouchWiget(false)
                    .setThumbImageView(imageView)
                    .setUrl(v.getVideo_url())
                    .setCacheWithPlay(true)
                    .setRotateViewAuto(true)
                    .setRotateWithSystem(true)
                    .setLockLand(false)
                    .setPlayTag(TAG)
                    .setMapHeadData(header)
                    .setShowFullAnimation(true)
                    .setNeedLockFull(false)
                    .setPlayPosition(position)
                    .setVideoAllCallBack(new GSYSampleCallBack() {
                        @Override
                        public void onPrepared(String url, Object... objects) {
                            super.onPrepared(url, objects);
                            if (!gsyVideoPlayer.isIfCurrentIsFullscreen()) {
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
                            gsyVideoPlayer.getCurrentPlayer().getTitleTextView().setText((String)objects[0]);
                        }
                    }).build(gsyVideoPlayer);


            //增加title
            gsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);

            //设置返回键
            gsyVideoPlayer.getBackButton().setVisibility(View.GONE);

            //设置全屏按键功能
            gsyVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resolveFullBtn(gsyVideoPlayer);
                }
            });

            //gsyVideoPlayer.loadCoverImageBy(v.getThumbnail_img(), v.getThumbnail_img());
        }

        /**
         * 全屏幕按键处理
         */
        private void resolveFullBtn(final StandardGSYVideoPlayer standardGSYVideoPlayer) {
            standardGSYVideoPlayer.startWindowFullscreen(context, true, true);
        }

    }



}
