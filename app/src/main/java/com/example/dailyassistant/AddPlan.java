package com.example.dailyassistant;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;


public class AddPlan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);

        Toolbar childToolbar = (Toolbar) findViewById(R.id.child_toolbar);
        setSupportActionBar(childToolbar);

        childToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ActionBar ab =getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
