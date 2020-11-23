package info.androidhive.firebaseauthapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PicturePostGridImage implements Parcelable  {

    private String ImagePath;

    public PicturePostGridImage( ) {

    }

    protected PicturePostGridImage(Parcel in) {
        ImagePath = in.readString();
    }

    public static final Creator<PicturePostGridImage> CREATOR = new Creator<PicturePostGridImage>() {
        @Override
        public PicturePostGridImage createFromParcel(Parcel in) {
            return new PicturePostGridImage(in);
        }

        @Override
        public PicturePostGridImage[] newArray(int size) {
            return new PicturePostGridImage[size];
        }
    };

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ImagePath);
    }
}
