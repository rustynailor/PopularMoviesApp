package uk.co.rustynailor.android.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This models a single movie
 * Created by russellhicks on 31/01/16.
 */
public class Movie implements Parcelable{

    private String mId;
    private String mTitle;
    private String mPosterPath;
    private String mReleaseDate;
    private String mVoteAverage;
    private String mMovieDescription;
    private String mLength;
    private Boolean mIsFavourite = false; //default value for favourite movie

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


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    /** return year only */
    public String getYearOfRelease() {

        return mReleaseDate.substring(0,4);
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    /** return vote with '/10' appended for display */
    public String getFormattedVoteAverage() {

        return mVoteAverage + "/10";
    }

    public void setVoteAverage(String voteAverage) {
        mVoteAverage = voteAverage;
    }

    public String getMovieDescription() {
        return mMovieDescription;
    }

    public void setMovieDescription(String mMovieDescription) {
        this.mMovieDescription = mMovieDescription;
    }

    public Boolean getIsFavourite() {
        return mIsFavourite;
    }

    public void setIsFavourite(Boolean isFavourite) {
        mIsFavourite = isFavourite;
    }

    @Override
    public int describeContents() {
        return 0; //unused
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mReleaseDate);
        dest.writeString(mVoteAverage);
        dest.writeString(mMovieDescription);
        dest.writeString(mLength);
        dest.writeByte((byte) (mIsFavourite ? 1 : 0));
    }

    public void readFromParcel(Parcel source){
        mId = source.readString();
        mTitle = source.readString();
        mPosterPath = source.readString();
        mReleaseDate = source.readString();
        mVoteAverage = source.readString();
        mMovieDescription = source.readString();
        mLength = source.readString();
        mIsFavourite = source.readByte() != 0;
    }

}
