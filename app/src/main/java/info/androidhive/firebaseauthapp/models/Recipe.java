package info.androidhive.firebaseauthapp.models;

public  class Recipe {
    private String title;
    private String videoUrl;
    private String time;
    private String image;
    private String content;


    public Recipe() {

    }

    public Recipe(String title, String videoUrl, String time, String image, String content) {
        this.title = title;
        this.videoUrl = videoUrl;
        this.time = time;
        this.image = image;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideourl() {
        return videoUrl;
    }

    public void setVideourl(String videourl) {
        this.videoUrl = videourl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
