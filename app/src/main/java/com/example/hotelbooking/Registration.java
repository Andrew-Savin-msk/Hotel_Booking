package com.example.hotelbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void registerUser(View view) {
        String login = ((EditText) findViewById(R.id.editTextLogin)).getText().toString();
        String name = ((EditText) findViewById(R.id.editTextName)).getText().toString();
        String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
        String passwordConfirm = ((EditText) findViewById(R.id.editTextPasswordConfirm)).getText().toString();
        Log.v("Registration.registerUser", "Data collected");
        if (login.isEmpty() || name.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Пароли должны совпадать", Toast.LENGTH_SHORT).show();
            return;
        }

        String encryptedPassword;
        try {
            encryptedPassword = AESCrypt.encrypt(password);
            Log.v("Login.loginUser", "Password encrypted");
        } catch (Exception e) {
            Log.e("Login.loginUser", "Encryption error: " + e.getMessage());
            Toast.makeText(this, "Ошибка шифрования", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.v("Registration.registerUser", "Validation passed");
        try {
            DbHelper dbHelper = new DbHelper(this);
            Log.v("Registration.registerUser", "Db connected");
            String err = UserState.getInstance().Register(login, name, encryptedPassword, dbHelper);
            Log.v("Registration.registerUser", "UserInstance get");
            if (!err.isEmpty()) {
                if (err.equals("loginExists")) {
                    Toast.makeText(this, "Такой логин уже существует", Toast.LENGTH_SHORT).show();
                } else if (err.equals("SQLException")) {
                    Toast.makeText(this, "Проблемы с подключением", Toast.LENGTH_SHORT).show();
                }
                Log.v("Registration.registerUser", "All without exceptions");
            }
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        } catch (SQLiteException e) {
            Log.e("Registration.registerUser", "Error whith connecting to database");
            return;
        }
        Log.v("Registration.registerUser", "Sucessful registration");
    }

    public void toProfile(View view){
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

    public void toLogin(View view){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void backOnMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}