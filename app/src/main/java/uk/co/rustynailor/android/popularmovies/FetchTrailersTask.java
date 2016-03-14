package uk.co.rustynailor.android.popularmovies; /**
 * Created by russellhicks on 12/03/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
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

/** Get movie data from the movie db api to display in the app **/
public class FetchTrailersTask extends AsyncTask<Void, Void, Trailer[]> {

    private Context mContext;
    private String mMovieId;

    public FetchTrailersTask(Context context, String movieId) {
        mContext = context;
        mMovieId = movieId;
    }

    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need for our movie app.
     */
    private Trailer[] getTrailerDataFromJson(String trailerJsonStr)
            throws JSONException {


        // These are the names of the JSON objects we need
        final String TRAILER_LIST = "results";
        final String TRAILER_ID = "id";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";
        final String TRAILER_SITE = "site";
        final String TRAILER_SIZE = "size";
        final String TRAILER_TYPE = "type";

        JSONObject trailerJson = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray(TRAILER_LIST);

        Trailer[] result = new Trailer[trailerArray.length()];
        for(int i = 0; i < trailerArray.length(); i++) {

            // Get the JSON object representing the trailer
            JSONObject trailerData = trailerArray.getJSONObject(i);

            Trailer trailer = new Trailer();

            //extract necessary fields
            trailer.setId(trailerData.getString(TRAILER_ID));
            trailer.setKey(trailerData.getString(TRAILER_KEY));
            trailer.setName(trailerData.getString(TRAILER_NAME));
            trailer.setSite(trailerData.getString(TRAILER_SITE));
            trailer.setSize(trailerData.getInt(TRAILER_SIZE));
            trailer.setType(trailerData.getString(TRAILER_TYPE));

            result[i] = trailer
            ;
        }



        return result;

    }


    @Override
    protected Trailer[] doInBackground(Void...params){

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
            final String MOVIE_API_PATH_3 = "videos";



            builder.scheme("https")
                    .authority(MOVIE_API_AUTHORITY)
                    .appendPath(MOVIE_API_PATH_1)
                    .appendPath(MOVIE_API_PATH_2)
                    .appendPath(mMovieId)
                    .appendPath(MOVIE_API_PATH_3);

            URL url = new URL(builder.build().toString());

            Log.e(LOG_TAG, "Getting page: " + MainDiscoveryFragment.mPageCount);


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
            ((Activity)mContext).runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(mContext, mContext.getString(R.string.connection_error_message), Toast.LENGTH_LONG);
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
                Trailer[] returnArray = getTrailerDataFromJson(movieJsonStr);
                return returnArray;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Trailer[] trailerData) {
        super.onPostExecute(trailerData);
        if(trailerData != null) {
            //for (Movie movie : movieData) {
                //mAdapter.add(movie);
                //mAdapter.notifyDataSetChanged();
         //   }
        }
    }
}