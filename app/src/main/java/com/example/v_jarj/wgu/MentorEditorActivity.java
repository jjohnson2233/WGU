package com.example.v_jarj.wgu;

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
import android.widget.EditText;

import java.util.Objects;

public class MentorEditorActivity extends AppCompatActivity {

    public static final int RESULT_DELETED = 2;
    private String action;
    private EditText name;
    private EditText email;
    private EditText phone;
    private String mentorFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra("Mentor");

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_mentor));
        } else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_mentor));
            mentorFilter = DBOpenHelper.ID + "=" + uri.getLastPathSegment();

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
            getMenuInflater().inflate(R.menu.menu_mentor_editor, menu);
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
                deleteMentor();
                break;
        }
        return true;
    }

    private void deleteMentor() {
        getContentResolver().delete(DataProvider.MENTORS_URI,
                mentorFilter, null);
        setResult(RESULT_DELETED);
        finish();
    }

    private void finishEditing() {
        String newName = name.getText().toString().trim();
        String newEmail = email.getText().toString().trim();
        String newPhone = phone.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                //Check to see if any of the fields are empty
                if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
                    //TODO create a pop up for when fields are empty
                    setResult(RESULT_CANCELED);
                } else {
                    createMentor(newName, newEmail, newPhone);
                }
                break;
            case Intent.ACTION_EDIT:
                updateMentor(newName, newEmail, newPhone);
        }
        finish();
    }

    private void updateMentor(String mentorName, String mentorEmail, String mentorPhone) {
        ContentValues mentorValues = new ContentValues();
        mentorValues.put(DBOpenHelper.MENTOR_NAME, mentorName);
        mentorValues.put(DBOpenHelper.MENTOR_EMAIL, mentorEmail);
        mentorValues.put(DBOpenHelper.MENTOR_PHONE, mentorPhone);
        getContentResolver().update(DataProvider.MENTORS_URI, mentorValues, mentorFilter, null);
        setResult(RESULT_OK);
    }

    private void createMentor(String mentorName, String mentorEmail, String mentorPhone) {
        ContentValues mentorValues = new ContentValues();
        mentorValues.put(DBOpenHelper.MENTOR_NAME, mentorName);
        mentorValues.put(DBOpenHelper.MENTOR_EMAIL, mentorEmail);
        mentorValues.put(DBOpenHelper.MENTOR_PHONE, mentorPhone);
        getContentResolver().insert(DataProvider.MENTORS_URI, mentorValues);
        setResult(RESULT_OK);
    }
}
