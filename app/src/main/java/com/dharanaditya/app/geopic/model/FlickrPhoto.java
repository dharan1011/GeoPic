package com.dharanaditya.app.geopic.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FlickrPhoto implements Parcelable {

    public static final Creator<FlickrPhoto> CREATOR = new Creator<FlickrPhoto>() {
        @Override
        public FlickrPhoto createFromParcel(Parcel in) {
            return new FlickrPhoto(in);
        }

        @Override
        public FlickrPhoto[] newArray(int size) {
            return new FlickrPhoto[size];
        }
    };
    private String id;
    private String owner;
    private String secret;
    private String server;
    private int farm;
    private String title;
    private int isPublic;
    private int isFriend;
    private int isFamily;
    private String url_m;
    private int height_m;
    private int width_m;

    public FlickrPhoto() {

    }

    public FlickrPhoto(String id, String owner, String secret, String server, int farm, String title, int isPublic, int isFriend, int isFamily, String url_m, int height_m, int width_m) {
        this.id = id;
        this.owner = owner;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
        this.title = title;
        this.isPublic = isPublic;
        this.isFriend = isFriend;
        this.isFamily = isFamily;
        this.url_m = url_m;
        this.height_m = height_m;
        this.width_m = width_m;
    }

    private FlickrPhoto(Parcel in) {

        id = in.readString();
        owner = in.readString();
        secret = in.readString();
        server = in.readString();
        farm = in.readInt();
        title = in.readString();
        isPublic = in.readInt();
        isFriend = in.readInt();
        isFamily = in.readInt();
        url_m = in.readString();
        height_m = in.readInt();
        width_m = in.readInt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getFarm() {
        return farm;
    }

    public void setFarm(int farm) {
        this.farm = farm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public int getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(int isFriend) {
        this.isFriend = isFriend;
    }

    public int getIsFamily() {
        return isFamily;
    }

    public void setIsFamily(int isFamily) {
        this.isFamily = isFamily;
    }

    public String getUrl_m() {
        return url_m;
    }

    public void setUrl_m(String url_m) {
        this.url_m = url_m;
    }

    public int getHeight_m() {
        return height_m;
    }

    public void setHeight_m(int height_m) {
        this.height_m = height_m;
    }

    public int getWidth_m() {
        return width_m;
    }

    public void setWidth_m(int width_m) {
        this.width_m = width_m;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(owner);
        parcel.writeString(secret);
        parcel.writeString(server);
        parcel.writeInt(farm);
        parcel.writeString(title);
        parcel.writeInt(isPublic);
        parcel.writeInt(isFriend);
        parcel.writeInt(isFamily);
        parcel.writeString(url_m);
        parcel.writeInt(height_m);
        parcel.writeInt(width_m);
    }
}
