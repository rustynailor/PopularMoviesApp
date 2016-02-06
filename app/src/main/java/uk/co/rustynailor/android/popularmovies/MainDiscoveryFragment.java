package uk.co.rustynailor.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * This fragment contains the logic for trhe main list of movie items
 */
public class MainDiscoveryFragment extends Fragment {

    //our custom adapter
    private MovieGridviewAdapter adapter;
    //Gridview for poster layout
    private RecyclerView mRecyclerview;
    //page count for polling API with, updated in onScrollListener
    private int mPageCount = 1;
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

    private class FetchMoviesTask extends AsyncTask<Void, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need for our movie app.
         */
        private Movie[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects we need
            final String MOVIE_LIST = "results";
            final String MOVIE_TITLE = "original_title";
            final String MOVIE_IMAGE_PATH = "poster_path";
            final String MOVIE_RELEASE_DATE = "release_date";
            final String MOVIE_RATING = "vote_average";
            final String MOVIE_DESCRIPTION = "overview";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);

            Movie[] result = new Movie[movieArray.length()];
            for(int i = 0; i < movieArray.length(); i++) {

                // Get the JSON object representing the movie
                JSONObject movieData = movieArray.getJSONObject(i);

                Movie movie = new Movie();

                //extract necessary fields
                movie.setTitle(movieData.getString(MOVIE_TITLE));
                movie.setPosterPath(movieData.getString(MOVIE_IMAGE_PATH));
                movie.setReleaseDate(movieData.getString(MOVIE_RELEASE_DATE));
                movie.setVoteAverage(movieData.getString(MOVIE_RATING));
                movie.setMovieDescription(movieData.getString(MOVIE_DESCRIPTION));

                result[i] = movie;
            }



            return result;

        }


        @Override
        protected Movie[] doInBackground(Void...params){

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                // Construct the URL for the TheMovieDb query
                Uri.Builder builder = new Uri.Builder();

                final String MOVIE_API_AUTHORITY = "api.themoviedb.org";
                final String MOVIE_API_PATH_1 = "3";
                final String MOVIE_API_PATH_2 = "movie";

                //get sort order from shared preferences
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String movie_sort_order = prefs.getString(getString(R.string.pref_movie_sort_order_key), getString(R.string.pref_movie_sort_order_default_value));


                builder.scheme("https")
                        .authority(MOVIE_API_AUTHORITY)
                        .appendPath(MOVIE_API_PATH_1)
                        .appendPath(MOVIE_API_PATH_2)
                        .appendPath(movie_sort_order)
                        .appendQueryParameter("api_key", BuildConfig.MY_MOVIES_SAVED_API_KEY)
                        .appendQueryParameter("page", Integer.toString(mPageCount));

                URL url = new URL(builder.build().toString());


               // Create the request to TheMovieDb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If we didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                // alert the user with a toast
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getActivity(), getActivity().getString(R.string.connection_error_message), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });

                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            //now convert JsonString to something usable
            try {
                if(movieJsonStr != null && !movieJsonStr.equals("")) {
                    Movie[] returnArray = getMovieDataFromJson(movieJsonStr);
                    return returnArray;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movieData) {
            super.onPostExecute(movieData);
            if(movieData != null) {
                for (Movie movie : movieData) {
                    adapter.add(movie);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        new FetchMoviesTask().execute();
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
        View rootView =  inflater.inflate(R.layout.fragment_main_discovery, container, false);
        mRecyclerview = (RecyclerView) rootView.findViewById(R.id.recyclerview_movies);
        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(getActivity(), mNumColumns,GridLayoutManager.VERTICAL, false);
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



    //reset adapter and reset page counter to 1
    public void clearList() {
        adapter.clear();
        adapter.notifyDataSetChanged();
        mEndlessRecyclerOnScrollListener.setTotal(mMoviesPerRequest);
        mEndlessRecyclerOnScrollListener.setLoadingState(false);
        mEndlessRecyclerOnScrollListener.setPageCount(1);
        mPageCount = 1;
    }

    public void updateMovies() {
        new FetchMoviesTask().execute();
    }

}
