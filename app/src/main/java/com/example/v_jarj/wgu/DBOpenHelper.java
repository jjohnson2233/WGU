package com.example.v_jarj.wgu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "wgu.db";
    private static final int DATABASE_VERSION = 1;

    //Constants for identifying tables and columns
    //Universal id value
    public static final String ID = "_id";
    //Terms table
    public static final String TABLE_TERMS = "terms";
    public static final String TERM_ID = "termID";
    public static final String TERM_TITLE = "title";
    public static final String TERM_START = "startDate";
    public static final String TERM_END = "endDate";
    public static final String[] TERMS_ALL_COLUMNS = 
            {ID, TERM_TITLE, TERM_START, TERM_END};

    //Mentors Table
    public static final String TABLE_MENTORS = "mentors";
    public static final String MENTOR_ID = "mentorID";
    public static final String MENTOR_NAME = "name";
    public static final String MENTOR_PHONE = "phone";
    public static final String MENTOR_EMAIL = "email";
    public static final String[] MENTORS_ALL_COLUMNS =
            {ID, MENTOR_NAME, MENTOR_PHONE, MENTOR_EMAIL};
    
    //Courses Table
    public static final String TABLE_COURSES = "courses";
    public static final String COURSE_ID = "course_ID";
    public static final String COURSE_STATUS = "status";
    public static final String[] COURSES_ALL_COLUMNS = 
            {ID, COURSE_STATUS, MENTOR_ID, TERM_ID};

    //Assessments Table
    public static final String TABLE_ASSESSMENTS = "assessments";
    public static final String ASSESSMENT_ID = "assessmentID";
    public static final String ASSESSMENT_TITLE = "title";
    public static final String ASSESSMENT_DUE = "dueDate";
    public static final String ASSESSMENT_TYPE = "type";
    public static final String ASSESSMENT_ALERT = "alert";
    public static final String[] ASSESSMENTS_ALL_COLUMNS = 
            {ID, ASSESSMENT_TITLE, ASSESSMENT_DUE, ASSESSMENT_TYPE, ASSESSMENT_ALERT, COURSE_ID};


    private static final String TABLE_CREATE_TERMS =
            "CREATE TABLE " + TABLE_TERMS + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TERM_TITLE + " TEXT, " +
                    TERM_START + " TEXT, " +
                    TERM_END + " TEXT" +
                    ")";

    //SQL to create tables
    private static final String TABLE_CREATE_MENTORS =
            "CREATE TABLE " + TABLE_MENTORS + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MENTOR_NAME + " TEXT, " +
                    MENTOR_PHONE + " TEXT, " +
                    MENTOR_EMAIL + " TEXT" +
                    ")";

    private static final String TABLE_CREATE_COURSES =
            "CREATE TABLE " + TABLE_COURSES + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TERM_TITLE + " TEXT, " +
                    TERM_START + " TEXT, " +
                    TERM_END + " TEXT, " +
                    COURSE_STATUS + " TEXT, " +
                    MENTOR_ID + " INTEGER, " +
                    TERM_ID + " INTEGER, " +
                    "FOREIGN KEY(" + MENTOR_ID + ") REFERENCES " + TABLE_MENTORS + "(" + ID + "), " +
                    "FOREIGN KEY(" + TERM_ID + ") REFERENCES " + TABLE_TERMS + "(" + ID + ")" +
                    ")";

    private static final String TABLE_CREATE_ASSESSMENTS =
            "CREATE TABLE " + TABLE_ASSESSMENTS + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TERM_TITLE + " TEXT, " +
                    ASSESSMENT_DUE + " TEXT, " +
                    ASSESSMENT_TYPE + " TEXT, " +
                    ASSESSMENT_ALERT + " TEXT, " +
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
