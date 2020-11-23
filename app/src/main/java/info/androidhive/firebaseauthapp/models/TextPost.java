package info.androidhive.firebaseauthapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TextPost implements Parcelable {

    private String PostID;
    private long postTime;
    private String user_avatar,user_name,description;
    private String user_ID;
    private int post_type;
    private ArrayList<Comments > comments;
    private ArrayList<Likes >likes;
    private int toUpdate;
    private int toDelete;

    public TextPost() {

    }

    protected TextPost(Parcel in) {
        PostID = in.readString();
        postTime = in.readLong();
        user_avatar = in.readString();
        user_name = in.readString();
        description = in.readString();
        user_ID = in.readString();
        post_type = in.readInt();
        toUpdate = in.readInt();
        toDelete = in.readInt();
    }

    public static final Creator<TextPost> CREATOR = new Creator<TextPost>() {
        @Override
        public TextPost createFromParcel(Parcel in) {
            return new TextPost(in);
        }

        @Override
        public TextPost[] newArray(int size) {
            return new TextPost[size];
        }
    };

    public int getToUpdate() {
        return toUpdate;
    }

    public void setToUpdate(int toUpdate) {
        this.toUpdate = toUpdate;
    }

    public int getToDelete() {
        return toDelete;
    }

    public void setToDelete(int toDelete) {
        this.toDelete = toDelete;
    }

    public ArrayList<Comments> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comments> comments) {
        this.comments = comments;
    }

    public ArrayList<Likes> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<Likes> likes) {
        this.likes = likes;
    }

    public String getPostID() {
        return PostID;
    }

    public void setPostID(String postID) {
        PostID = postID;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public int getPost_type() {
        return post_type;
    }

    public void setPost_type(int post_type) {
        this.post_type = post_type;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getDescription() {
        return description;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(PostID);
        dest.writeLong(postTime);
        dest.writeString(user_avatar);
        dest.writeString(user_name);
        dest.writeString(description);
        dest.writeString(user_ID);
        dest.writeInt(post_type);
        dest.writeInt(toUpdate);
        dest.writeInt(toDelete);
    }
}