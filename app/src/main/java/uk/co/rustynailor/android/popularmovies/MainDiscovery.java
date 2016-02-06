package uk.co.rustynailor.android.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

        Log.d("CLICK ON MENU", "ID:" + id);

        //noinspection SimplifiableIfStatement
         if (id == R.id.sort_highest_rated) {
             //update default sort order
             SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
             prefs.edit().putString(getString(R.string.pref_movie_sort_order_key), getString(R.string.highest_rated_sort_value)).commit();
             MainDiscoveryFragment fragment = (MainDiscoveryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
             fragment.clearList();
             fragment.updateMovies();

        } else if (id == R.id.sort_most_popular) {
                 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                 prefs.edit().putString(getString(R.string.pref_movie_sort_order_key), getString(R.string.most_popular_sort_value)).commit();
                 MainDiscoveryFragment fragment = (MainDiscoveryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                 fragment.clearList();
                 fragment.updateMovies();

         }

        return super.onOptionsItemSelected(item);
    }
}
