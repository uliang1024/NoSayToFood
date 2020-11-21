package info.androidhive.firebaseauthapp.models;

public class fastRecords {
    private int id;
    private String uid;
    private long startTime;
    private long endTime;
    private int emoji;
    private long timeStamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startDime) {
        this.startTime = startDime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endDime) {
        this.endTime = endDime;
    }

    public int getEmoji() {
        return emoji;
    }

    public void setEmoji(int emoji) {
        this.emoji = emoji;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
