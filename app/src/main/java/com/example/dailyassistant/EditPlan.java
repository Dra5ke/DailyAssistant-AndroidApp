package com.example.dailyassistant;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class EditPlan extends AppCompatActivity {

    private static final String TAG = "Edit";
    Button cancelButton;
    Button okButton;
    TextView titleText;
    TextView descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plan);

        setUpVariables();

        Log.d(TAG, "APPBAR SETUP");
        //[APPBAR_SETUP_START]
        Toolbar childToolbar = (Toolbar) findViewById(R.id.child_toolbar_edit);
        setSupportActionBar(childToolbar);

        //Make back arrow finish the intent
        childToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ActionBar ab =getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //[APPBAR_SETUP_END]

        Log.d(TAG, "CANCEL BUTTON LISTENER");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Log.d(TAG, "OK BUTTON LISTENER");
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditPlan.this, mDateSetListener ,year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "ON START");
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            boolean ok = true;

            if(ok)
            {
                Bundle bundle = getIntent().getExtras();
                int clickedItemIndex = bundle.getInt("clickedItemIndex");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("EDIT_PLAN_TITLE", titleText.getText().toString());
                returnIntent.putExtra("EDIT_PLAN_DESCRIPTION", descriptionText.getText().toString());
                returnIntent.putExtra("EDIT_PLAN_DAY", dayOfMonth);
                returnIntent.putExtra("EDIT_PLAN_MONTH", month+1);
                returnIntent.putExtra("EDIT_PLAN_YEAR", year);
                returnIntent.putExtra("EDIT_PLAN_INDEX", clickedItemIndex);
                setResult(RESULT_OK, returnIntent);
            } else {
                setResult(RESULT_CANCELED);
            }

            finish();
        }
    };

    private void setUpVariables() {
        cancelButton = findViewById(R.id.editCancel);
        okButton = findViewById(R.id.editOk);
        titleText = findViewById(R.id.titleEditText);
        descriptionText = findViewById(R.id.descriptionEditText);
    }
}
