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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.classModels.FitClass;
import info.androidhive.firebaseauthapp.models.Comments;
import info.androidhive.firebaseauthapp.util.DiffUtilCallback;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Comments> commentList;

    public CommentAdapter(Context context, List<Comments> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    public void insertData(List<Comments> insertList){
        //this will add new data to list
        DiffUtilCallback diffUtilCallback = new DiffUtilCallback(commentList,insertList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);

        commentList.addAll(insertList);
        diffResult.dispatchUpdatesTo(this);
    }

    public void updateData(List<Comments> newList){
        //this will clear old data add new data
        DiffUtilCallback diffUtilCallback = new DiffUtilCallback(commentList,newList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
        commentList.clear();
        commentList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
        return new CommentViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Log.e("type",commentList.get(position).getClass().toString());
        Comments comments = commentList.get(position);
        ((CommentAdapter.CommentViewHolder)holder).setCommentInfo(comments,position);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView tv_username_comment,tv_comment_time,tv_comment;
        ImageView img_user_comment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_comment = itemView.findViewById(R.id.tv_comment);
            tv_comment_time = itemView.findViewById(R.id.tv_comment_time);
            tv_username_comment = itemView.findViewById(R.id.tv_username_comment);
            img_user_comment = itemView.findViewById(R.id.img_user_comment);

        }

        void setCommentInfo(Comments comments, int position){
            tv_comment.setText(comments.getComment());
            tv_username_comment.setText(comments.getUser_name());
            Glide.with(context).load(comments.getUser_avatar()).into(img_user_comment);

            long time=comments.getCommentTime();//long now = android.os.SystemClock.uptimeMillis();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1=new Date(time);
            String t1=format.format(d1);
            tv_comment_time.setText(t1);
        }
    }
//    private ArrayList<Comments> getCommentList(){
//        ArrayList<Comments> comments = new ArrayList<>();
//        Comments c1 = new Comments(1111,"eduwdwugd","dwdwdwdw","bob","dsdsfsfsee");
//        Comments c2 = new Comments(1111,"eduwdwugd","dwdwdwdw","bob","dsdsfsfsee");
//        Comments c3 = new Comments(1111,"eduwdwugd","dwdwdwdw","bob","dsdsfsfsee");
//
//        comments.add(c1);
//        comments.add(c2);
//        comments.add(c3);
//
//        return comments;
//    }

}
