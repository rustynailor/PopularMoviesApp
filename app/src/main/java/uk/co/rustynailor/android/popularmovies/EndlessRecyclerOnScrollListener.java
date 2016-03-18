package uk.co.rustynailor.android.popularmovies;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 *  Created by russellhicks on 02/02/16.
 *  Based on code originally found here: https://gist.github.com/ssinss/e06f12ef66c51252563e
 *  Written by https://gist.github.com/ssinss
 *  Minor updates to use grid layout manager instead of linear
 *  and to offer reset methods on reload of data
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int mPreviousTotal; // The total number of items in the dataset after the initial load
    private boolean mLoading = false; // True if we are still waiting for the last set of data to load.
    private int mVisibleThreshold = 6; // The minimum amount of items to have below your current scroll position before loading more.
    int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;

    private GridLayoutManager mGridLayoutManager;

    public EndlessRecyclerOnScrollListener(int mPreviousTotal, GridLayoutManager mGridLayoutManager) {
        this.mPreviousTotal = mPreviousTotal;
        this.mGridLayoutManager = mGridLayoutManager;
    }

    public EndlessRecyclerOnScrollListener(GridLayoutManager gridLayoutManager) {
        this.mGridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        mVisibleItemCount = recyclerView.getChildCount();
        mTotalItemCount = mGridLayoutManager.getItemCount();
        mFirstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();

        if (mLoading) {

            if (mTotalItemCount > mPreviousTotal) {
                mLoading = false;
                mPreviousTotal = mTotalItemCount;
            }
        }

        if (!mLoading &&
                mFirstVisibleItem != -1 && //deal with repopulated array from onRotate
                (mTotalItemCount - mVisibleItemCount)
                <= (mFirstVisibleItem + mVisibleThreshold)) {
                // End has been reached
                // Trigger Load in calling Activity
                onLoadMore();
                mLoading = true;
        }
    }

    /** set method for member variable */
    // when the sort preference is changed, we need to reset the total and loading state
    //as the previous item count is no longer relevant
    public void setTotal(int newTotal)
    {
        mPreviousTotal = newTotal;
    }

    /** set method for member variable */
    //also when sort order is changed, this is used to reset the loading state to false
    //to prevent it being stuck in a loading state with no api call
    public void setLoadingState(boolean loadingState)
    {
        mLoading = loadingState;
    }

    //this is implemented in MainDiscoveryFragment to carry out the background update
    public abstract void onLoadMore();
}