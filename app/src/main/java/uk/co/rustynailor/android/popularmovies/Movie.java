package uk.co.rustynailor.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This models a single movie
 * Created by russellhicks on 31/01/16.
 */
public class Movie implements Parcelable{

    private String mTitle;
    private String mPosterPath;
    private String mReleaseDate;
    private String mVoteAverage;
    private String mMovieDescription;

    //constructors added for parcelable
    public Movie(){
    }

    public Movie(Parcel source){
        readFromParcel(source);
    }

    public String getTitle() {
        return mTitle;
    }

    public static final Parcelable.Creator<Movie> CREATOR =
            new Parcelable.Creator<Movie>(){

                @Override
                public Movie createFromParcel(Parcel source) {
                    return new Movie(source);
                }

                @Override
                public Movie[] newArray(int size) {
                    return new Movie[size];
                }
            };

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(String mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public String getMovieDescription() {
        return mMovieDescription;
    }

    public void setMovieDescription(String mMovieDescription) {
        this.mMovieDescription = mMovieDescription;
    }

    @Override
    public int describeContents() {
        return 0; //unused
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mReleaseDate);
        dest.writeString(mVoteAverage);
        dest.writeString(mMovieDescription);
    }

    public void readFromParcel(Parcel source){
        mTitle = source.readString();
        mPosterPath = source.readString();
        mReleaseDate = source.readString();
        mVoteAverage = source.readString();
        mMovieDescription = source.readString();
    }
}
