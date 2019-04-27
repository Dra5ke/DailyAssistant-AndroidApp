package com.example.dailyassistant;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements PlanAdapter.OnListItemClickListener {

    private static final String TAG = "Main";
    FirebaseFirestore database;
    CollectionReference plansReference;
    Toolbar myToolbar;
    RecyclerView mPlanList;
    RecyclerView.Adapter mPlanAdapter;
    PlanFirebaseAdapter planAdapter;
    ArrayList<Plan> mPlans;

    int list_item_index;
    private final int ADD_REQUEST_CODE = 5151;
    private final int EDIT_REQUEST_CODE = 5252;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        } else {
            setUpFireStore();
            setUpToolbar();
            setUpRecyclerView();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, AddPlan.class);
                    startActivityForResult(intent, ADD_REQUEST_CODE);
                }
            });
        }
    }

    private void setUpRecyclerView() {
        Query query = plansReference.orderBy("year", Query.Direction.ASCENDING)
                .orderBy("month", Query.Direction.ASCENDING).orderBy("day", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Plan> options = new FirestoreRecyclerOptions.Builder<Plan>()
                .setQuery(query, Plan.class).build();

        planAdapter = new PlanFirebaseAdapter(options);

        mPlanList = findViewById(R.id.rv);
        mPlanList.hasFixedSize();
        mPlanList.setLayoutManager(new LinearLayoutManager(this));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final String swipedItemIndex = Integer.toString(viewHolder.getAdapterPosition());
                plansReference.document(swipedItemIndex).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Delete plan success");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Delete plan error:" + e.getLocalizedMessage());
                            }
                        });
            }
        }).attachToRecyclerView(mPlanList);
        mPlanList.setAdapter(planAdapter);

    }

    private void setUpToolbar() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    private void setUpFireStore() {
        database = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        plansReference = database.collection("users").document(user.getUid()).collection("plans");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mPlans.add(new Plan(data.getExtras().getString("NEW_PLAN_TITLE"),
                        data.getExtras().getString("NEW_PLAN_DESCRIPTION"), data.getExtras().getInt("NEW_PLAN_YEAR"),
                        data.getExtras().getInt("NEW_PLAN_MONTH"), data.getExtras().getInt("NEW_PLAN_DAY")));
                mPlanAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == EDIT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int index = data.getExtras().getInt("EDIT_PLAN_INDEX");
                String newTitle = data.getExtras().getString("EDIT_PLAN_TITLE");
                String newDescription = data.getExtras().getString("EDIT_PLAN_DESCRIPTION");
                if (!newTitle.equals("")) mPlans.get(index).setTitle(newTitle);
                if (!newDescription.equals("")) mPlans.get(index).setDescription(newDescription);
                mPlans.get(index).setDay(data.getExtras().getInt("EDIT_PLAN_DAY"));
                mPlans.get(index).setMonth(data.getExtras().getInt("EDIT_PLAN_MONTH"));
                mPlans.get(index).setYear(data.getExtras().getInt("EDIT_PLAN_YEAR"));
                mPlanAdapter.notifyItemChanged(index);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
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
        planAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        planAdapter.stopListening();
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
        plans.add(new Plan("Syn", "Go for Syn inspectionfdghjmgfdsfghjbhvgcfxghvjbvhgcfxgcvhngc", 2019, 5, 26));
        plans.add(new Plan("Syn", "Go for Syn inspection", 2019, 5, 26));
        plans.add(new Plan("Syn", "Go for Syn inspection", 2019, 5, 26));
        plans.add(new Plan("Syn", "Go for Syn inspectionasadasdsasadsadsadasdsasadsadsaddasdadassadsadsadsadadssadsaddassadsasasadsasadsadsadsadsadsasadsadsaddasasdsadsadsdsadsadadsdsdssadsadsadsd", 2019, 5, 26));

        return plans;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

        Intent intent = new Intent(MainActivity.this, EditPlan.class);
        intent.putExtra("clickedItemIndex", clickedItemIndex);
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    @Override
    public void onCalendarClick(int clickedItemIndex) {

        list_item_index = clickedItemIndex;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, mDateSetListener, year, month, dayOfMonth);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mPlans.get(list_item_index).setDay(dayOfMonth);
            mPlans.get(list_item_index).setMonth(month + 1);
            mPlans.get(list_item_index).setYear(year);
            mPlanAdapter.notifyItemChanged(list_item_index);
        }
    };
}
