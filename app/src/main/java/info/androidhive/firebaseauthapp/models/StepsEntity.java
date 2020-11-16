package info.androidhive.firebaseauthapp.models;


import android.os.Parcel;
import android.os.Parcelable;

public class StepsEntity implements Parcelable {
    private String description;
    private String stepName;
    private String stepImage;

    public StepsEntity() {

    }

    public StepsEntity(String description, String stepName, String stepimage) {
        this.description = description;
        this.stepName = stepName;
        this.stepImage = stepImage;
    }

    protected StepsEntity(Parcel in) {
        description = in.readString();
        stepName = in.readString();
        stepImage = in.readString();
    }

    public static final Creator<StepsEntity> CREATOR = new Creator<StepsEntity>() {
        @Override
        public StepsEntity createFromParcel(Parcel in) {
            return new StepsEntity(in);
        }

        @Override
        public StepsEntity[] newArray(int size) {
            return new StepsEntity[size];
        }
    };

    public String getDescription() {
            return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStepname() {
        return stepName;
    }

    public void setStepname(String stepName) {
        this.stepName = stepName;
    }

    public String getStepimage() {
        return stepImage;
    }

        public void setStepimage(String stepImage) {
            this.stepImage = stepImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(stepName);
        dest.writeString(stepImage);
    }
}

