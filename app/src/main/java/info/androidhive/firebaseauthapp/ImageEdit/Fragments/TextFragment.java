package info.androidhive.firebaseauthapp.ImageEdit.Fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import info.androidhive.firebaseauthapp.ImageEdit.Adapter.ColorAdapter;
import info.androidhive.firebaseauthapp.ImageEdit.Adapter.FontAdapter;
import info.androidhive.firebaseauthapp.ImageEdit.InterFace.AddTextFragmentListener;
import info.androidhive.firebaseauthapp.R;


public class TextFragment extends DialogFragment implements ColorAdapter.ColorAdapterListener, FontAdapter.FontAdapterListener {

    static TextFragment instance;
    AddTextFragmentListener listener;
    EditText edt_add_text;
    RecyclerView recycler_font;
    RecyclerView recycler_text_color;
    Button btn_add_text;
    Typeface typeface = Typeface.DEFAULT;

    int colorSelected = Color.parseColor("#000000");//預設顏色黑色

    public void setListener(AddTextFragmentListener listener) {
        this.listener = listener;
    }

    public static TextFragment getInstance() {
        if(instance==null){
            return new TextFragment();
        }
        return instance;
    }

    public TextFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text, container, false);

        edt_add_text = view.findViewById(R.id.edt_add_text);
        recycler_font = view.findViewById(R.id.recycler_font);
        recycler_text_color = view.findViewById(R.id.recycler_text_color);
        btn_add_text = view.findViewById(R.id.btn_add_text);


        //recycler_text_color.setHasFixedSize(true);
        recycler_text_color.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        //recycler_font.setHasFixedSize(true);
        recycler_font.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        ColorAdapter colorAdapter= new ColorAdapter(getContext(),this);
        recycler_text_color.setAdapter(colorAdapter);

        FontAdapter fontAdapter = new FontAdapter(getContext(),this);
        recycler_font.setAdapter(fontAdapter);

        btn_add_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTextAdded(typeface,edt_add_text.getText().toString(),colorSelected);
                Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onColorSelected(int color) {
        edt_add_text.setTextColor(color);
        colorSelected = color;
    }

    @Override
    public void onFontItemSelected(String fontName) {
        typeface = Typeface.createFromAsset(getContext().getAssets(),new StringBuilder("fonts/").append(fontName).toString());
        edt_add_text.setTypeface(typeface);
    }
}