package info.androidhive.firebaseauthapp.util;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import info.androidhive.firebaseauthapp.models.Comments;

public class DiffUtilCallback extends DiffUtil.Callback {

    private List<Comments> oldCommentList;
    private List<Comments> newCommentList;

    public DiffUtilCallback(List<Comments> oldCommentList, List<Comments> newCommentList) {
        this.oldCommentList = oldCommentList;
        this.newCommentList = newCommentList;
    }

    @Override
    public int getOldListSize() {
        return oldCommentList.size();
    }

    @Override
    public int getNewListSize() {
        return newCommentList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItemPosition ==newItemPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCommentList.get(oldItemPosition)==newCommentList.get(newItemPosition);
    }
}
