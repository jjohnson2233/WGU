package com.example.v_jarj.wgu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("Welcome Message", "Welcome!").apply();

        String welcomeMessage = sharedPreferences.getString("Welcome Message", "Default Welcome");
        TextView welcomeText = findViewById(R.id.welcomeMessage);

        welcomeText.setText(welcomeMessage);
    }

    public void openTermsList(View view) {
        Intent intent = new Intent(this, TermsListActivity.class);
        startActivity(intent);
    }

    public void openCoursesList(View view) {
        Intent intent = new Intent(this, CoursesListActivity.class);
        startActivity(intent);
    }

    public void openMentorsList(View view) {
        Intent intent = new Intent(this, MentorsListActivity.class);
        startActivity(intent);
    }

    public void openAssessmentsList(View view) {
        Intent intent = new Intent(this, AssessmentsListActivity.class);
        startActivity(intent);
    }
}
