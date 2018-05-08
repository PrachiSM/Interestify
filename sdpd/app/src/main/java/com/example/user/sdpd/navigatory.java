package com.example.user.sdpd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class navigatory extends MainActivity implements View.OnClickListener{

    String muid;
    boolean gn;
    Button inte, sp, ae, nf, so;
    //protected FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigatory);

        muid = getIntent().getStringExtra("uid");
        gn = getIntent().getBooleanExtra("whichSignIn",true);

        inte = (Button)findViewById(R.id.button5);
        sp = findViewById(R.id.button6);
        ae = findViewById(R.id.button7);
        nf = findViewById(R.id.button8);
        so = findViewById(R.id.button9);

        inte.setOnClickListener(this);
        sp.setOnClickListener(this);
        ae.setOnClickListener(this);
        nf.setOnClickListener(this);
        so.setOnClickListener(this);

        //mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View v){
        if(v==inte){
            Intent signed_in =  new Intent(navigatory.this, AddInterests.class);
            signed_in.putExtra("uid",muid);
            signed_in.putExtra("whichSignIn",gn); //true---normal sign in. False ---google sign in
            startActivity(signed_in);
        }
        if(v==sp){
            Intent signed_in =  new Intent(navigatory.this, SearchPeople.class);
            signed_in.putExtra("uid",muid);
            signed_in.putExtra("whichSignIn",gn); //true---normal sign in. False ---google sign in
            startActivity(signed_in);
        }
        if(v==ae){
            Intent signed_in =  new Intent(navigatory.this, AddEvent.class);
            signed_in.putExtra("uid",muid);
            signed_in.putExtra("whichSignIn",gn); //true---normal sign in. False ---google sign in
            startActivity(signed_in);
        }
        if(v==nf){
            Intent signed_in =  new Intent(navigatory.this, notifications.class);
            signed_in.putExtra("uid",muid);
            signed_in.putExtra("whichSignIn",gn); //true---normal sign in. False ---google sign in
            startActivity(signed_in);
        }
        if(v==so){
            signOut();
            finish();
        }

    }

    public void signOut(){
        if(gn){
            mAuth.signOut();
        }
        else{
            mAuth.signOut();
            mGoogleSignInClient.revokeAccess();
        }
    }


}
