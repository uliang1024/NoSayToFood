package info.androidhive.firebaseauthapp.classModels;

public class ClassdataEntity {
    private String unit;
    private int moveTimes;
    private String moveName;
    private String moveImage;

    public int getMoveTimes() {
        return moveTimes;
    }

    public void setMoveTimes(int moveTimes) {
        this.moveTimes = moveTimes;
    }

    public String getMoveName() {
        return moveName;
    }

    public void setMoveName(String moveName) {
        this.moveName = moveName;
    }

    public String getMoveImage() {
        return moveImage;
    }

    public void setMoveImage(String moveImage) {
        this.moveImage = moveImage;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
