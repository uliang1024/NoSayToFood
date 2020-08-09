package info.androidhive.firebaseauthapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PicturePost implements Parcelable {
    private String user_avatar;
    private String user_name;
    private String description;
    private int ItemID,post_type;
    private String ItemName;
    private int mDisplay;
    private int mTotal;
    private ArrayList<PicturePostGridImage >Images;


    public PicturePost(int itemID,
                ArrayList<PicturePostGridImage> itemImages,String user_avatar,String user_name, String description) {
        super();
        this.ItemID = itemID;
        this.Images = itemImages;
        this.user_avatar = user_avatar;
        this.user_name = user_name;
        this.description = description;

    }

    public int getmDisplay() {
        return mDisplay;
    }

    public void setmDisplay(int mDisplay) {
        this.mDisplay = mDisplay;
    }

    public int getmTotal() {
        return mTotal;
    }

    public void setmTotal(int mTotal) {
        this.mTotal = mTotal;
    }

    public int getPost_type() {
        return post_type;
    }

    public void setPost_type(int post_type) {
        this.post_type = post_type;
    }

    public PicturePost() {
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public ArrayList<PicturePostGridImage> getImages() {
        return Images;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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