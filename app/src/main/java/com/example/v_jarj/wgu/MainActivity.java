package com.example.v_jarj.wgu;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBOpenHelper helper = new DBOpenHelper(this);
        SQLiteDatabase database = helper.getWritableDatabase();
    }

    public void openTermsList(View view) {
        Intent intent = new Intent(this, TermsListActivity.class);
        startActivity(intent);
    }

    public void openCoursesList(View view) {
        Intent intent = new Intent(this, CourseListActivity.class);
        startActivity(intent);
    }

    public void openAssessmentsList(View view) {
        Intent intent = new Intent(this, AssessmentListActivity.class);
        startActivity(intent);
    }
}
