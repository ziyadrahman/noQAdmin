package com;

public class item {
    private String timeSlot;
    private String uid;

    public item() {

    }

    public item(String timeSlot, String uid) {
        this.timeSlot = timeSlot;
        this.uid = uid;

    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getUid() {
        return uid;
    }
}


