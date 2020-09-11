package info.androidhive.firebaseauthapp.models;



public class Comments  {
    private long commentTime;
    private String comment;
    private String user_avatar;
    private String user_name;
    private String user_Id;


    public Comments() {

    }

    public Comments(long commentTime, String comment, String user_avatar, String user_name, String user_Id) {
        this.commentTime = commentTime;
        this.comment = comment;
        this.user_avatar = user_avatar;
        this.user_name = user_name;
        this.user_Id = user_Id;
    }

    public long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(long commentTime) {
        this.commentTime = commentTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
