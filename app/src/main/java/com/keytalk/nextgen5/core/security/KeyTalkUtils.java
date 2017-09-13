package com.keytalk.nextgen5.core.security;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;

/*
 * Class  :  KeyTalkUtils
 * Description : Util class for various operations
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class KeyTalkUtils {
    static final private String defaultAnswer = "000000000000";
    private static final int mUpperRange = 300;
    private static final int mLowestUnused = 213;

    protected static String scrape(String resp, String start, String stop) {
        int offset, len;
        if ((offset = resp.indexOf(start)) < 0)
            return "";
        if ((len = resp.indexOf(stop, offset + start.length())) < 0)
            return "";
        return resp.substring(offset + start.length(), len);
    }

    protected static String md5(String data) {
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            digester.update(data.getBytes());
            byte[] messageDigest = digester.digest();
            return byteArrayToHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }

    protected static byte[] sha256(String data, String encoding) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes(encoding));// "UTF-8"));
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    protected static byte[] sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);// "UTF-8"));
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    protected static boolean equalUrls(String url1, String url2) {
        return url1.equals(url2);
    }

    protected static int[] findAllIndexes(String text, String field){
        int startIndex = 0;
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        int newIndex = text.indexOf(field, startIndex);
        while( newIndex >= 0 ){
            indexes.add(newIndex);
            startIndex = newIndex + 1;
            newIndex = text.indexOf(field, startIndex);
        }
        return convertIntegers(indexes);
    }

    protected static byte[] toByteArray(long value) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        return bb.putLong(value).array();
    }

    protected static byte[] appendByteArrays(byte[] in1, byte[] in2) {
        byte[] combined = null;
        if (in1 == null) {
            if (in2 == null) {
                combined = new byte[0];
            } else {
                combined = in2;
            }
        } else if (in2 == null) {
            combined = in1;
        } else {
            combined = new byte[in1.length + in2.length];
            System.arraycopy(in1, 0, combined, 0, in1.length);
            System.arraycopy(in2, 0, combined, in1.length, in2.length);
        }
        return combined;
    }

    protected static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character
                    .digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    protected static String byteArrayToHexString(byte[] array) {
        StringBuffer hexString = new StringBuffer();
        for (byte b : array) {
            int intVal = b & 0xff;
            if (intVal < 0x10)
                hexString.append("0");
            hexString.append(Integer.toHexString(intVal));
        }
        return hexString.toString();
    }

    protected static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

    static protected String getScreenSize(Context c){
        int width = ((WindowManager) c.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        int height = ((WindowManager) c.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
        return String.valueOf(width) + "x" + String.valueOf(height);
    }

    static protected String getSerialNr(){
        return Build.SERIAL;
    }

    static protected String getAndroidId(Context c){
        return Secure.getString(c.getContentResolver(),Secure.ANDROID_ID);
    }

    static protected String getAndroidOS(){
        return android.os.Build.VERSION.RELEASE;
    }

    static protected String getCountry(Context context){
        return context.getResources().getConfiguration().locale.getCountry();
    }


    static protected String getMacAddress(Context c){
        try{
            return ((WifiManager) c.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
        } catch(Exception e){
            return defaultAnswer;
        }
    }

    static protected int getAndroidVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    static protected String getDeviceImeiId(Context c){
        return ((TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    static protected String getSimNr(Context c){
        try{
            return ((TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE)).getSimSerialNumber();
        } catch(Exception e){
            return defaultAnswer;
        }
    }

    static protected String getSubscriberImsiId(Context c){
        try{
            return ((TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
        } catch(Exception e){
            return defaultAnswer;
        }
    }

    static protected String getSimOperatorName(Context c){
        try{
            return ((TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE)).getSimOperatorName();
        } catch(Exception e){
            return defaultAnswer;
        }
    }

    static protected String getBoardName(){
        return android.os.Build.BOARD;
    }

    static protected String getManufacturer(){
        return android.os.Build.MANUFACTURER;
    }

    static protected String getModel(){
        return android.os.Build.MODEL;
    }

    static protected String getDefault(){
        return defaultAnswer;
    }

    static protected String getHwSig(String response, Context c) {
        String[] hwsigTypes = getHwSigType(response);
        Log.e("TAG", "Hardware formula requested 1: "+hwsigTypes.length);
        StringBuilder hwsig = new StringBuilder();
        for (int i = 0; i < hwsigTypes.length; i++){
            if(isValidInt(hwsigTypes[i])){
                int sigType = Integer.decode(hwsigTypes[i]);
                switch(sigType){
                    case 201:
                        String output = getSerialNr();
                        if(output != null && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(output);
                        break;
                    case 202:
                        output = getAndroidId(c);
                        if(output != null && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(output);
                        break;
                    case 203:
                        output = getMacAddress(c);
                        if(output != null && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(output);
                        break;
                    case 204:
                        output = getDeviceImeiId(c);
                        if(output != null && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(output);
                        break;
                    case 205:
                        output = getSimNr(c);
                        if(output != null && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(output);
                        break;
                    case 206:
                        output = getSubscriberImsiId(c);
                        if(output != null && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(output);
                        break;
                    case 207:
                        output = getSimOperatorName(c);
                        if(output != null && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(output);
                        break;
                    case 208:
                        output = getBoardName();
                        if(output != null && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(output);
                        break;
                    case 209:
                        output = getManufacturer();
                        if(output != null && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(output);
                        break;
                    case 210:
                        output = getModel();
                        if(output != null && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(output);
                        break;
                    case 211:
                        output = String.valueOf(getAndroidVersion());
                        if(output != null && !output.equals("0") && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(getAndroidVersion());
                        break;
                    case 212:
                        output = getScreenSize(c);
                        if(output != null && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(output);
                        break;
                    case 297:
                        output = getSimNr(c);
                        if(output != null && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(output);
                        break;
                    case 298:
                        output = getDeviceImeiId(c);
                        if(output != null && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(output);
                        break;
                    case 299:
                        String randomData = getRandom(c);
                        if(randomData != null && !randomData.isEmpty() && !TextUtils.isEmpty(randomData))
                            hwsig.append(randomData);
                        break;
                    case 0:
                        output = getDefault();
                        if(output != null && !TextUtils.isEmpty(output) && !output.equals("null") && !output.equals("unknown"))
                            hwsig.append(output);
                        break;
                }
                if(sigType >= mLowestUnused && sigType <= mUpperRange){
                    hwsig.append(getDefault());
                }
            }
        }
        String totalSig = "CS-" + byteArrayToHexString(KeyTalkUtils.sha256(hwsig.toString(), "UTF-8")).toUpperCase();
        //String totalSig = hwsig.toString();
        Log.e("TAG", "Hardware formula requested 2: "+totalSig);
        return totalSig;
    }

    static private String[] getHwSigType(String response){
        //return new String(Base64.decode(response, Base64.NO_WRAP)).split(",");
        return new String(response).split(",");
    }

    static protected boolean isValidInt(String number){
        try{
            Integer.decode(number);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    static protected String getRandom(Context c){
        String randomData = null;
        try {
            SharedPreferences sharedPreference = c.getSharedPreferences(ProtocolConstants.RANDOMSTAMP, 0);
            SharedPreferences.Editor editor = sharedPreference.edit();
            String dbTimeStamp = sharedPreference.getString(ProtocolConstants.RANDOMSTAMP, null);
            if(dbTimeStamp != null && !dbTimeStamp.isEmpty() && !TextUtils.isEmpty(dbTimeStamp)) {
                randomData = dbTimeStamp;
            } else {
                randomData = getRandomData();
                editor.putString(ProtocolConstants.RANDOMSTAMP, randomData);
                editor.commit();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return randomData;
    }

    private static String getRandomData() {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
}