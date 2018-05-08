package com.example.user.sdpd;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.user.sdpd.models.TheEvents;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class AddEvent extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "EVENTACTIVITY";
    private Button addBtn, dateBtn, timeBtn;
    private EditText txtName, txtDesc, txtDate, txtTime;
    private AutoCompleteTextView keyw1, keyw2, keyw3;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String muid;
    public ArrayList<String> theInterests = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mInterestsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        muid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //muid = getIntent().getStringExtra("uid");

        if (muid == null) {
            throw new IllegalArgumentException("Must pass UID");
        }

        //views
        txtName = (EditText) findViewById(R.id.editText);
        txtDesc = (EditText) findViewById(R.id.editText4);
        txtDate = (EditText) findViewById(R.id.editText2);
        txtTime = (EditText) findViewById(R.id.editText3);

        //btns
        addBtn = (Button) findViewById(R.id.button4);
        timeBtn = (Button) findViewById(R.id.button3);
        dateBtn = (Button) findViewById(R.id.button2);
        //setOnclickListener
        addBtn.setOnClickListener(this);
        timeBtn.setOnClickListener(this);
        dateBtn.setOnClickListener(this);

        //autoCompleteText
        keyw1 = (AutoCompleteTextView) findViewById(R.id.auto);
        keyw2 = (AutoCompleteTextView) findViewById(R.id.auto2);
        keyw3 = (AutoCompleteTextView) findViewById(R.id.auto3);
        //adapters
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,theInterests);
        keyw1.setAdapter(adapter);
        keyw2.setAdapter(adapter);
        keyw3.setAdapter(adapter);

        //db references
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mInterestsReference = FirebaseDatabase.getInstance().getReference().child("interests");

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
    public void onClick(View v) {

        int i = v.getId();
        if (v == dateBtn) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == timeBtn) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        else if(v == addBtn){
            getAllInterests(txtName.getText().toString(), keyw1.getText().toString(),
                    txtDate.getText().toString(), txtTime.getText().toString(),
                    txtDesc.getText().toString(),keyw1.getText().toString(),
                    keyw2.getText().toString(), keyw3.getText().toString());
            txtTime.setText("");
            txtDesc.setText("");
            txtDate.setText("");
            txtName.setText("");
            keyw1.setText("");
            keyw2.setText("");
            keyw3.setText("");
        }
    }

    /*
    public void addNewEvent(String name, final String interest, String date, String time,
                            String description){
        final String key = mDatabaseReference.child("TheEvents").push().getKey();
        final TheEvents event = new TheEvents(name, interest, date, time, description);
        final Map<String, Object> eventValues = event.toMap();

        mDatabaseReference.child("TheEvents").child(key).setValue(event);

        //final ArrayList<String> uids = new ArrayList<>();
        mInterestsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getValue().equals(interest)) {
                        mDatabaseReference.child("Events").child(dataSnapshot.getKey())
                                .child(key).setValue(event);
                        Toast.makeText(AddEvent.this, "Event Added\n" + event.printFormat(),
                                Toast.LENGTH_SHORT).show();
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

    }
    */
    public void getAllInterests(String name, final String interest, String date, String time,
                                String description,final String keyword1, final String keyword2,
                                final String keyword3){

        final ArrayList<String> allInterests = new ArrayList<>();
        final ArrayAdapter<String> adapterForKeywords = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,allInterests);

        final ArrayList<String> keywAdded = new ArrayList<>();
        if(keyword1!=null && !keyword1.isEmpty())
            keywAdded.add(keyword1);
        if(keyword2!=null && !keyword2.isEmpty())
            keywAdded.add(keyword2);
        if(keyword3!=null && !keyword3.isEmpty())
            keywAdded.add(keyword3);

        //final ArrayList<String> uids = new ArrayList<>();
        mDatabaseReference.child("TheInterests").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Toast.makeText(AddEvent.this,"Here2", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(AddEvent.this,snapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                        if (keywAdded.contains(snapshot.getValue().toString())) {
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

        final String key = mDatabaseReference.child("TheEvents").push().getKey();
        final TheEvents event = new TheEvents(name, interest, date, time, description);
        //final Map<String, Object> eventValues = event.toMap();

        mDatabaseReference.child("TheEvents").child(key).setValue(event);
        Toast.makeText(AddEvent.this, "Event Added\n" + event.printFormat(),
                Toast.LENGTH_SHORT).show();

        //final ArrayList<String> uids = new ArrayList<>();
        mInterestsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(allInterests.contains(snapshot.getValue().toString())) {
                        mDatabaseReference.child("Events").child(dataSnapshot.getKey())
                                .child(key).setValue(event);
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


    }

}
