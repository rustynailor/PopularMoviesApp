package uk.co.rustynailor.android.popularmovies.network;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.rustynailor.android.popularmovies.adapters.MovieGridviewAdapter;
import uk.co.rustynailor.android.popularmovies.data.FavouriteMovieProvider;
import uk.co.rustynailor.android.popularmovies.models.Movie;

/**
 *  Created by russellhicks on 24/03/16.
 *  This class loads movies from the database into the list adapter
 */
public class FetchMoviesFromDatabase implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;
    private MovieGridviewAdapter mAdapter;
    private static final String LOG_TAG = FetchMoviesFromDatabase.class.getSimpleName();

    private static final int CURSOR_LOADER_ID = 0;

    public FetchMoviesFromDatabase(Context mContext, MovieGridviewAdapter mAdapter) {
        this.mContext = mContext;
        this.mAdapter = mAdapter;
    }

    private Movie[] getMovieDataCursor(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects we need
        final String MOVIE_LIST = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_IMAGE_PATH = "poster_path";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_DESCRIPTION = "overview";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);

        Movie[] result = new Movie[movieArray.length()];
        for(int i = 0; i < movieArray.length(); i++) {

            // Get the JSON object representing the movie
            JSONObject movieData = movieArray.getJSONObject(i);

            Movie movie = new Movie();

            //extract necessary fields
            movie.setId(movieData.getString(MOVIE_ID));
            movie.setTitle(movieData.getString(MOVIE_TITLE));
            movie.setPosterPath(movieData.getString(MOVIE_IMAGE_PATH));
            movie.setReleaseDate(movieData.getString(MOVIE_RELEASE_DATE));
            movie.setVoteAverage(movieData.getString(MOVIE_RATING));
            movie.setMovieDescription(movieData.getString(MOVIE_DESCRIPTION));

            result[i] = movie;
        }



        return result;

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        return new CursorLoader(mContext, FavouriteMovieProvider.Movies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        //if there is data in the cursor, update the adapter
        if (data != null) {
            // move cursor to first row
            if (data.moveToFirst()) {
                do {
                    //TODO: create movie from cursor row and add to adapter
                    //String bookName = cursor.getString(cursor.getColumnIndex("bookTitle"));
                    //bookTitles.add(bookName);

                } while (data.moveToNext());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
