package uk.co.rustynailor.android.popularmovies.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.rustynailor.android.popularmovies.MainDiscoveryFragment;
import uk.co.rustynailor.android.popularmovies.MovieItemClickListener;
import uk.co.rustynailor.android.popularmovies.models.Movie;

/**
 * Adapter to movie list recyclerview
 * Created by russellhicks on 31/01/16.
 */
public class MovieGridviewAdapter extends RecyclerView.Adapter<MovieGridviewAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Movie> mMovies;
    private MovieItemClickListener mListener;
    private int mTargetHeight, mTargetWidth;


    public MovieGridviewAdapter(Context c, MovieItemClickListener listener) {
        mContext = c;
        mMovies = new ArrayList<Movie>();
        mListener = listener;
        calculateDimensions();
    }

    public MovieGridviewAdapter(Context c, MovieItemClickListener listener, ArrayList<Movie> movies) {
        mContext = c;
        mMovies = movies;
        mListener = listener;
        calculateDimensions();
    }

    private void calculateDimensions(){

        //get display width to resize images accurately
        int width= mContext.getResources()
                .getDisplayMetrics()
                .widthPixels;

        //cinema poster ratio, as found here
        //http://www.imdb.com/help/show_leaf?photosspecs
        double posterRatio = 1.48;


        //set appropriate size for resizing based on layout type
        if(MainDiscoveryFragment.mNumColumns == 2){
            mTargetWidth = width / MainDiscoveryFragment.mNumColumns;
        } else {
            //2 pane layout, so reduce width by 50%
            mTargetWidth = width / MainDiscoveryFragment.mNumColumns / 2;
        }
        mTargetHeight = (int) Math.round((mTargetWidth) * posterRatio);

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;
        public ViewHolder(ImageView v) {
            super(v);
            mImageView = v;
        }
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void add(Movie movie){
        mMovies.add(movie);
    }

    public void clear(){
        mMovies.clear();
    }

    public Movie getItem(int position){
        return mMovies.get(position);
    }

    public ArrayList<Movie> getAllItems() {
        return mMovies;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public MovieGridviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new GridView.LayoutParams(mTargetWidth, mTargetHeight));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(0, 0, 0, 0);

        final ViewHolder vh = new ViewHolder(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, vh.getPosition());
            }
        });


        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        //Build URL to download image
        Uri.Builder builder = new Uri.Builder();

        final String MOVIE_API_IMAGE_AUTHORITY = "image.tmdb.org";
        final String MOVIE_API_PATH_1 = "t";
        final String MOVIE_API_PATH_2 = "p";
        final String MOVIE_API_PATH_3 = "w185";

        //Trim leading slash from path
        String posterPath = mMovies.get(position).getPosterPath().substring(1);

        builder.scheme("http")
                .authority(MOVIE_API_IMAGE_AUTHORITY)
                .appendPath(MOVIE_API_PATH_1)
                .appendPath(MOVIE_API_PATH_2)
                .appendPath(MOVIE_API_PATH_3)
                .appendPath(posterPath);


        final String url = builder.build().toString();

        //edited to use Picasso image cache first
        //then attempt load - based on this answer:
        //http://stackoverflow.com/questions/23978828/how-do-i-use-disk-caching-in-picasso
        Picasso.with(mContext)
                .load(url)
                .resize(mTargetWidth, mTargetHeight)
                .centerCrop()
                .into(holder.mImageView, new Callback() {

                    @Override
                    public void onSuccess() {
                        //not required
                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                    Picasso.with(mContext)
                            .load(url)
                            .resize(mTargetWidth, mTargetHeight)
                            .centerCrop()
                            .into(holder.mImageView, new Callback() {

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
    }

}