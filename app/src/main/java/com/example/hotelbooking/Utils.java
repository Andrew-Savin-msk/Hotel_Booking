package com.example.hotelbooking;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class Utils {
    public static void onMain(Context context, View view) {
        Intent intent = new Intent(context, MainActivity.class);
        // Если нужно добавить какие-то флаги к интенту, добавьте их здесь. Например:
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
