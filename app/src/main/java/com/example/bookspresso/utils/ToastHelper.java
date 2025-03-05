package com.example.bookspresso.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ToastHelper {
    public static void showToast(Context context, String message) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(() ->
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            );
        } else {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}
