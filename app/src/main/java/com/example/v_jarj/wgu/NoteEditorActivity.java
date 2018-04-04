package com.example.v_jarj.wgu;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class NoteEditorActivity extends AppCompatActivity {

    public static final int RESULT_DELETED = 2;
    private String action;
    private EditText content;
    private String noteFilter;
    private Uri courseUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.note_editor_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        content = findViewById(R.id.content);



        Intent intent = getIntent();
        courseUri = intent.getParcelableExtra("Course");
        Uri noteUri = intent.getParcelableExtra("Note");

        if (noteUri == null) {
            action = Intent.ACTION_INSERT;
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.ID + "=" + noteUri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(noteUri,
                    DBOpenHelper.NOTES_ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            String oldContent = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_CONTENT));
            content.setText(oldContent);
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
            getMenuInflater().inflate(R.menu.menu_note_editor, menu);
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
                deleteNote();
                break;
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, content.getText().toString().trim());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share"));
        }
        return true;
    }

    private void deleteNote() {
        getContentResolver().delete(DataProvider.NOTES_URI,
                noteFilter, null);
        setResult(RESULT_DELETED);
        finish();
    }

    private void finishEditing() {
        String newContent = content.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                //Check to see if any of the fields are empty
                if (newContent.isEmpty()) {
                    setResult(RESULT_CANCELED);
                } else {
                    createNote(newContent);
                }
                break;
            case Intent.ACTION_EDIT:
                updateNote(newContent);
        }
        finish();
    }

    private void updateNote(String noteContent) {
        ContentValues noteValues = new ContentValues();
        noteValues.put(DBOpenHelper.NOTE_CONTENT, noteContent);
        getContentResolver().update(DataProvider.NOTES_URI, noteValues, noteFilter, null);
        setResult(RESULT_OK);
    }

    private void createNote(String noteContent) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_CONTENT, noteContent);
        values.put(DBOpenHelper.COURSE_ID, courseUri.getLastPathSegment());
        getContentResolver().insert(DataProvider.NOTES_URI, values);
        setResult(RESULT_OK);
    }

}
