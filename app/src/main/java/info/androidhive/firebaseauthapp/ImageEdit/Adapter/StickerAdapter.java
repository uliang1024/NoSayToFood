package info.androidhive.firebaseauthapp.ImageEdit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.firebaseauthapp.R;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerViewHolder> {

    Context context;
    List<Integer> stickerList;
    StickerAdapterListener listener;

    public StickerAdapter(Context context , StickerAdapterListener listener) {
        this.context = context;
        this.stickerList = getStickerList();
        this.listener = listener;
    }

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.sticker_item,parent,false);
        return new StickerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        holder.sticker_item.setImageResource(stickerList.get(position));
    }

    @Override
    public int getItemCount() {
        return stickerList.size();
    }

    public class StickerViewHolder extends RecyclerView.ViewHolder{

        ImageView sticker_item;

        public StickerViewHolder(@NonNull View itemView) {
            super(itemView);

            sticker_item = itemView.findViewById(R.id.img_sticker_item);

            sticker_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onStickerItemSelected(stickerList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface StickerAdapterListener{
        void onStickerItemSelected(int sticker);
    }

    private List<Integer> getStickerList() {
        List<Integer> stickers = new ArrayList();
        stickers.add(R.drawable.ic_hat);
        stickers.add(R.drawable.ic_cap);
        return stickers;
    }
}
