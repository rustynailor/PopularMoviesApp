package uk.co.rustynailor.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by russellhicks on 31/01/16.
 */
public class MovieGridviewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Movie> mMovies;

    public MovieGridviewAdapter(Context c) {
        mContext = c;
        mMovies = new ArrayList<Movie>();
    }

    public int getCount() {
        return mMovies.size();
    }

    public Movie getItem(int position) {
        return mMovies.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public void add(Movie movie){
        mMovies.add(movie);
    }

    public void clear(){
        mMovies.clear();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

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


        String url = builder.build().toString();
        Log.d("MOVIE GRID VIEW", url);

        //get display width to resize images accurately
        //Todo: numColumns needs to reflect number of columns in layout
        // revisit in design rework
        int width= mContext.getResources()
                .getDisplayMetrics()
                .widthPixels;

        int numColumns = 3;

        //cinema poster ratio, as found here
        //http://www.imdb.com/help/show_leaf?photosspecs
        double posterRatio = 1.48;


        Picasso.with(mContext)
                .load(url)
                .centerCrop()
                .resize(width / numColumns, (int) Math.round((width/numColumns)*posterRatio))
                .into(imageView);
        return imageView;
    }



}