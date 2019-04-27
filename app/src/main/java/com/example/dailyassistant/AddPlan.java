package com.example.dailyassistant;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class AddPlan extends AppCompatActivity {

    Button cancelButton;
    Button okButton;
    TextView titleText;
    TextView descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);

        setUpVariables();

        //[APPBAR_SETUP_START]
        Toolbar childToolbar = (Toolbar) findViewById(R.id.child_toolbar);
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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleText.getText().toString();
                String description = descriptionText.getText().toString();
                if(title.trim().isEmpty() || description.trim().isEmpty())
                {
                    Toast.makeText(AddPlan.this, "Please insert a title and description", Toast.LENGTH_LONG).show();
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddPlan.this, mDateSetListener ,year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            boolean ok = true;

            if(ok)
            {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("NEW_PLAN_TITLE", titleText.getText().toString());
                returnIntent.putExtra("NEW_PLAN_DESCRIPTION", descriptionText.getText().toString());
                returnIntent.putExtra("NEW_PLAN_DAY", dayOfMonth);
                returnIntent.putExtra("NEW_PLAN_MONTH", month+1);
                returnIntent.putExtra("NEW_PLAN_YEAR", year);
                setResult(RESULT_OK, returnIntent);
            } else {
                setResult(RESULT_CANCELED);
            }

            finish();
        }
    };

    private void setUpVariables() {
        cancelButton = findViewById(R.id.cancel);
        okButton = findViewById(R.id.ok);
        titleText = findViewById(R.id.titleText);
        descriptionText = findViewById(R.id.descriptionText);
    }
}
