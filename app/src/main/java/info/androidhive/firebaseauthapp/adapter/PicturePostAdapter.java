package info.androidhive.firebaseauthapp.adapter;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.firebaseauthapp.Fragments.CommentFragment;
import info.androidhive.firebaseauthapp.HomeActivity;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.manager.ManagerPage;
import info.androidhive.firebaseauthapp.models.Comments;
import info.androidhive.firebaseauthapp.models.Item;
import info.androidhive.firebaseauthapp.models.Likes;
import info.androidhive.firebaseauthapp.models.PicturePost;
import info.androidhive.firebaseauthapp.models.PicturePostGridImage;
import info.androidhive.firebaseauthapp.models.TextPost;
import info.androidhive.firebaseauthapp.models.VideoPost;
import info.androidhive.firebaseauthapp.util.SampleCoverVideo;
import info.androidhive.firebaseauthapp.util.SpacesItemDecoration;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

import static com.facebook.internal.CallbackManagerImpl.RequestCodeOffset.Like;

//Adapter
public class PicturePostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static String TAG = "RecyclerView2List";
    //recyclerView將要展示的元素
    private List<Item> items;
//    private ArrayList<Object> likesList;
//    ArrayList<Likes> loadedlist = new ArrayList<>();
    //requestManager管理Glide的實體
    //private RequestManager requestManager;
    private Context context = null;
    private GSYVideoHelper smallVideoHelper;
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    private  OnItemClickedListener mListener;
    private int AnimId = R.anim.left_to_right;
    ArrayList<Likes> likesArrayList = new ArrayList<>();

    public boolean isShimmer = true;//judge if shimmer or not
    int SHIMMER_ITEM_NUMBER = 5;//numbers of item shimmer



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
        }else if (viewType ==2){

            View v = LayoutInflater.from(context).inflate(R.layout.item_container_videopost, parent, false);
            final RecyclerView.ViewHolder holder = new RecyclerItemNormalHolder(context, v);
            return holder;
        }
        ///shimmer layout
        else{
            return  new TextPostViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.item_container_textpost
                            ,parent
                            ,false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //如果第n個位置傳過來的getItemViewType是0
        if (isShimmer){
            ((TextPostViewHolder)holder).shimmer_textpost.startShimmer();
        }else{

            if(getItemViewType(position)==0){
                //picturePost代表在items裡面第n個位置的物件，並將其轉型為PicturePost
                PicturePost picturePost = (PicturePost)items.get(position).getObject();
                ((pictuerPostViewHolder)holder).set_picPost_Content(picturePost,position ,holder);
            }
            //如果第n個位置傳過來的getItemViewType是1
            else if(getItemViewType(position)==1){
                //textPost代表在items裡面第n個位置的物件，並將其轉型為TextPost
                ((TextPostViewHolder)holder).shimmer_textpost.stopShimmer();
                ((TextPostViewHolder)holder).shimmer_textpost.setShimmer(null);
                TextPost textPost = (TextPost)items.get(position).getObject();
                ((TextPostViewHolder)holder).set_textPost_Content(textPost);
            }else if (getItemViewType(position)==2){

                //創造一個holder
                RecyclerItemNormalHolder recyclerItemViewHolder = (RecyclerItemNormalHolder) holder;
                //將holder設定一個RecyclerBaseAdapter
                //recyclerItemViewHolder.setRecyclerBaseAdapter(this);
                VideoPost videoPost = (VideoPost)items.get(position).getObject();
                recyclerItemViewHolder.onBind(position, videoPost);
            }
            //shimmer layout
            else{
                ((TextPostViewHolder)holder).shimmer_textpost.stopShimmer();
                ((TextPostViewHolder)holder).shimmer_textpost.setShimmer(null);
            }
        }


    }

    @Override
    public int getItemCount() {
        return isShimmer?SHIMMER_ITEM_NUMBER:items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isShimmer){
            return 100;
        }else {
            return items.get(position).getType();
        }

    }



    //===========================ViewHolders===============================//

    //picture post
    public class pictuerPostViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView_picpost;


        private LinearLayout lv_pic_like,lv_pic_comment;
        public ViewPager image_slider;
        private ImageView img_user,btn_like_picpost;
        private TextView tv_username_pic, tv_img_count,tv_time_picpost,tv_likecount_pic,tv_commentcount_pic;
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
            lv_pic_comment=itemView.findViewById(R.id.lv_pic_comment);
            lv_pic_like = itemView.findViewById(R.id.lv_pic_like);
            tv_time_picpost = itemView.findViewById(R.id.tv_time_picpost);
            btn_like_picpost = itemView.findViewById(R.id.btn_like_picpost);
            tv_likecount_pic = itemView.findViewById(R.id.tv_likecount_pic);
            tv_commentcount_pic = itemView.findViewById(R.id.tv_commentcount_pic);
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
            long time=p.getPostTime();//long now = android.os.SystemClock.uptimeMillis();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1=new Date(time);
            String t1=format.format(d1);
            tv_time_picpost.setText(t1);

            listenForComments(p.getPostID(), new CommentChangeListener() {
                @Override
                public void onCommentChanged(int count) {
                    tv_commentcount_pic.setText(count+" 條留言");
                }
            });

            listenForLikes(p.getPostID(), new LikeChangeListener() {
                @Override
                public void onLikeChanged(int count, boolean hasliked) {
                    tv_likecount_pic.setText(count+" 個讚");
                    if (hasliked){
                        btn_like_picpost.setImageResource(R.drawable.ic_like_blue);
                    }else {
                        btn_like_picpost.setImageResource(R.drawable.ic_like_empty);
                    }
                }
            });

