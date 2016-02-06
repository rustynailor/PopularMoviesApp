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

    private int mPreviousTotal = 20; // The total number of items in the dataset after the initial load
    private boolean mLoading = false; // True if we are still waiting for the last set of data to load.
    private int mVisibleThreshold = 2; // The minimum amount of items to have below your current scroll position before loading more.
    int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;

    private int mCurrent_page = 1;

    private GridLayoutManager mGridLayoutManager;

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
                (mTotalItemCount - mVisibleItemCount)
                <= (mFirstVisibleItem + mVisibleThreshold)) {
            // End has been reached
            // Trigger Load in calling Activity
            mCurrent_page++;
            onLoadMore(mCurrent_page);
            mLoading = true;
        }
    }

    //when the sort preference is changed, we need to reset the total and loading state
    //as the previous item count is no longer relevant
    public void setTotal(int newTotal)
    {
        mPreviousTotal = newTotal;
    }

    //also when sort order is changed, this is used to reset the loading state to false
    //to prevent it being stuck in a loading state with no api call
    public void setLoadingState(boolean loadingState)
    {
        mLoading = loadingState;
    }

    //set page count
    public void setPageCount(int pageCount){
        mCurrent_page = pageCount;
    }

    //this is implemented in MainDiscoveryFragment to carry out the background update
    public abstract void onLoadMore(int current_page);
}