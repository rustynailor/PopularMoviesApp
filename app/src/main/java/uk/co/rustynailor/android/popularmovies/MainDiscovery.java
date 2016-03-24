package uk.co.rustynailor.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainDiscovery extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_discovery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_discovery, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        else if (id == R.id.sort_highest_rated) {
             //update default sort order
             updateMovieSortOrder(getString(R.string.highest_rated_sort_value));
        } else if (id == R.id.sort_most_popular) {
             updateMovieSortOrder(getString(R.string.most_popular_sort_value));
        } else if (id == R.id.sort_favourites) {
            updateMovieSortOrder(getString(R.string.favourites_sort_value));
        }


        return super.onOptionsItemSelected(item);
    }

    /** update shared preferences and initiate a list refresh when a new sort order is selected **/
    private void updateMovieSortOrder(String sortParameter) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(getString(R.string.pref_movie_sort_order_key), sortParameter).commit();
        refreshMovieList();
    }

    private void refreshMovieList() {
        MainDiscoveryFragment fragment = (MainDiscoveryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        fragment.clearList();
        fragment.updateMovies();
    }
}
