package com.example.v_jarj.wgu;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TermEditorActivity extends AppCompatActivity
implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int RESULT_DELETED = 2;
    private String action;
    private EditText title;
    private EditText startDate;
    private EditText endDate;
    private String termFilter;
    private String courseFilter;
    private String oldTitle;
    private String oldStart;
    private String oldEnd;
    private Calendar calendar;
    private CursorAdapter cursorAdapter;
    private Uri uri;
    private String[] courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setResult(RESULT_CANCELED);

        title = findViewById(R.id.title);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);

        String[] from = {DBOpenHelper.COURSE_TITLE};
        int[] to = {android.R.id.text1};
        cursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_multiple_choice, null, from, to, 0);

        ListView list = findViewById(R.id.courseList);
        list.setAdapter(cursorAdapter);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setItemsCanFocus(false);

        Cursor cursor = cursorAdapter.getCursor();

        getLoaderManager().initLoader(0, null, this);

        Intent intent = getIntent();

        uri = intent.getParcelableExtra("Term");

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_term));
        } else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_term));
            termFilter = DBOpenHelper.ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.TERMS_ALL_COLUMNS, termFilter, null, null);
            cursor.moveToFirst();
            oldTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_TITLE));
            oldStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START));
            oldEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END));
            title.setText(oldTitle);
            startDate.setText(oldStart);
            endDate.setText(oldEnd);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_term_editor, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_delete:
                deleteTerm();
                break;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProvider.COURSES_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    private void deleteTerm() {
        getContentResolver().delete(DataProvider.TERMS_URI,
                termFilter, null);
        setResult(RESULT_DELETED);
        finish();
    }

    //Format for start and end date strings
    final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

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

        switch (action) {
            case Intent.ACTION_INSERT:
                //Check to see if any of the fields are empty
                if (newTitle.isEmpty() || newStart.isEmpty() || newEnd.isEmpty()) {
                    //TODO create a pop up for when fields are empty
                    setResult(RESULT_CANCELED);
                } else {
                    createTerm(newTitle, newStart, newEnd);
                }
                break;
            case Intent.ACTION_EDIT:
                    updateTerm(newTitle, newStart, newEnd);
        }
        finish();
    }

    //Update and existing term
    private void updateTerm(String termTitle, String termStart, String termEnd) {
        ContentValues termValues = new ContentValues();
        ContentValues courseValues = new ContentValues();
        //Get the values for the term
        termValues.put(DBOpenHelper.TERM_TITLE, termTitle);
        termValues.put(DBOpenHelper.TERM_START, termStart);
        termValues.put(DBOpenHelper.TERM_END, termEnd);
        //Get the values for the courses
        courseValues.put(DBOpenHelper.TERM_ID, uri.getLastPathSegment());
        //Update value in the database
        getContentResolver().update(DataProvider.TERMS_URI, termValues, termFilter, null);
        setResult(RESULT_OK);
    }

    private void createTerm(String termTitle, String termStart, String termEnd) {
            ContentValues values = new ContentValues();
            values.put(DBOpenHelper.TERM_TITLE, termTitle);
            values.put(DBOpenHelper.TERM_START, termStart);
            values.put(DBOpenHelper.TERM_END, termEnd);
            getContentResolver().insert(DataProvider.TERMS_URI, values);
            setResult(RESULT_OK);/**/
    }
}
