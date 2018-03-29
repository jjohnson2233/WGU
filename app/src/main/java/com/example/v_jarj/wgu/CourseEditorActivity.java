package com.example.v_jarj.wgu;

        import android.app.DatePickerDialog;
        import android.app.LoaderManager;
        import android.content.ContentValues;
        import android.content.CursorLoader;
        import android.content.Intent;
        import android.content.Loader;
        import android.database.Cursor;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.design.widget.FloatingActionButton;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.WindowManager;
        import android.widget.ArrayAdapter;
        import android.widget.CursorAdapter;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.SimpleCursorAdapter;
        import android.widget.Spinner;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Locale;
        import java.util.Objects;

public class CourseEditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int RESULT_DELETED = 2;
    private String action;
    private EditText title;
    private EditText startDate;
    private EditText endDate;
    private Spinner statusSpinner;
    private String courseFilter;
    private String oldTitle;
    private String oldStart;
    private String oldEnd;
    private String oldStatus;
    private Calendar calendar;
    private SimpleDateFormat format;
    private CursorAdapter mentorCursorAdapter;
    private CursorAdapter assessmentCursorAdapter;
    private Uri uri;
    ListView mentorList;
    ListView assessmentList;
    String mentorFilter;
    String assessmentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        title = findViewById(R.id.title);
        startDate = findViewById(R.id.dueDate);
        endDate = findViewById(R.id.endDate);
        format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        statusSpinner = findViewById(R.id.status_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        String[] mentorFrom = {DBOpenHelper.MENTOR_NAME};
        String[] assessmentFrom = {DBOpenHelper.ASSESSMENT_TITLE};
        int[] to = {android.R.id.text1};
        mentorCursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_multiple_choice, null, mentorFrom, to, 0);
        assessmentCursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_multiple_choice, null, assessmentFrom, to, 0);

        mentorList = findViewById(R.id.mentorsList);
        assessmentList = findViewById(R.id.assessmentsList);
        mentorList.setAdapter(mentorCursorAdapter);
        assessmentList.setAdapter(assessmentCursorAdapter);
        mentorList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        assessmentList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mentorList.setItemsCanFocus(false);
        assessmentList.setItemsCanFocus(false);

        Intent intent = getIntent();

        uri = intent.getParcelableExtra("Course");

        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_course));
        } else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_course));
            courseFilter = DBOpenHelper.ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.COURSES_ALL_COLUMNS, courseFilter, null, null);
            cursor.moveToFirst();
            oldTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TITLE));
            oldStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_START));
            oldEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END));
            oldStatus = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_STATUS));
            title.setText(oldTitle);
            startDate.setText(oldStart);
            endDate.setText(oldEnd);
            switch (oldStatus) {
                case "Not Started":
                    statusSpinner.setSelection(0);
                    break;
                case "In-progress":
                    statusSpinner.setSelection(1);
                    break;
                case "Completed":
                    statusSpinner.setSelection(2);
                    break;
            }
            cursor.close();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishEditing();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.action_delete:
                deleteCourse();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_course_editor, menu);
        }
        return true;
    }

    private void deleteCourse() {
        getContentResolver().delete(DataProvider.COURSES_URI,
                courseFilter, null);
        setResult(RESULT_DELETED);
        finish();
    }

    public void openStartDatePicker(View view) throws ParseException {
        startDate = findViewById(R.id.dueDate);
        calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                String date = format.format(calendar.getTime());
                startDate.setText(date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void openEndDatePicker(View view) {
        endDate = findViewById(R.id.endDate);
        calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                String date = format.format(calendar.getTime());
                endDate = findViewById(R.id.endDate);
                endDate.setText(date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void finishEditing() {
        String newTitle = title.getText().toString().trim();
        String newStart = startDate.getText().toString().trim();
        String newEnd = endDate.getText().toString().trim();
        String newStatus = (String) statusSpinner.getSelectedItem();
        long[] mentors = mentorList.getCheckedItemIds();
        long[] assessments = assessmentList.getCheckedItemIds();

        switch (action) {
            case Intent.ACTION_INSERT:
                //Check to see if any of the fields are empty
                if (newTitle.isEmpty() || newStart.isEmpty() || newEnd.isEmpty()) {
                    //TODO create a pop up for when fields are empty
                    setResult(RESULT_CANCELED);
                } else {
                    createCourse(newTitle, newStart, newEnd, newStatus, mentors, assessments);
                }
                break;
            case Intent.ACTION_EDIT:
                //Check to see if anything was changed
                if (oldTitle.equals(newTitle) && oldStart.equals(newStart) && oldEnd.equals(newEnd)
                        && oldStatus.equals(newStatus) && mentors.length == 0 && assessments.length == 0) {
                    //If nothing was changed, don't do anything
                    setResult(RESULT_CANCELED);
                } else {
                    updateCourse(newTitle, newStart, newEnd, newStatus, mentors, assessments);
                }
        }
        finish();
    }

    private void updateCourse(String courseTitle, String courseStart, String courseEnd,
                              String courseStatus, long[] courseMentors, long[] courseAssessments) {
        ContentValues courseValues = new ContentValues();
        ContentValues mentorValues = new ContentValues();
        ContentValues assessmentValues = new ContentValues();
        ContentValues emptyValues = new ContentValues();
        String courseID = uri.getLastPathSegment();
        //Get the values for the course
        courseValues.put(DBOpenHelper.COURSE_TITLE, courseTitle);
        courseValues.put(DBOpenHelper.COURSE_START, courseStart);
        courseValues.put(DBOpenHelper.COURSE_END, courseEnd);
        courseValues.put(DBOpenHelper.COURSE_STATUS, courseStatus);
        //Get the values for the mentors
        mentorValues.put(DBOpenHelper.COURSE_ID, courseID);
        assessmentValues.put(DBOpenHelper.COURSE_ID, courseID);
        //Get null values for unchecked items
        emptyValues.putNull(DBOpenHelper.COURSE_ID);
        //Update course values in the database
        getContentResolver().update(DataProvider.COURSES_URI, courseValues, courseFilter, null);
        //Clear the course values for the unchecked items
        getContentResolver().update(DataProvider.MENTORS_URI, emptyValues, null, null);
        getContentResolver().update(DataProvider.ASSESSMENTS_URI, emptyValues, null, null);
        //Update mentor values in the database
        for (long id : courseMentors) {
            mentorFilter = DBOpenHelper.ID + "=" + id;
            getContentResolver().update(DataProvider.MENTORS_URI, mentorValues, mentorFilter, null);
        }
        for (long id : courseAssessments) {
            assessmentFilter = DBOpenHelper.ID + "=" + id;
            getContentResolver().update(DataProvider.ASSESSMENTS_URI, assessmentValues, assessmentFilter, null);
        }
        setResult(RESULT_OK);
    }

    private void createCourse(String courseTitle, String courseStart, String courseEnd, String courseStatus, long[] courseMentors, long[] courseAssessments) {
        ContentValues courseValues = new ContentValues();
        ContentValues mentorValues = new ContentValues();
        ContentValues assessmentValues = new ContentValues();
        //Get values for the course
        courseValues.put(DBOpenHelper.COURSE_TITLE, courseTitle);
        courseValues.put(DBOpenHelper.COURSE_START, courseStart);
        courseValues.put(DBOpenHelper.COURSE_END, courseEnd);
        courseValues.put(DBOpenHelper.COURSE_STATUS, courseStatus);
        //Create a new course with the values
        getContentResolver().insert(DataProvider.COURSES_URI, courseValues);
        courseFilter = DBOpenHelper.COURSE_TITLE + "=\"" + courseTitle + "\"";
        Cursor cursor = getContentResolver().query(DataProvider.COURSES_URI,
                DBOpenHelper.COURSES_ALL_COLUMNS, courseFilter, null, null);
        cursor.moveToFirst();
        int termID = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ID));
        mentorValues.put(DBOpenHelper.TERM_ID, termID);
        assessmentValues.put(DBOpenHelper.TERM_ID, termID);
        for (long id : courseMentors) {
            mentorFilter = DBOpenHelper.ID + "=" + id;
            getContentResolver().update(DataProvider.MENTORS_URI, mentorValues, mentorFilter, null);
        }
        for (long id : courseAssessments) {
            assessmentFilter = DBOpenHelper.ID + "=" + id;
            getContentResolver().update(DataProvider.ASSESSMENTS_URI, assessmentValues, assessmentFilter, null);
        }
        setResult(RESULT_OK);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        switch (id) {
            case 0:
                loader = new CursorLoader(this, DataProvider.MENTORS_URI,
                        DBOpenHelper.MENTORS_ALL_COLUMNS, null, null, null);
                break;
            case 1:
                loader = new CursorLoader(this, DataProvider.ASSESSMENTS_URI,
                        DBOpenHelper.ASSESSMENTS_ALL_COLUMNS, null, null, null);
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case 0:
                mentorCursorAdapter.swapCursor(data);
                if (Objects.equals(action, Intent.ACTION_EDIT)) {
                    mentorFilter = DBOpenHelper.COURSE_ID + "=" + uri.getLastPathSegment();
                    Cursor checkedCursor = getContentResolver().query(DataProvider.MENTORS_URI,
                            DBOpenHelper.MENTORS_ALL_COLUMNS, mentorFilter, null, null);
                    Cursor uncheckedCursor = mentorCursorAdapter.getCursor();
                    checkedCursor.moveToFirst();
                    uncheckedCursor.moveToFirst();
                    while (!checkedCursor.isAfterLast()) {
                        int i = 0;
                        while (!uncheckedCursor.isAfterLast()) {
                            if (checkedCursor.getInt(checkedCursor.getColumnIndex(DBOpenHelper.ID))
                                    == uncheckedCursor.getInt(uncheckedCursor.getColumnIndex(DBOpenHelper.ID))) {
                                mentorList.setItemChecked(i, true);
                            }
                            i++;
                            uncheckedCursor.moveToNext();
                        }
                        uncheckedCursor.moveToFirst();
                        checkedCursor.moveToNext();
                    }
                } else {

                }
                break;
            case 1:
                assessmentCursorAdapter.swapCursor(data);
                if (Objects.equals(action, Intent.ACTION_EDIT)) {
                    assessmentFilter = DBOpenHelper.COURSE_ID + "=" + uri.getLastPathSegment();
                    Cursor checkedCursor = getContentResolver().query(DataProvider.ASSESSMENTS_URI,
                            DBOpenHelper.ASSESSMENTS_ALL_COLUMNS, assessmentFilter, null, null);
                    Cursor uncheckedCursor = assessmentCursorAdapter.getCursor();
                    checkedCursor.moveToFirst();
                    uncheckedCursor.moveToFirst();
                    while (!checkedCursor.isAfterLast()) {
                        int i = 0;
                        while (!uncheckedCursor.isAfterLast()) {
                            if (checkedCursor.getInt(checkedCursor.getColumnIndex(DBOpenHelper.ID))
                                    == uncheckedCursor.getInt(uncheckedCursor.getColumnIndex(DBOpenHelper.ID))) {
                                assessmentList.setItemChecked(i, true);
                            }
                            i++;
                            uncheckedCursor.moveToNext();
                        }
                        uncheckedCursor.moveToFirst();
                        checkedCursor.moveToNext();
                    }
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mentorCursorAdapter.swapCursor(null);
        assessmentCursorAdapter.swapCursor(null);
    }
}
