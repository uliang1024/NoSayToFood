package info.androidhive.firebaseauthapp.ImageEdit.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.firebaseauthapp.ImageEdit.FontClass;
import info.androidhive.firebaseauthapp.R;

public class FontAdapter extends RecyclerView.Adapter<FontAdapter.FontViewHolder> {

    Context context;
    List<FontClass> fontList;
    FontAdapterListener listener;

    int row_selected = -1;

    public FontAdapter(Context context, FontAdapterListener listener) {
        this.context = context;
        this.fontList = loadFontList();
        this.listener = listener;
    }

    @NonNull
    @Override
    public FontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.font_item,parent,false);

        return new FontViewHolder( itemView );
    }

    @Override
    public void onBindViewHolder(@NonNull FontViewHolder holder, int position) {
        if (row_selected == position){
            holder.img_check.setVisibility(View.VISIBLE);
        }else{
            holder.img_check.setVisibility(View.INVISIBLE);
        }

        Typeface typeface= Typeface.createFromAsset(context.getAssets(),new StringBuilder("fonts/").append(fontList.get(position).getFontPath()).toString());
        holder.txt_font_name.setText(fontList.get(position).getFontName());

        if (fontList.get(position).getLanguage().equals("english")){
            holder.txt_font_demo.setText("DEMO");
            holder.txt_font_demo.setTypeface(typeface);
            holder.txt_font_language.setText("英文字體");
        }else if(fontList.get(position).getLanguage().equals("chinese")){
            holder.txt_font_demo.setText("範例文字");
            holder.txt_font_demo.setTypeface(typeface);
            holder.txt_font_language.setText("中文字體");
        }else if (fontList.get(position).getLanguage().equals("both")){
            holder.txt_font_demo.setText("範例文字");
            holder.txt_font_demo.setTypeface(typeface);
            holder.txt_font_language.setText("中英字體");
        }

    }

    @Override
    public int getItemCount() {
        return fontList.size();
    }

    public class FontViewHolder extends RecyclerView.ViewHolder{

        ImageView img_check;
        TextView txt_font_name,txt_font_demo,txt_font_language;
        CardView cardView_font;

        public FontViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_font_demo = itemView.findViewById(R.id.txt_font_demo);
            txt_font_name = itemView.findViewById(R.id.txt_font_name);
            txt_font_language = itemView.findViewById(R.id.txt_font_language);
            img_check = itemView.findViewById(R.id.img_check);
            cardView_font = itemView.findViewById(R.id.cardview_font);

            cardView_font.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFontItemSelected(fontList.get(getAdapterPosition()).getFontPath());
                    row_selected = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface FontAdapterListener{
        void onFontItemSelected(String fontName);
    }

    private List<FontClass> loadFontList() {
        List<FontClass> fonts = new ArrayList<>();
        FontClass font1 = new FontClass("Cheque-Black","Cheque-Black.otf","english");
        fonts.add(font1);

        FontClass font2 = new FontClass("Cheque-Regular","Cheque-Regular.otf","english");
        fonts.add(font2);

        FontClass font3 = new FontClass("jf open 粉圓字型","jf-openhuninn-1.1.ttf","both");
        fonts.add(font3);

        FontClass font4 = new FontClass("台北黑體","TaipeiSansTCBeta-Regular.ttf","both");
        fonts.add(font4);


        FontClass font6 = new FontClass("鋼筆鶴體","I.PenCrane-B.ttf","both");
        fonts.add(font6);

        return fonts;
    }
}
