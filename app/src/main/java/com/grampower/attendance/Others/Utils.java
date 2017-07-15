package com.grampower.attendance.Others;

/**
 * Created by samdroid on 14/6/17.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {


    public static boolean isNameValid(String name) {
        if (name.matches("^[A-Za-z][A-Za-z. ]{0,50}$") && !name.contains("..") && !name.contains("  ")) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isMobileNumberValid(String mobileNumber) {
        if (mobileNumber.charAt(0) == '9' || mobileNumber.charAt(0) == '8' || mobileNumber.charAt(0) == '7') {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isEmailValid(String email) {
        boolean result;

        String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        boolean temp = matcher.matches();
        System.out.println("Regular Expression Result 1 :  " + matcher.matches());
        if (!matcher.matches()) {
            result = false;
        } else {
            result = true;
        }
        return result;
    }

}
