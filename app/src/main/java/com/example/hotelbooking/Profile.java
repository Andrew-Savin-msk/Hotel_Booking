package com.example.hotelbooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    private AppCompatButton button;
    private TextView username;
    private DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DbHelper(Profile.this);

        button = findViewById(R.id.appCompatButton);
        username = findViewById(R.id.textViewUsername);

        AppCompatButton editHotelsButton = findViewById(R.id.editHotelsButton);

        // Устанавливаем текст и обработчик для кнопки выхода/входа
        if (UserState.getInstance().isLoggedIn()) {
            username.setText(UserState.getInstance().getName());
            button.setText("Выйти");
        }
        Log.v("НЕ РАБОТАЕТ", UserState.getInstance().getAdmin() + "");
        // Проверяем, является ли пользователь администратором
        if (UserState.getInstance().isAdmin()) {
            editHotelsButton.setVisibility(View.VISIBLE);  // Делаем кнопку видимой
            editHotelsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Переход на активность администратора
                    Intent intent = new Intent(Profile.this, AdminActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            editHotelsButton.setVisibility(View.GONE);
        }
        RecyclerView recyclerView = findViewById(R.id.hotelBroni);
        ArrayList<Reservation> reservations = new ArrayList<Reservation>();
        if (UserState.getInstance().isLoggedIn()) {
            reservations = getReservations();
        }
        if (reservations != null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            MyAdapter3 adapter = new MyAdapter3(this, reservations, new OnReservationClickListner() {
                @Override
                public boolean onReservationClick(Reservation reservation) {
                    if (!UserState.getInstance().isLoggedIn()) {
                        Toast.makeText(Profile.this, "Чтобы забронировать войдите в аккаунт", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    undoReserve(reservation);
                    return true;
                }
            });
            recyclerView.setAdapter(adapter);
        }

    }

    public void goToAdminActivity(View view) {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    public ArrayList<Reservation> getReservations() {
        if (dbHelper == null) {
            dbHelper = new DbHelper(Profile.this);
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ArrayList<Reservation> result = new ArrayList<Reservation>();

        String sql = "SELECT reservations.id AS id, city.city AS city, room.places AS places, reservations.inDay AS inDay, reservations.outDay AS outDay, room.img AS img " +
                "FROM reservations " +
                "JOIN room ON reservations.roomId = room.id " +
                "JOIN city ON room.cityId = city.id " +
                "WHERE reservations.userId = ?";
        String[] selectionArgs = new String[] { String.valueOf(UserState.getInstance().getId()) };
        Cursor cursor = cursor = db.rawQuery(sql, new String[] {String.valueOf(UserState.getInstance().getId())});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int numberIndex = cursor.getColumnIndex("id");
                int cityIndex = cursor.getColumnIndex("city");
                int placesIndex = cursor.getColumnIndex("places");
                int inDayIndex = cursor.getColumnIndex("inDay");
                int outDayIndex = cursor.getColumnIndex("outDay");
                int imageIndex = cursor.getColumnIndex("img");
                if (numberIndex != -1) {
                    result.add(new Reservation(cursor.getInt(numberIndex),
                            cursor.getLong(inDayIndex),
                            cursor.getLong(outDayIndex),
                            cursor.getString(cityIndex),
                            cursor.getInt(placesIndex),
                            cursor.getString(imageIndex)));
                }
            } while (cursor.moveToNext());
            cursor.close();
            return result;
        }
        return null;
    }

    public void undoReserve(Reservation reservation) {
        if (dbHelper == null) {
            dbHelper = new DbHelper(Profile.this);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("reservations", "id = ?", new String[]{String.valueOf(reservation.getId())});
        db.close();
    }

    public void toLogin(View view){
        if (UserState.getInstance().isLoggedIn()) {
            UserState.getInstance().Logout();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
    }

    public void backOnMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}