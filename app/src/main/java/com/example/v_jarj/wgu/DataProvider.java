package com.example.v_jarj.wgu;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DataProvider extends ContentProvider {
    //Authority
    private static final String AUTHORITY = "com.example.v_jarj.wgu.dataprovider";

    //Paths
    private static final String TERMS_PATH = "terms";
    private static final String COURSES_PATH = "courses";
    private static final String MENTORS_PATH = "mentors";
    private static final String ASSESSMENTS_PATH = "assessments";

    //Uri's
    public static final Uri TERMS_URI = Uri.parse("content://" + AUTHORITY + "/" + TERMS_PATH );
    public static final Uri COURSES_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSES_PATH );
    public static final Uri MENTORS_URI = Uri.parse("content://" + AUTHORITY + "/" + MENTORS_PATH );
    public static final Uri ASSESSMENTS_URI = Uri.parse("content://" + AUTHORITY + "/" + ASSESSMENTS_PATH );

    //Constants for identification
    private static final int TERMS = 1;
    private static final int TERMS_ID = 2;
    private static final int COURSES = 3;
    private static final int COURSES_ID = 4;
    private static final int MENTORS = 5;
    private static final int MENTORS_ID = 6;
    private static final int ASSESSMENTS = 7;
    private static final int ASSESSMENTS_ID = 8;

    //Create uri matcher
    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    //Match uri's
    static {
        //Match Terms
        uriMatcher.addURI(AUTHORITY, TERMS_PATH, TERMS);
        uriMatcher.addURI(AUTHORITY, TERMS_PATH + "/#", TERMS_ID);
        //Match Courses
        uriMatcher.addURI(AUTHORITY, COURSES_PATH, COURSES);
        uriMatcher.addURI(AUTHORITY, COURSES_PATH + "/#", COURSES_ID);
        //Match Mentors
        uriMatcher.addURI(AUTHORITY, MENTORS_PATH, MENTORS);
        uriMatcher.addURI(AUTHORITY, MENTORS_PATH + "/#", MENTORS_ID);
        //Match Assessments
        uriMatcher.addURI(AUTHORITY, ASSESSMENTS_PATH, ASSESSMENTS);
        uriMatcher.addURI(AUTHORITY, ASSESSMENTS_PATH + "/#", ASSESSMENTS_ID);
    }

    //Declare database
    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        if (uriMatcher.match(uri) == TERMS_ID) {
            s = DBOpenHelper.ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DBOpenHelper.TABLE_TERMS, DBOpenHelper.TERMS_ALL_COLUMNS,
                s, null, null, null,
                DBOpenHelper.TERM_TITLE + " ASC");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long id = database.insert(DBOpenHelper.TABLE_TERMS,
                null, contentValues);
        return Uri.parse(TERMS_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return database.delete(DBOpenHelper.TABLE_TERMS, s, strings);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return database.update(DBOpenHelper.TABLE_TERMS, contentValues, s, strings);
    }
}
