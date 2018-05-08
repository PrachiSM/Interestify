package com.example.user.sdpd;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddKeywords extends AppCompatActivity implements View.OnClickListener {

    private Button addBtn, okkBtn;
    private TextView keyw;
    private AutoCompleteTextView inte;
    private ListView lstt;
    private DatabaseReference mDatabaseReference;
    private ArrayList<String> keywList = new ArrayList<>();
    public ArrayList<String> theInterests = new ArrayList<>();
    private ArrayAdapter<String> adapter2;
    private ArrayAdapter<String> adapterKeyw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_keywords);

        addBtn = findViewById(R.id.button10);
        okkBtn = findViewById(R.id.button11);
        keyw = findViewById(R.id.editText5);
        inte = findViewById(R.id.autoC);
        lstt = findViewById(R.id.keywListview);

        addBtn.setOnClickListener(this);
        okkBtn.setOnClickListener(this);

        adapterKeyw = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                keywList);
        lstt.setAdapter(adapterKeyw);

        adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,theInterests);
        inte.setAdapter(adapter2);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

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



    @Override
    public void onClick(View v){
        int i = v.getId();
        if(i == R.id.button10){
            mDatabaseReference.child("TheInterests").child(inte.getText().toString())
                    .child(keyw.getText().toString()).setValue(keyw.getText().toString());
            inte.setText("");
            keyw.setText("");
        }

        if(i == R.id.button11) {
            mDatabaseReference.child("TheInterests").child(inte.getText().toString())
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            keywList.add(dataSnapshot.getKey());
                            adapterKeyw.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            keywList.remove(dataSnapshot.getValue(String.class));
                            adapterKeyw.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }
}
