package uk.co.rustynailor.android.popularmovies;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.rustynailor.android.popularmovies.data.FavouriteMovieColumns;
import uk.co.rustynailor.android.popularmovies.data.FavouriteMovieProvider;
import uk.co.rustynailor.android.popularmovies.models.Movie;
import uk.co.rustynailor.android.popularmovies.network.FetchMoviesTask;
import uk.co.rustynailor.android.popularmovies.network.FetchReviewsTask;
import uk.co.rustynailor.android.popularmovies.network.FetchTrailersTask;


/**
 * A fragment containing a singel movie view
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> { 
    private Movie mMovie;
    private TextView mMovieTitle;
    private TextView mYearOfRelease;
    private TextView mRating;
    private TextView mSynopsis;
    private ImageView mPoster;
    private LinearLayout mTrailerContainer;
    private LinearLayout mReviewContainer;
    private Button mFavouriteButton;

    //for sharing intent
    private ShareActionProvider mShareActionProvider;
    private MenuItem mSharingButton;

    public static final int FAVOURITE_CHECK_LOADER_ID = 1;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_movie_detail, menu);

        // Retrieve the share menu item
        mSharingButton = menu.findItem(R.id.action_share);

        //hide it until we are sure we have a trailer
        mSharingButton.setVisible(false);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(mSharingButton);

        //load trailers and reviews
        //we do this here so we can pass the share action provider to the fetch trailers task
        new FetchTrailersTask(getContext(), mMovie.getId(), mTrailerContainer, mShareActionProvider, mSharingButton).execute();
        new FetchReviewsTask(getContext(), mMovie.getId(), mReviewContainer).execute();

        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        //unbundle parceled movie data
        //either passed directly
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = (Movie) arguments.getParcelable(getActivity().getString(R.string.parceled_movie_identifier));
        } else {
           //started by Movie detail activity - get args from parent
            mMovie = (Movie) getActivity().getIntent().getExtras().getParcelable(getActivity().getString(R.string.parceled_movie_identifier));
        }

        mTrailerContainer = (LinearLayout)view.findViewById(R.id.trailerContainer);
        mReviewContainer = (LinearLayout)view.findViewById(R.id.reviewContainer);

        //assign to TextViews
        mMovieTitle = (TextView) view.findViewById(R.id.movieTitle);
        mMovieTitle.setText(mMovie.getTitle());

        mYearOfRelease = (TextView) view.findViewById(R.id.yearOfRelease);
        mYearOfRelease.setText(mMovie.getYearOfRelease());

        mRating = (TextView) view.findViewById(R.id.rating);
        mRating.setText(mMovie.getFormattedVoteAverage());

        mSynopsis = (TextView) view.findViewById(R.id.synopsis);
        mSynopsis.setText(mMovie.getMovieDescription());

        //favourite button - assign to variable and start loader
        mFavouriteButton = (Button)view.findViewById(R.id.favourite_button);
        //we do this here so there is a reference available to mFavouriteButton
        getLoaderManager().initLoader(FAVOURITE_CHECK_LOADER_ID, null, this);

        mPoster = (ImageView) view.findViewById(R.id.poster);

        //Build URL to download image
        Uri.Builder builder = new Uri.Builder();

        final String MOVIE_API_IMAGE_AUTHORITY = "image.tmdb.org";
        final String MOVIE_API_PATH_1 = "t";
        final String MOVIE_API_PATH_2 = "p";
        final String MOVIE_API_PATH_3 = "w185";

        //Trim leading slash from path
        String posterPath = mMovie.getPosterPath().substring(1);

        builder.scheme("http")
                .authority(MOVIE_API_IMAGE_AUTHORITY)
                .appendPath(MOVIE_API_PATH_1)
                .appendPath(MOVIE_API_PATH_2)
                .appendPath(MOVIE_API_PATH_3)
                .appendPath(posterPath);


        final String url = builder.build().toString();

        int width= getActivity().getResources()
                .getDisplayMetrics()
                .widthPixels;

        int numColumns = 2;

        //cinema poster ratio, as found here
        //http://www.imdb.com/help/show_leaf?photosspecs
        double posterRatio = 1.48;

        //set appropriate size for resizing based on layout type
        int targetWidth;
        if(MainDiscoveryFragment.mNumColumns == 2){
            // phone layout - take up 1/2 of screen
            targetWidth = width / 2;
        } else {
            // tablet layout - take up 1/4 of screen
            targetWidth = width / 4;
        }

        int targetHeight  =   (int) Math.round(targetWidth * posterRatio);

        //edited to use Picasso image cache first
        //then attempt load - based on this answer:
        //http://stackoverflow.com/questions/23978828/how-do-i-use-disk-caching-in-picasso
        Picasso.with(getActivity())
                .load(url)
                .resize(targetWidth, targetHeight)
                .centerInside()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(mPoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        //not required
                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(getActivity())
                                .load(url)
                                .into(mPoster, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        //not required
                                    }

                                    @Override
                                    public void onError() {
                                        Log.e("Picasso", "Could not fetch image from either cache or online");
                                    }
                                });
                    }
                });

        return view;
    }

    /* add movie to favourites via content provider */
    public void insertMovieToFavourites(){

        //Insert movie to database
        ContentValues valuesForInsert = new ContentValues();
        valuesForInsert.put(FavouriteMovieColumns.API_ID, mMovie.getId());
        valuesForInsert.put(FavouriteMovieColumns.MOVIE_DESCRIPTION, mMovie.getMovieDescription());
        valuesForInsert.put(FavouriteMovieColumns.POSTER_PATH, mMovie.getPosterPath());
        valuesForInsert.put(FavouriteMovieColumns.RELEASE_DATE, mMovie.getReleaseDate());
        valuesForInsert.put(FavouriteMovieColumns.TITLE, mMovie.getTitle());
        valuesForInsert.put(FavouriteMovieColumns.VOTE_AVERAGE, mMovie.getVoteAverage());

        getActivity().getContentResolver().insert(FavouriteMovieProvider.Movies.CONTENT_URI, valuesForInsert);

    }

    /* remove movie from favourites via content provider */
    public void deleteMovieFromFavourites(){

        //build appropriate URI
        Uri movieUri = FavouriteMovieProvider.Movies.CONTENT_URI.buildUpon().appendPath(mMovie.getId()).build();

        //Delete values //
        String [] arguments = new String[1];
        arguments[0] = mMovie.getId();
        String selectionClause = FavouriteMovieColumns.API_ID + " = ?";

        getContext().getContentResolver().delete(movieUri, selectionClause, arguments);

        //after delete, update button function:
        mFavouriteButton.setText(R.string.add_favourite_text);
        mFavouriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                insertMovieToFavourites();
            }
        });


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        //build appropriate URI
        Uri movieUri = FavouriteMovieProvider.Movies.CONTENT_URI.buildUpon().appendPath(mMovie.getId()).build();

        CursorLoader favouriteCheck = new CursorLoader(
                getContext(),
                movieUri,
                null,
                null,
                null,
                null);

        return favouriteCheck;
    }

    /* this is called both on initial load, and when a favourite is inserted or removed */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data != null && data.moveToFirst()){
            //movie is already a favourite - change button text and display
            mFavouriteButton.setText(R.string.remove_favourite_text);
            mFavouriteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    deleteMovieFromFavourites();
                }
            });
        } else {
            //movie is not a favourite - add listener to add to favourites
            mFavouriteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    insertMovieToFavourites();
                }
            });
        }

        //now make button visible
        mFavouriteButton.setVisibility(View.VISIBLE);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //not required here
    }
}
