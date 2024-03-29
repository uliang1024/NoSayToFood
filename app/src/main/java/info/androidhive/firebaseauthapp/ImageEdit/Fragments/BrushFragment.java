package info.androidhive.firebaseauthapp.ImageEdit.Fragments;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import info.androidhive.firebaseauthapp.ImageEdit.Adapter.ColorAdapter;
import info.androidhive.firebaseauthapp.ImageEdit.InterFace.BrushFragmentListener;
import info.androidhive.firebaseauthapp.R;


public class BrushFragment extends BottomSheetDialogFragment implements ColorAdapter.ColorAdapterListener {

    SeekBar seekBar_brush_size,seekBar_opacity_size;
    RecyclerView recycler_color;
    ToggleButton btn_brush_state;
    ColorAdapter colorAdapter;
    //require a interface BrushFragmentListener
    BrushFragmentListener listener;

    static BrushFragment instance;

    public static BrushFragment getInstance() {
        if(instance == null){
            instance = new BrushFragment();
        }
        return instance;
    }

    //set BrushFragmentListener
    public void setListener(BrushFragmentListener listener) {
        this.listener = listener;
    }

    public BrushFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView =  inflater.inflate(R.layout.fragment_brush, container, false);

        seekBar_brush_size = itemView.findViewById(R.id.seekbar_brush_size);
        seekBar_opacity_size = itemView.findViewById(R.id.seekbar_brush_opacity);
        btn_brush_state = itemView.findViewById(R.id.brush_state);
        recycler_color = itemView.findViewById(R.id.recycler_color);

        recycler_color.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        colorAdapter= new ColorAdapter(getContext(),this);
        recycler_color.setAdapter(colorAdapter);
        //events
        seekBar_opacity_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //pass opacity value to seekBar_opacity_size
                listener.onBrushOpacityChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar_brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //pass brush_size value to seekBar_brush_size
                listener.onBrushSizeChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btn_brush_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //pass checkState to btn_brush_state
                listener.onBrushStateChangeListener(isChecked);
            }
        });

        return itemView;
    }



    //implemented from ColorAdapter
    //get color (int) from onColorSelected method
    @Override
    public void onColorSelected(int color) {
        listener.onBrushColorChangeListener(color);
    }
}