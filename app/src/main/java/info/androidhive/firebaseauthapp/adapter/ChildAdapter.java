package info.androidhive.firebaseauthapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.firebaseauthapp.Assymetric.AGVRecyclerViewAdapter;
import info.androidhive.firebaseauthapp.Assymetric.AsymmetricItem;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.models.PicturePostGridImage;

class ChildAdapter extends AGVRecyclerViewAdapter<ViewHolder> {
    private final List<PicturePostGridImage> items;
    private int mDisplay = 0;
    private int mTotal = 0;
    private Context context;

    public ChildAdapter(List<PicturePostGridImage> items , Context context, int mDisplay, int mTotal) {
        this.items = items;
        this.mDisplay = mDisplay;
        this.mTotal = mTotal;
        this.context = context;

    }




    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("RecyclerViewActivity", "onCreateView");
        return new ViewHolder(parent);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("RecyclerViewActivity", "onBindView position=" + position);
        holder.bind(items,context,position,mDisplay,mTotal);
    }

    @Override public int getItemCount() {
        return items.size();
    }

    @Override public AsymmetricItem getItem(int position) {
        return (AsymmetricItem) items.get(position);
    }

    @Override public int getItemViewType(int position) {
        return 0;
    }
}


class ViewHolder extends RecyclerView.ViewHolder {
    private final ImageView mImageView;
    private final TextView textView;

    public ViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_item, parent, false));

        mImageView = (ImageView) itemView.findViewById(R.id.mImageView);
        textView = (TextView) itemView.findViewById(R.id.tvCount);



    }

    //item 長度有多少 這整段 bind 就會跑幾次
    public void bind(List<PicturePostGridImage> item,Context context, int position, int mDisplay, int mTotal) {
//        Log.e("in child adapter","bind has run ="+position+" times");
        Glide.with(context).load(String.valueOf(item.get(position).getImagePath())).centerCrop().into(mImageView);
        //mTotal:總共有幾張照片
        //mDisplay:總共有幾張已被展出
        textView.setText("+"+(mTotal - mDisplay+1));
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "total :" + mTotal + " visible :"+mDisplay, Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "clicked :"+position+" mDisplay-1 ="+(mDisplay-1), Toast.LENGTH_SHORT).show();
            }
        });
        //如果照片總數大於展出的照片數
//        Log.e("in child adapter","mTotal ="+mTotal+"mDisplay ="+mDisplay);
        if(mTotal > mDisplay)
        {
            if(position  == mDisplay-1) {
                textView.setVisibility(View.VISIBLE);
                mImageView.setImageAlpha(72);
            }
            else {
                textView.setVisibility(View.INVISIBLE);
                mImageView.setImageAlpha(255);
            }
        }
        //如果照片總數小於展出的照片數
        else
        {
            mImageView.setImageAlpha(255);
            textView.setVisibility(View.INVISIBLE);
        }
    }
}