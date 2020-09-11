package info.androidhive.firebaseauthapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Likes  {
    private long likeTime;
    private String user_avatar;
    private String user_Id;
    private String user_name;

    public Likes() {
    }

    public Likes(long likeTime, String user_avatar, String user_Id, String user_name) {
        this.likeTime = likeTime;
        this.user_avatar = user_avatar;
        this.user_Id = user_Id;
        this.user_name = user_name;
    }

    public long getLikeTime() {
        return likeTime;
    }

    public void setLikeTime(long likeTime) {
        this.likeTime = likeTime;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    public String getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(String user_Id) {
        this.user_Id = user_Id;
    }
}
