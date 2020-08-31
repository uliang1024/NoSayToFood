package info.androidhive.firebaseauthapp.ImageEdit.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DecimalFormat;

import info.androidhive.firebaseauthapp.ImageEdit.InterFace.EditImageFragmentListener;
import info.androidhive.firebaseauthapp.R;


public class EditImageFragment extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    private TextView btn_reset, tv_brightness,tv_contrast,tv_saturation;
    private SeekBar seekBar_brightness,seekBar_contrast,seekBar_saturation;
    //契約 EditImageFragmentListener (interface)
    private EditImageFragmentListener listener;
    static EditImageFragment instance;

    public EditImageFragment() {
        // Required empty public constructor
    }


    public static EditImageFragment getInstance() {
        if(instance ==null){
            instance = new EditImageFragment();
        }
        return instance;
    }

    //EditImageActivity可以透過 setListener 方法來與fragment 簽訂契約
    //並且必須實作契約定義的方法
    public void setListener(EditImageFragmentListener listener) {
        //此處的listener是ImageEditActivity 中呼喚 setListener的
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView =  inflater.inflate(R.layout.fragment_edit_image, container, false);
        seekBar_brightness = itemView.findViewById(R.id.seekbar_brightness);
        seekBar_contrast = itemView.findViewById(R.id.seekbar_contrast);
        seekBar_saturation = itemView.findViewById(R.id.seekbar_saturation);
        tv_brightness = itemView.findViewById(R.id.tv_brightness);
        tv_contrast = itemView.findViewById(R.id.tv_contrast);
        tv_saturation = itemView.findViewById(R.id.tv_saturation);
        btn_reset = itemView.findViewById(R.id.btn_reset);

        seekBar_brightness.setMax(200);
        seekBar_brightness.setProgress(100);

        seekBar_contrast.setMax(20);
        seekBar_contrast.setProgress(0);

        seekBar_saturation.setMax(30);
        seekBar_saturation.setProgress(10);

        seekBar_brightness.setOnSeekBarChangeListener(this);
        seekBar_contrast.setOnSeekBarChangeListener(this);
        seekBar_saturation.setOnSeekBarChangeListener(this);

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar_brightness.setProgress(100);
                seekBar_contrast.setProgress(0);
                seekBar_saturation.setProgress(10);

                listener.onFilterCleared();
            }
        });

        return  itemView;
    }

    //當seeekbar有變動時
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (listener!=null){
            //當seekbar_brightness改變時
            if (seekBar.getId() == R.id.seekbar_brightness){
                //listener呼喚 onBrightnessChanged
                //listener設定 onBrightnessChanged 參數
                tv_brightness.setText("brightness:"+(progress-100));
                listener.onBrightnessChanged(progress-100);

            }
            //當seekbar_contrast改變時
            else if (seekBar.getId() == R.id.seekbar_contrast){
                progress+=10;
                float value = progress*.10f;
                //listener呼喚 onContrastChanged
                //listener設定 onBrightnessChanged 參數
                DecimalFormat df = new DecimalFormat("##.0");
                tv_contrast.setText("contrast:"+Double.parseDouble(df.format(value)));
                listener.onContrastChanged(value);

            }
            //當seekbar_saturation改變時
            else if(seekBar.getId() == R.id.seekbar_saturation){
                float value= .10f*progress;
                //listener呼喚 onSaturationChanged
                //listener設定 onBrightnessChanged 參數
                DecimalFormat df = new DecimalFormat("##.0");
                tv_saturation.setText("saturation:"+Double.parseDouble(df.format(value)));
                listener.onSaturationChanged(value);

            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //當開始拖動時
        if (listener!=null){
            //listener呼喚onEditStarted
            listener.onEditStarted();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //當拖動結束時
        if (listener!=null){
            //listener呼喚 onEditCompleted
            listener.onEditCompleted();
        }
    }


}