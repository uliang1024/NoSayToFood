package info.androidhive.firebaseauthapp.models;
public class TextPost {
    private int user_avatar;
    private String user_name,title,description;

    public TextPost(int user_avatar, String user_name, String title, String description) {
        this.user_avatar = user_avatar;
        this.user_name = user_name;
        this.title = title;
        this.description = description;
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