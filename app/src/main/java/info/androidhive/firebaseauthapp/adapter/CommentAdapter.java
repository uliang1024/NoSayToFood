package info.androidhive.firebaseauthapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.classModels.FitClass;
import info.androidhive.firebaseauthapp.models.Comments;
import info.androidhive.firebaseauthapp.util.DiffUtilCallback;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    Context context;
    List<Comments> commentList;
    public boolean isShimmer = true;//to judge shimmer or not
    int SHIMMER_ITEM_NUMBER = 8;//numbers of shimmer shown during loading

    public CommentAdapter(Context context, List<Comments> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
        return new CommentViewHolder(view);

    }



    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder , int position) {
        //Log.e("type",commentList.get(position).getClass().toString());
        if (isShimmer){
            holder.shimmer_layout.startShimmer();//start shimmer
        }else{
            holder.shimmer_layout.stopShimmer();//stop shimmer
            holder.shimmer_layout.setShimmer(null);//remove shimmer overlay
            Comments comments = commentList.get(position);
            ((CommentAdapter.CommentViewHolder)holder).setCommentInfo(comments,position);
        }

    }

    @Override
    public int getItemCount() {
        return isShimmer?SHIMMER_ITEM_NUMBER:commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ShimmerFrameLayout shimmer_layout;
        TextView tv_username_comment,tv_comment_time,tv_comment;
        ImageView img_user_comment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_comment = itemView.findViewById(R.id.tv_comment);
            tv_comment_time = itemView.findViewById(R.id.tv_comment_time);
            tv_username_comment = itemView.findViewById(R.id.tv_username_comment);
            img_user_comment = itemView.findViewById(R.id.img_user_comment);
            shimmer_layout = itemView.findViewById(R.id.shimmer_layout_comment);

        }

        void setCommentInfo(Comments comments, int position){
            tv_comment.setBackground(null);
            tv_comment.setText(comments.getComment());

            tv_username_comment.setBackground(null);
            tv_username_comment.setText(comments.getUser_name());


            Glide.with(context).load(comments.getUser_avatar()).into(img_user_comment);

            long time=comments.getCommentTime();//long now = android.os.SystemClock.uptimeMillis();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1=new Date(time);
            String t1=format.format(d1);
            tv_comment_time.setBackground(null);
            tv_comment_time.setText(t1);
        }
    }


}
