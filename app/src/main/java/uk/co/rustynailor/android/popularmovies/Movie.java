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
    private String mLength;

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

    public String getLength() {
        return mLength;
    }

    public void setLength(String length) {
        mLength = length;
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
        dest.writeString(mLength);
    }

    public void readFromParcel(Parcel source){
        mTitle = source.readString();
        mPosterPath = source.readString();
        mReleaseDate = source.readString();
        mVoteAverage = source.readString();
        mMovieDescription = source.readString();
        mLength = source.readString();
    }

}
