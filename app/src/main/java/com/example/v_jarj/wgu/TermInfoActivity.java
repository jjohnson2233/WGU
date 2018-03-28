package com.example.v_jarj.wgu;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TermInfoActivity extends AppCompatActivity
implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EDITOR_REQUEST_CODE = 1001;
    private TextView title;
    private TextView startDate;
    private TextView endDate;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.term_info);
        setSupportActionBar(toolbar);

        String[] from = {DBOpenHelper.COURSE_TITLE};
        int[] to = {android.R.id.text1};
        cursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null, from, to, 0);

        ListView list = findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TermInfoActivity.this, CourseInfoActivity.class);
                Uri uri = Uri.parse(DataProvider.COURSES_URI + "/" + id);
                intent.putExtra("Course", uri);
                startActivity(intent);
            }
        });

        title = findViewById(R.id.title);
        startDate = findViewById(R.id.dueDate);
        endDate = findViewById(R.id.endDate);

        populateFields();

        getLoaderManager().initLoader(0, null, this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TermInfoActivity.this, TermEditorActivity.class);
                Uri uri = getIntent().getParcelableExtra("Term");
                intent.putExtra("Term", uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }

        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void populateFields() {
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra("Term");
        String termFilter = DBOpenHelper.ID + "=" + uri.getLastPathSegment();

        //Populate the Term info
        Cursor cursor = getContentResolver().query(uri,
                DBOpenHelper.TERMS_ALL_COLUMNS, termFilter, null, null);
        cursor.moveToFirst();
        String oldTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_TITLE));
        String oldStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START));
        String oldEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END));
        title.setText(oldTitle);
        startDate.setText(oldStart);
        endDate.setText(oldEnd);
        cursor.close();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            populateFields();
            restartLoader();
        } else if (requestCode == EDITOR_REQUEST_CODE && resultCode == TermEditorActivity.RESULT_DELETED) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = getIntent().getParcelableExtra("Term");
        String courseFilter = DBOpenHelper.TERM_ID + "=" + uri.getLastPathSegment();
        return new CursorLoader(this, DataProvider.COURSES_URI,
                null, courseFilter, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
