package com.example.user.sdpd;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sdpd.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends MainActivity implements View.OnClickListener{



    EditText _nameText;
    EditText _emailText;
    EditText _mobileText;
    EditText _passwordText;
    EditText _reEnterPasswordText;
    Button _signupButton;
    TextView _loginLink;

    //protected FirebaseUser currUser;
    // [START declare_auth]
    //protected FirebaseAuth mAuth;
    // [END declare_auth]

    //protected DatabaseReference mDatabase;

    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    private static final String TAG = "EmailPassword";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _nameText = findViewById(R.id.input_name);
        _emailText = findViewById(R.id.input_email);
        _mobileText = findViewById(R.id.input_mobile);
        _passwordText = findViewById(R.id.input_password);
        _reEnterPasswordText = findViewById(R.id.input_reEnterPassword);
        _signupButton = findViewById(R.id.btn_signup);
        _signupButton.setOnClickListener(this);
        _loginLink = findViewById(R.id.link_login);
        _loginLink.setOnClickListener(this);

        // [START initialize_auth]
        //mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        //mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_signup) {
            if(!(_passwordText.getText().toString().equals(_reEnterPasswordText.getText().toString())))
                Toast.makeText(SignupActivity.this, "Password does not match.",
                        Toast.LENGTH_SHORT).show();
            else
                createAccount(_emailText.getText().toString(), _passwordText.getText().toString());
        } else if (i == R.id.link_login) {
            //signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            //new intent of sign up
            finish();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            _nameText.setError("Required.");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            onAuthSuccess(user);
                            //updateUI(user);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            FirebaseAuthException e = (FirebaseAuthException )task.getException();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed."
                                            + e.getMessage(), Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void onAuthSuccess(final FirebaseUser user) {
        final String username = usernameFromEmail(user.getEmail());
        final String name = _nameText.getText().toString();
        final String mobile = _mobileText.getText().toString();

        DatabaseReference userNameRef = mDatabase.child("users").child(username);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if(u==null) {
                    // Write new user
                    writeNewUser(name, mobile, user.getUid(), username, user.getEmail());
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

    // [START basic_write]
    private void writeNewUser(String name, String mobile, String userId, String uname, String email) {
        User user = new User(name, mobile, userId, uname, email);
        mDatabase.child("users").child(userId).setValue(user);

        //Interests inte = new Interests(mAuth.getCurrentUser().getUid(),true,false);
        //mDatabase.child("interests").child(mAuth.getCurrentUser().getUid()).setValue(inte);
    }
    // [END basic_write]

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

}
