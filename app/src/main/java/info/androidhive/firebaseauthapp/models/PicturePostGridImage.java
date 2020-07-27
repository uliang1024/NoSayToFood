package info.androidhive.firebaseauthapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PicturePostGridImage extends PicturePostImagePosition {
    private int ItemImageId;
    private String ImagePath;
    private String Thumb;


    public PicturePostGridImage(int itemImageId, String imagePath, String thumb) {
        super();
        ItemImageId = itemImageId;
        ImagePath = imagePath;
        Thumb = thumb;

    }

    protected PicturePostGridImage(Parcel in) {
        ItemImageId = in.readInt();
        ImagePath = in.readString();
        Thumb = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ItemImageId);
        dest.writeString(ImagePath);
        dest.writeString(Thumb);

    }

    @Override
    public String toString() {
        return "ItemImage{" +
                "ItemImageId=" + ItemImageId +
                ", ImagePath='" + ImagePath + '\'' +
                ", Thumb='" + Thumb + '\'' +
                '}';
    }

    public String getThumb() {
        return Thumb;
    }

    public void setThumb(String thumb) {
        Thumb = thumb;
    }

    public int getItemImageId() {
        return ItemImageId;
    }

    public void setItemImageId(int itemImageId) {
        ItemImageId = itemImageId;
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
