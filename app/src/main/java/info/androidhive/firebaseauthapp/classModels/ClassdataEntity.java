package info.androidhive.firebaseauthapp.classModels;

import android.os.Parcel;
import android.os.Parcelable;


public class ClassdataEntity implements Parcelable {
    private String unit;
    private int moveTimes;
    private String moveName;
    private String moveImage;

    public ClassdataEntity(){}


    //new Constructor by parcelable
    protected ClassdataEntity(Parcel in) {
        unit = in.readString();
        moveTimes = in.readInt();
        moveName = in.readString();
        moveImage = in.readString();
    }


    public static final Creator<ClassdataEntity> CREATOR = new Creator<ClassdataEntity>() {
        @Override
        public ClassdataEntity createFromParcel(Parcel in) {
            return new ClassdataEntity(in);
        }

        @Override
        public ClassdataEntity[] newArray(int size) {
            return new ClassdataEntity[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(unit);
        dest.writeInt(moveTimes);
        dest.writeString(moveName);
        dest.writeString(moveImage);
    }
}
