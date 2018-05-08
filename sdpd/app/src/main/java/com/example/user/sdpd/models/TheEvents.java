package com.example.user.sdpd.models;

/**
 * Created by user on 18/4/18.
 */

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class TheEvents {
    public String name;
    //public String interest;
    public String date;
    public String time;
    public String description;
    //public String eid = "";

    public TheEvents(String name, String interest, String date, String time, String description){
        this.name = name;
        this.date = date;
        this.time = time;
        //this.interest = interest;
        this.description = description;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name",name);
        //result.put("interest",interest);
        result.put("date", date);
        result.put("time", time);
        result.put("description", description);

        return result;
    }

    public String printFormat(){
        return "Event Name: " + name + "\nDate: " + date + "\nTime: " +
                time + "\nDescription: " + description;
    }
}
