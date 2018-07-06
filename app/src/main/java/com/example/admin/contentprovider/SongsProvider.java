package com.example.admin.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

public class SongsProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "om.example.admin.contentprovider.SongsProvider";
    public static final String URL = "content://" + PROVIDER_NAME + "/songs";
    public static final Uri CONTENT_URI = Uri.parse(URL);


    static final String _ID = "_id";
    static final String NAME = "name";
    static final String TYPE = "type";
    static final String COMPOSER = "composer";
    static final String MASTERPIECE = "masterPiece";
    private HashMap<String,String> SONGS_PROJECTION_MAP;
    static final int SONGS = 1;
    static final int SONGS_ID = 2;
    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "songs", SONGS);
        uriMatcher.addURI(PROVIDER_NAME, "songs/#", SONGS_ID);
    }

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Collection";
    static final String SONGS_TABLE_NAME = "songs";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE = "CREATE TABLE "+SONGS_TABLE_NAME+
            " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
            " name TEXT NOT NULL, "+
            " type TEXT NOT NULL, "+
            " composer TEXT NOT NULL, "+
            " masterPiece TEXT NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + SONGS_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
       Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long rowID = db.insert(SONGS_TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(SONGS_TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case SONGS:
                qb.setProjectionMap(SONGS_PROJECTION_MAP);
                break;
            case SONGS_ID:
                qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
                break;
                default:
        }
        if (sortOrder == null || sortOrder == "") {
            sortOrder = NAME;
        }
        Cursor cursor = db.query(SONGS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case SONGS:
                count = db.delete(SONGS_TABLE_NAME, selection, selectionArgs);
                break;
            case SONGS_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(SONGS_TABLE_NAME, _ID + " = " + id + (TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
                break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case SONGS:
                count = db.update(SONGS_TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case SONGS_ID:
                count = db.update(SONGS_TABLE_NAME, contentValues, _ID + " = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
                break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SONGS:
                return "vnd.android.cursor.dir/vnd.example.admin.songs";

            case SONGS_ID:
                return "vnd.android.cursor.item/vnd.example.admin.songs";
                default:
                    throw new IllegalArgumentException("Unsopported URI: " + uri);
        }

    }
}
