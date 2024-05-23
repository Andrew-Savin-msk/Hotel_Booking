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

public class AdminActivity extends AppCompatActivity {
    private DbHelper dbHelper;
    private BookingRequest bookingRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels_results);

        dbHelper = new DbHelper(AdminActivity.this);

        ArrayList<HotelRoom> rooms = getRooms(dbHelper);

        RecyclerView recyclerView = findViewById(R.id.hotelCards);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (rooms != null) {
            Log.v("HotelsResults.onCreate", rooms.size() + "???");
            MyAdapter2 adapter = new MyAdapter2(this, rooms, new OnRoomClickListener() {
                @Override
                public boolean onRoomClick(HotelRoom room) {
                    if (!UserState.getInstance().isLoggedIn()) {
                        Toast.makeText(AdminActivity.this, "Чтобы забронировать войдите в аккаунт", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    deleteRoom(room);
                    return true;
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    public ArrayList<HotelRoom> getRooms(DbHelper dbHelper) {
        // Initing db connection and cursor
        ArrayList<HotelRoom> result = new ArrayList<HotelRoom>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        // Getting table by BookingResults restrictions
        String sql = "SELECT room.id AS id, city.city AS city, room.places AS places, room.img AS img, room.description AS description " +
                "FROM room " +
                "JOIN city ON room.cityId = city.id " +
                "LEFT JOIN reservations ON room.id = reservations.roomId ";
        cursor = db.rawQuery(sql, null);
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

    // TODO: Do cascade delition of room reservetions connected on room_id
    public void deleteRoom(HotelRoom room) {
        if (dbHelper == null) {
            dbHelper = new DbHelper(AdminActivity.this);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("room", "id = ?", new String[]{String.valueOf(room.getId())});
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