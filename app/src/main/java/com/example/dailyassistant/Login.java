package com.example.dailyassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "Login";
    FirebaseFirestore database;
    private EditText mEmailField;
    private FirebaseAuth mAuth;
    private EditText mPasswordField;
    SharedPreferences emailPrefs;
//  private int google_request_code = 1234;              // GoogleSignInClient mGoogleSignInClient;  disabled

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Fields
        mEmailField = findViewById(R.id.emailText);
        mPasswordField = findViewById(R.id.passwordText);

        // Click listeners
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.SignUp).setOnClickListener(this);

        //SignInButton googleButton; Disabled

        database = FirebaseFirestore.getInstance();
        // [START initialize_auth]
        // Initialize Firebase Auth
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    private void updateUI() {
        String email = emailPrefs.getString("username", "");
        mEmailField.setText(email);
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            CheckBox box = findViewById(R.id.rememberEmail_box);
                            if(box.isChecked())
                            {
                                SharedPreferences.Editor editor = emailPrefs.edit();
                                editor.putString("username", mEmailField.getText().toString());
                                editor.apply();
                            }
                            updateUI();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        if (!task.isSuccessful()) {

                        }
                    }
                });
    }

    //Create FirabaseAuth account and add user information to DB
    private void createAccount(String email, String password)
    {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            CheckBox box = findViewById(R.id.rememberEmail_box);
                            if(box.isChecked())
                            {
                                SharedPreferences.Editor editor = emailPrefs.edit();
                                editor.putString("username", mEmailField.getText().toString());
                                editor.apply();
                            }
                            updateUI();
                            addUserToDb(user);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    //After a successful Sign Up create a document for the user with their Uid as the DocumentID in Firestore
    //The document will then hold a reference to a Plans collection to store all of their created plans
    private void addUserToDb(FirebaseUser user)
    {
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(user.getUid(), user.getEmail());
        database.collection("users").document(user.getUid()).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Added new user");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error loading document", e);
                    }
                });
    }

    //Checks content in TextFields to prevent the SignIn and CreateAccount methods from crashing
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    //Handler for both buttons
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.SignUp) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.login) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
    }

    //Pressing back on this Activity would return to the Main which in turn will send us back here
    //So we make the back button close the app
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }



    // Google Sign-In still throws Exception: 10
    // Disabled for the Hand-In because I couldn't figure out how to fix it
//    private void setUpGoogle()
//    {
        //google button
//        googleButton = findViewById(R.id.button_google_sign_in);
//        googleButton.setSize(SignInButton.SIZE_STANDARD);

        //calls google emails select pop up
//        findViewById(R.id.button_google_sign_in).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()) {
//                    case R.id.button_google_sign_in:
//                        googleSignIn();
//                        break;
//                    // ...
//                }
//            }
//        });

        // Set up the login form.
        //Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestIdToken(getString(R.string.api_user_id_google_sign_in))
//                .build();
//        //client
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//    }

//    private void googleSignIn() {
//        //gets The Google Sign In intent to select email
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, google_request_code);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == google_request_code)
//        {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Log.d(TAG, "Start FirebaseGoogleAuth: "+ account.getId());
//
//                mAuth.signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(),null))
//                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInGoogle:success");
//                            finish();
//                        }
//                        else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInGoogle:failure", task.getException());
//                            Toast.makeText(Login.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google Sign In Error: " + e.getStatusCode());
//                updateUI(null);
//                // ...
//            }
//        }
//    }
}
