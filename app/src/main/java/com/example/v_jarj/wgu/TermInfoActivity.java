package com.example.v_jarj.wgu;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TermInfoActivity extends AppCompatActivity {
    private static final int EDITOR_REQUEST_CODE = 1001;
    private TextView title;
    private TextView startDate;
    private TextView endDate;
    private String termFilter;
    private String oldTitle;
    private String oldStart;
    private String oldEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.term_info);
        setSupportActionBar(toolbar);

        title = findViewById(R.id.title);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);

        populateFields();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            populateFields();
        }
    }
}
