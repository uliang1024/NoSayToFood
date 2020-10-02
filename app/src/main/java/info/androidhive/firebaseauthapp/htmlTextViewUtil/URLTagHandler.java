package info.androidhive.firebaseauthapp.htmlTextViewUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.xml.sax.XMLReader;

import java.util.Locale;
import java.util.Stack;

import info.androidhive.firebaseauthapp.R;

public class URLTagHandler implements Html.TagHandler {
    /**
     * html 标签的开始下标
     */
    private Stack<Integer> startIndex;

    /**
     * html的标签的属性值 value，如:<size value='16'></size>
     * 注：value的值不能带有单位,默认就是sp
     */
    private Stack<String> propertyValue;

    private Context mContext;
    private PopupWindow popupWindow;
    //需要放大的图片
    private ZoomImageView tecent_chat_image;

    public URLTagHandler(Context context) {
        mContext = context.getApplicationContext();
        View popView = LayoutInflater.from(context).inflate(R.layout.pub_zoom_popwindow_layout, null);
        tecent_chat_image = (ZoomImageView) popView.findViewById(R.id.image_scale_image);

        popView.findViewById(R.id.image_scale_rll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);// 设置允许在外点击消失
        ColorDrawable dw = new ColorDrawable(0x50000000);
        popupWindow.setBackgroundDrawable(dw);
    }



    @Override
    //處理那些html的標籤
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        Log.e("处理标签","yes");
        // 处理标签<img>
        if (tag.toLowerCase(Locale.getDefault()).equals("img")) {
            // 获取长度
            int len = output.length();
            // 获取图片地址
            ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
            String imgURL = images[0].getSource();
            // 使图片可点击并监听点击事件
            output.setSpan(new ClickableImage(mContext, imgURL), len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (tag.toLowerCase(Locale.getDefault()).equals("blockquote")) {
            // 获取长度
            Log.e("blockquote","yes");
        }

        //自定義標籤屬性
        if (opening){
            Log.e("標籤頭","yes");
            //處理自定意標籤開頭
            handlerStartTAG(tag, output, xmlReader);

        }
        else{
            Log.e("標籤尾","yes");
            //處理自定意標籤結尾
            handlerEndTAG(tag, output);
        }

    }

    /**
     * 处理开始的标签位
     *
     * @param tag
     * @param output
     * @param xmlReader
     */
    private void handlerStartTAG(String tag, Editable output, XMLReader xmlReader) {
        if (tag.equalsIgnoreCase("del")) {
            //handlerStartDEL(output);
        } else if (tag.toLowerCase(Locale.getDefault()).equals("blockquote")) {
            Log.e("foont","yes");
            //handlerStartSIZE(output, xmlReader);
        }
    }

    /**
     * 处理结尾的标签位
     *
     * @param tag
     * @param output
     */
    private void handlerEndTAG(String tag, Editable output) {
        if (tag.equalsIgnoreCase("del")) {
            //handlerEndDEL(output);
        } else if (tag.toLowerCase(Locale.getDefault()).equals("blockquote")) {
            Log.e("foont","yes");
            //handlerEndSIZE(output);
        }
    }







    private class ClickableImage extends ClickableSpan {
        private String url;
        private Context context;

        public ClickableImage(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        @Override
        public void onClick(View widget) {
            // 进行图片点击之后的处理

            Log.e("onClick","點擊圖片");
            popupWindow.showAtLocation(widget, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    tecent_chat_image.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    tecent_chat_image.setImageDrawable(errorDrawable);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    tecent_chat_image.setImageDrawable(placeHolderDrawable);
                }
            };
            tecent_chat_image.setTag(target);
            ImageLoad.loadPlaceholder(context, url, target);
        }
    }
}
