package com.example.hotelbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void loginUser(View view) {
        String login = ((EditText) findViewById(R.id.editTextLogin)).getText().toString();
        String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
        Log.v("Login.loginUser", "Data collected");
        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
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

        Log.v("Login.loginUser", "Validation passed");
        try {
            DbHelper dbHelper = new DbHelper(this);
            Log.v("Login.loginUser", "Db connected");
            String err = UserState.getInstance().Login(login, encryptedPassword, dbHelper);
            Log.v("Login.loginUser", "UserInstance get");
            if (!err.isEmpty()) {
                if (err.equals("internalError")) {
                    Toast.makeText(this, "Проблемы на серверной стороне", Toast.LENGTH_SHORT).show();
                } else if (err.equals("unexistingEmailorPassword")) {
                    Toast.makeText(this, "Неправильный логин или пароль", Toast.LENGTH_SHORT).show();
                }
                Log.v("Login.loginUser", "All without exceptions");
            }
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } catch (SQLiteException e) {
            Log.e("Login.loginUser", "Error whith connecting to database");
            return;
        }

        String tmpUserData = UserState.getInstance().getId() + "|" +
                UserState.getInstance().getEmail() + "|" +
                UserState.getInstance().getName() + "|" +
                UserState.getInstance().getPassword() + "|" +
                UserState.getInstance().getAdmin();
        Log.v("Login.loginUser", tmpUserData);
        Log.v("Login.loginUser", "Sucessfully logged in");
    }

    public void toProfile(View view){
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

    public void toRegistration(View view){
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    public void backOnMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}