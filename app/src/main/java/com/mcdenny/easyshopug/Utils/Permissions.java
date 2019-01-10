package com.mcdenny.easyshopug.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Permissions {
    private static final String FILE_NAME = "file_name";
    private static final String KEY_THEM_ALL = "b73317ab5815a6c0ae402b0467276e56318902b18ed725fbecaf87c4ddda6ca6";

    public static void safeVerification(Context context) {
        // we shall save 1, if user is already verified. Anything else will mean that
        // user is not verified
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_THEM_ALL, "1");
        editor.apply();
    }

    public static boolean isUserVerified(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String code = sharedPreferences.getString(KEY_THEM_ALL, "");

        return code.contentEquals("1");

        // in the else part we can just return false
    }
}
