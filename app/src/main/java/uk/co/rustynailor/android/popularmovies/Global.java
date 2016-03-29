package uk.co.rustynailor.android.popularmovies;

import android.app.Application;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Global class to enable Picasso image caching
 * based on answer here: http://stackoverflow.com/questions/23978828/how-do-i-use-disk-caching-in-picasso
 * Created by russell on 29/03/2016.
 */
public class Global extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(false);
        Picasso.setSingletonInstance(built);

    }
}