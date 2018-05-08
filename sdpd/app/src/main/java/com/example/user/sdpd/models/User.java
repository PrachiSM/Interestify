package com.example.user.sdpd.models;

/**
 * Created by user on 26/3/18.
 */

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String name;
    public String mobile;
    public String email;
    public String userId;
    //public Interests interests;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String mobile, String userId, String username, String email) {
        this.name = name;
        this.mobile = mobile;
        this.username = username;
        this.email = email;
        this.userId = userId;
        //interests = new Interests();
    }

    public String printformat(){
        return "\nName: " + name + "\nemail: " + email + "\nMobile Number: " + mobile + "\n";
    }

}
// [END blog_user_class]