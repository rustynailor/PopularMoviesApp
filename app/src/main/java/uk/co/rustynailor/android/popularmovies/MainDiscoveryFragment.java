package uk.co.rustynailor.android.popularmovies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This fragment contains the main list of movie items
 */
public class MainDiscoveryFragment extends Fragment {

    //an array to hold movie items
    //TODO: update this to hold images as a custom Array Adapter
    public ArrayAdapter<String> mMovieAdapter;

    public MainDiscoveryFragment() {
    }

    private class FetchMoviesTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need for our movie app.
         */
        private String[] getMovieDataFromJson(String movieJsonStr)
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

            String[] resultStrs = new String[movieArray.length()];
            for(int i = 0; i < movieArray.length(); i++) {

                //These fields will be concatenated into a temporary return string
                String title;
                String posterPath;
                String releaseDate;
                String voteAverage;
                String movieDescription;

                // Get the JSON object representing the movie
                JSONObject movie = movieArray.getJSONObject(i);

                //extract neccessary fields
                title = movie.getString(MOVIE_TITLE);
                posterPath = movie.getString(MOVIE_IMAGE_PATH);
                releaseDate = movie.getString(MOVIE_RELEASE_DATE);
                voteAverage = movie.getString(MOVIE_RATING);
                movieDescription = movie.getString(MOVIE_DESCRIPTION);

                resultStrs[i] = title + " " + posterPath + " " + releaseDate + " " + voteAverage + " " + movieDescription;
            }



            return resultStrs;

        }


        @Override
        protected String[] doInBackground(Void...params){

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            final int numMovies = 7;

            try {
                // Construct the URL for the TheMovieDb query
                Uri.Builder builder = new Uri.Builder();


                //https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=a797949877dc172dd0682df3ec77aa21

                final String MOVIE_API_AUTHORITY = "api.themoviedb.org";
                final String MOVIE_API_PATH_1 = "3";
                final String MOVIE_API_PATH_2 = "discover";
                final String MOVIE_API_PATH_3 = "movie";
                final String MOVIE_API_KEY = BuildConfig.MY_MOVIES_SAVED_API_KEY;

                //get sort order from shared preferences
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String movie_sort_order = prefs.getString(getString(R.string.sort_pref_key), getString(R.string.sort_pref_default_value));


                builder.scheme("https")
                        .authority(MOVIE_API_AUTHORITY)
                        .appendPath(MOVIE_API_PATH_1)
                        .appendPath(MOVIE_API_PATH_2)
                        .appendPath(MOVIE_API_PATH_3)
                        .appendQueryParameter("sort_by", movie_sort_order)
                        .appendQueryParameter("api_key", MOVIE_API_KEY);

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
                Log.d(LOG_TAG, movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If we didn't successfully get the movie data, there's no point in attempting
                // to parse it.
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
                String[] returnArray = getMovieDataFromJson(movieJsonStr);
                return returnArray;
            } catch (JSONException e) {
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(String[] movieData) {
            super.onPostExecute(movieData);
            mMovieAdapter.clear(); // clear existing data
            for(String movie : movieData){
                mMovieAdapter.add(movie);
            }

        }
    }


    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    //we ovverrode
    @Override
    public void onResume() {
        super.onResume();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new FetchMoviesTask().execute();

        View rootView =  inflater.inflate(R.layout.fragment_main_discovery, container, false);

        //temporary string array for updates
        String[] placeholder = {
                "Movies Loading..."
        };

        List<String> placeholderArrayList = new ArrayList<String>(Arrays.asList(placeholder));


        mMovieAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_movie, R.id.list_item_movie_textview, placeholderArrayList);

        ListView movieList = (ListView)rootView.findViewById(R.id.listViewMovies);

        movieList.setAdapter(mMovieAdapter);

        return rootView;
    }

    private void updateMovies() {
        //get shared preference
        new FetchMoviesTask().execute();
    }

}
