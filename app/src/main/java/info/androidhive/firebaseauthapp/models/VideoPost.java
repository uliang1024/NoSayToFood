package info.androidhive.firebaseauthapp.models;

import android.app.Activity;

public class VideoPost {
    private int user_avatar;
    private String user_name;
    private int thumbnail_img;

    private String video_url;
    private String title;
    private String description;

    int index;

    public VideoPost(int user_avatar, String user_name, int thumbnail_img, String video_url, String title, String description, int index ) {
        this.user_avatar = user_avatar;
        this.user_name = user_name;
        this.thumbnail_img = thumbnail_img;
        this.video_url = video_url;
        this.title = title;
        this.description = description;
        this.index = index;

    }

    public int getUser_avatar() {
        return user_avatar;
    }

    public String getUser_name() {
        return user_name;
    }

    public int getThumbnail_img() {
        return thumbnail_img;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    public int getIndex() {
        return index;
    }


}
