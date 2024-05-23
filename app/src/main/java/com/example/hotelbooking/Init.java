package com.example.hotelbooking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.icu.util.Calendar;
import android.util.Base64;
import android.util.Log;
import com.example.hotelbooking.AESCrypt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class Init {
    private DbHelper dbHelper;

    public Init(DbHelper dbHelper, Context context) {
        this.dbHelper = dbHelper;

        String[] filenames = new String[] {"output1.txt", "output2.txt", "output3.txt"};
        String[] descriptions = new String [] {
                "Просторный номер с потрясающим видом на коралловые рифы. Терраса с прямым доступом к океану идеально подходит для наблюдения за морской жизнью и отдыха.",
                "Уютный бунгало среди зелени и экзотических цветов. Традиционный декор и душ на открытом воздухе создают идеальную атмосферу для полного расслабления и спокойствия.",
                "Номер с панорамным видом на океан и белоснежные пляжи. Частная терраса с бассейном создает атмосферу уединения и роскоши, идеальную для романтического отдыха.",
        };
        loadAssets(context, filenames);
        ArrayList<String> images = new ArrayList<String>(){};
        images = loadImages(context, filenames);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(db, 1, 1);
        String [] cities = {"Мальдивы", "Бали", "Мауи", "Сейшелы", "Санторини", "Бора-Бора"};
        int [] users = {1, 2, 3};
        Calendar calendar = Calendar.getInstance();
        long cityId;
        try {
            ContentValues values = new ContentValues();
            for (String city : cities) {
                values.put("city", city);
                cityId = db.insert("city", null, values);
                values.remove("city");
                if (cityId != -1) {
                    values.put("cityId", cityId);

                    values.put("places", 2);
                    values.put("img", images.get(0).toString());
                    values.put("description", descriptions[0]);
                    db.insert("room", null, values);
                    values.remove("places");
                    values.remove("img");
                    values.remove("description");

                    values.put("places", 3);
                    values.put("img", images.get(1).toString());
                    values.put("description", descriptions[1]);
                    db.insert("room", null, values);
                    values.remove("places");
                    values.remove("img");
                    values.remove("description");

                    values.put("places", 4);
                    values.put("img", images.get(2).toString());
                    values.put("description", descriptions[2]);
                    db.insert("room", null, values);
                    values.remove("places");
                    values.remove("img");
                    values.remove("description");

                    values.remove("cityId");

                    Log.v("After rooms in", "Rooms incerted");
                }
            }


            try {
                String encryptedPassword = AESCrypt.encrypt("user1pwd");
                values.put("password", encryptedPassword);
                values.put("email", "user1@mail.ru");
                values.put("name", "User1");
                values.put("admin", 0);
                db.insert("users", null, values);
                values.remove("email");
                values.remove("name");
                values.remove("password");
                values.remove("admin");
            } catch (Exception e) {
                Log.e("Init", "ununable to init user 1 password");
            }

            try {
                String encryptedPassword = AESCrypt.encrypt("user2pwd");
                values.put("password", encryptedPassword);
                values.put("email", "user2@mail.ru");
                values.put("name", "User2");
                values.put("admin", 0);
                db.insert("users", null, values);
                values.remove("email");
                values.remove("name");
                values.remove("password");
                values.remove("admin");
            } catch (Exception e) {
                Log.e("Init", "ununable to init user 2 password");
            }

            try {
                String encryptedPassword = AESCrypt.encrypt("adminpwd");
                values.put("password", encryptedPassword);
                values.put("email", "admin@mail.ru");
                values.put("name", "Admin");
                values.put("admin", 1);
                db.insert("users", null, values);
                values.remove("email");
                values.remove("name");
                values.remove("password");
                values.remove("admin");
            } catch (Exception e) {
                Log.e("Init", "ununable to init user 2 password");
            }

            Log.v("After users in", "Users incerted");

            Log.v("Before first date", "First day setting");
            long inDay = LocalDateTime.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    12,
                    0
            ).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
            values.put("inDay", inDay);

            calendar.add(Calendar.DAY_OF_MONTH, 1);

            Log.v("Before second date", "Second day setting");
            long outDay = LocalDateTime.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    12,
                    0
            ).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
            values.put("outDay", outDay);

            for (int user : users) {
                values.put("userId", user);
                values.put("roomId", 1 + user);
                db.insert("reservations", null, values);
                values.remove("userId");
                values.remove("roomId");
                Log.v("Reservations input", "Reservations for user" + user);
            }

            values.remove("inDay");
            values.remove("outDay");

            Log.v("After reservations in", "reservations incerted");

        } catch(SQLiteException e) {
            Log.e("Init.Init", "Error with DB initialisation");
        } finally {
            db.close();
        }
    }

    public ArrayList<String> loadImages(Context context, String[] filenames) {
        ArrayList<String> images = new ArrayList<String>(){};
        for (String filename : filenames) {
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader bufferedReader = null;
            StringBuilder builder = new StringBuilder();
            try {
                fis = context.openFileInput(filename);
                isr = new InputStreamReader(fis);
                bufferedReader = new BufferedReader(isr);

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (isr != null) {
                    try {
                        isr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            images.add(builder.toString());
        }

        return images;
    }

    public void loadAssets(Context context, String[] filenames) {
        InputStream is = null;
        FileOutputStream fos = null;
        for (String filename : filenames) {
            try {
                is = context.getAssets().open(filename);
                fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

