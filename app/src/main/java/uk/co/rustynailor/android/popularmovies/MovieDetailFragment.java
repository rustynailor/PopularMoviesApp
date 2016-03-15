package uk.co.rustynailor.android.popularmovies;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import uk.co.rustynailor.android.popularmovies.models.Movie;
import uk.co.rustynailor.android.popularmovies.network.FetchMoviesTask;
import uk.co.rustynailor.android.popularmovies.network.FetchReviewsTask;
import uk.co.rustynailor.android.popularmovies.network.FetchTrailersTask;


/**
 * A fragment containing a singel movie view
 */
public class MovieDetailFragment extends Fragment {
    private Movie mMovie;
    private TextView mMovieTitle;
    private TextView mYearOfRelease;
    private TextView mLength;
    private TextView mRating;
    private TextView mSynopsis;
    private ImageView mPoster;


    public MovieDetailFragment() {
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

        //load trailers and reviews
        new FetchTrailersTask(getContext(), mMovie.getId()).execute();
        new FetchReviewsTask(getContext(), mMovie.getId()).execute();

        //assign to TextViews
        mMovieTitle = (TextView) view.findViewById(R.id.movieTitle);
        mMovieTitle.setText(mMovie.getTitle());

        mYearOfRelease = (TextView) view.findViewById(R.id.yearOfRelease);
        mYearOfRelease.setText(mMovie.getYearOfRelease());

        mRating = (TextView) view.findViewById(R.id.rating);
        mRating.setText(mMovie.getFormattedVoteAverage());

        mSynopsis = (TextView) view.findViewById(R.id.synopsis);
        mSynopsis.setText(mMovie.getMovieDescription());

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


        String url = builder.build().toString();
        Log.d("MOVIE POSTER VIEW", url);

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


        Picasso.with(getActivity())
                .load(url)
                .resize(targetWidth, targetHeight)
                .centerInside()
                .into(mPoster);

        return view;
    }
}
