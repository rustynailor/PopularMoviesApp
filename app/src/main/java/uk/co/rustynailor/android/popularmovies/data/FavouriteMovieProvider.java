package uk.co.rustynailor.android.popularmovies.data;

import android.content.ContentValues;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.NotifyInsert;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by russellhicks on 19/03/16.
 */
@ContentProvider(authority = FavouriteMovieProvider.AUTHORITY, database = FavouriteMovieDatabase.class)
public final class  FavouriteMovieProvider {
    public static final String AUTHORITY =
            "uk.co.rustynailor.android.popularmovies.data.FavouriteMovieProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String MOVIES = "movies";
    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }
    @TableEndpoint(table = FavouriteMovieDatabase.FAVOURITE_MOVIES) public static class Movies{
        @ContentUri(
                path = Path.MOVIES,
                type = "vnd.android.cursor.dir/movie",
                defaultSort = FavouriteMovieColumns.TITLE + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.MOVIES + "/#",
                type = "vnd.android.cursor.item/movie",
                whereColumn = FavouriteMovieColumns.API_ID,
                pathSegment = 1)
        public static Uri withId(long id){
            return buildUri(Path.MOVIES, String.valueOf(id));
        }

        @NotifyInsert(paths = Path.MOVIES) public static Uri[] onInsert(ContentValues values) {
            final long movieId = values.getAsLong(FavouriteMovieColumns.API_ID);
            return new Uri[] {
                    Movies.withId(movieId)
            };
        }
    }

}