package com.example.v_jarj.wgu;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

public class AssessmentInfoActivity extends AppCompatActivity {
    private final int EDITOR_REQUEST_CODE = 1001;
    private TextView title;
    private TextView dueDate;
    private TextView type;
    private TextView reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Assessment Info");

        title = findViewById(R.id.title);
        dueDate = findViewById(R.id.dueDate);
        type = findViewById(R.id.type);
        reminder = findViewById(R.id.reminder);

        populateFields();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssessmentInfoActivity.this, AssessmentEditorActivity.class);
                Uri uri = getIntent().getParcelableExtra("Assessment");
                intent.putExtra("Assessment", uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }

        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            populateFields();
        } else if (requestCode == EDITOR_REQUEST_CODE && resultCode == TermEditorActivity.RESULT_DELETED) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void populateFields() {
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra("Assessment");
        String assessmentFilter = DBOpenHelper.ID + "=" + uri.getLastPathSegment();

        //Populate the Assessment info
        Cursor cursor = getContentResolver().query(uri,
                DBOpenHelper.ASSESSMENTS_ALL_COLUMNS, assessmentFilter, null, null);
        Objects.requireNonNull(cursor).moveToFirst();
        String oldTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TITLE));
        String oldDue = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DUE));
        String oldType = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TYPE));
        String oldReminder = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_ALERT));
        title.setText(oldTitle);
        dueDate.setText(oldDue);
        type.setText(oldType);
        reminder.setText(oldReminder);
        cursor.close();
    }
}

