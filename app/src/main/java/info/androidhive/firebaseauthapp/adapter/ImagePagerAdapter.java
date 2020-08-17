package info.androidhive.firebaseauthapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import info.androidhive.firebaseauthapp.ImageViewPagerActivity;
import info.androidhive.firebaseauthapp.models.PicturePostGridImage;

public class ImagePagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> images ;

    public ImagePagerAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        Glide.with(context).load(images.get(position)).centerCrop().into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"u clicked"+position,Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context, ImageViewPagerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("images",images);
                bundle.putInt("position",position);
                i.putExtras(bundle);
                context.startActivity(i);
            }
        });
        container.addView(imageView,0);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView)object);
    }
}
