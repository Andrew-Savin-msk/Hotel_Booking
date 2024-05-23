package com.example.hotelbooking;

import java.io.Serializable;

public class BookingRequest implements Serializable {
    String city;
    long inDay;
    long outDay;
    int guests;

    public BookingRequest(String city, long inDay, long outDay, int guests) {
        this.city = city;
        this.inDay = inDay;
        this.outDay = outDay;
        this.guests = guests;
    }

    public String getCity() {
        return this.city;
    }

    public long getInDay() {
        return this.inDay;
    }

    public long getOutDay() {
        return this.outDay;
    }

    public int getGuests() {
        return this.guests;
    }
}
