package info.androidhive.firebaseauthapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.classModels.FitClass;
import info.androidhive.firebaseauthapp.models.Article;
;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.myArticleViewHolder>{

    ArticleClickedListener listener;
    List<Article> articles ;
    Context context ;

    public ArticleAdapter(List<Article> articles, Context context , ArticleClickedListener listener) {
        this.articles = articles;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public myArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.article_raw,parent,false);
        return new myArticleViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull myArticleViewHolder holder, int position) {
        Article article = articles.get(position);
        ((ArticleAdapter.myArticleViewHolder)holder).setArticleInfo(article);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class myArticleViewHolder extends RecyclerView.ViewHolder {

        ImageView img_article;
        TextView tv_title,tv_time;
        ExpandableTextView tv_preview;

        public myArticleViewHolder(@NonNull View itemView , ArticleClickedListener listener) {
            super(itemView);
            img_article = itemView.findViewById(R.id.img_article);
            tv_preview = itemView.findViewById(R.id.tv_article_preview);
            tv_time = itemView.findViewById(R.id.tv_article_time);
            tv_title = itemView.findViewById(R.id.tv_article_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onArticleClicked(getAbsoluteAdapterPosition());
                }
            });
        }

        public void setArticleInfo(Article article){
            tv_title.setText(article.getTitle());
            tv_time.setText(article.getTime());
            tv_preview.setText(article.getPreview());
            Log.e("thumb",""+article.getImage());
            Glide.with(context).load(article.getImage()).into(img_article);


        }
    }
    public interface ArticleClickedListener{
        void onArticleClicked(int position);
    }
}
