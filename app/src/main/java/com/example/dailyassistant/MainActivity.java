package com.example.dailyassistant;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PlanAdapter.OnListItemClickListener {

    Toolbar myToolbar;
    RecyclerView mPlanList;
    RecyclerView.Adapter mPlanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mPlanList =findViewById(R.id.rv);
        mPlanList.hasFixedSize();
        mPlanList.setLayoutManager(new LinearLayoutManager(this));

        mPlanAdapter = new PlanAdapter(getMockData(), this);
        mPlanList.setAdapter(mPlanAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPlan.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_favourite:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                return true;

            case R.id.later:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)
        {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }
    }

    public ArrayList<Plan> getMockData() {
        ArrayList<Plan> plans = new ArrayList<>();

        plans.add(new Plan("Mom gift", "Pick up gift from the shop", 2019, 4, 26));
        plans.add(new Plan("Client meeting", "Meeting with ECorp representative", 2019, 4, 30));
        plans.add(new Plan("Review session", "Team review, prepare presentation", 2019, 5, 10));
        plans.add(new Plan("Boss inspection", "Prepare summary of progress on work", 2019, 5, 15));
        plans.add(new Plan("Trip prep", "Pack for the Weekend Trip", 2019, 5, 17));
        plans.add(new Plan("Flight Check In", "Make Check in for everyone", 2019, 5, 18));
        plans.add(new Plan("Return Check In", "Check In the return flight", 2019, 5, 19));
        plans.add(new Plan("Syn", "Go for Syn inspection", 2019, 5, 26));
        plans.add(new Plan("Syn", "Go for Syn inspection", 2019, 5, 26));
        plans.add(new Plan("Syn", "Go for Syn inspection", 2019, 5, 26));
        plans.add(new Plan("Syn", "Go for Syn inspection", 2019, 5, 26));
        plans.add(new Plan("Syn", "Go for Syn inspection", 2019, 5, 26));

        return plans;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

    }
}
