package info.androidhive.firebaseauthapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.firebaseauthapp.Assymetric.AGVRecyclerViewAdapter;
import info.androidhive.firebaseauthapp.Assymetric.AsymmetricItem;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.classModels.FitClass;
import info.androidhive.firebaseauthapp.models.PicturePost;
import info.androidhive.firebaseauthapp.models.PicturePostGridImage;

class ChildAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PicturePostGridImage> items;
    private PicturePost picturePost;
    private Context context;

    public ChildAdapter(Context context, PicturePost picturePost,List<PicturePostGridImage> items) {
        this.items = items;
        this.context = context;
        this.picturePost = picturePost;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item,parent,false);
        return new childViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PicturePostGridImage pic = items.get(position);
        ((childViewHolder)holder).bind(pic,position,picturePost);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class childViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mImageView;
        private final TextView textView;

        public childViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.mImageView);
            textView = itemView.findViewById(R.id.tvCount);


        }

        //item 長度有多少 這整段 bind 就會跑幾次
        public void bind(PicturePostGridImage pics,int position,PicturePost post) {
            Log.e("in child adapter","bind has run ="+position+" times");
            Log.e("in child adapter","image path is="+pics.getImagePath());
            mImageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            Glide.with(context).load(pics.getImagePath()).centerCrop().into(mImageView);
            //mTotal:總共有幾張照片
            //mDisplay:總共有幾張已被展出
            int mDisplay= post.getmDisplay();
            int mTotal= post.getmTotal();
            ArrayList<PicturePostGridImage> totImages = post.getImages();

            textView.setText("+"+(mTotal - mDisplay+1));
            //Log.e("in child adapter","TOTAL ="+mTotal+"DISPLAY = "+mDisplay);
            //如果照片總數大於展出的照片數
//        Log.e("in child adapter","mTotal ="+mTotal+"mDisplay ="+mDisplay);
//            if(mTotal > mDisplay)
//            {
//                if(position  == mDisplay-1) {
//                    textView.setVisibility(View.VISIBLE);
//                    mImageView.setImageAlpha(72);
//                }
//                else {
//                    textView.setVisibility(View.INVISIBLE);
//                    mImageView.setImageAlpha(255);
//                }
//            }
//            //如果照片總數小於展出的照片數
//            else
//            {
//                mImageView.setImageAlpha(255);
//                textView.setVisibility(View.INVISIBLE);
//            }
        }
    }
}

