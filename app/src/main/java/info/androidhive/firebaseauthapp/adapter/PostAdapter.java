package info.androidhive.firebaseauthapp.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;

import java.util.List;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.models.Item;
import info.androidhive.firebaseauthapp.models.PicturePost;
import info.androidhive.firebaseauthapp.models.TextPost;
import info.androidhive.firebaseauthapp.models.VideoPost;
import info.androidhive.firebaseauthapp.util.EmptyControlVideo;

public class PostAdapter extends RecyclerView.Adapter {
    public final static String TAG = "RecyclerView2List";
    //recyclerView將要展示的元素
    private List<Item> items;
    //requestManager管理Glide的實體
    //private RequestManager requestManager;

    public PostAdapter(List<Item> items ) {
        this.items = items;

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
                            R.layout.item_imagepost
                            ,parent
                            ,false
                    )
            );
        }else if (viewType ==1){
            return  new TextPostViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.item_textpost
                            ,parent
                            ,false
                    )
            );
        }else{

            return  new VideoPostViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.item_videopost
                            ,parent
                            ,false
                    )
            );

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //如果第n個位置傳過來的getItemViewType是0
        if(getItemViewType(position)==0){
            //picturePost代表在items裡面第n個位置的物件，並將其轉型為PicturePost
            PicturePost picturePost = (PicturePost)items.get(position).getObject();
            ((pictuerPostViewHolder)holder).set_picPost_Content(picturePost);
        }
        //如果第n個位置傳過來的getItemViewType是1
        else if(getItemViewType(position)==1){
            //textPost代表在items裡面第n個位置的物件，並將其轉型為TextPost
            TextPost textPost = (TextPost)items.get(position).getObject();
            ((TextPostViewHolder)holder).set_textPost_Content(textPost);
        }else{
            VideoPost videoPost = (VideoPost)items.get(position).getObject();
            //((VideoPostViewHolder)holder).set_videoPost_Content(videoPost);
            //創造一個holder
            VideoPostViewHolder recyclerItemViewHolder = (VideoPostViewHolder) holder;
            //將holder設定一個RecyclerBaseAdapter

            recyclerItemViewHolder.set_videoPost_Content(videoPost);

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
    static class pictuerPostViewHolder extends RecyclerView.ViewHolder{

        private ImageView img_posting,img_user;
        private TextView tv_username_pic, tv_picPost,tv_des_picPost;

        public pictuerPostViewHolder(@NonNull View itemView) {
            super(itemView);
            img_posting = itemView.findViewById(R.id.img_cover_imgpost);
            img_user = itemView.findViewById(R.id.img_user_imgpost);
            tv_username_pic = itemView.findViewById(R.id.tv_user_imgpost);
            tv_picPost = itemView.findViewById(R.id.tv_title_imgpost);
            tv_des_picPost = itemView.findViewById(R.id.tv_content_img_post);

        }
        void set_picPost_Content(PicturePost p){
            img_posting.setImageResource(p.getPicImg());
            img_user.setImageResource(p.getUser_avatar());
            tv_username_pic.setText(p.getUser_name());
            tv_picPost.setText(p.getTitle());
            tv_des_picPost.setText(p.getDescription());
        }
    }

    static class TextPostViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_user_text;
        private TextView tv_username_text, tv_textPost, tv_des_textPost;

        public TextPostViewHolder(@NonNull View itemView) {
            super(itemView);

            img_user_text = itemView.findViewById(R.id.img_user_txtpost);
            tv_username_text = itemView.findViewById(R.id.tv_user_txtpost);
            tv_textPost = itemView.findViewById(R.id.tv_title_txtpost);
            tv_des_textPost = itemView.findViewById(R.id.tv_content_txtpost);
        }

        void set_textPost_Content(TextPost t) {
            img_user_text.setImageResource(t.getUser_avatar());
            tv_username_text.setText(t.getUser_name());
            tv_textPost.setText(t.getTitle());
            tv_des_textPost.setText(t.getDescription());
        }



    }

    static class VideoPostViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        private EmptyControlVideo gsyVideoPlayer;
        private ImageView img_user_video;
        private TextView tv_username_video, tv_videoPost, tv_des_videoPost;
        GSYVideoOptionBuilder gsyVideoOptionBuilder;

        public VideoPostViewHolder(@NonNull View itemView) {
            super(itemView);
            gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
            imageView = new ImageView(itemView.getContext());
            gsyVideoPlayer = itemView.findViewById(R.id.detail_player);
            img_user_video = itemView.findViewById(R.id.img_user_video);
            tv_username_video = itemView.findViewById(R.id.tv_username_video);
            tv_videoPost = itemView.findViewById(R.id.tv_videoPost);
            tv_des_videoPost = itemView.findViewById(R.id.tv_des_videoPost);
        }

        void set_videoPost_Content(VideoPost v) {

            gsyVideoPlayer.setUp(v.getVideo_url(), true, "test");

            imageView.setImageResource(v.getThumbnail_img());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            gsyVideoPlayer.setThumbImageView(imageView);


            img_user_video.setImageResource(v.getUser_avatar());
            tv_username_video.setText(v.getUser_name());
            tv_videoPost.setText(v.getTitle());
            tv_des_videoPost.setText(v.getDescription());
        }
    }

}
