package com.example.v_jarj.wgu;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CourseEditorActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        title = findViewById(R.id.title);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        statusSpinner = findViewById(R.id.status_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra("Course");

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
                case "In-progress":
                    statusSpinner.setSelection(0);
                    break;
                case "Not Started":
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
        startDate = findViewById(R.id.startDate);
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

        switch (action) {
            case Intent.ACTION_INSERT:
                //Check to see if any of the fields are empty
                if (newTitle.isEmpty() || newStart.isEmpty() || newEnd.isEmpty()) {
                    //TODO create a pop up for when fields are empty
                    setResult(RESULT_CANCELED);
                } else {
                    createCourse(newTitle, newStart, newEnd, newStatus);
                }
                break;
            case Intent.ACTION_EDIT:
                if (oldTitle.equals(newTitle) && oldStart.equals(newStart) && oldEnd.equals(newEnd)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateCourse(newTitle, newStart, newEnd, newStatus);
                }
        }
        finish();
    }

    private void updateCourse(String courseTitle, String courseStart, String courseEnd, String courseStatus) {
        ContentValues values = new ContentValues();
        //Performs checks to see if changes were actually made
        if (!oldTitle.equals(courseTitle)) {
            values.put(DBOpenHelper.COURSE_TITLE, courseTitle);
        }
        if (!oldStart.equals(courseStart)) {
            values.put(DBOpenHelper.COURSE_START, courseStart);
        }
        if (!oldEnd.equals(courseEnd)) {
            values.put(DBOpenHelper.COURSE_END, courseEnd);
        }
        if (!oldStatus.equals(courseStatus)) {
            values.put(DBOpenHelper.COURSE_STATUS, courseStatus);
        }
        //Update value in the database
        getContentResolver().update(DataProvider.COURSES_URI, values, courseFilter, null);
        setResult(RESULT_OK);
    }

    private void createCourse(String courseTitle, String courseStart, String courseEnd, String courseStatus) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TITLE, courseTitle);
        values.put(DBOpenHelper.COURSE_START, courseStart);
        values.put(DBOpenHelper.COURSE_END, courseEnd);
        values.put(DBOpenHelper.COURSE_STATUS, courseStatus);
        getContentResolver().insert(DataProvider.COURSES_URI, values);
        setResult(RESULT_OK);
    }
}
