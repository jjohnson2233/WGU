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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private TextView status;
    private CursorAdapter mentorCursorAdapter;
    private CursorAdapter assessmentCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.course_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.title);
        startDate = findViewById(R.id.dueDate);
        endDate = findViewById(R.id.endDate);
        status = findViewById(R.id.status);

        String[] mentorFrom = {DBOpenHelper.MENTOR_NAME};
        String[] assessmentFrom = {DBOpenHelper.ASSESSMENT_TITLE};
        int[] to = {android.R.id.text1};
        mentorCursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null, mentorFrom, to, 0);
        assessmentCursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null, assessmentFrom, to, 0);

        ListView mentorList = findViewById(R.id.mentorsList);
        ListView assessmentList = findViewById(R.id.assessmentsList);
        mentorList.setAdapter(mentorCursorAdapter);
        assessmentList.setAdapter(assessmentCursorAdapter);

        mentorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CourseInfoActivity.this, MentorInfoActivity.class);
                Uri uri = Uri.parse(DataProvider.MENTORS_URI + "/" + id);
                intent.putExtra("Mentor", uri);
                startActivity(intent);
            }
        });

        assessmentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CourseInfoActivity.this, AssessmentInfoActivity.class);
                Uri uri = Uri.parse(DataProvider.ASSESSMENTS_URI + "/" + id);
                intent.putExtra("Assessment", uri);
                startActivity(intent);
            }
        });

        populateFields();

        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.action_notes:
                Intent intent = new Intent(CourseInfoActivity.this, NotesListActivity.class);
                Uri uri = getIntent().getParcelableExtra("course");
                intent.putExtra("Course", uri);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_info, menu);
        return true;
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
        CursorLoader loader = null;
        Uri uri = getIntent().getParcelableExtra("Course");
        switch (id) {
            case 0:
                String mentorFilter = DBOpenHelper.COURSE_ID + "=" + uri.getLastPathSegment();
                loader = new CursorLoader(this, DataProvider.MENTORS_URI,
                        null, mentorFilter, null, null);
                break;
            case 1:
                String assessmentFilter = DBOpenHelper.COURSE_ID + "=" + uri.getLastPathSegment();
                loader = new CursorLoader(this, DataProvider.ASSESSMENTS_URI,
                        null, assessmentFilter, null, null);
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case 0:
                mentorCursorAdapter.swapCursor(data);
                break;
            case 1:
                assessmentCursorAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mentorCursorAdapter.swapCursor(null);
        assessmentCursorAdapter.swapCursor(null);
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
        String oldStatus = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_STATUS));
        title.setText(oldTitle);
        startDate.setText(oldStart);
        endDate.setText(oldEnd);
        status.setText(oldStatus);
        cursor.close();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
        getLoaderManager().restartLoader(1, null, null);
    }
}
