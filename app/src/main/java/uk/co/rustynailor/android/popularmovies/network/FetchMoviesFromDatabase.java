package uk.co.rustynailor.android.popularmovies.network;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.rustynailor.android.popularmovies.adapters.MovieGridviewAdapter;
import uk.co.rustynailor.android.popularmovies.data.FavouriteMovieColumns;
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
    private TextView mNoFavouritesTextView;

    public static final int CURSOR_LOADER_ID = 0;

    public FetchMoviesFromDatabase(Context context, MovieGridviewAdapter adapter, TextView noFavouritesTextView) {
        mContext = context;
        mAdapter = adapter;
        mNoFavouritesTextView = noFavouritesTextView;
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

        //clear any existing movies from previous load
        mAdapter.clear();

        //if there is data in the cursor, update the adapter
        if (data != null) {
            // move cursor to first row
            if (data.moveToFirst()) {
                //hide  message informing user that they have no favourites
                mNoFavouritesTextView.setVisibility(View.GONE);
                do {
                    //create movie from cursor row and add to adapter
                    Movie movie = new Movie();
                    //movie is by definition a favourite
                    movie.setIsFavourite(true);
                    //extract necessary fields
                    movie.setId(data.getString(data.getColumnIndex(FavouriteMovieColumns.API_ID)));
                    movie.setTitle(data.getString(data.getColumnIndex(FavouriteMovieColumns.TITLE)));
                    movie.setPosterPath(data.getString(data.getColumnIndex(FavouriteMovieColumns.POSTER_PATH)));
                    movie.setReleaseDate(data.getString(data.getColumnIndex(FavouriteMovieColumns.RELEASE_DATE)));
                    movie.setVoteAverage(data.getString(data.getColumnIndex(FavouriteMovieColumns.VOTE_AVERAGE)));
                    movie.setMovieDescription(data.getString(data.getColumnIndex(FavouriteMovieColumns.MOVIE_DESCRIPTION)));

                    mAdapter.add(movie);
                } while (data.moveToNext());
            } else {
                //empty cursor - show message informing user that they have no favourites
                mNoFavouritesTextView.setVisibility(View.VISIBLE);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
