package com.example.tanimart.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsUtil {
    private static final String PREFS = "tanimart_prefs";

    public static void savePhotoPath(Context ctx, String userId, String path) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString("photo_path_" + userId, path).apply();
    }

    public static String getPhotoPath(Context ctx, String userId) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString("photo_path_" + userId, null);
    }

    public static void removePhotoPath(Context ctx, String userId) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().remove("photo_path_" + userId).apply();
    }
}
