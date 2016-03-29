package uk.co.rustynailor.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.rustynailor.android.popularmovies.adapters.MovieGridviewAdapter;
import uk.co.rustynailor.android.popularmovies.models.Movie;
import uk.co.rustynailor.android.popularmovies.network.FetchMoviesFromDatabase;
import uk.co.rustynailor.android.popularmovies.network.FetchMoviesTask;


/**
 * This fragment contains the logic for the main list of movie items
 */
public class MainDiscoveryFragment extends Fragment {

    //base View
    private View mRootView;
    //our custom adapter
    private MovieGridviewAdapter adapter;
    //holder for "no favourites" message
    private TextView mNoFavouritesMessage;
    //Gridview for poster layout
    private RecyclerView mRecyclerview;
    //page count for polling API with, updated in onScrollListener
    //set to public / static so it can be accessed by the Fetch Movies Task
    public static int mPageCount = 1;
    //layout manager for recycler view
    private GridLayoutManager mLayoutManager;
    //endless scroll manager
    private EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;
    //number of columns in gridlayout - placed in member var for later
    // manipulation into a tablet layout
    //it is public to allow access from the adapter
    public static int mNumColumns;
    //number of movies returned per page from api
    public static int mMoviesPerRequest = 20;
    //boolean to track whether or not this is a two panel tablet layout
    private boolean mTwoPane;
    private static final String MOVIEDETAILFRAGMENT_TAG = "MDFTAG";

    //sort order
    private String mMovieSortOrder;
    private SharedPreferences mSharedPreferences;


    //used to store list state to preserve position
    private int mSavedListPosition;
    private static final String LIST_STATE_KEY = "LIST_STATE_KEY";
    private static final String PAGE_COUNT_KEY = "PAGE_COUNT_KEY";
    private static final String MOVIE_ARRAY_KEY = "MOVIE_ARRAY_KEY";
    private ArrayList<Movie> mInitialMovies;
    private int mInitialMovieCount;


    public MainDiscoveryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mRootView =  inflater.inflate(R.layout.fragment_main_discovery, container, false);

        if (mRootView.findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            mNumColumns = 3;

        } else {
            //as we have a smaller phone layout, set number of columns to be 2
            mNumColumns = 2;
            mTwoPane = false;
        }

        //get shared preferences - used later to check sort order
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        //initial read of sort order
        mMovieSortOrder = mSharedPreferences.getString(getContext().getString(R.string.pref_movie_sort_order_key),
                getContext().getString(R.string.pref_movie_sort_order_default_value));

        return mRootView;
    }


    //store list state for device rotation etc
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save list state
        outState.putInt(LIST_STATE_KEY, mEndlessRecyclerOnScrollListener.mFirstVisibleItem);
        outState.putInt(PAGE_COUNT_KEY, mPageCount);
        outState.putParcelableArrayList(MOVIE_ARRAY_KEY, adapter.getAllItems());
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            // Retrieve list state and list/item positions
            if(savedInstanceState != null) {
               mSavedListPosition = savedInstanceState.getInt(LIST_STATE_KEY);
               mPageCount = savedInstanceState.getInt(PAGE_COUNT_KEY);
               mInitialMovies = savedInstanceState.getParcelableArrayList(MOVIE_ARRAY_KEY);
            } else {
                mPageCount = 1;
                mSavedListPosition = 1;
            }
        MovieItemClickListener movieItemClickListener = new MovieItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Bundle b = new Bundle();
                b.putParcelable(getActivity().getString(R.string.parceled_movie_identifier), adapter.getItem(position));
                if (mTwoPane) {
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(b);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment, MOVIEDETAILFRAGMENT_TAG)
                            .commit();
                } else {
                    Intent i = new Intent(getActivity(), MovieDetail.class);
                    i.putExtras(b);
                    getActivity().startActivity(i);
                }
            }
        };


        //get reference to no saved favourites message
        mNoFavouritesMessage = (TextView) mRootView.findViewById(R.id.no_favourites_message);

        if(mInitialMovies == null){
            adapter = new MovieGridviewAdapter(getActivity(), movieItemClickListener);
            mInitialMovieCount = mMoviesPerRequest;
            updateMovies();
        } else {
            adapter = new MovieGridviewAdapter(getActivity(),movieItemClickListener, mInitialMovies);
            mInitialMovieCount = mInitialMovies.size();

        }



        mRecyclerview = (RecyclerView) mRootView.findViewById(R.id.recyclerview_movies);
        // use a linear layout manager
        mRecyclerview.setAdapter(adapter);
        mLayoutManager = new GridLayoutManager(getActivity(), mNumColumns, GridLayoutManager.VERTICAL, false);
        mRecyclerview.setLayoutManager(mLayoutManager);
        mRecyclerview.setHasFixedSize(true);

        mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(mInitialMovieCount, mLayoutManager) {
            @Override
            public void onLoadMore() {
                // load next page of movies - only if this isn't favourites (as favourites is loaded in one hit)
                if(!mMovieSortOrder.equals(getContext().getString(R.string.favourites_sort_value))){
                    mPageCount++;
                    updateMovies();
                }
            }
        };

        mRecyclerview.addOnScrollListener(mEndlessRecyclerOnScrollListener);



        //initial movie load if we aren't restoring from bundle
        if(mInitialMovies == null){
            updateMovies();
        } else {
            //as we are restoring from bundle, scroll to last position
            mRecyclerview.scrollToPosition(mSavedListPosition);
        }
    }

    //** restart loader onResume if we are in favourites view - required to keep add / delete favourites functionality after device rotation **/
    @Override
    public void onResume() {
        super.onResume();
        //load movies from database
        if(mMovieSortOrder.equals(getContext().getString(R.string.favourites_sort_value)))
        {
            //load movies from database
            getLoaderManager().restartLoader(FetchMoviesFromDatabase.CURSOR_LOADER_ID, null, new FetchMoviesFromDatabase(getActivity(), adapter, mNoFavouritesMessage));
        }
    }


    /** reset adapter and reset page counter to 1 */
    public void clearList() {
        adapter.clear();
        adapter.notifyDataSetChanged();
        mEndlessRecyclerOnScrollListener.setTotal(mMoviesPerRequest);
        mEndlessRecyclerOnScrollListener.setLoadingState(false);
        mPageCount = 1;
    }

    /** update the movie list by initiating a background load from the api or from db **/
    public void updateMovies() {
        //check sort order here - if favourites, we load from db
        mMovieSortOrder = mSharedPreferences.getString(getContext().getString(R.string.pref_movie_sort_order_key),
                getContext().getString(R.string.pref_movie_sort_order_default_value));

        //if we are loading from favourites, grab directly from database
        if(mMovieSortOrder.equals(getContext().getString(R.string.favourites_sort_value)))
        {
            //load movies from database
            getLoaderManager().initLoader(FetchMoviesFromDatabase.CURSOR_LOADER_ID, null, new FetchMoviesFromDatabase(getActivity(), adapter, mNoFavouritesMessage));
        }
        else
        {
            //destroy database cursor loader if we are not in favourites view
            getLoaderManager().destroyLoader(FetchMoviesFromDatabase.CURSOR_LOADER_ID);
            //make sure no favourites message is hidden
            mNoFavouritesMessage.setVisibility(View.GONE);
            //else initiate database load
            new FetchMoviesTask(getContext(), adapter).execute();
        }
    }

}
