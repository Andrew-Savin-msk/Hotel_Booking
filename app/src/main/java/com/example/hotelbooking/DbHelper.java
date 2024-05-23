package com.example.hotelbooking;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hotelapp.db";
    private static final int DATABASE_VERSION = 1;

    // TODO: Hotel delition methods, (maybe put here data querry methods from HotelResults), (maybe add NOT NULL constraints)
    private static final String CREATE_TABLE_CITY = "CREATE TABLE city (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "city TEXT" +
            ")";

    private static final String CREATE_TABLE_ROOM = "CREATE TABLE room (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "places INTEGER NOT NULL," +
            "cityId INTEGER NOT NULL," +
            "img TEXT NOT NULL," +
            "description TEXT NOT NULL," +
            // Foreign Key Constraint to ensure integrity
            "FOREIGN KEY(cityId) REFERENCES city(id)" +
            ")";

    private static final String CREATE_TABLE_RESERVATIONS = "CREATE TABLE reservations (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "inDay INTEGER," +
            "outDay INTEGER," +
            "roomId INTEGER," +
            "userId INTEGER," +
            // Foreign Key Constraint to ensure integrity
            "FOREIGN KEY(roomId) REFERENCES room(id)," +
            "FOREIGN KEY(userId) REFERENCES users(id)" +
            ")";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "email TEXT NOT NULL UNIQUE," +
            "name TEXT NOT NULL," +
            "password TEXT NOT NULL," +
            "admin INTEGER NOT NULL" +
            ")";

    private static final String RESERVATIONS_TRIGGER = "CREATE TRIGGER Delete_Reservations_After_Room_Deletion " +
            "AFTER DELETE ON room " +
            "FOR EACH ROW " +
            "BEGIN " +
            "    DELETE FROM reservations WHERE roomId = OLD.id; " +
            "END;";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute SQL statements to create multiple tables
        db.execSQL(CREATE_TABLE_CITY);
        db.execSQL(CREATE_TABLE_ROOM);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_RESERVATIONS);
        db.execSQL(RESERVATIONS_TRIGGER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS city");
        db.execSQL("DROP TABLE IF EXISTS room");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS reservations");
        // Create tables again
        onCreate(db);
    }
}
