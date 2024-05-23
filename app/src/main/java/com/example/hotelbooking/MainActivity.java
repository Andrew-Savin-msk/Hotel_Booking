package com.example.hotelbooking;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FIRST_RUN = "firstRun";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isFirstRun = settings.getBoolean(FIRST_RUN, true);

        Log.v("MainActivity.onCreate", "|" + UserState.getInstance().isLoggedIn() + "|");
        // App initialisation with db
        Log.v("AppState", "|" + isFirstRun + "|");
        try {
            DbHelper dbHelper = new DbHelper(this);
            if (isFirstRun) {
                Init init = new Init(dbHelper, getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(FIRST_RUN, false);
                editor.apply();
            }
//            Init init = new Init(dbHelper, getApplicationContext());
        } catch (SQLiteException e) {
            Log.e("MainActivity.onCreate", "Error whith connecting to database");
        }

        // Presetting of in and out dates
        setDateInForms();


        // Setting city spinner
        String [] cities = getCities();
        for (String city : cities) {
            Log.v("MainActivity", city);
        }
        Log.v("MainActivity", "Cities ended");
        if (cities.length == 0) {
            cities = new String[] {"На данный момент нет городов с доступными отелями"};
        }
        // TODO: Do all cities variation
        setSpinnerCity(cities);
        // Setting in and out buttons
        setInOutButtons();
    }

    // Maybe put in some "presets" class
    public void setDateInForms() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String sMonth = (month + 1) + "";
        String sDay = dayOfMonth + "";
        if (month + 1 < 10) {
            sMonth ="0" + sMonth;
        }
        if (dayOfMonth < 10) {
            sDay ="0" + sDay;
        }
        TextView editTextArr = findViewById(R.id.TextArrive);
        String date = year + "." + sMonth + "." + sDay;
        editTextArr.setText(date);

        SharedPreferences prefs = getSharedPreferences("inDay", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Date", date);
        editor.apply();

        calendar.add(Calendar.DAY_OF_MONTH, 1);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        TextView editTextDep = findViewById(R.id.TextDeparture);
        sMonth = (month + 1) + "";
        sDay = dayOfMonth + "";
        if (month + 1 < 10) {
            sMonth ="0" + sMonth;
        }
        if (dayOfMonth < 10) {
            sDay ="0" + sDay;
        }
        date = year + "." + sMonth + "." + sDay;
        editTextDep.setText(date);

        prefs = getSharedPreferences("outDay", MODE_PRIVATE);
        editor = prefs.edit();
        editor.putString("Date", date);
        editor.apply();
    }

    public void toProfile(View view){
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

    public void toInfo(View view){
        Intent intent = new Intent(this,  Info.class);
        startActivity(intent);
    }


    public void getInfo(View view){
        int places = 1;
        String guests = ((EditText) findViewById(R.id.editTextGuests)).getText().toString();

        Log.v("MainActivity.getInfo", "|" + guests + "|");

        if (guests.isEmpty()) {
            Log.v("MainActivity.getInfo.guests", "IsEmpty");
        } else {
            places = Integer.parseInt(guests);
        }

        SharedPreferences prefsCity = getSharedPreferences("city", MODE_PRIVATE);
        String city = prefsCity.getString("city", "");
        SharedPreferences prefsIn = getSharedPreferences("inDay", MODE_PRIVATE);
        long inDateInSec = dateToSec(prefsIn);
        SharedPreferences prefsOut = getSharedPreferences("outDay", MODE_PRIVATE);
        long outDateInSec = dateToSec(prefsOut);

        BookingRequest bookingRequest = new BookingRequest(city, inDateInSec, outDateInSec, places);

//        Log.v("MainActivity.getInfo", bookingRequest.getCity() + "|" + bookingRequest.getInDay() + "|" + bookingRequest.getOutDay() + "|" + bookingRequest.getGuests());

        // TODO: Maybe compose to model object
        Intent intent = new Intent(this, HotelsResults.class);
        intent.putExtra("BR", bookingRequest);
        startActivity(intent);
    }

    private long dateToSec(SharedPreferences prefs) {
        Log.v("MainActivity.dateToSec", "Entered");
        String date = prefs.getString("Date", null);
        Log.v("MainActivity.dateToSec", date + "!!!");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate lDate = LocalDate.parse(date, formatter);
        Log.v("MainActivity.dateToSec", "Get formatter");
        int year = lDate.getYear();
        int month = lDate.getMonthValue();
        int day = lDate.getDayOfMonth();
        Log.v("MainActivity.dateToSec", "Dates in int");
        Log.v("MainActivity.dateToSec", day + "|" + month + "|" + year);

        if (day == -1 || month == -1 || year == -1) {
            return 0;
        }

        LocalDateTime dateTime = LocalDateTime.of(year, month, day, 12, 0);
        Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        return instant.getEpochSecond();
    }

    public void setInOutButtons() {
        TextView buttonInDate = findViewById(R.id.TextArrive);
        TextView buttonOutDate = findViewById(R.id.TextDeparture);

        buttonInDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputInDateDialog();
            }
        });

        buttonOutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputOutDateDialog();
            }
        });
    }

    private void setSpinnerCity(String [] cities) {
        Spinner spinnerCity = findViewById(R.id.spinnerCity);
        ArrayAdapter<String> adapterCity = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapterCity);

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int
                    position, long id) {
                String city = cities[position];

                SharedPreferences prefs = getSharedPreferences("city", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("city", city);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                SharedPreferences prefs = getSharedPreferences("city", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("city", "");
                editor.apply();
            }
        });
    }

    public String [] getCities() {
        ArrayList<String> resultList = new ArrayList<String>();
        resultList.add("Все острова");
        DbHelper dbHelper = new DbHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT city FROM city";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int cityIdIndex = cursor.getColumnIndex("city");

                if (cityIdIndex != -1) {
                    resultList.add(cursor.getString(cityIdIndex));
                }
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
            return resultList.toArray(new String[resultList.size()]);
        }
        db.close();
        return null;
    }

    private void inputInDateDialog() {
        TextView editText = findViewById(R.id.TextArrive);
        String dateStr = editText.getText().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate date = LocalDate.parse(dateStr, formatter);

        int year = date.getYear();
        int month = date.getMonthValue() - 1;
        int dayOfMonth = date.getDayOfMonth();

        Log.v("MainActivity.inputInDateDialog", year + "." + month + "." + dayOfMonth);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                TextView editText = findViewById(R.id.TextArrive);
                String sMonth = (month + 1) + "";
                String sDay = dayOfMonth + "";
                if (month + 1 < 10) {
                    sMonth ="0" + sMonth;
                }
                if (dayOfMonth < 10) {
                    sDay ="0" + sDay;
                }
                String date = year + "." + sMonth + "." + sDay;
                editText.setText(date);
                SharedPreferences prefs = getSharedPreferences("inDay", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Date", date);
                editor.apply();
            }
        }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void inputOutDateDialog() {
        TextView editText = findViewById(R.id.TextDeparture);
        String dateStr = editText.getText().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate date = LocalDate.parse(dateStr, formatter);

        int year = date.getYear();
        int month = date.getMonthValue() - 1;
        int dayOfMonth = date.getDayOfMonth();

        Log.v("MainActivity.inputOutDateDialog", year + "." + month + "." + dayOfMonth);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                TextView editText = findViewById(R.id.TextDeparture);
                String sMonth = (month + 1) + "";
                String sDay = dayOfMonth + "";
                if (month + 1 < 10) {
                    sMonth ="0" + sMonth;
                }
                if (dayOfMonth < 10) {
                    sDay ="0" + sDay;
                }
                String date = year + "." + sMonth + "." + sDay;
                editText.setText(date);
                SharedPreferences prefs = getSharedPreferences("outDay", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Date", date);
                editor.apply();
            }
        }, year, month, dayOfMonth);
        datePickerDialog.show();
    }
}