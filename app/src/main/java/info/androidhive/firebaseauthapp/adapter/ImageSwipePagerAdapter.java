package info.androidhive.firebaseauthapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.finsky.externalreferrer.IGetInstallReferrerService;

import java.util.List;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.util.TouchImageView;
import ua.zabelnikov.swipelayout.layout.frame.SwipeableLayout;
import ua.zabelnikov.swipelayout.layout.listener.OnLayoutPercentageChangeListener;
import ua.zabelnikov.swipelayout.layout.listener.OnLayoutSwipedListener;

public class ImageSwipePagerAdapter extends PagerAdapter  {
    public final List<String> items;
    private final Context context;
    private OnLayoutSwipedListener onLayoutSwipedListener;
    //======================\\

    public ImageSwipePagerAdapter(Context context, List<String> photos) {
        this.context = context;
        items = photos;
    }

    public void setOnLayoutSwipedListener(OnLayoutSwipedListener onLayoutSwipedListener) {
        this.onLayoutSwipedListener = onLayoutSwipedListener;
    }


    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        String link = items.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.viewpager_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        Glide.with(context).load(link).into(imageView);

        final View colorFrame = view.findViewById(R.id.colorContainer);
        SwipeableLayout swipeableLayout = (SwipeableLayout) view.findViewById(R.id.swipeableLayout);

        //監聽現在垂直滑動了幾度，根據度數來漸層背景
        swipeableLayout.setOnLayoutPercentageChangeListener(new OnLayoutPercentageChangeListener() {
            private float lastAlpha = 1.0f;

            @Override
            public void percentageY(float percentage) {
                float alphaCorrector = percentage / 2;
                Log.e("degree",String.valueOf(percentage));
                //透明度從1到alphaCorrector(0.5)
                AlphaAnimation alphaAnimation = new AlphaAnimation(lastAlpha, 1 - alphaCorrector);
                alphaAnimation.setDuration(300);
                colorFrame.startAnimation(alphaAnimation);
                lastAlpha = 1 - alphaCorrector;

            }
        });
        swipeableLayout.setOnSwipedListener(new OnLayoutSwipedListener() {
            @Override
            public void onLayoutSwiped() {
                if (onLayoutSwipedListener != null) {
                    onLayoutSwipedListener.onLayoutSwiped();
                }
            }
        });


        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return items.size();
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

}

