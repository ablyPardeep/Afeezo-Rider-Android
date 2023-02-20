package com.rider.afeezo.model;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties

public class User {
        public double lattitude;
        public double longitude;
        public String time;

        // Default constructor required for calls to
        // DataSnapshot.getValue(User.class)
        public User() {
        }

        public User(double lattitude, double longitude,String time) {
            this.lattitude = lattitude;
            this.longitude = longitude;
            this.time = time;
        }
    }

