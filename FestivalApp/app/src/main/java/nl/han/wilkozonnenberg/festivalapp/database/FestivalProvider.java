package nl.han.wilkozonnenberg.festivalapp.database;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Wilko on 06-11-16.
 */

public class FestivalProvider extends ContentProvider{

    private FestivalDBHelper festivalDB;

    private static final String AUTHORITY =
            "nl.han.wilkozonnenberg.festivalapp.database";
    public static final String FESTIVALS_TABLE = "Festivals";
    private static final String PERFORMANCES_TABLE = "Performances";
    private static final String USER_PROGRAM_TABLE = "UserProgram";
    public static final Uri PERFORMANCES_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PERFORMANCES_TABLE);
    public static final Uri FESTIVALS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + FESTIVALS_TABLE);
    public static final Uri USER_PROGRAM_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + USER_PROGRAM_TABLE);
    public static final Uri USER_PROGRAM_ID_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + USER_PROGRAM_TABLE + "/ID");

    private static final UriMatcher sURIMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final int FESTIVALS = 1;
    public static final int FESTIVALS_ID = 2;
    public static final int PERFORMANCES = 3;
    public static final int USER_PROGRAM = 4;
    public static final int USER_PROGRAM_ID = 5;

    static {
        sURIMatcher.addURI(AUTHORITY, FESTIVALS_TABLE, FESTIVALS);
        sURIMatcher.addURI(AUTHORITY, FESTIVALS_TABLE + "/#",
                FESTIVALS_ID);
        sURIMatcher.addURI(AUTHORITY, PERFORMANCES_TABLE, PERFORMANCES);
        sURIMatcher.addURI(AUTHORITY, USER_PROGRAM_TABLE, USER_PROGRAM);
        sURIMatcher.addURI(AUTHORITY, USER_PROGRAM_TABLE + "/ID", USER_PROGRAM_ID);
    }

    private Cursor getWeatherByLocationSettingAndDate(
            Uri uri, String[] projection, String sortOrder) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(FESTIVALS_TABLE);

        return sqliteQueryBuilder.query(festivalDB.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        festivalDB = new FestivalDBHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sURIMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case FESTIVALS:
                return "Festival";
            case FESTIVALS_ID:
                return "Festival";
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sURIMatcher.match(uri);

        SQLiteDatabase sqlDB = festivalDB.getWritableDatabase();

        long id = 0;
        switch (match) {
            case FESTIVALS:
                String description = (String)values.get("description");
                if(description.contains("'")) {
                    description =  description.replace("'", "");
                }
                String title = (String) values.get("title");
                title = title.replace("'", "");
                String latitude = (String)values.get("latitude");
                String longitude = (String)values.get("longitude");
                String genre = (String)values.get("genre");
                String image_large = (String)values.get("image_large");
                String image_thumb = (String)values.get("image_thumb");
                String _id = String.valueOf((int)values.get("_id"));
                festivalDB.getWritableDatabase().execSQL("INSERT INTO " + FESTIVALS_TABLE + " VALUES('" + _id + "','"+ title + "', '" + genre + "', '" +
                        description + "', '" + image_large + "', '" + image_thumb + "', '" +
                        longitude + "', '" + latitude + "')");
                break;
            case PERFORMANCES:
                String title2 = (String) values.get("title");
                String end = (String)values.get("end");
                String start = (String)values.get("start");
                String festival_id = Integer.toString((int)values.get("festival_id"));
                double price = Double.parseDouble((String)values.get("price"));
                String _id2 = (String)values.get("_id");

                festivalDB.getWritableDatabase().execSQL("INSERT INTO " + PERFORMANCES_TABLE + " VALUES('" + _id2 + "','"+ festival_id + "', '" + end + "', '" +
                        start + "', '" + title2 + "', '" + price + "')");
                break;
            case USER_PROGRAM:
                String _id3 = (String)values.get("performance_id");
                festivalDB.getWritableDatabase().execSQL("INSERT INTO " + USER_PROGRAM_TABLE + "(performance_id) VALUES('" + _id3 + "')");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(FESTIVALS_TABLE + "/" + id);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();


        int uriType = sURIMatcher.match(uri);
        Cursor cursor;
        switch (uriType) {
            case PERFORMANCES:
                queryBuilder.setTables(PERFORMANCES_TABLE);
                cursor = queryBuilder.query(festivalDB.getReadableDatabase(), null, selection, null, null,null,null);
                break;
            case FESTIVALS:
                queryBuilder.setTables(FESTIVALS_TABLE);
                cursor = queryBuilder.query(festivalDB.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case USER_PROGRAM:
                queryBuilder.setTables(USER_PROGRAM_TABLE + " LEFT OUTER JOIN " +
                        PERFORMANCES_TABLE + " ON (" + USER_PROGRAM_TABLE +".performance_id = " +
                        PERFORMANCES_TABLE + "._id) JOIN " + FESTIVALS_TABLE + " ON " +
                        "(" + PERFORMANCES_TABLE + ".festival_id = " + FESTIVALS_TABLE + "._id)" );
                String[] strings = {USER_PROGRAM_TABLE+".id as _id", FESTIVALS_TABLE + ".title","start", "end", "price"};
                cursor = queryBuilder.query(festivalDB.getReadableDatabase(),
                        strings, selection, selectionArgs, null, null, "date(" + PERFORMANCES_TABLE + ".start) ASC");
                break;
            case USER_PROGRAM_ID:
                queryBuilder.setTables(USER_PROGRAM_TABLE);
                cursor = queryBuilder.query(festivalDB.getReadableDatabase(),
                        null, selection, null, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = festivalDB.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriType) {
            case FESTIVALS:
                rowsUpdated = sqlDB.update(FESTIVALS_TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case FESTIVALS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(FESTIVALS_TABLE,
                            values,
                            "_id" + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(FESTIVALS_TABLE,
                            values,
                            "_id" + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }


    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = festivalDB.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case FESTIVALS:
                rowsDeleted = sqlDB.delete(FESTIVALS_TABLE,
                        selection,
                        selectionArgs);
                break;
            case USER_PROGRAM:
                rowsDeleted = sqlDB.delete(USER_PROGRAM_TABLE,
                        selection,
                        selectionArgs);
                break;
            case FESTIVALS_ID:
                String id = uri.getLastPathSegment();
                    rowsDeleted = sqlDB.delete(FESTIVALS_TABLE,
                            "_id" + "=" + id,
                            null);

                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = festivalDB.getWritableDatabase();
        final int match = sURIMatcher.match(uri);
        switch (match) {
            case FESTIVALS:
                festivalDB.resetDB(festivalDB.getWritableDatabase());
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        String description = (String)value.get("description");
                        if(description.contains("'")) {
                           description =  description.replace("'", "");
                        }
                        String title = (String) value.get("title");
                        title = title.replace("'", "");
                        String latitude = (String)value.get("latitude");
                        String longitude = (String)value.get("longitude");
                        String genre = (String)value.get("genre");
                        String image_large = (String)value.get("image_large");
                        String image_thumb = (String)value.get("image_thumb");
                        String _id = String.valueOf((int)value.get("_id"));

                        db.execSQL("INSERT INTO Festivals VALUES('" + _id + "','"+ title + "', '" + genre + "', '" +
                                description + "', '" + image_large + "', '" + image_thumb + "', '" +
                                longitude + "', '" + latitude + "')");

                            returnCount++;

                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case PERFORMANCES:
                festivalDB.resetDB(festivalDB.getWritableDatabase());
                db.beginTransaction();
                int returnCount2 = 0;
                try {
                    for (ContentValues value : values) {
                        String title = (String) value.get("title");
                        title = title.replace("'", "");
                        String end = (String)value.get("end");
                        String start = (String)value.get("start");
                        String festival_id = (String)value.get("festival_id");
                        double price = (double)value.get("price");
                        String _id = String.valueOf((int)value.get("_id"));

                        db.execSQL("INSERT INTO Performances VALUES('" + _id + "','"+ festival_id + "', '" + end + "', '" +
                                start + "', '" + title + "', '" + price + "')");

                        returnCount2++;

                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount2;
            default:
                return super.bulkInsert(uri, values);
        }
    }




    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    /*
//        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
//        test this by uncommenting testGetType in TestProvider.
//     */
//    @Override
//    public String getType(Uri uri) {
//
//        // Use the Uri Matcher to determine what kind of URI this is.
//        final int match = sUriMatcher.match(uri);
//
//        switch (match) {
//            // Student: Uncomment and fill out these two cases
//            case WEATHER_WITH_LOCATION_AND_DATE:
//                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
//            case WEATHER_WITH_LOCATION:
//                return WeatherContract.WeatherEntry.CONTENT_TYPE;
//            case WEATHER:
//                return WeatherContract.WeatherEntry.CONTENT_TYPE;
//            case LOCATION:
//                return WeatherContract.LocationEntry.CONTENT_TYPE;
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
//    }
//
//    @Override
//    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
//                        String sortOrder) {
//        // Here's the switch statement that, given a URI, will determine what kind of request it is,
//        // and query the database accordingly.
//        Cursor retCursor;
//        switch (sUriMatcher.match(uri)) {
//            // "weather/*/*"
//            case WEATHER_WITH_LOCATION_AND_DATE:
//            {
//                retCursor = getWeatherByLocationSettingAndDate(uri, projection, sortOrder);
//                break;
//            }
//            // "weather/*"
//            case WEATHER_WITH_LOCATION: {
//                retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
//                break;
//            }
//            // "weather"
//            case WEATHER: {
//                retCursor = festivalDB.getReadableDatabase().query(
//                        WeatherContract.WeatherEntry.TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        sortOrder
//                );
//                break;
//            }
//            // "location"
//            case LOCATION: {
//                retCursor = festivalDB.getReadableDatabase().query(
//                        WeatherContract.LocationEntry.TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        sortOrder
//                );
//                break;
//            }
//
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
//        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
//        return retCursor;
//    }
//
//    /*
//        Student: Add the ability to insert Locations to the implementation of this function.
//     */
////    @Override
////    public Uri insert(Uri uri, ContentValues values) {
////        final SQLiteDatabase db = festivalDB.getWritableDatabase();
////        final int match = sUriMatcher.match(uri);
////        Uri returnUri;
////
////        switch (match) {
////            case WEATHER: {
////                normalizeDate(values);
////                long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
////                if ( _id > 0 )
////                    returnUri = WeatherContract.WeatherEntry.buildWeatherUri(_id);
////                else
////                    throw new android.database.SQLException("Failed to insert row into " + uri);
////                break;
////            }
////            case LOCATION: {
////                long _id = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);
////                if ( _id > 0 )
////                    returnUri = WeatherContract.LocationEntry.buildLocationUri(_id);
////                else
////                    throw new android.database.SQLException("Failed to insert row into " + uri);
////                break;
////            }
////            default:
////                throw new UnsupportedOperationException("Unknown uri: " + uri);
////        }
////        getContext().getContentResolver().notifyChange(uri, null);
////        return returnUri;
////    }
//
//    @Override
//    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        final SQLiteDatabase db = festivalDB.getWritableDatabase();
//        final int match = sUriMatcher.match(uri);
//        int rowsDeleted;
//        // this makes delete all rows return the number of rows deleted
//        if ( null == selection ) selection = "1";
//        switch (match) {
//            case WEATHER:
//                rowsDeleted = db.delete(
//                        WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
//                break;
//            case LOCATION:
//                rowsDeleted = db.delete(
//                        WeatherContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
//                break;
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
//        // Because a null deletes all rows
//        if (rowsDeleted != 0) {
//            getContext().getContentResolver().notifyChange(uri, null);
//        }
//        return rowsDeleted;
//    }
//
//    private void normalizeDate(ContentValues values) {
//        // normalize the date value
//        if (values.containsKey(WeatherContract.WeatherEntry.COLUMN_DATE)) {
//            long dateValue = values.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
//            values.put(WeatherContract.WeatherEntry.COLUMN_DATE, WeatherContract.normalizeDate(dateValue));
//        }
//    }
//
//    @Override
//    public int update(
//            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//        final SQLiteDatabase db = festivalDB.getWritableDatabase();
//        final int match = sUriMatcher.match(uri);
//        int rowsUpdated;
//
//        switch (match) {
//            case WEATHER:
//                normalizeDate(values);
//                rowsUpdated = db.update(WeatherContract.WeatherEntry.TABLE_NAME, values, selection,
//                        selectionArgs);
//                break;
//            case LOCATION:
//                rowsUpdated = db.update(WeatherContract.LocationEntry.TABLE_NAME, values, selection,
//                        selectionArgs);
//                break;
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
//        if (rowsUpdated != 0) {
//            getContext().getContentResolver().notifyChange(uri, null);
//        }
//        return rowsUpdated;
//    }
//
//
//
//    // You do not need to call this method. This is a method specifically to assist the testing
//    // framework in running smoothly. You can read more at:
//    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
//    @Override
//    @TargetApi(11)
//    public void shutdown() {
//        festivalDB.close();
//        super.shutdown();
//    }
}