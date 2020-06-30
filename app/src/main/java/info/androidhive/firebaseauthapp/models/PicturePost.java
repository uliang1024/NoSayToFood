package info.androidhive.firebaseauthapp.models;

public class PicturePost {
    private int picImg, user_avatar;
    private String user_name,title,description;

    public PicturePost(int picImg, int user_avatar, String user_name, String title, String description) {
        this.picImg = picImg;
        this.user_avatar = user_avatar;
        this.user_name = user_name;
        this.title = title;
        this.description = description;
    }

    public int getPicImg() {
        return picImg;
    }


    public int getUser_avatar() {
        return user_avatar;
    }


    public String getUser_name() {
        return user_name;
    }


    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }
}