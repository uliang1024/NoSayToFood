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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.SQLite.FastRecord;
import info.androidhive.firebaseauthapp.models.fastRecords;

public class FastRecordAdapter extends RecyclerView.Adapter<FastRecordAdapter.FastRecordViewHolder> {

    Context context;
    ArrayList<fastRecords> fast_record;
    RecordClickedListener listener;

    public FastRecordAdapter() {
    }
    public FastRecordAdapter(Context context , ArrayList<fastRecords> fast_record ,RecordClickedListener listener) {
        this.context = context;
        this.fast_record = fast_record;
        this.listener=listener;
    }

    @NonNull
    @Override
    public FastRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fast_record_raw,parent,false);
        return new FastRecordViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FastRecordViewHolder holder, int position) {
        fastRecords records = fast_record.get(position);
        ((FastRecordAdapter.FastRecordViewHolder)holder).setRecordInfo(records);
        Log.e("get record", ""+position);
    }

    @Override
    public int getItemCount() {
        return fast_record.size();
    }

    public class FastRecordViewHolder extends RecyclerView.ViewHolder{
        TextView tv_fast_days;
        ImageView img_emoji;


        public FastRecordViewHolder(@NonNull View itemView, RecordClickedListener listener) {
            super(itemView);
            tv_fast_days = itemView.findViewById(R.id.tv_fast_days);
            img_emoji= itemView.findViewById(R.id.img_emoji);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRecordClicked(getAbsoluteAdapterPosition());
                }
            });
        }

        public void setRecordInfo(fastRecords records){
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date start = new Date();
            start.setTime(records.getStartTime());
            Date end = new Date();
            end.setTime(records.getEndTime());
            int emoji = records.getEmoji();
            String days= df.format(start)+"\n到\n"+df.format(end);
            tv_fast_days.setText(days);

            switch(emoji){
                case 0:
                    img_emoji.setImageResource(R.drawable.ic_happy);
                    break;
                case 1:
                    img_emoji.setImageResource(R.drawable.ic_soso);
                    break;
                case 2:
                    img_emoji.setImageResource(R.drawable.ic_sad);
                    break;
            }
        }
    }

    public interface RecordClickedListener{
        void onRecordClicked(int position);
    }
}
