package info.androidhive.firebaseauthapp.htmlTextViewUtil;

import android.content.Context;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import info.androidhive.firebaseauthapp.R;

public class ImageLoad {
    public static void loadPlaceholder(Context context, String url, Target target) {

        Picasso picasso = new Picasso.Builder(context).loggingEnabled(true).build();
        picasso.load(url)
                .placeholder(R.drawable.moren)
                .error(R.drawable.moren)
                //.transform(new ImageTransform())
//                .transform(new CompressTransformation())
                .into(target);
    }
}
