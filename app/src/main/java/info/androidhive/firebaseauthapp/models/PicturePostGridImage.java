package info.androidhive.firebaseauthapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PicturePostGridImage extends PicturePostImagePosition {

    private String ImagePath;

    public PicturePostGridImage( ) {

    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

}
