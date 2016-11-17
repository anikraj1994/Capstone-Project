package me.anikraj.campussecrets;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by anikr on 11/17/2016.
 */
public class PostDB extends SQLiteOpenHelper {
    private static final String DEBUG_TAG = "PostDB";
    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "post_data";

    public static final String TABLE_NAME = "posts";
    public static final String ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_TYPE = "type";

    private static final String CREATE_TABLE = "create table " + TABLE_NAME
            + " (" + ID + " integer primary key autoincrement, " + COL_TITLE
            + " text not null, "+ COL_TYPE
            + " text not null);";

    private static final String DB_SCHEMA = CREATE_TABLE;

    public PostDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DEBUG_TAG, "Upgrading database. Existing contents will be lost. ["
                + oldVersion + "]->[" + newVersion + "]");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
