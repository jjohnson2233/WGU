package com.example.v_jarj.wgu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "wgu.db";
    private static final int DATABASE_VERSION = 1;

    //Constants for identifying tables and columns
    public static final String TABLE_TERMS = "terms";
    public static final String TITLE = "title";
    public static final String ID = "_id";
    public static final String START = "startDate";
    public static final String END = "endDate";
    public static final String COURSE_ID = "course_ID";
    public static final String TABLE_COURSES = "courses";
    public static final String COURSE_STATUS = "status";
    public static final String MENTOR_ID = "mentorID";
    public static final String ASSESSMENT_ID = "assessmentID";
    public static final String TABLE_ASSESSMENTS = "assessments";
    public static final String ASSESSMENTS_DUE = "dueDate";
    public static final String ASSESSMENTS_TYPE = "type";
    public static final String ASSESSMENTS_ALERT = "alert";
    public static final String TABLE_MENTORS = "mentors";
    public static final String MENTORS_NAME = "name";
    public static final String MENTORS_PHONE = "phone";
    public static final String MENTORS_EMAIL = "email";


    //SQL to create tables
    private static final String TABLE_CREATE_MENTORS =
            "CREATE TABLE " + TABLE_MENTORS + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MENTORS_NAME + " TEXT, " +
                    MENTORS_PHONE + " TEXT, " +
                    MENTORS_EMAIL + " TEXT" +
                    ")";

    private static final String TABLE_CREATE_ASSESSMENTS =
            "CREATE TABLE " + TABLE_ASSESSMENTS + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TITLE + " TEXT, " +
                    ASSESSMENTS_DUE + " TEXT, " +
                    ASSESSMENTS_TYPE + " TEXT" +
                    ASSESSMENTS_ALERT + " TEXT" +
                    ")";

    private static final String TABLE_CREATE_COURSES =
            "CREATE TABLE " + TABLE_COURSES + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TITLE + " TEXT, " +
                    START + " TEXT, " +
                    END + " TEXT" +
                    COURSE_STATUS + " TEXT" +
                    MENTOR_ID + " INTEGER, " +
                    ASSESSMENT_ID + " INTEGER, " +
                    "FOREIGN KEY(" + MENTOR_ID + ") REFERENCES " + TABLE_MENTORS + "(" + ID + ")" +
                    "FOREIGN KEY(" + ASSESSMENT_ID + ") REFERENCES " + TABLE_ASSESSMENTS + "(" + ID + ")" +
                    ")";

    private static final String TABLE_CREATE_TERMS =
            "CREATE TABLE " + TABLE_TERMS + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TITLE + " TEXT, " +
                    START + " TEXT, " +
                    END + " TEXT, " +
                    COURSE_ID + " INTEGER, " +
                    "FOREIGN KEY(" + COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + ID + ")" +
                    ")";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_MENTORS);
        db.execSQL(TABLE_CREATE_ASSESSMENTS);
        db.execSQL(TABLE_CREATE_COURSES);
        db.execSQL(TABLE_CREATE_TERMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);
        onCreate(db);
    }
}
