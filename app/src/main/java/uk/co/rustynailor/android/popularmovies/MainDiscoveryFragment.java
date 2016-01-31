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
                // If we didn't successfully get the movie data, there's no point in attemping
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


            return null;
        }

        @Override
        protected void onPostExecute(String[] forecastData) {
            super.onPostExecute(forecastData);
            //mMovieAdapter.clear(); // clear existing data

        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new FetchMoviesTask().execute();

        return inflater.inflate(R.layout.fragment_main_discovery, container, false);
    }
}
