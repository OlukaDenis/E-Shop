package com.mcdenny.easyshopug.Utils;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static String cleanEmailKey(String email) {
        if(TextUtils.isEmpty(email))
            return null;
        return email.replace(".",",");
    }

    public static String formatRating(String rating) {
        if(TextUtils.isEmpty(rating))
            return null;
        return formatNumberNoComma(rating) + "/5.0";
    }

    public static String formatNumberNoComma(String wholeNumber){

        Double number= Double.parseDouble(wholeNumber);

        //NumberFormat.getInstance().format(myNumber);

        DecimalFormat formatter = new DecimalFormat("0.0") ;
        final String formattedNumber = formatter.format(number);

        return formattedNumber;
    }

    /**
     * Check if a given string value can be parsed to a double
     * @param someText a string to check if is double
     * @return true if some text can be parsed to a double, false otherwise
     */
    public static boolean isDouble(String someText) {
        try {
            Double.parseDouble(someText);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Round off to 2dp
     * and add comma separator.
     * @param wholeNumber
     * @return
     */
    public static String formatNumber(String wholeNumber){

        Double number= Double.parseDouble(wholeNumber);

        //NumberFormat.getInstance().format(myNumber);

        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        //NumberFormat formatter =NumberFormat.getInstance();
        final String formattedNumber = formatter.format(number);//add commas

        return formattedNumber;
    }

    public static String getDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime());
    }
}
