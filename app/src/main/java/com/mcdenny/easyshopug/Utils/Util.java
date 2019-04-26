package com.mcdenny.easyshopug.Utils;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private String XreferenceId = "c72025f5-5cd1-5530-99e4-8ba4722fad46";
    private String apiKey = "3f2c21fb9e384b0a8e6c2f0512becfae";
    private String subscriptionKey = "703e4eaf5049405bbc09906d134a335b";
    private String targetEnvironment = "application/json";
    private String contentType = "sandbox";

    public String getContentType() {
        return contentType;
    }

    public String getXreferenceId() {
        return XreferenceId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSubscriptionKey() {
        return subscriptionKey;
    }

    public String getTargetEnvironment() {
        return targetEnvironment;
    }

    public static String cleanEmailKey(String email) {
        if(TextUtils.isEmpty(email))
            return null;
        return email.replace(".",",");
    }

    public static String normalEmailKey(String email) {
        if(TextUtils.isEmpty(email))
            return null;
        return email.replace(",",".");
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

        DecimalFormat formatter = new DecimalFormat("#,##0");
        //NumberFormat formatter =NumberFormat.getInstance();
        final String formattedNumber = formatter.format(number);//add commas

        return formattedNumber;
    }

    public static String getDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime());
    }

    /**
     * Validating email
     * @param email The user's email
     * @return True if the email matches the pattern
     */
    public static boolean isValidEmail(String email){
        String email_pattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(email_pattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Validating password
     * @param pass  password entered by the user
     * @return False when the password length is short and
     * returns true when the passwor is long
     */
    public static boolean isValidPassword(String pass){
        if(pass!=null && pass.length()>6){
            return true;
        }
        return false;
    }
}
