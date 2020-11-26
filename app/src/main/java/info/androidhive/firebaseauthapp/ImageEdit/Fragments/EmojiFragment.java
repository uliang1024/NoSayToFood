package info.androidhive.firebaseauthapp.ImageEdit.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import info.androidhive.firebaseauthapp.ImageEdit.Adapter.ColorAdapter;
import info.androidhive.firebaseauthapp.ImageEdit.Adapter.EmojiAdapter;
import info.androidhive.firebaseauthapp.ImageEdit.InterFace.EmojiFragmentListener;
import info.androidhive.firebaseauthapp.R;
import ja.burhanrashid52.photoeditor.PhotoEditor;


public class EmojiFragment extends BottomSheetDialogFragment implements EmojiAdapter.EmojiAdapterListener {

    RecyclerView recycler_emoji;
    EmojiFragmentListener listener;
    static EmojiFragment instance;

    EmojiAdapter emojiAdapter;


    public void setListener(EmojiFragmentListener listener) {
        this.listener = listener;
    }

    public static EmojiFragment getInstance() {
        if(instance==null){
            instance = new EmojiFragment();
        }
        return instance;
    }

    public EmojiFragment() {
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
        View itemView= inflater.inflate(R.layout.fragment_emoji, container, false);

        recycler_emoji = itemView.findViewById(R.id.recycler_emoji);
        //recycler_emoji.setHasFixedSize(true);
        recycler_emoji.setLayoutManager(new GridLayoutManager(getActivity(),5));

        emojiAdapter  = new EmojiAdapter(getContext(), PhotoEditor.getEmojis(getContext()),this );
        recycler_emoji.setAdapter(emojiAdapter);

        return itemView;
    }

    @Override
    public void onEmojiItemSelected(String emoji) {
        listener.onEmojiSelected(emoji);
    }
}