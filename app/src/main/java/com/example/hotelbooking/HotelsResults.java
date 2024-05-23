package com.example.hotelbooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class HotelsResults extends AppCompatActivity {
    private DbHelper dbHelper;
    private BookingRequest bookingRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels_results);

        Intent intent = getIntent();
        dbHelper = new DbHelper(HotelsResults.this);

        ArrayList<HotelRoom> rooms = getRooms(intent, dbHelper);

        RecyclerView recyclerView = findViewById(R.id.hotelCards);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (rooms != null) {
            Log.v("HotelsResults.onCreate", rooms.size() + "???");
            MyAdapter adapter = new MyAdapter(this, rooms, new OnRoomClickListener() {
                @Override
                public boolean onRoomClick(HotelRoom room) {
                    if (!UserState.getInstance().isLoggedIn()) {
                        Toast.makeText(HotelsResults.this, "Чтобы забронировать войдите в аккаунт", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    reserveRoom(room);
                    return true;
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    public ArrayList<HotelRoom> getRooms(Intent intent, DbHelper dbHelper) {
        // Initing db connection and cursor
        ArrayList<HotelRoom> result = new ArrayList<HotelRoom>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        // Parcing BookingRequest
        bookingRequest = intent.getSerializableExtra("BR", BookingRequest.class);
        Log.v("HotelsResults.getRooms", bookingRequest.getCity() + "|" + bookingRequest.getInDay() + "|" + bookingRequest.getOutDay() + "|" + bookingRequest.getGuests());
        String city = bookingRequest.getCity();
        String strArrival = String.valueOf(bookingRequest.getInDay());
        String strDeparture = String.valueOf(bookingRequest.getOutDay());
        String strGuests = String.valueOf(bookingRequest.getGuests());
        Log.v("String Arr and Dep", strArrival + "|" + strDeparture);
        // Getting table by BookingResults restrictions
        if (city.equals("Все острова")) {
            String sql = "SELECT room.id AS id, city.city AS city, room.places AS places, room.img AS img, room.description AS description " +
                    "FROM room " +
                    "JOIN city ON room.cityId = city.id " +
                    "LEFT JOIN reservations ON room.id = reservations.roomId " +
                    "WHERE room.places >= ? " +
                    "AND (((? < reservations.inDay OR reservations.inDay IS NULL) AND (? < reservations.inDay OR reservations.inDay IS NULL)) " +
                    "OR ((? > reservations.outDay OR reservations.outDay IS NULL) AND (? > reservations.outDay OR reservations.outDay IS NULL))) ";
            cursor = db.rawQuery(sql, new String[] {strGuests, strArrival, strDeparture, strArrival, strDeparture});
        } else {
            String sql = "SELECT room.id AS id, city.city AS city, room.places AS places, room.img AS img, room.description AS description " +
                    "FROM room " +
                    "JOIN city ON room.cityId = city.id " +
                    "LEFT JOIN reservations ON room.id = reservations.roomId " +
                    "WHERE room.places >= ? " +
                    "AND city.city = ? " +
                    "AND (((? < reservations.inDay OR reservations.inDay IS NULL) AND (? < reservations.inDay OR reservations.inDay IS NULL)) " +
                    "OR ((? > reservations.outDay OR reservations.outDay IS NULL) AND (? > reservations.outDay OR reservations.outDay IS NULL))) ";
            cursor = db.rawQuery(sql, new String[] {strGuests, city, strArrival, strDeparture, strArrival, strDeparture});
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int numberIndex = cursor.getColumnIndex("id");
                int cityIdIndex = cursor.getColumnIndex("city");
                int placesIndex = cursor.getColumnIndex("places");
                int imgIndex = cursor.getColumnIndex("img");
                int descriptionIndex = cursor.getColumnIndex("description");

                if (numberIndex != -1 && cityIdIndex != -1 && placesIndex != -1 && imgIndex != -1) {
                    result.add(new HotelRoom(cursor.getInt(numberIndex),
                            cursor.getString(cityIdIndex),
                            cursor.getInt(placesIndex),
                            cursor.getString(imgIndex),
                            cursor.getString(descriptionIndex)));
                }
            } while (cursor.moveToNext());
            cursor.close();
            return result;
        }
        return null;
    }


    public void reserveRoom(HotelRoom room) {
        if (dbHelper == null) {
            dbHelper = new DbHelper(HotelsResults.this);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("inDay", bookingRequest.getInDay());
        values.put("outDay", bookingRequest.getOutDay());
        values.put("roomId", room.getId());
        values.put("userId", UserState.getInstance().getId());
        db.insert("reservations", null, values);
        values.remove("inDay");
        values.remove("outDay");
        values.remove("outDay");
        values.remove("outDay");

        db.close();
    }

    public void toProfile(View view){
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

    public void backOnMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}