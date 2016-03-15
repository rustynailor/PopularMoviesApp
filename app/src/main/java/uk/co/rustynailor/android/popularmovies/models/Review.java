package uk.co.rustynailor.android.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This models a single movie
 * Created by russellhicks on 31/01/16.
 */
public class Review implements Parcelable{

    private String mMovieId;
    private String mId;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    //constructors added for parcelable
    public Review(){
    }

    public Review(Parcel source){
        readFromParcel(source);
    }


    public static final Creator<Review> CREATOR =
            new Creator<Review>(){

                @Override
                public Review createFromParcel(Parcel source) {
                    return new Review(source);
                }

                @Override
                public Review[] newArray(int size) {
                    return new Review[size];
                }
            };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getMovieId() {
        return mMovieId;
    }

    public void setMovieId(String movieId) {
        mMovieId = movieId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public int describeContents() {
        return 0; //unused
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieId);
        dest.writeString(mId);
        dest.writeString(mAuthor);
        dest.writeString(mContent);
        dest.writeString(mUrl);
    }

    public void readFromParcel(Parcel source){
        mMovieId = source.readString();
        mId = source.readString();
        mAuthor = source.readString();
        mContent = source.readString();
        mUrl = source.readString();
    }

}
