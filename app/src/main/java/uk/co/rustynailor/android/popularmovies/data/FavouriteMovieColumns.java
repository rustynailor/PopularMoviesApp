package uk.co.rustynailor.android.popularmovies.data;
import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by russellhicks on 19/03/16.
 */
public interface FavouriteMovieColumns {


    //primary key auto increment integer
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";

    //api_id - mId in model
    @DataType(DataType.Type.INTEGER) @NotNull
    public static final String API_ID = "api_id";

    //title - mTitle
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String TITLE = "title";

    //poster_path - mPosterPath
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String POSTER_PATH = "poster_path";

    //release_date - mReleaseDate
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String RELEASE_DATE = "release_date";

    //vote_average - mVoteAverage
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String VOTE_AVERAGE = "vote_average";

    //movie_description - mMovieDescription
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String MOVIE_DESCRIPTION = "movie_description";

    //length - mLength
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String LENGTH = "length";



}
