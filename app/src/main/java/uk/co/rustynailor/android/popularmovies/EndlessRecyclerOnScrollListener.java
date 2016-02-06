package uk.co.rustynailor.android.popularmovies;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 *  Created by russellhicks on 02/02/16.
 *  Based on code originally found here: https://gist.github.com/ssinss/e06f12ef66c51252563e
 *  Written by https://gist.github.com/ssinss
 *  Minor updates to use grid layout manager instead of linear
 *
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 19; // The total number of items in the dataset after the initial load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 4; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private GridLayoutManager mGridLayoutManager;

    public EndlessRecyclerOnScrollListener(GridLayoutManager gridLayoutManager) {
        this.mGridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mGridLayoutManager.getItemCount();
        firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        if (!loading &&
                firstVisibleItem != -1  && //don't trigger load if we are at very beginning of results set.
                (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached
            // Trigger Load in calling Activity
            current_page++;
            Log.d("SCROLLING", "loading: " + loading + " | visibleItemCount: " + visibleItemCount + " | firstVisibleItem : " + firstVisibleItem);
            Log.d("LOAD MORE", "loading: " + loading + " | totalItemCount: " + totalItemCount + " | previousTotal : " + previousTotal);
            onLoadMore(current_page);


            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page);
}