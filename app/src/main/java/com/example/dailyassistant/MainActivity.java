package com.example.dailyassistant;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    private static final String QUOTE_URL = "https://quotes.rest/qod.json";
    FirebaseFirestore database;
    CollectionReference plansReference;
    Toolbar myToolbar;
    RecyclerView mPlanList;
    PlanFirebaseAdapter planAdapter;

    //auxiliary used because in the onDateSetListener there is no access to the snapshot
    DocumentSnapshot docSnap;
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
                planAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(mPlanList);

        planAdapter.setOnItemClickListener(new PlanFirebaseAdapter.OnListItemClickListener() {
            @Override
            public void onListItemClick(DocumentSnapshot documentSnapshot, int position) {
                String docPath = documentSnapshot.getReference().getPath();
                Intent intent = new Intent(MainActivity.this, EditPlan.class);
                intent.putExtra("DOC_PATH", docPath);
                intent.putExtra("position", position);
                Log.d(TAG, String.valueOf(position));
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }

            @Override
            public void onCalendarClick(DocumentSnapshot documentSnapshot, int position) {

                docSnap = documentSnapshot;
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, mDateSetListener, year, month, dayOfMonth);

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });
        mPlanList.setAdapter(planAdapter);
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            Plan plan = docSnap.toObject(Plan.class);
            plan.setDay(dayOfMonth);
            plan.setMonth(month + 1);
            plan.setYear(year);
            database.document(docSnap.getReference().getPath()).set(plan);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                plansReference.add(new Plan(data.getExtras().getString("NEW_PLAN_TITLE"),
                        data.getExtras().getString("NEW_PLAN_DESCRIPTION"), data.getExtras().getInt("NEW_PLAN_YEAR"),
                        data.getExtras().getInt("NEW_PLAN_MONTH"), data.getExtras().getInt("NEW_PLAN_DAY")));
            }
        } else if (requestCode == EDIT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String docPath = data.getExtras().getString("DOC_PATH");
                int position = data.getExtras().getInt("EDIT_ITEM_INDEX");
                Log.d(TAG, "Activity result index: " + position);
                String title = data.getExtras().getString("EDIT_PLAN_TITLE");
                String description = data.getExtras().getString("EDIT_PLAN_DESCRIPTION");
                //I wanted to make it so that if the title/desc fields are empty the old ones are kept
                //get methods throw NullPointerException
//                if(title.trim().isEmpty()) title = planAdapter.getSnapshots().get(position).getTitle();
//                if(description.trim().isEmpty()) description = planAdapter.getSnapshots().get(position).getDescription();

                Plan edittedPlan = new Plan(title, description, data.getExtras().getInt("EDIT_PLAN_YEAR"),
                        data.getExtras().getInt("EDIT_PLAN_MONTH"), data.getExtras().getInt("EDIT_PLAN_DAY"));

                database.document(docPath).set(edittedPlan);
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

                GetQuoteAsync task = new GetQuoteAsync();
                task.execute(QUOTE_URL);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetQuoteAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            String jsonResponse = "";

            try {
                url = new URL(strings[0]);
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            JSONObject root = null;
            try {
                root = new JSONObject(s);
                String quote = root.getJSONObject("contents").getJSONArray("quotes").getJSONObject(0).getString("quote");
                Toast.makeText(getApplicationContext(), quote, Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream is = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                is = urlConnection.getInputStream();
                jsonResponse = readFromStream(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (is != null)
                is.close();
        }

        return jsonResponse;
    }

    private String readFromStream(InputStream is) throws IOException {
        StringBuilder output = new StringBuilder();

        if (is != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(is, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
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

    private void setUpToolbar() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    private void setUpFireStore() {
        database = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        plansReference = database.collection("users").document(user.getUid()).collection("plans");
    }
}
