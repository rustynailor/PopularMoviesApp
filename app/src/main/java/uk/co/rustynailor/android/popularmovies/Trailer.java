package uk.co.rustynailor.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This models a single movie
 * Created by russellhicks on 31/01/16.
 */
public class Trailer implements Parcelable{

    private String mId;
    private String mKey;
    private String mName;
    private String mSite;
    private int mSize;
    private String mType;

    //constructors added for parcelable
    public Trailer(){
    }

    public Trailer(Parcel source){
        readFromParcel(source);
    }


    public static final Creator<Trailer> CREATOR =
            new Creator<Trailer>(){

                @Override
                public Trailer createFromParcel(Parcel source) {
                    return new Trailer(source);
                }

                @Override
                public Trailer[] newArray(int size) {
                    return new Trailer[size];
                }
            };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSite() {
        return mSite;
    }

    public void setSite(String site) {
        mSite = site;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        mSize = size;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    @Override
    public int describeContents() {
        return 0; //unused
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeString(mSite);
        dest.writeInt(mSize);
        dest.writeString(mType);
    }

    public void readFromParcel(Parcel source){
        mId = source.readString();
        mKey = source.readString();
        mName = source.readString();
        mSite = source.readString();
        mSize = source.readInt();
        mType = source.readString();
    }

}
