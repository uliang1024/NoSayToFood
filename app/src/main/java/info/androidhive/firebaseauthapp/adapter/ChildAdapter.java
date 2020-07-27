package info.androidhive.firebaseauthapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;

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

        return new ViewHolder(parent, viewType,items);
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
        return position % 2 == 0 ? 1 : 0;
    }
}


class ViewHolder extends RecyclerView.ViewHolder {
    private final ImageView mImageView;
    private final TextView textView;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public ViewHolder(ViewGroup parent, int viewType, List<PicturePostGridImage> items) {
        super(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_item, parent, false));

        mImageView = (ImageView) itemView.findViewById(R.id.mImageView);
        textView = (TextView) itemView.findViewById(R.id.tvCount);



    }


    public void bind(List<PicturePostGridImage> item,Context context, int position, int mDisplay, int mTotal) {
        Glide.with(context).load(String.valueOf(item.get(position).getImagePath())).into(mImageView);
        //imageLoader.displayImage(String.valueOf(item.get(position).getImagePath()), mImageView);
        textView.setText("+"+(mTotal-mDisplay));
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
        else
        {
            mImageView.setImageAlpha(255);
            textView.setVisibility(View.INVISIBLE);
        }

        // textView.setText(String.valueOf(item.getPosition()));
    }
}