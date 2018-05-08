package com.example.user.sdpd;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sdpd.models.Interests;
import com.example.user.sdpd.models.TheInterests;
import com.example.user.sdpd.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private static final String TAGG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int REQUEST_SIGNUP = 0;

    //private DatabaseReference mDatabaseReference;

    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;

    protected GoogleSignInClient mGoogleSignInClient;
    protected FirebaseUser currUser;
    // [START declare_auth]
    protected FirebaseAuth mAuth;
    // [END declare_auth]

    protected DatabaseReference mDatabase;

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        );
        // Views
        _emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _loginButton =  findViewById(R.id.btn_login);
        _signupLink = findViewById(R.id.link_signup);

        _emailText.setOnClickListener(this);
        _passwordText.setOnClickListener(this);
        _loginButton.setOnClickListener(this);
        _signupLink.setOnClickListener(this);

        findViewById(R.id.sign_in_buttong).setOnClickListener(this);
        //mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    // [END on_start_check_user]

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //onAuthSuccess(user);
                            updateUI(user,true);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null,true);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            //mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null,true);
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            _emailText.setError("Required.");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        String password = _passwordText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            _passwordText.setError("Required.");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user, boolean gn) {
        hideProgressDialog();
        if (user != null) { //success in sign in ---go to next intent
            _emailText.setText("");
            _passwordText.setText("");
            Intent signed_in =  new Intent(MainActivity.this, navigatory.class);
            signed_in.putExtra("uid",mAuth.getCurrentUser().getUid());
            signed_in.putExtra("whichSignIn",gn); //true---normal sign in. False ---google sign in
            startActivity(signed_in);

        } else {
            //Log.w(TAG, "loadInterests:onCancelled", DatabaseError.toException());
            // [START_EXCLUDE]
            Toast.makeText(MainActivity.this, "Failed login.",
                    Toast.LENGTH_SHORT).show();
            // [END_EXCLUDE]
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_login) {
            signIn(_emailText.getText().toString(), _passwordText.getText().toString());
        } else if (i == R.id.link_signup) {
            //signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            //new intent of sign up
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
            //finish();
        }
        else
        if (i == R.id.sign_in_buttong) {
            signIng();
        }
    }


    private void onAuthSuccess(final FirebaseUser user) {
        startService(new Intent(this, NotificationService.class));
        final String username = usernameFromEmail(user.getEmail());

        DatabaseReference userNameRef = mDatabase.child("users").child(username);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if(u==null) {
                    // Write new user
                    writeNewUser(username, null, user.getUid(), username, user.getEmail());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);

        // Go to MainActivity
        //startActivity(new Intent(SignInActivity.this, MainActivity.class));
        //finish();
    }
    /*
    private boolean userExists(String userName) {
        Query q = mDatabase.child("users").orderByChild("username").equalTo(userName);
        if(q!=null)
            return true;
        return false;
    }*/

    // [START basic_write]
    private void writeNewUser(String name, String mobile,String userId, String uname, String email) {
        User user = new User(name, mobile, userId, uname, email);
        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    /*
    //adding or updating interests. Go to new intent
    public void addInterests(){
        Intent interestPage = new Intent(MainActivity.this, AddInterests.class);
        interestPage.putExtra("uid", mAuth.getCurrentUser().getUid());
        //interestPage.putExtra("mailid", mAuth.getCurrentUser().getUid());
        startActivity(interestPage);
    }

    //searching for people with similar interests. Go to new intent
    public void searchPeople(){
        Intent searchPage = new Intent(MainActivity.this, SearchPeople.class);
        searchPage.putExtra("uid", mAuth.getCurrentUser().getUid());
        startActivity(searchPage);
    }
    */
    // [START onactivityresult] for google login
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            @SuppressLint("RestrictedApi") Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAGG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null,false);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAGG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAGG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            onAuthSuccess(user);
                            //addNewInterest("Sdpd");
                            updateUI(user,false);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAGG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null,false);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]
    // [START signin]
    private void signIng() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]
    /*
    private void signOutg() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        final Task<Void> voidTask = mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUIg(null);
                    }
                });
    }
    */
    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null,false);
                    }
                });
    }
}