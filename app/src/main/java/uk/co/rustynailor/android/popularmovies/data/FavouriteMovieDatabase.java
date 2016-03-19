package uk.co.rustynailor.android.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by russellhicks on 19/03/16.
 */

@Database(version = FavouriteMovieDatabase.VERSION)
public final class FavouriteMovieDatabase {

    private FavouriteMovieDatabase(){}

    public static final int VERSION = 1;

    @Table(FavouriteMovieColumns.class) public static final String FAVOURITE_MOVIES = "favourite_movies";

}
