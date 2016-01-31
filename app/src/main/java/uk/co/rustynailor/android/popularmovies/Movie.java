package uk.co.rustynailor.android.popularmovies;

/**
 * This models a single movie
 * Created by russellhicks on 31/01/16.
 */
public class Movie {

    private String mTitle;
    private String mPosterPath;
    private String mReleaseDate;
    private String mVoteAverage;
    private String mMovieDescription;

    public String getTitle() {
        return mTitle;
    }

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
}
