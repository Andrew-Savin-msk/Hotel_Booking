package com.example.hotelbooking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class HotelRoom {

    private int id;
    private String city;
    private int places;
    public String img;
    public String description;

    public HotelRoom(Integer id, String city, Integer places, String img, String description) {
        this.id = id;
        this.city = city;
        this.places = places;
        this.img = img;
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public String getCity() {
        return this.city;
    }

    public int getPlaces() {
        return this.places;
    }

    public String getImg() {return this.img;}

    public Bitmap getImgBitmap() {return decodeBase64ToBitmap(img);}

    public Bitmap decodeBase64ToBitmap(String base64String) {
        String pureBase64Encoded = base64String.substring(base64String.indexOf(",") + 1);
        byte[] decodedBytes = android.util.Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public String getDescription() {
        return this.description;
    }
}
