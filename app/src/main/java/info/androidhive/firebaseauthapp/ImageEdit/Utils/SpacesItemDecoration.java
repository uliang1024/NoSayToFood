package info.androidhive.firebaseauthapp.ImageEdit.Utils;


import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public SpacesItemDecoration(int space) {
        this.space = space  ;
    }

    @Override public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) ==state.getItemCount()-1){
            outRect.left= space;
            outRect.right = 0;
        }else{
            outRect.left= 0;
            outRect.right = space;
        }
    }
}