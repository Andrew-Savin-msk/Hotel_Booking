package com.example.hotelbooking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Reservation {
    private int id;
    private long inDay;
    private long outDay;
    private String city;
    private int places;
    private String img;
    private Bitmap imgBitmap;


    public Reservation(Integer id, long inDay, long outDay, String city, Integer places, String img) {
        this.id = id;
        this.inDay = inDay;
        this.outDay = outDay;
        this.city = city;
        this.places = places;
        this.img = img;
        this.imgBitmap = decodeBase64ToBitmap(img);
    }

    public int getId() {
        return this.id;
    }

    public long getInDay() {
        return inDay;
    }

    public String getInDayFormatted() {
        return toRightForm(this.inDay);
    }

    public long getOutDay() {
        return outDay;
    }

    public String getOutDayFormatted() {
        return toRightForm(this.outDay);
    }

    public String getCity() {
        return this.city;
    }

    public int getPlaces() {
        return this.places;
    }

    public String getImg() {return this.img;}

    public Bitmap getImgBitmap() {return this.imgBitmap;}

    private Bitmap decodeBase64ToBitmap(String base64String) {
        String pureBase64Encoded = base64String.substring(base64String.indexOf(",") + 1);
        byte[] decodedBytes = android.util.Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    private String toRightForm(long unixTimestamp) {
        LocalDate date = convertUnixToLocalDate(unixTimestamp);
        return formatDate(date);
    }

    private LocalDate convertUnixToLocalDate(long unixTimestamp) {
        // Конвертация UNIX временной метки в LocalDate
        return Instant.ofEpochSecond(unixTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private String formatDate(LocalDate date) {
        // Форматирование даты в формат yyyy.MM.dd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return date.format(formatter);
    }
}
