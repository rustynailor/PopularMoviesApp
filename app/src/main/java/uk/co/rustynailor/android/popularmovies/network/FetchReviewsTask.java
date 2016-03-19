package uk.co.rustynailor.android.popularmovies.network; /**
 * Created by russellhicks on 12/03/16.
 */

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import uk.co.rustynailor.android.popularmovies.BuildConfig;
import uk.co.rustynailor.android.popularmovies.MainDiscoveryFragment;
import uk.co.rustynailor.android.popularmovies.R;
import uk.co.rustynailor.android.popularmovies.models.Review;
import uk.co.rustynailor.android.popularmovies.models.Trailer;

/** Get movie data from the movie db api to display in the app **/
public class FetchReviewsTask extends AsyncTask<Void, Void, Review[]> {

    private Context mContext;
    private String mMovieId;
    private LinearLayout mReviewsContainer;

    public FetchReviewsTask(Context context, String movieId, LinearLayout reviewsContainer) {
        mContext = context;
        mMovieId = movieId;
        mReviewsContainer = reviewsContainer;
    }

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need for our movie app.
     */
    private Review[] getReviewDataFromJson(String ReviewJsonStr)
            throws JSONException {


        // These are the names of the JSON objects we need
        final String REVIEW_LIST = "results";
        final String REVIEW_ID = "id";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_URL = "url";

        JSONObject ReviewJson = new JSONObject(ReviewJsonStr);
        JSONArray ReviewArray = ReviewJson.getJSONArray(REVIEW_LIST);

        Review[] result = new Review[ReviewArray.length()];
        for(int i = 0; i < ReviewArray.length(); i++) {

            // Get the JSON object representing the Review
            JSONObject ReviewData = ReviewArray.getJSONObject(i);

            Review Review = new Review();

            //extract necessary fields
            Review.setId(ReviewData.getString(REVIEW_ID));
            Review.setAuthor(ReviewData.getString(REVIEW_AUTHOR));
            Review.setContent(ReviewData.getString(REVIEW_CONTENT));
            Review.setUrl(ReviewData.getString(REVIEW_URL));
            Review.setMovieId(mMovieId);

            result[i] = Review
            ;
        }



        return result;

    }


    @Override
    protected Review[] doInBackground(Void...params){

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
            final String MOVIE_API_PATH_3 = "reviews";



            builder.scheme("https")
                    .authority(MOVIE_API_AUTHORITY)
                    .appendPath(MOVIE_API_PATH_1)
                    .appendPath(MOVIE_API_PATH_2)
                    .appendPath(mMovieId)
                    .appendPath(MOVIE_API_PATH_3)
                    .appendQueryParameter("api_key", BuildConfig.MY_MOVIES_SAVED_API_KEY);

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
                Review[] returnArray = getReviewDataFromJson(movieJsonStr);
                return returnArray;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Review[] reviewData) {
        super.onPostExecute(reviewData);
        if(reviewData != null) {
            if(reviewData.length > 0) {
                mReviewsContainer.setVisibility(View.VISIBLE);
            }
            for (Review review : reviewData) {

                //add review to Linear Layout
                LayoutInflater inflater = LayoutInflater.from(mContext);

                LinearLayout reviewSingle = (LinearLayout)inflater.inflate(R.layout.single_review, null);

                TextView reviewAuthor = (TextView)reviewSingle.findViewById(R.id.authorName);
                reviewAuthor.setText(review.getAuthor());

                TextView reviewContent = (TextView)reviewSingle.findViewById(R.id.reviewContent);
                reviewContent.setText(review.getContent());

                //add to layout
                mReviewsContainer.addView(reviewSingle);

            }
        }
    }
}