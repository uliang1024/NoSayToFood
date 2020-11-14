package info.androidhive.firebaseauthapp.adapter;

import android.annotation.SuppressLint;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.models.Recipe;

;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.myRecipeViewHolder>{

    RecipeClickedListener listener;
    List<Recipe> recipes ;
    Context context ;

    public boolean isShimmer = true;//to judge shimmer or not
    //以後如果添加更多課程再新增
    int SHIMMER_ITEM_NUMBER = 3;//numbers of shimmer shown during loading

    public RecipeAdapter(List<Recipe> recipes, Context context , RecipeClickedListener listener) {
        this.recipes = recipes;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public myRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_raw,parent,false);
        return new myRecipeViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull myRecipeViewHolder holder, int position) {
        if (isShimmer){
            holder.shimmer_layout_recipe.startShimmer();
        }else{
            holder.shimmer_layout_recipe.stopShimmer();
            holder.shimmer_layout_recipe.setShimmer(null);
            Recipe recipe = recipes.get(position);
            ((RecipeAdapter.myRecipeViewHolder)holder).setArticleInfo(recipe);
        }

    }

    @Override
    public int getItemCount() {
        return isShimmer? SHIMMER_ITEM_NUMBER:recipes.size();
    }

    public class myRecipeViewHolder extends RecyclerView.ViewHolder {
        ShimmerFrameLayout shimmer_layout_recipe;
        ImageView img_recipe;
        TextView tv_title;


        public  myRecipeViewHolder(@NonNull View itemView , RecipeClickedListener listener) {
            super(itemView);
            shimmer_layout_recipe = itemView.findViewById(R.id.shimmer_layout_recipe);
            img_recipe = itemView.findViewById(R.id.img_recipe);
            //tv_time = itemView.findViewById(R.id.tv_recipe_time);
            tv_title = itemView.findViewById(R.id.tv_recipe_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRecipeClicked(getAbsoluteAdapterPosition());
                }
            });
        }



        @SuppressLint("ResourceAsColor")
        public void setArticleInfo(Recipe recipe){
            tv_title.setText(recipe.getRecipename());
            //tv_time.setText(recipe.getTime());
            Log.e("thumb",""+recipe.getRecipeimage());
            Glide.with(context).load(recipe.getRecipeimage()).into(img_recipe);
            img_recipe.setBackground(null);
            //tv_time.setBackground(null);
            tv_title.setBackgroundColor(R.color.transparentBlack);

        }
    }
    public interface RecipeClickedListener{
        void onRecipeClicked(int position);
    }
}
