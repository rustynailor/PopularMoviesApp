package uk.co.rustynailor.android.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.rustynailor.android.popularmovies.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {
    private Movie mMovie;
    private TextView mMovieTitle;
    private TextView mYearOfRelease;
    private TextView mLength;
    private TextView mRating;
    private TextView mSynopsis;


    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        //unbundle parceled movie data
        mMovie = (Movie) getActivity().getIntent().getExtras().getParcelable(getActivity().getString(R.string.parceled_movie_identifier));

        //assign to TextViews
        mMovieTitle = (TextView) view.findViewById(R.id.movieTitle);
        mMovieTitle.setText(mMovie.getTitle());

        mYearOfRelease = (TextView) view.findViewById(R.id.yearOfRelease);
        mYearOfRelease.setText(mMovie.getReleaseDate());

        mLength = (TextView) view.findViewById(R.id.length);
        mLength.setText(mMovie.getLength());

        mRating = (TextView) view.findViewById(R.id.rating);
        mRating.setText(mMovie.getVoteAverage());

        mSynopsis = (TextView) view.findViewById(R.id.synopsis);
        mSynopsis.setText(mMovie.getMovieDescription());

        return view;
    }
}
