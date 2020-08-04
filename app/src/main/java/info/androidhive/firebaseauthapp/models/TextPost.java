package info.androidhive.firebaseauthapp.models;
public class TextPost {

    private String user_avatar,user_name,description;

    public TextPost() {

    }

    public TextPost(String user_avatar, String user_name, String title, String description) {
        this.user_avatar = user_avatar;
        this.user_name = user_name;

        this.description = description;
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
}