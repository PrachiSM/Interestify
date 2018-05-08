package com.example.user.sdpd.models;

/**
 * Created by user on 27/3/18.
 */

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

// [START interests_class]
@IgnoreExtraProperties
public class Interests {
    public String uid;
    //public boolean a=false;
    //public boolean b=false;
    //public String mailid;
    public ArrayList<String> interestList;
    public Interests(){

    }

    public Interests(String uid){
        this.uid=uid;
        interestList = new ArrayList<>();
        //this.mailid=mailid;
    }

    public void addInterest(String interest){
        if(!searchInterest(interest))
            interestList.add(interest);
    }

    public int getNumberOfInterests(){
        return interestList.size();
    }

    public boolean searchInterest(String interest){
        return !interestList.isEmpty() && interestList.contains(interest);
    }

    public void delInterest(String interest){
        if(searchInterest(interest))
            interestList.remove(interest);
    }

}
// [END interests_class]