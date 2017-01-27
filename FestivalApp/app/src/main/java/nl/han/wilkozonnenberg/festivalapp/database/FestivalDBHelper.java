package nl.han.wilkozonnenberg.festivalapp.database;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Wilko on 06-11-16.
 */



public class FestivalDBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    public static final String DB_NAME = "FestivalDB.db";

    public FestivalDBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        resetDB(db);
    }

    public static void resetDB(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS Festivals");
        db.execSQL("DROP TABLE IF EXISTS Performances");
        db.execSQL("DROP TABLE IF EXISTS UserProgram");
        db.execSQL("CREATE TABLE Festivals(" +
                "_id TEXT PRIMARY KEY, " +
                "title TEXT, " +
                "genre TEXT, " +
                "description TEXT, " +
                "image_large TEXT, " +
                "image_thumb TEXT, " +
                "longitude TEXT," +
                "latitude TEXT" +
                " );");

        db.execSQL("CREATE TABLE Performances(" +
                "_id TEXT PRIMARY KEY, " +
                "festival_id TEXT NOT NULL, " +
                "end TEXT,  " +
                "start TEXT, " +
                "title TEXT, " +
                "price DOUBLE, " +
                "FOREIGN KEY(festival_id) REFERENCES Festivals(_id));");

        db.execSQL("CREATE TABLE UserProgram(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "performance_id TEXT UNIQUE, " +
                "FOREIGN KEY(performance_id) REFERENCES Performances(_id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("We've got no plans for an upgrade just yet");
    }
}
