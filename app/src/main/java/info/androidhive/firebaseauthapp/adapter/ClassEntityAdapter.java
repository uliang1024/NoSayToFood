package info.androidhive.firebaseauthapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.classModels.ClassdataEntity;
import info.androidhive.firebaseauthapp.classModels.FitClass;

public class ClassEntityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<ClassdataEntity> classData;

    public ClassEntityAdapter(Context context, List<ClassdataEntity> classData) {
        this.context = context;
        this.classData = classData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.class_entity,parent,false);
        return new ClassEntityAdapter.classEntityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ClassdataEntity classdataEntity = classData.get(position);
        ((ClassEntityAdapter.classEntityViewHolder)holder).setClassDataInfo(classdataEntity,position);
    }

    @Override
    public int getItemCount() {
        return classData.size();
    }

    public class classEntityViewHolder extends RecyclerView.ViewHolder{
        ImageView moveImage;
        TextView  moveName;
        TextView moveTime;
        public classEntityViewHolder(@NonNull View itemView) {
            super(itemView);

            moveImage = itemView.findViewById(R.id.moveImage);
            moveName = itemView.findViewById(R.id.moveName);
            moveTime = itemView.findViewById(R.id.moveTimes);

        }

        void setClassDataInfo(ClassdataEntity classdataEntity,int position){
            Glide.with(context).load(classdataEntity.getMoveImage()).centerCrop().into(moveImage);
            moveName.setText(classdataEntity.getMoveName());
            String unit = classdataEntity.getUnit();
            switch (unit){
                case "time":
                    moveTime.setText( classdataEntity.getMoveTimes()+"次");
                    break;
                case "second":
                    moveTime.setText( classdataEntity.getMoveTimes()+"秒");
                    break;
            }
        }
    }
}