//            Log.e("images",p.getImages().size()+"");
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

            lv_pic_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "u ckicked comment on"+p.getPostID(), Toast.LENGTH_SHORT).show();
                    CommentFragment commentFragment =new CommentFragment();
                    Bundle bundle = new Bundle();

                    bundle.putString("id", p.getPostID());
                    commentFragment.setArguments(bundle);
                    commentFragment.show(((ManagerPage) context).getSupportFragmentManager(),commentFragment.getTag());
                }
            });
            lv_pic_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "u ckicked like on"+p.getPostID(), Toast.LENGTH_SHORT).show();
                    //readLikesFromFirebase(p.getPostID());
                    readLikesFromFirebase(p.getPostID(), new ReadLikeCalback() {
                        @Override
                        public void onCallBacked(boolean needRemove) {
                            Log.e("final like",""+needRemove);
                            if (!needRemove){
                                btn_like_picpost.setImageResource(R.drawable.ic_like_blue);
                            }
                            else {
                                btn_like_picpost.setImageResource(R.drawable.ic_like_empty);
                            }
                        }
                    });
                }
            });
        }
    }

    public class TextPostViewHolder extends RecyclerView.ViewHolder {
        private ShimmerFrameLayout shimmer_textpost;
        private CardView cardView_textpost;
        private ImageView img_user_text,btn_like_textpost,btn_comment_textpost;
        private TextView tv_username_text, tv_time_textPost,tv_likecount_text,tv_commentcount_text;
        private ExpandableTextView tv_des_textPost;
        private LinearLayout lv_text_like,lv_text_comment;
        public TextPostViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView_textpost =itemView.findViewById(R.id.cardview_textpost);
            img_user_text = itemView.findViewById(R.id.img_user_textpost);
            tv_username_text = itemView.findViewById(R.id.tv_user_textpost);
            tv_des_textPost = itemView.findViewById(R.id.expand_text_view_textpost);
            tv_time_textPost = itemView.findViewById(R.id.tv_time_textpost);
            lv_text_comment = itemView.findViewById(R.id.lv_text_comment);
            lv_text_like = itemView.findViewById(R.id.lv_text_like);
            btn_like_textpost = itemView.findViewById(R.id.btn_like_textpost);
            btn_comment_textpost = itemView.findViewById(R.id.btn_comment_textpost);
            tv_likecount_text = itemView.findViewById(R.id.tv_likecount_text);
            tv_commentcount_text = itemView.findViewById(R.id.tv_commentcount_text);
            shimmer_textpost = itemView.findViewById(R.id.shimmer_textpost);
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

        //text post
        // TODO: 2020/9/9 案讚功能有問題待修，以textpost為基準測試
        void set_textPost_Content(TextPost t) {
            Glide.with(context).load(t.getUser_avatar()).centerCrop().into(img_user_text);

            tv_username_text.setBackground(null);
            tv_username_text.setText(t.getUser_name());

            tv_des_textPost.setBackground(null);
            tv_des_textPost.setText(t.getDescription());

            long time=t.getPostTime();//long now = android.os.SystemClock.uptimeMillis();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1=new Date(time);
            String t1=format.format(d1);
            tv_time_textPost.setBackground(null);
            tv_time_textPost.setText(t1);

            btn_like_textpost.setBackground(null);
            btn_comment_textpost.setBackground(null);
            tv_commentcount_text.setBackground(null);
            tv_likecount_text.setBackground(null);

            listenForComments(t.getPostID(), new CommentChangeListener() {
                @Override
                public void onCommentChanged(int count) {
                    tv_commentcount_text.setText(count+" 條留言");
                }
            });

            listenForLikes(t.getPostID(), new LikeChangeListener() {
                @Override
                public void onLikeChanged(int count ,boolean hasliked) {
                    tv_likecount_text.setText(count+" 個讚");
                    if (hasliked){
                        btn_like_textpost.setImageResource(R.drawable.ic_like_blue);
                    }else {
                        btn_like_textpost.setImageResource(R.drawable.ic_like_empty);
                    }
                }
            });

            lv_text_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "u clicked like on"+t.getPostID(), Toast.LENGTH_SHORT).show();
                    //readLikesFromFirebase(t.getPostID());
                    readLikesFromFirebase(t.getPostID(), new ReadLikeCalback() {
                        @Override
                        public void onCallBacked(boolean needRemove) {
                            Log.e("final like",""+needRemove);
                            if (!needRemove){
                                btn_like_textpost.setImageResource(R.drawable.ic_like_blue);
                            }
                            else {
                                btn_like_textpost.setImageResource(R.drawable.ic_like_empty);
                            }
                        }
                    });
                }
            });
            lv_text_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "u ckicked comment on"+t.getPostID(), Toast.LENGTH_SHORT).show();
                    CommentFragment commentFragment = new CommentFragment();
                    Bundle bundle = new Bundle();

                    bundle.putString("id", t.getPostID());
                    commentFragment.setArguments(bundle);
                    commentFragment.show(((ManagerPage) context).getSupportFragmentManager(),commentFragment.getTag());
                }
            });
        }

    }

    //video post
    public class RecyclerItemNormalHolder extends RecyclerItemBaseHolder {

        public final static String TAG = "RecyclerView2List";
        protected Context context = null;


        SampleCoverVideo gsyVideoPlayer;
        private CardView cardView_videopost;
        ImageView imageView ,btn_like_videopost;
        private ImageView img_user_video;
        private TextView tv_username_video, tv_time_videoPost,tv_likecount_video,tv_commentcount_video;
        GSYVideoOptionBuilder gsyVideoOptionBuilder;
        private ExpandableTextView tv_des_videoPost;
        private LinearLayout lv_video_like,lv_video_comment;
        public RecyclerItemNormalHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;

            imageView = new ImageView(context);
            cardView_videopost =itemView.findViewById(R.id.cardview_videopost);
            img_user_video = itemView.findViewById(R.id.img_user_videopost);
            tv_username_video = itemView.findViewById(R.id.tv_user_videopost);
            tv_des_videoPost = itemView.findViewById(R.id.expand_text_view_videopost);
            lv_video_comment = itemView.findViewById(R.id.lv_video_comment);
            lv_video_like=itemView.findViewById(R.id.lv_video_like);
            tv_time_videoPost=itemView.findViewById(R.id.tv_time_videopost);
            gsyVideoPlayer = itemView.findViewById(R.id.video_item_player);
            btn_like_videopost = itemView.findViewById(R.id.btn_like_videopost);
            tv_likecount_video = itemView.findViewById(R.id.tv_likecount_video);
            tv_commentcount_video = itemView.findViewById(R.id.tv_commentcount_video);

            gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
//            cardView_videopost.setAnimation(AnimationUtils.loadAnimation(context,AnimId));
            itemView.setOnClickListener(new View.OnClickListener() {
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

        public void onBind(final int position, VideoPost videoPost) {

            Glide.with(context).load(videoPost.getUser_avatar()).centerCrop().into(img_user_video);
            long time=videoPost.getPostTime();//long now = android.os.SystemClock.uptimeMillis();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1=new Date(time);
            String t1=format.format(d1);
            tv_time_videoPost.setText(t1);
            tv_username_video.setText(videoPost.getUser_name());
            tv_des_videoPost.setText(videoPost.getDescription());
            Glide.with(context).load(videoPost.getThumbnail_img()).centerCrop().into(imageView);

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
                    .setUrl(videoPost.getVideo_url())
                    .setCacheWithPlay(true)
                    .setRotateViewAuto(true)
                    .setRotateWithSystem(true)
                    .setLockLand(false)
//                    .setPlayTag(TAG)
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

            listenForComments(videoPost.getPostID(), new CommentChangeListener() {
                @Override
                public void onCommentChanged(int count) {
                    tv_commentcount_video.setText(count+" 條留言");
                }
            });

            listenForLikes(videoPost.getPostID(), new LikeChangeListener() {
                @Override
                public void onLikeChanged(int count, boolean hasliked) {
                    tv_likecount_video.setText(count+" 個讚");
                    if (hasliked){
                        btn_like_videopost.setImageResource(R.drawable.ic_like_blue);
                    }else {
                        btn_like_videopost.setImageResource(R.drawable.ic_like_empty);
                    }
                }
            });

            lv_video_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    readLikesFromFirebase(videoPost.getPostID(), new ReadLikeCalback() {
                        @Override
                        public void onCallBacked(boolean needRemove) {
                            Log.e("final like",""+needRemove);
                            if (!needRemove){
                                btn_like_videopost.setImageResource(R.drawable.ic_like_blue);
                            }
                            else {
                                btn_like_videopost.setImageResource(R.drawable.ic_like_empty);
                            }
                        }
                    });
                }
            });
            lv_video_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "u ckicked comment on"+videoPost.getPostID(), Toast.LENGTH_SHORT).show();
                    CommentFragment commentFragment = new CommentFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", videoPost.getPostID());

                    commentFragment.setArguments(bundle);
                    commentFragment.show(((ManagerPage) context).getSupportFragmentManager(),commentFragment.getTag());
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

    //負責初次載入時檢查likes list
    //由資料庫變動觸發
    private void listenForLikes(String postId,LikeChangeListener likeChangeListener) {
        databaseReference.child("posting").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("likes")){
                    ArrayList<Object> loadList = (ArrayList<Object>) dataSnapshot.child("likes").getValue();
                    ArrayList<Likes> getLikes=new ArrayList<>();
                    boolean userHasLiked = false;
                    for (Object o:loadList){
                        Map<String, Object> map = (Map<String, Object>) o;
                        Likes likes = new Likes((long)map.get("likeTime"),(String)map.get("user_avatar"),(String)map.get("user_Id"),(String)map.get("user_name"));
                        getLikes.add(likes);
                    }
                    for (Likes likes :getLikes){
                        if (likes.getUser_Id().equals(firebaseAuth.getCurrentUser().getUid())){
                            userHasLiked = true;
                        }
                    }

                    likeChangeListener.onLikeChanged(getLikes.size(),userHasLiked);
                }else {
                    likeChangeListener.onLikeChanged(0,false);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //負責判斷當前使用者是否有like這篇貼文，沒有like的話幫like，有的話就幫取消
    //由使用者點案like按鍵觸發
    private void readLikesFromFirebase(String postId, ReadLikeCalback likeCalback){


        databaseReference.child("posting").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likesArrayList.clear();
                //如果有likes
                if (dataSnapshot.hasChild("likes")){
                    //先把所有的likes load下來
                    Toast.makeText(context, "has liked", Toast.LENGTH_SHORT).show();
                    ArrayList<Object> loadedList;
                    loadedList = (ArrayList<Object>) dataSnapshot.child("likes").getValue();
                    for (Object o:loadedList){
                        Map<String, Object> map = (Map<String, Object>) o;
                        Likes c = new Likes((long)map.get("likeTime"),(String)map.get("user_avatar"),(String)map.get("user_Id"),(String)map.get("user_name"));
                        likesArrayList.add(c);
                    }
                    boolean userliked = false;
                    //needRemoveLike == true,移除like needRemoveLike == false,like顯示
                    boolean needRemoveLike = false;
                    //現在已取得使用者的like
                    for (Likes likes:likesArrayList){
                        Log.e("read id",""+likes.getUser_Id());
                        Log.e("current id",""+firebaseAuth.getCurrentUser().getUid());
                        if(likes.getUser_Id().equals(firebaseAuth.getCurrentUser().getUid())){
                            Log.e("already liked","already liked");
                            userliked = true;
                            needRemoveLike = true;
                            //如果使用者有案讚
                            //幫她移除
                            likesArrayList.remove(likes);

                            break;
                        }else{
                            Log.e("hasent liked yet","hasent liked yet");
                            userliked = false;
                            needRemoveLike = false;
                        }
                    }

                    Log.e("like staus",""+userliked);
                    //如果使用者沒案讚
                    //幫他案
                    if (!userliked){
                        Log.e("user unlikes","幫他案");
                        Likes likes = new Likes();
                            likes.setLikeTime(System.currentTimeMillis());
                            likes.setUser_avatar(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                            likes.setUser_Id(firebaseAuth.getCurrentUser().getUid());
                            likes.setUser_name(firebaseAuth.getCurrentUser().getDisplayName());
                            likesArrayList.add(likes);

                    }
                    //將完成後的likesArrayList推上去
                    boolean finalneedRemoved = needRemoveLike;
                    databaseReference.child("posting").child(postId).child("likes").setValue(likesArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        likeCalback.onCallBacked(finalneedRemoved);
                                    }
                                }
                            });

                //如果沒有likes
                }else{

                    Likes likes = new Likes();
                    likes.setLikeTime(System.currentTimeMillis());
                    likes.setUser_avatar(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                    likes.setUser_Id(firebaseAuth.getCurrentUser().getUid());
                    likes.setUser_name(firebaseAuth.getCurrentUser().getDisplayName());
                    likesArrayList.add(likes);
                    databaseReference.child("posting").child(postId).child("likes").setValue(likesArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.e("likes","0讚，案讚");
                                likeCalback.onCallBacked(false);
                            }else{
                                Toast.makeText(context, "上船失敗，請檢察網路", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //負責初次載入時檢查likes list
    //由資料庫變動觸發
    private void listenForComments(String postId,CommentChangeListener commentChangeListener) {
        databaseReference.child("posting").child(postId).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Object> loadList = (ArrayList<Object>) dataSnapshot.getValue();
                ArrayList<Comments> getComments=new ArrayList<>();
                Log.e("getComment size", getComments.size()+"");
                for (Object o:loadList){
                    Map<String, Object> map = (Map<String, Object>) o;
                    Comments c = new Comments((long)map.get("commentTime"),(String) map.get("comment"),(String)map.get("user_avatar"),(String)map.get("user_name"),(String)map.get("user_Id"));
                    if (c.getUser_Id()!=null){
                        getComments.add(c);
                    }

                }
                commentChangeListener.onCommentChanged(getComments.size());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    interface ReadLikeCalback{
        void onCallBacked(boolean needRemove);

    }
    interface LikeChangeListener{
        void onLikeChanged(int count,boolean hasliked);

    }

    interface CommentChangeListener{
        void onCommentChanged(int count);

    }
    public void doFilterList(ArrayList<Item> filteredList) {
        items = filteredList;
        notifyDataSetChanged();
    }

}
