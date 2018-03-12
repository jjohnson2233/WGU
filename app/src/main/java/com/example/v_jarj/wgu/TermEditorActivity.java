package com.example.v_jarj.wgu;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class TermEditorActivity extends AppCompatActivity {

    private String action;
    private EditText title;
    private EditText startDate;
    private EditText endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_term);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        String newText = title.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    createTerm(newText);
                }
        }
        finish();
    }

    private void createTerm(String termTitle) {
            ContentValues values = new ContentValues();
            values.put(DBOpenHelper.TERM_TITLE, termTitle);
            values.put(DBOpenHelper.TERM_START, "blah");
            values.put(DBOpenHelper.TERM_END, "blah");
            Uri termUri = getContentResolver().insert(DataProvider.TERMS_URI, values);
            setResult(RESULT_OK);
    }
}
