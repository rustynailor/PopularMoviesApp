package uk.co.rustynailor.android.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * This fragment contains the logic for the main list of movie items
 * the asynchronous download method is also in this file
 */
public class MainDiscoveryFragment extends Fragment {

    //our custom adapter
    private MovieGridviewAdapter adapter;
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
    public static int mNumColumns = 2;
    //number of movies returned per page from api
    public static int mMoviesPerRequest = 20;


    public MainDiscoveryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        adapter = new MovieGridviewAdapter(getActivity(), new MovieItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent i = new Intent(getActivity(), MovieDetail.class);
                Bundle b = new Bundle();
                b.putParcelable(getActivity().getString(R.string.parceled_movie_identifier), adapter.getItem(position));
                i.putExtras(b);
                getActivity().startActivity(i);
            }
        });
        new FetchMoviesTask(getContext(), adapter).execute();

        View rootView =  inflater.inflate(R.layout.fragment_main_discovery, container, false);
        mRecyclerview = (RecyclerView) rootView.findViewById(R.id.recyclerview_movies);
        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(getActivity(), mNumColumns, GridLayoutManager.VERTICAL, false);
        mRecyclerview.setLayoutManager(mLayoutManager);

        mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                // load next page of movies
                mPageCount = current_page;
                updateMovies();
            }
        };

        mRecyclerview.setOnScrollListener(mEndlessRecyclerOnScrollListener);

        mRecyclerview.setAdapter(adapter);

        return rootView;
    }



    /** reset adapter and reset page counter to 1 */
    public void clearList() {
        adapter.clear();
        adapter.notifyDataSetChanged();
        mEndlessRecyclerOnScrollListener.setTotal(mMoviesPerRequest);
        mEndlessRecyclerOnScrollListener.setLoadingState(false);
        mEndlessRecyclerOnScrollListener.setPageCount(1);
        mPageCount = 1;
    }

    /** update the movie list by initiating a background load from the api **/
    public void updateMovies() {
        new FetchMoviesTask(getContext(), adapter).execute();
    }

}
