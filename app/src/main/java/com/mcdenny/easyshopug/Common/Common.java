package com.mcdenny.easyshopug.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mcdenny.easyshopug.Model.Cart;
import com.mcdenny.easyshopug.Model.User;

import java.util.ArrayList;
import java.util.List;

public class Common {
    //This class saves the current user details
    public static User user_Current;
    public static String USER_KEY = "User";
    public static String PASSWORD_KEY = "Password";

    //Arraylist to store current cart items
    public static List<Cart> Current_cart_list = new ArrayList<>();
    public static int cart_item_total = 0;

    //User's address
    public static String area = " ", place = " ";

    //User's delivery method
    public static String delivery_method = " ";

    //standard shipping fees
    public static int standard_shipping_fees = 1000;
    public static int chosen_delivery_method = 0;




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


    public static String convertCodeToStatus(String status) {
        if(status.equals("0")){
            return "Placed";
        }
        else if(status.equals("1")){
            return "Currently Being Shipped";
        }
        else {
            return "Shipped";
        }
    }
}

