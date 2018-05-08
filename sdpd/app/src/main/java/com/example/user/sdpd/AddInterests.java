package com.example.user.sdpd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddInterests extends AppCompatActivity implements View.OnClickListener{

    //private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static final String TAG = "INTERESTACTIVITY";
    private Button okBtn, addKeywBtn;
    //private CheckBox checkA, checkB;
    private String muid;
    private TextView editText;
    private Button btn;
    private ListView listView;
    private AutoCompleteTextView adding;

    //private String mmailid;
    private DatabaseReference mInterestReference;
    private DatabaseReference mDatabaseReference;
    public ArrayList<String> theInterests = new ArrayList<>();
    public ArrayList<String> currUserInterests = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interests);
        //mAuth = FirebaseAuth.getInstance();
        // [START initialize_database_ref]
        //mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        okBtn = findViewById(R.id.addBtn);
        okBtn.setOnClickListener(this);

        addKeywBtn = findViewById(R.id.addKeywords);
        addKeywBtn.setOnClickListener(this);
        //my_layout = (LinearLayout)findViewById(R.id.my_layout);

        muid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //mmailid = getIntent().getStringExtra("mailid");

        //if (muid == null) {
          //  throw new IllegalArgumentException("Must pass UID");
        //}

        mInterestReference = FirebaseDatabase.getInstance().getReference()
                .child("interests").child(muid);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        listView = (ListView)findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, currUserInterests);
        listView.setAdapter(adapter);
        //updateUIWithChecks();

        adding = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView2);
        adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,theInterests);
        adding.setAdapter(adapter2);

        mInterestReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                currUserInterests.add(dataSnapshot.getValue(String.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                currUserInterests.remove(dataSnapshot.getValue(String.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference.child("TheInterests").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                theInterests.add(dataSnapshot.getKey());
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                theInterests.remove(dataSnapshot.getValue(String.class));
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }

    @Override
    public void onStop(){
        super.onStop();
        //hideProgressDialog();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.addBtn) {
            //addInterest();
            String newInt = adding.getText().toString();
            addInterest(newInt);
            adding.setText("");
        }
        else if(i == R.id.addKeywords){
            Intent keyw = new Intent(AddInterests.this, AddKeywords.class);
            startActivity(keyw);
        }
    }

    private void addInterest(String interestToAdd) {
        /*Interests inte = new Interests(muid, checkA.isChecked(),checkB.isChecked());
        mDatabase.child("interests").child(muid).setValue(inte);
        Toast.makeText(AddInterests.this,
                "Updated your Interests", Toast.LENGTH_SHORT).show();*/
        //currUserInterests.add(interestToAdd);
        mInterestReference.child(interestToAdd).setValue(interestToAdd);
        if (!interestThere(interestToAdd)) {
            mDatabaseReference.child("TheInterests").child(interestToAdd).setValue(interestToAdd);
            Toast.makeText(AddInterests.this, "Add the keywords for this interest!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void delInterest(String interest){
        mInterestReference.child(interest).removeValue();
        //currUserInterests.remove(interest);
    }

    private boolean interestThere(String interest){
        return theInterests.contains(interest);
    }

    public void addNewInterest(String interest){
        //check in the TheInterests is already done here
        mDatabaseReference.child("TheInterests").child(interest).setValue(interest);
        theInterests.add(interest);
        addInterest(interest);
    }
}
