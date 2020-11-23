package info.androidhive.firebaseauthapp.models;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class VideoPost implements Parcelable {

    private String PostID;
    private long postTime;
    private String user_avatar;
    private String user_name;
    private String thumbnail_img;
    private int post_type;
    private String video_url;
    private String description;
    private String user_ID;
    private ArrayList<Comments > comments;
    private ArrayList<Likes >likes;
    private int toUpdate;
    private int toDelete;

    public VideoPost() {

    }

    protected VideoPost(Parcel in) {
        PostID = in.readString();
        postTime = in.readLong();
        user_avatar = in.readString();
        user_name = in.readString();
        thumbnail_img = in.readString();
        post_type = in.readInt();
        video_url = in.readString();
        description = in.readString();
        user_ID = in.readString();
        toUpdate = in.readInt();
        toDelete = in.readInt();
    }

    public static final Creator<VideoPost> CREATOR = new Creator<VideoPost>() {
        @Override
        public VideoPost createFromParcel(Parcel in) {
            return new VideoPost(in);
        }

        @Override
        public VideoPost[] newArray(int size) {
            return new VideoPost[size];
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

    public VideoPost(String user_avatar, String user_name, String thumbnail_img, String video_url, String title, String description ) {
        this.user_avatar = user_avatar;
        this.user_name = user_name;
        this.thumbnail_img = thumbnail_img;
        this.video_url = video_url;

        this.description = description;


    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getThumbnail_img() {
        return thumbnail_img;
    }

    public String getVideo_url() {
        return video_url;
    }


    public String getDescription() {
        return description;
    }

    public int getPost_type() {
        return post_type;
    }

    public void setPost_type(int post_type) {
        this.post_type = post_type;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setThumbnail_img(String thumbnail_img) {
        this.thumbnail_img = thumbnail_img;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
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
        dest.writeString(thumbnail_img);
        dest.writeInt(post_type);
        dest.writeString(video_url);
        dest.writeString(description);
        dest.writeString(user_ID);
        dest.writeInt(toUpdate);
        dest.writeInt(toDelete);
    }
}
