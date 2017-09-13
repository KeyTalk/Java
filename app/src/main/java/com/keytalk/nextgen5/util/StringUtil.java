package com.keytalk.nextgen5.util;

/*
 * Class  :  StringUtil
 * Description : Supporting class for string operations.
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class StringUtil {
    /***
     * Check if string represents boolean value
     * @param booleanString String to be verified
     * @return
     */
    public static boolean isBoolean(String booleanString) {
        if (booleanString != null && (booleanString.trim().equalsIgnoreCase("true") || booleanString.trim().equalsIgnoreCase("false"))) {
            return true;
        }
        return false;
    }

    /****
     * check if string represents numeric value
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
}
