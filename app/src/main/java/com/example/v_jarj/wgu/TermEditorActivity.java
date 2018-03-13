package com.example.v_jarj.wgu;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TermEditorActivity extends AppCompatActivity {

    private String action;
    private EditText title;
    private EditText startDate;
    private EditText endDate;
    private String termFilter;
    private String oldTitle;
    private String oldStart;
    private String oldEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_term);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.title);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra("Term");

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_term));
        } else {
            action = Intent.ACTION_EDIT;
            termFilter = DBOpenHelper.ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.TERMS_ALL_COLUMNS, termFilter, null, null);
            cursor.moveToFirst();
            oldTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_TITLE));
            //cursor.moveToFirst();
            oldStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START));
            //cursor.moveToFirst();
            oldEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END));
            title.setText(oldTitle);
            startDate.setText(oldStart);
            endDate.setText(oldEnd);
            cursor.close();
            title.requestFocus();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishEditing();
            }
        });
    }

    public void openStartDatePicker(View view) {
    }

    public void openEndDatePicker(View view) {
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
                if (oldTitle.equals(newTitle) && oldStart.equals(newStart) && oldEnd.equals(newEnd)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateTerm(newTitle, newStart, newEnd);
                }
        }
        finish();
    }

    //Update and existing term
    private void updateTerm(String termTitle, String termStart, String termEnd) {
        ContentValues values = new ContentValues();
        //Performs checks to see if changes were actually made
        if (!oldTitle.equals(termTitle)) {
            values.put(DBOpenHelper.TERM_TITLE, termTitle);
        }
        if (!oldStart.equals(termStart)) {
            values.put(DBOpenHelper.TERM_START, termStart);
        }
        if (!oldEnd.equals(termEnd)) {
            values.put(DBOpenHelper.TERM_END, termEnd);
        }
        //Update value in the database
        getContentResolver().update(DataProvider.TERMS_URI, values, termFilter, null);
        setResult(RESULT_OK);
    }

    private void createTerm(String termTitle, String termStart, String termEnd) {
            ContentValues values = new ContentValues();
            values.put(DBOpenHelper.TERM_TITLE, termTitle);
            values.put(DBOpenHelper.TERM_START, termStart);
            values.put(DBOpenHelper.TERM_END, termEnd);
            getContentResolver().insert(DataProvider.TERMS_URI, values);
            setResult(RESULT_OK);
    }
}
