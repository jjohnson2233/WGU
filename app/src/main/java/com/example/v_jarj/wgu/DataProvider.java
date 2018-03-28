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
    private static final String NOTES_PATH = "notes";
    private static final String MENTORS_PATH = "mentors";
    private static final String ASSESSMENTS_PATH = "assessments";

    //Uri's
    public static final Uri TERMS_URI = Uri.parse("content://" + AUTHORITY + "/" + TERMS_PATH );
    public static final Uri COURSES_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSES_PATH );
    public static final Uri NOTES_URI = Uri.parse("content://" + AUTHORITY + "/" + NOTES_PATH);
    public static final Uri MENTORS_URI = Uri.parse("content://" + AUTHORITY + "/" + MENTORS_PATH );
    public static final Uri ASSESSMENTS_URI = Uri.parse("content://" + AUTHORITY + "/" + ASSESSMENTS_PATH );

    //Constants for identification
    private static final int TERMS = 1;
    private static final int TERMS_ID = 2;
    private static final int COURSES = 3;
    private static final int COURSES_ID = 4;
    private static final int NOTES = 5;
    private static final int NOTES_ID = 6;
    private static final int MENTORS = 7;
    private static final int MENTORS_ID = 8;
    private static final int ASSESSMENTS = 9;
    private static final int ASSESSMENTS_ID = 10;

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
        //Match Notes
        uriMatcher.addURI(AUTHORITY, NOTES_PATH, NOTES);
        uriMatcher.addURI(AUTHORITY, NOTES_PATH + "/#", NOTES_ID);
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

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        switch (uriMatcher.match(uri)) {
            case TERMS:case TERMS_ID:
                return database.query(DBOpenHelper.TABLE_TERMS, DBOpenHelper.TERMS_ALL_COLUMNS,
                        s, null, null, null,
                        DBOpenHelper.TERM_TITLE + " ASC");
            case COURSES:case COURSES_ID:
                return database.query(DBOpenHelper.TABLE_COURSES, DBOpenHelper.COURSES_ALL_COLUMNS,
                        s, null, null, null,
                        DBOpenHelper.COURSE_TITLE + " ASC");
            case NOTES: case NOTES_ID:
                return database.query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.NOTES_ALL_COLUMNS,
                        s, null, null, null,
                        DBOpenHelper.ID + " ASC");
            case MENTORS:case MENTORS_ID:
                return database.query(DBOpenHelper.TABLE_MENTORS, DBOpenHelper.MENTORS_ALL_COLUMNS,
                        s, null, null, null,
                        DBOpenHelper.MENTOR_NAME + " ASC");
            case ASSESSMENTS:case ASSESSMENTS_ID:
                return database.query(DBOpenHelper.TABLE_ASSESSMENTS, DBOpenHelper.ASSESSMENTS_ALL_COLUMNS,
                        s, null, null, null,
                        DBOpenHelper.ASSESSMENT_TITLE + " ASC");
            default:
                throw new IllegalArgumentException("Invalid Uri");
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long id;
        switch (uriMatcher.match(uri)) {
            case TERMS:case TERMS_ID:
                id = database.insert(DBOpenHelper.TABLE_TERMS,
                        null, contentValues);
                return Uri.parse(TERMS_PATH + "/" + id);
            case COURSES:case COURSES_ID:
                id = database.insert(DBOpenHelper.TABLE_COURSES,
                        null, contentValues);
                return Uri.parse(COURSES_PATH + "/" + id);
            case NOTES:case NOTES_ID:
                id = database.insert(DBOpenHelper.TABLE_NOTES,
                        null, contentValues);
                return Uri.parse(NOTES_PATH + "/" + id);
            case MENTORS:case MENTORS_ID:
                id = database.insert(DBOpenHelper.TABLE_MENTORS,
                        null, contentValues);
                return Uri.parse(MENTORS_PATH + "/" + id);
            case ASSESSMENTS:case ASSESSMENTS_ID:
                id = database.insert(DBOpenHelper.TABLE_ASSESSMENTS,
                        null, contentValues);
                return Uri.parse(ASSESSMENTS_PATH + "/" + id);
            default:
                throw new IllegalArgumentException("Invalid Uri");
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        switch (uriMatcher.match(uri)) {
            case TERMS:case TERMS_ID:
                return database.delete(DBOpenHelper.TABLE_TERMS, s, strings);
            case COURSES:case COURSES_ID:
                return database.delete(DBOpenHelper.TABLE_COURSES, s, strings);
            case NOTES:case NOTES_ID:
                return database.delete(DBOpenHelper.TABLE_NOTES, s, strings);
            case MENTORS:case MENTORS_ID:
                return database.delete(DBOpenHelper.TABLE_MENTORS, s, strings);
            case ASSESSMENTS:case ASSESSMENTS_ID:
                return database.delete(DBOpenHelper.TABLE_ASSESSMENTS, s, strings);
            default:
                throw new IllegalArgumentException("Invalid Uri");
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        switch (uriMatcher.match(uri)) {
            case TERMS:case TERMS_ID:
                return database.update(DBOpenHelper.TABLE_TERMS, contentValues, s, strings);
            case COURSES:case COURSES_ID:
                return database.update(DBOpenHelper.TABLE_COURSES, contentValues, s, strings);
            case NOTES:case NOTES_ID:
                return database.update(DBOpenHelper.TABLE_NOTES, contentValues, s, strings);
            case MENTORS:case MENTORS_ID:
                return database.update(DBOpenHelper.TABLE_MENTORS, contentValues, s, strings);
            case ASSESSMENTS:case ASSESSMENTS_ID:
                return database.update(DBOpenHelper.TABLE_ASSESSMENTS, contentValues, s, strings);
            default:
                throw new IllegalArgumentException("Invalid Uri");
        }
    }
}
