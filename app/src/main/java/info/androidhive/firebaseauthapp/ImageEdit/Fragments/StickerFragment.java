package info.androidhive.firebaseauthapp.ImageEdit.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import info.androidhive.firebaseauthapp.ImageEdit.Adapter.StickerAdapter;
import info.androidhive.firebaseauthapp.ImageEdit.InterFace.StickerFragmentListener;
import info.androidhive.firebaseauthapp.R;


public class StickerFragment extends BottomSheetDialogFragment implements StickerAdapter.StickerAdapterListener {

    RecyclerView recycler_sticker;
    StickerFragmentListener listener;
    static StickerFragment instance;

    public static StickerFragment getInstance() {
        if (instance==null){
            instance = new StickerFragment();
        }
        return instance;
    }

    public void setListener(StickerFragmentListener listener) {
        this.listener = listener;
    }

    public StickerFragment() {
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
        View view = inflater.inflate(R.layout.fragment_sticker, container, false);
        recycler_sticker = view.findViewById(R.id.recycler_sticker);

        StickerAdapter adapter = new StickerAdapter(getContext(),this);
        recycler_sticker.setHasFixedSize(false);
        recycler_sticker.setLayoutManager(new GridLayoutManager(getActivity(),3));
        recycler_sticker.setAdapter(adapter);

        return  view;

    }

    @Override
    public void onStickerItemSelected(int sticker) {
        listener.onStickerSelected(sticker);
    }
}