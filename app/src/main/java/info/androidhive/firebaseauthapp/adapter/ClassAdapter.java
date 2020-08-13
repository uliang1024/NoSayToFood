package info.androidhive.firebaseauthapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.classModels.FitClass;
import info.androidhive.firebaseauthapp.models.PicturePost;

public class ClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<FitClass> classes;

    public ClassAdapter(Context ctx ,List<FitClass> classes) {
        this.classes = classes;
        this.context = ctx;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.raw_data,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FitClass fitClass = classes.get(position);
        ((ClassAdapter.myViewHolder)holder).setClassInfo(fitClass,position);
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        ImageView classImg;
        TextView className;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            classImg = itemView.findViewById(R.id.class_img);
            className = itemView.findViewById(R.id.class_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(),""+classes.get(getAbsoluteAdapterPosition()).getClassName(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        void setClassInfo(FitClass fitClass, int position){
            Glide.with(context).load(fitClass.getClassImage()).into(classImg);
            className.setText(fitClass.getClassName());
        }
    }
}
