package info.androidhive.firebaseauthapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PicturePostGridImage extends PicturePostImagePosition {

    private String ImagePath;

    public PicturePostGridImage( ) {

    }

    public PicturePostGridImage( String imagePath) {
        super();
        ImagePath = imagePath;
    }

    protected PicturePostGridImage(Parcel in) {
        ImagePath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(ImagePath);


    }

    @Override
    public String toString() {
        return "ItemImage{" +

                ", ImagePath='" + ImagePath + '\'' +
                 +
                '}';
    }



    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }



    public static final Parcelable.Creator<PicturePostGridImage> CREATOR = new Parcelable.Creator<PicturePostGridImage>() {
        @Override
        public PicturePostGridImage createFromParcel(Parcel in) {
            return new PicturePostGridImage(in);
        }

        @Override
        public PicturePostGridImage[] newArray(int size) {
            return new PicturePostGridImage[size];
        }
    };
}
