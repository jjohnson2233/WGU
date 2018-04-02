package com.example.v_jarj.wgu;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AssessmentEditorActivity extends AppCompatActivity {

    public static final int RESULT_DELETED = 2;
    private String action;
    private EditText title;
    private EditText dueDate;
    private CheckBox reminder;
    private Spinner spinner;
    private String assessmentFilter;
    private Calendar calendar;
    private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        title = findViewById(R.id.title);
        dueDate = findViewById(R.id.dueDate);
        reminder = findViewById(R.id.reminder);

        spinner = findViewById(R.id.typeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra("Assessment");

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_assessment));
        } else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_assessment));
            assessmentFilter = DBOpenHelper.ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ASSESSMENTS_ALL_COLUMNS, assessmentFilter, null, null);
            cursor.moveToFirst();
            String oldTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TITLE));
            String oldDue = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DUE));
            String oldType = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TYPE));
            String oldReminder = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_ALERT));
            title.setText(oldTitle);
            dueDate.setText(oldDue);
            switch (oldType) {
                case "Objective Assessment":
                    spinner.setSelection(0);
                    break;
                case "Performance Assessment":
                    spinner.setSelection(1);
                    break;
            }
            switch (oldReminder) {
                case "On":
                    reminder.setChecked(true);
                    break;
                case "Off":
                    reminder.setChecked(false);
                    break;
            }
            cursor.close();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    finishEditing();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_assessment_editor, menu);
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
                deleteAssessment();
                break;
        }
        return true;
    }

    private void deleteAssessment() {
            getContentResolver().delete(DataProvider.ASSESSMENTS_URI,
                    assessmentFilter, null);
            setResult(RESULT_DELETED);
            finish();
    }

    public void openDueDatePicker(View view) throws ParseException {
        dueDate = findViewById(R.id.dueDate);
        calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                String date = format.format(calendar.getTime());
                dueDate.setText(date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void createReminder(String date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(date));
        calendar.set(Calendar.HOUR_OF_DAY, 9);

        Intent intent = new Intent(this, AssessmentNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void finishEditing() throws ParseException {
        String newTitle = title.getText().toString().trim();
        String newDue = dueDate.getText().toString().trim();
        String newType = (String) spinner.getSelectedItem();

        switch (action) {
            case Intent.ACTION_INSERT:
                //Check to see if any of the fields are empty
                if (newTitle.isEmpty() || newDue.isEmpty()) {
                    //TODO create a pop up for when fields are empty
                    setResult(RESULT_CANCELED);
                } else {
                    createAssessment(newTitle, newDue, newType);
                }
                break;
            case Intent.ACTION_EDIT:
                updateAssessment(newTitle, newDue, newType);
        }
        finish();
    }

    private void updateAssessment(String assessmentTitle, String assessmentStart, String assessmentType) throws ParseException {
        final ContentValues assessmentValues = new ContentValues();
        //Get the values for the assessment
        assessmentValues.put(DBOpenHelper.ASSESSMENT_TITLE, assessmentTitle);
        assessmentValues.put(DBOpenHelper.ASSESSMENT_DUE, assessmentStart);
        assessmentValues.put(DBOpenHelper.ASSESSMENT_TYPE, assessmentType);
        if (reminder.isChecked()) {
            assessmentValues.put(DBOpenHelper.ASSESSMENT_ALERT, "On");
            createReminder(dueDate.getText().toString().trim());
        } else {
            assessmentValues.put(DBOpenHelper.ASSESSMENT_ALERT, "Off");
        }
        //Update value in the database
        getContentResolver().update(DataProvider.ASSESSMENTS_URI, assessmentValues, assessmentFilter, null);
        setResult(RESULT_OK);
    }

    private void createAssessment(String assessmentTitle, String assessmentStart, String assessmentType) throws ParseException {
        final ContentValues assessmentValues = new ContentValues();
        //Get the values for the new assessment
        assessmentValues.put(DBOpenHelper.ASSESSMENT_TITLE, assessmentTitle);
        assessmentValues.put(DBOpenHelper.ASSESSMENT_DUE, assessmentStart);
        assessmentValues.put(DBOpenHelper.ASSESSMENT_TYPE, assessmentType);
        if (reminder.isChecked()) {
            assessmentValues.put(DBOpenHelper.ASSESSMENT_ALERT, "On");
            createReminder(dueDate.getText().toString().trim());
        } else {
            assessmentValues.put(DBOpenHelper.ASSESSMENT_ALERT, "Off");
        }
        //Add values to the database
        getContentResolver().insert(DataProvider.ASSESSMENTS_URI, assessmentValues);
        setResult(RESULT_OK);
    }
}
