package com.example.v_jarj.wgu;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CourseInfoActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EDITOR_REQUEST_CODE = 1001;
    private TextView title;
    private TextView startDate;
    private TextView endDate;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.course_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] from = {DBOpenHelper.ASSESSMENT_TITLE};
        int[] to = {android.R.id.text1};
        cursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null, from, to, 0);

        ListView list = findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        title = findViewById(R.id.title);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);

        populateFields();

        getLoaderManager().initLoader(0, null, this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseInfoActivity.this, CourseEditorActivity.class);
                Uri uri = getIntent().getParcelableExtra("Course");
                intent.putExtra("Course", uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }

        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            populateFields();
            restartLoader();
        } else if (requestCode == EDITOR_REQUEST_CODE && resultCode == CourseEditorActivity.RESULT_DELETED) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProvider.COURSES_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    private void populateFields() {
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra("Course");
        String courseFilter = DBOpenHelper.ID + "=" + uri.getLastPathSegment();

        Cursor cursor = getContentResolver().query(uri,
                DBOpenHelper.COURSES_ALL_COLUMNS, courseFilter, null, null);
        cursor.moveToFirst();
        String oldTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TITLE));
        String oldStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_START));
        String oldEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END));
        title.setText(oldTitle);
        startDate.setText(oldStart);
        endDate.setText(oldEnd);
        cursor.close();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }
}
