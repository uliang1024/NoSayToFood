package info.androidhive.firebaseauthapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import info.androidhive.firebaseauthapp.FitnessClassActivity;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.classModels.FitClass;
import info.androidhive.firebaseauthapp.models.PicturePost;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.myViewHolder> {

    Context context;
    List<FitClass> classes;
    public boolean isShimmer = true;//to judge shimmer or not
    //以後如果添加更多課程再新增
    int SHIMMER_ITEM_NUMBER = 2;//numbers of shimmer shown during loading

    public ClassAdapter(Context ctx ,List<FitClass> classes) {
        this.classes = classes;
        this.context = ctx;
    }

    @NonNull
    @Override
    public ClassAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.raw_data,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassAdapter.myViewHolder holder, int position) {

        if (isShimmer){
            holder.shimmer_layout_class.startShimmer();
        }else {
            holder.shimmer_layout_class.stopShimmer();
            holder.shimmer_layout_class.setShimmer(null);
            FitClass fitClass = classes.get(position);
            ((ClassAdapter.myViewHolder)holder).setClassInfo(fitClass,position);
        }

    }

    @Override
    public int getItemCount() {
        return isShimmer? SHIMMER_ITEM_NUMBER:classes.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        ShimmerFrameLayout shimmer_layout_class;
        ImageView classImg;
        TextView className;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            classImg = itemView.findViewById(R.id.class_img);
            className = itemView.findViewById(R.id.class_name);
            shimmer_layout_class = itemView.findViewById(R.id.shimmer_layout_class);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String className = classes.get(getAbsoluteAdapterPosition()).getClassName();
                    Toast.makeText(v.getContext(),className,Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, FitnessClassActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("className",className);
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }
            });
        }

        void setClassInfo(FitClass fitClass, int position){
            classImg.setBackground(null);
            Glide.with(context).load(fitClass.getClassImage()).into(classImg);

            className.setBackground(null);
            className.setText(fitClass.getClassName());
        }
    }

}
