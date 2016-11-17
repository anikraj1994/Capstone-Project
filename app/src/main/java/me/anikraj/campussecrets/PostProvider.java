package me.anikraj.campussecrets;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by anikr on 11/17/2016.
 */
public class PostProvider extends ContentProvider {
    private PostDB mDB;
    private static final String AUTHORITY = "me.anikraj.campussecrets.PostProvider";
    public static final int POSTS = 100;
    public static final int POST_ID = 110;

    private static final String TUTORIALS_BASE_PATH = "posts";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TUTORIALS_BASE_PATH);
    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, TUTORIALS_BASE_PATH, POSTS);
        sURIMatcher.addURI(AUTHORITY, TUTORIALS_BASE_PATH + "/#", POST_ID);
    }



    @Override
    public boolean onCreate() {
        mDB = new PostDB(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(PostDB.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case POST_ID:
                queryBuilder.appendWhere(PostDB.ID + "="
                        + uri.getLastPathSegment());
                break;
            case POSTS:
                // no filter
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case POSTS:
                return "vnd.android.cursor.dir/vnd.example.posts";
            case POST_ID:
                return "vnd.android.cursor.item/vnd.example.posts";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = mDB.getWritableDatabase().insert(	mDB.TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        mDB.getWritableDatabase().delete(PostDB.TABLE_NAME,null,null);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
