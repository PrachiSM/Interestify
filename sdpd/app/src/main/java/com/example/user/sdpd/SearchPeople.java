package com.example.user.sdpd;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.example.user.sdpd.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchPeople extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "SEARCHACTIVITY";
    private AutoCompleteTextView mInterest;
    private List<String> uids = new ArrayList<>();
    private ArrayList<String> uList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter2;
    private String muid;
    public ArrayList<String> theInterests = new ArrayList<>();
    private DatabaseReference mInterestsReference;
    private DatabaseReference mUsersReference;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_people);

        //Views
        mInterest = (AutoCompleteTextView) findViewById(R.id.inpIntGrp);
        //adapters
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,theInterests);
        mInterest.setAdapter(adapter);

        ListView lst = (ListView)findViewById(R.id.listview);
        //adapter
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,uList);
        adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,uids);
        //set adapter
        lst.setAdapter(adapter);
        //button
        findViewById(R.id.addBtn).setOnClickListener(this);

        muid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //muid = getIntent().getStringExtra("uid");

        if (muid == null) {
            throw new IllegalArgumentException("Must pass UID");
        }
        //db references
        mInterestsReference = FirebaseDatabase.getInstance().getReference().child("interests");
        mUsersReference = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //for autocomplete
        mDatabaseReference.child("TheInterests").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                theInterests.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                theInterests.remove(dataSnapshot.getValue(String.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i==R.id.addBtn) {
            uids.clear();
            uList.clear();
            display();
        }
    }

    public void display(){
        final String interestToSearch = mInterest.getText().toString();

        final ArrayList<String> allInterests = new ArrayList<>();
        final ArrayAdapter<String> adapterForKeywords = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,allInterests);
        allInterests.add(interestToSearch);
        adapterForKeywords.notifyDataSetChanged();

        mDatabaseReference.child("TheInterests").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Toast.makeText(AddEvent.this,"Here2", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(AddEvent.this,snapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                        if (interestToSearch.equals(snapshot.getValue().toString())) {
                            //Toast.makeText(AddEvent.this,"Here", Toast.LENGTH_SHORT).show();
                            if (!allInterests.contains(dataSnapshot.getKey())) {
                                allInterests.add(dataSnapshot.getKey());
                                adapterForKeywords.notifyDataSetChanged();
                                //Toast.makeText(AddEvent.this, "" + allInterests.size(),
                                //    Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    }
                }
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mInterestsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.getKey().equals(muid)) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (allInterests.contains(snapshot.getValue())) {
                            uids.add(dataSnapshot.getKey());
                            adapter2.notifyDataSetChanged();
                        }

                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUsersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(uids.contains(dataSnapshot.getKey())) {
                    uList.add(dataSnapshot.getValue(User.class).printformat());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
            //ArrayList<String> uids = getUsers(interestToSearch, muid);
            //Toast.makeText(SearchPeople.this, uids.size(),Toast.LENGTH_SHORT).show();
            //uList = getUserDetails(uids);
            //adapter.notifyDataSetChanged();

    }

}
