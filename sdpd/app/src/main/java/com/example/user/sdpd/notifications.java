package com.example.user.sdpd;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class notifications extends AppCompatActivity {

    private static final String TAG = "NOTIFICATIONACTIVITY";
    private ListView lstview;
    private ArrayList<String> nList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private String muid;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        //listView
        lstview = (ListView) findViewById(R.id.lstView);
        //adapter
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nList);
        //setadapter
        lstview.setAdapter(adapter);

        //mAuth
        mAuth = FirebaseAuth.getInstance();
        muid = mAuth.getCurrentUser().getUid();
        //String n = getIntent().getExtras();
        //db reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child("Events").child(muid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Toast.makeText(notifications.this, dataSnapshot.getKey(),Toast.LENGTH_SHORT).show();
                //TheEvents ev = dataSnapshot.getValue(TheEvents.class);
                Map<String,String> nMap = new HashMap<>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    nMap.put(snapshot.getKey(),snapshot.getValue().toString());
                }
                String ss;
                ss = "\nName: " + nMap.get("name");
                ss = ss + "\nDate: " + nMap.get("date");
                ss = ss + "\nTime: " + nMap.get("time");
                ss = ss + "\nDescription: " + nMap.get("description") + "\n";
                nList.add(ss);
                adapter.notifyDataSetChanged();
                //generateNotification(nMap.get("name"));
                //Toast.makeText(notifications.this, ss, Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onStop() {
        super.onStop();
        //hideProgressDialog();
    }

}
