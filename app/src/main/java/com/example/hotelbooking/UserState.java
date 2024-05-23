package com.example.hotelbooking;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class UserState {
    private static UserState instance;

    private UserState(){}
    private DbHelper dbHelper;
    private long id = -1;
    private String password;
    private String email;
    private String name;
    private int admin = -1;

    public static UserState getInstance() {
        if (instance == null) {
            instance = new UserState();
        }
        return instance;
    }

    public String Register(String login, String name, String password, DbHelper dbHelper) {
        Log.v("UserState.Register", "Method reached");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.v("UserState.Register", "Writable db get");
        ContentValues values = new ContentValues();
        values.put("email", login);
        values.put("name", name);
        values.put("password", password);
        values.put("admin", 0);
        try {
//            this.id = db.insertOrThrow("users", null, values);
            db.insertOrThrow("users", null, values);
            Log.v("UserState.Register", "Users sucessfuly inserted");
        } catch (SQLiteConstraintException e) {
            Log.w("UserState.Register", "Такой пользователь уже существует", e);
            return "loginExists";
        } catch (SQLException e) {
            Log.w("UserState.Register", "Ошибка при вставке данных", e);
            return "SQLException";
        } finally {
            values.remove("email");
            values.remove("name");
            values.remove("password");
            values.remove("admin");
            db.close();
//            this.password = password;
//            this.name = name;
//            this.email = login;
//            this.admin = 0;
        }
        return "";
    }

    public String Login(String login, String password, DbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        String sql = "SELECT id, email, name, password, admin " +
                "FROM users " +
                "WHERE email = ? AND password = ?";
        cursor = db.rawQuery(sql, new String[] {login, password});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int emailIdIndex = cursor.getColumnIndex("email");
                int nameIndex = cursor.getColumnIndex("name");
                int passwordIdIndex = cursor.getColumnIndex("password");
                int adminIdIndex = cursor.getColumnIndex("admin");

                if (idIndex != -1 && emailIdIndex != -1 && nameIndex != -1 && passwordIdIndex != -1 && adminIdIndex != -1) {
                    this.id = cursor.getInt(idIndex);
                    this.email = cursor.getString(emailIdIndex);
                    this.name = cursor.getString(nameIndex);
                    this.password = cursor.getString(passwordIdIndex);
                    this.admin = cursor.getInt(adminIdIndex);
                } else {
                    Log.e("UserState.Login", "No such colums in users table");
                    return "internalError";
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.e("UserState.Login", "No such email in db");
            return "unexistingEmailorPassword";
        }
        return "";
    }

    public void Logout() {
        this.id = -1;
        this.email = null;
        this.name = null;
        this.password = null;
        this.admin = -1;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getAdmin() {
        return this.admin;
    }

    public boolean isLoggedIn() {
        return !(this.name == null) && !(this.password == null);
    }

    public boolean isAdmin() {
        return this.admin == 1;
    }
}
