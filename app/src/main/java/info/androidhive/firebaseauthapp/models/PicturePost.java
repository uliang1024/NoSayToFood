package info.androidhive.firebaseauthapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PicturePost implements Parcelable {
    private int ItemID;
    private String ItemName;
    private ArrayList<PicturePostGridImage> Images;

    public PicturePost(int itemID, String itemName,
                ArrayList<PicturePostGridImage> itemImages) {
        super();
        ItemID = itemID;
        ItemName = itemName;
        Images = itemImages;


    }


    public int getProductID() {
        return ItemID;
    }

    public void setProductID(int productID) {
        ItemID = productID;
    }


    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }


    public ArrayList<PicturePostGridImage> getImages() {
        return Images;
    }

    public void setImages(ArrayList<PicturePostGridImage> images) {
        Images = images;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected PicturePost(Parcel in) {
        ItemID = in.readInt();
        ItemName = in.readString();
        Images = (ArrayList) in.readValue(ArrayList.class.getClassLoader());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ItemID);
        dest.writeString(ItemName);
        dest.writeValue(Images);

    }

    public static final Parcelable.Creator<PicturePost> CREATOR = new Parcelable.Creator<PicturePost>() {
        @Override
        public PicturePost createFromParcel(Parcel in) {
            return new PicturePost(in);
        }

        @Override
        public PicturePost[] newArray(int size) {
            return new PicturePost[size];
        }
    };

    @Override
    public String toString() {
        return "ItemList{" +
                "IID=" + ItemID +
                ", ItemName='" + ItemName + '\'' +
                ", Images=" + Images +
                '}';
    }
}