package com.example.user.sdpd.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by user on 18/4/18.
 */
@IgnoreExtraProperties
public class TheInterests {
    public String iid;
    public ArrayList<String> interests;

    public TheInterests(){
        interests = new ArrayList<>();
    }

    public void setiid(String iid){
        this.iid = iid;
    }

    public void addInterest(String interestName){

        interests.add(interestName);
    }

}
