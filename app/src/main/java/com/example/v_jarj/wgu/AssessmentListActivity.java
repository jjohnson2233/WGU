package com.example.v_jarj.wgu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AssessmentListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
