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

public class MentorInfoActivity extends AppCompatActivity {
    private static final int EDITOR_REQUEST_CODE = 1001;
    private TextView name;
    private TextView email;
    private TextView phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.mentor_info_title);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);

        populateFields();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MentorInfoActivity.this, MentorEditorActivity.class);
                Uri uri = getIntent().getParcelableExtra("Mentor");
                intent.putExtra("Mentor", uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }

        });
        }

    private void populateFields() {
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra("Mentor");
        String mentorFilter = DBOpenHelper.ID + "=" + uri.getLastPathSegment();

        //Populate the Mentor info
        Cursor cursor = getContentResolver().query(uri,
                DBOpenHelper.MENTORS_ALL_COLUMNS, mentorFilter, null, null);
        Objects.requireNonNull(cursor).moveToFirst();
        String oldName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_NAME));
        String oldEmail = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_EMAIL));
        String oldPhone = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_PHONE));
        name.setText(oldName);
        email.setText(oldEmail);
        phone.setText(oldPhone);
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            populateFields();
        } else if (requestCode == EDITOR_REQUEST_CODE && resultCode == MentorEditorActivity.RESULT_DELETED) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
