package com.example.v_jarj.wgu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
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
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ToggleButton;

import java.security.PrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AssessmentEditorActivity extends AppCompatActivity {

    public static final int RESULT_DELETED = 2;
    private String action;
    private EditText title;
    private EditText dueDate;
    private Switch reminderSwitch;
    private Spinner spinner;
    private String assessmentFilter;
    private String oldTitle;
    private String oldDue;
    private Calendar calendar;
    private CursorAdapter cursorAdapter;
    private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        title = findViewById(android.R.id.title);
        dueDate = findViewById(R.id.dueDate);
        reminderSwitch = findViewById(R.id.reminderSwitch);

        Spinner spinner = findViewById(R.id.typeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Intent intent = getIntent();
        uri = intent.getParcelableExtra("Assessment");

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
            oldTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TITLE));
            oldDue = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DUE));
            title.setText(oldTitle);
            dueDate.setText(oldDue);
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

    private void finishEditing() {
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

    private void updateAssessment(String assessmentTitle, String assessmentStart, String assessmentType) {
        final ContentValues assessmentValues = new ContentValues();
        //Get the values for the assessment
        assessmentValues.put(DBOpenHelper.ASSESSMENT_TITLE, assessmentTitle);
        assessmentValues.put(DBOpenHelper.ASSESSMENT_DUE, assessmentStart);
        assessmentValues.put(DBOpenHelper.ASSESSMENT_TYPE, assessmentType);
        //Get status of the reminder switch
        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    assessmentValues.put(DBOpenHelper.ASSESSMENT_ALERT, dueDate.getText().toString().trim());
                } else {
                    assessmentValues.putNull(DBOpenHelper.ASSESSMENT_ALERT);
                }
            }
        });
        //Update value in the database
        getContentResolver().update(DataProvider.ASSESSMENTS_URI, assessmentValues, assessmentFilter, null);
        setResult(RESULT_OK);
    }

    private void createAssessment(String assessmentTitle, String assessmentStart, String assessmentType) {
        final ContentValues assessmentValues = new ContentValues();
        //Get the values for the new assessment
        assessmentValues.put(DBOpenHelper.ASSESSMENT_TITLE, assessmentTitle);
        assessmentValues.put(DBOpenHelper.ASSESSMENT_DUE, assessmentStart);
        assessmentValues.put(DBOpenHelper.ASSESSMENT_TYPE, assessmentType);
        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    assessmentValues.put(DBOpenHelper.ASSESSMENT_ALERT, dueDate.getText().toString().trim());
                }
            }
        });
        //Add values to the database
        getContentResolver().insert(DataProvider.ASSESSMENTS_URI, assessmentValues);
        setResult(RESULT_OK);
    }
}
