package com.mcdenny.easyshopug.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mcdenny.easyshopug.Model.User;

public class Common {
    //This class saves the current user details
    public static User user_Current;
    public static String USER_KEY = "User";
    public static String PASSWORD_KEY = "Password";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo[] info = manager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) ;
                    return true;
                }
            }
        }
        return false;
    }
}

