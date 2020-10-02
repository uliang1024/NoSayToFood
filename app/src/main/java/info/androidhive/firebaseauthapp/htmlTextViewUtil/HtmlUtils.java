package info.androidhive.firebaseauthapp.htmlTextViewUtil;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class HtmlUtils {

    public static Spanned getHtml(Context context, TextView textView, String string) {
//        textView.setMovementMethod(ScrollingMovementMethod.getInstance());// 滚动
        textView.setMovementMethod(LinkMovementMethod.getInstance());//设置超链接可以打开网页//click must
        return Html.fromHtml(string, new URLImageGetter(textView, context), new URLTagHandler(context));
    }

}