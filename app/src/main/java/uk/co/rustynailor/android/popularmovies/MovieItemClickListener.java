package uk.co.rustynailor.android.popularmovies;

import android.view.View;

/**
 * Interface to handle clicks within the recyclerview
 * Created by russellhicks on 04/02/16.
 */
public interface MovieItemClickListener {
        void onItemClick(View v, int position);
}
