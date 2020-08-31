package info.androidhive.firebaseauthapp.ImageEdit.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.firebaseauthapp.R;


public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    Context context;
    List<Integer> colorList;
    ColorAdapterListener listener;

    public ColorAdapter(Context context, ColorAdapterListener listener) {
        this.context = context;
        this.colorList = getColorList();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.color_item,parent,false);
        return new ColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        holder.color_section.setCardBackgroundColor(colorList.get(position));
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {
        public CardView color_section;
        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            color_section = itemView.findViewById(R.id.color_section);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //當按鈕按下時
                    //colorList.get(getAdapterPosition())將會傳送到
                    //辣個implements 這個契約(listener)的class 手上
                    //繼續下一步的旅程
                    listener.onColorSelected(colorList.get(getAdapterPosition()));
                }
            });
        }
    }

    ////締結一個契約叫做EmojiAdapterListener
    public interface ColorAdapterListener{
        //當實作我這個契約的class呼叫onColorSelected()方法時，得到一個int color
        void onColorSelected(int color);
    }

    private List<Integer> getColorList() {
        List<Integer> colorList=new ArrayList<>();

        colorList.add(Color.parseColor("#f1f1ea"));
        colorList.add(Color.parseColor("#faf6ea"));
        colorList.add(Color.parseColor("#e53c67"));
        colorList.add(Color.parseColor("#a2ca4a"));
        colorList.add(Color.parseColor("#ffbbff"));
        colorList.add(Color.parseColor("#ff3267"));
        colorList.add(Color.parseColor("#007bae"));
        colorList.add(Color.parseColor("#bdcf46"));
        colorList.add(Color.parseColor("#f4d4f4"));
        colorList.add(Color.parseColor("#e9a2e0"));
        colorList.add(Color.parseColor("#ff7f00"));
        colorList.add(Color.parseColor("#b5a8ea"));


        return colorList;
    }
}

