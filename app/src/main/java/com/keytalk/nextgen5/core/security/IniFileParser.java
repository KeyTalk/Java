package com.keytalk.nextgen5.core.security;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;

/*
 * Class  :  IniFileParser
 * Description : Representing .ini file form rccd file system
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class IniFileParser {

    /***
     * Different delimiter used to parse INI file.
     */
    // order in which they may occur
    private static final char KEYVALUSEPRATOR = '=';
    private static final char COMMENT = ';';
    private static final char INIARRAYOPEN = '(';
    private static final char INISTRUCTUREOPEN = '{';
    private static final char INIVALUEARRAYOPEN = '[';
    private static final char INIVALUEARRAYCLOSE = ']';
    private static final char INISTRUCTURECLOSE = '}';
    private static final char INIARRAYCLOSE = ')';
    private static final char ARRAYSEPRATOR = ',';

    /***
     * parsing INI String character by character
     *
     * @param
     * @return Parsed Ini Object
     */
    @SuppressWarnings("unchecked")
    protected static IniResponseData parseINI(String iniString) {
        try {

            // This main Ini object that will be returned to calling class
            IniResponseData parsedIni = new IniResponseData();

            // Used to save current tag parser is inside example:'(','{','['
            // storing in the stack
            // so as to push and pop it in order they apear
            Stack<Character> currentTag = new Stack<Character>();

            // Used to save keys in LIFO order
            Stack<String> keys = new Stack<String>();

            // Used to save inner object mostly it may be other INI data or
            // array of values.
            Stack<Object> value = new Stack<Object>();

            value.push(parsedIni);

            int start = 0;

            // Sometime '=' is apearing in value which we were assuming as the
            // key value seprator so parser is using this variable to check if
            // current position is keyvalue seprator or inside value
            boolean isvalueEnded = true;
            /***
             * Use stack to keep track of inner tags like '(',']','{'.Use pop
             * and push mechanism while adding data to different keys
             */

            for (int i = 0; i < iniString.length(); i++) {
                char ch = iniString.charAt(i);
                if (ch == KEYVALUSEPRATOR && isvalueEnded) // '='
                {
                    String key = iniString.substring(start, i).trim();// Separating
                    keys.push(key);
                    start = i + 1;
                    isvalueEnded = false;// disable flag as we are parsing value
                    // for key
                }
                if (ch == COMMENT)// ';' //TODO Neglect the comment after ;
                // (according INI standard)
                {
                    isvalueEnded = true;// enable flag as we got the value and
                    // can move for next key
                    if (currentTag.size() > 0
                            && currentTag.peek() == Character
                            .valueOf(INIVALUEARRAYCLOSE)) {
                        currentTag.pop(); // if ']' tag found as last
                        // added,remove opening and closing
                        // brace from the stack to clear
                        // array loop,hence two times pop
                        currentTag.pop();
                        ArrayList<String> local = (ArrayList<String>) value
                                .pop();
                        ((IniResponseData) value.peek()).put(keys.pop(), local);

                    } else if (currentTag.size() > 0
                            && currentTag.peek() == Character
                            .valueOf(INIARRAYCLOSE)) {
                        currentTag.pop();// if ')' tag found as last
                        // added,remove opening and closing
                        // brace from the stack to clear INI
                        // array loop,hence two times pop
                        currentTag.pop();
                        IniResponseData innerValue = (IniResponseData) value
                                .pop();
                        ArrayList<IniResponseData> value_array = (ArrayList<IniResponseData>) value
                                .pop();
                        value_array.add(innerValue);
                        ((IniResponseData) value.peek()).put(keys.pop(),
                                value_array);
                    } else if (keys.size() > 0)// TODO adding as Type of object
                    // like
                    // Integer,Double....Currently
                    // adding as string and
                    // retrieving as
                    // Integer,Double...at run time
                    // as requaire
                    {
                        ((IniResponseData) value.peek()).put(
                                keys.pop(),
                                iniString.substring(start, i).trim()
                                        .replace("\"", ""));
                    }
                    start = i + 1;

                } else if (ch == INIVALUEARRAYOPEN)// '[' ; array value
                // representation
                {
                    currentTag.push(Character.valueOf(INIVALUEARRAYOPEN));
                    start = i + 1;
                    ArrayList<String> valueArray = new ArrayList<String>();// create
                    // ArrayList
                    // where
                    // each
                    // value
                    // gets
                    // added
                    // when
                    // ','
                    // appears
                    value.push(valueArray);

                } else if (ch == INIVALUEARRAYCLOSE
                        && currentTag.peek() == Character
                        .valueOf(INIVALUEARRAYOPEN))// ']' //array value
                // close add last
                // item to ArrayList
                // here or on
                // comment ';'
                {
                    currentTag.push(Character.valueOf(INIVALUEARRAYCLOSE));
                    ((ArrayList<String>) value.peek()).add(iniString.substring(
                            start, i).replace("\"", ""));
                    start = i + 1;
                } else if (ch == ARRAYSEPRATOR)// ',' Add data to ArrayList may
                // be value or INI Structure
                // data
                {
                    if (currentTag.peek() == INIVALUEARRAYOPEN)// '['
                    {
                        ((ArrayList<String>) value.peek()).add(iniString
                                .substring(start, i).replace("\"", ""));
                        start = i + 1;
                    } else if (currentTag.peek() == INIARRAYOPEN)// '('
                    {
                        IniResponseData local = (IniResponseData) value.pop();
                        ((ArrayList<IniResponseData>) value.peek()).add(local);
                    }
                } else if (ch == INIARRAYOPEN)// '(' //create new INI ArrayList
                // on INI array Tag
                {
                    isvalueEnded = true;// enable as we are entering INI array
                    // and can get new key
                    value.push(new ArrayList<IniResponseData>());
                    currentTag.push(Character.valueOf(INIARRAYOPEN));
                    start = i + 1;

                } else if (ch == INIARRAYCLOSE
                        && currentTag.peek() == Character.valueOf(INIARRAYOPEN))// ')'//close
                // INI
                // Array
                // tag
                {
                    currentTag.push(Character.valueOf(INIARRAYCLOSE));
                    start = i + 1;
                } else if (ch == INISTRUCTUREOPEN)// '{' //start of INI
                // Structure
                {
                    isvalueEnded = true;// enable as we are entering INI
                    // structure and can get new key
                    value.push(new IniResponseData());
                    currentTag.push(Character.valueOf(INISTRUCTUREOPEN));
                    start = i + 1;
                } else if (ch == INISTRUCTURECLOSE
                        && currentTag.peek() == Character
                        .valueOf(INISTRUCTUREOPEN))// '}'//end of the
                // INI structure
                {
                    currentTag.pop();
                    start = i + 1;
                }
            }

            return (IniResponseData) value.pop();

        } catch (Exception e) {
            RCCDFileUtil.e("Pasrse INI File Exception:" + e.getMessage());
        }
        return null;
    }

    /***
     * parsing INI InputStream character by character
     *
     * @param
     * @return Parsed Ini Object
     */
    @SuppressWarnings("unchecked")
    protected static IniResponseData parseINI(InputStream iniInputStream) {

        try {

            // This main Ini object that will be returned to calling class
            IniResponseData parsedIni = new IniResponseData();

            // Used to save current tag parser is inside example:'(','{','['
            // storing in the stack
            // so as to push and pop it in order they apear
            Stack<Character> currentTag = new Stack<Character>();

            // Used to save keys in LIFO order
            Stack<String> keys = new Stack<String>();

            // Used to save inner object mostly it may be other INI data or
            // array of values.
            Stack<Object> value = new Stack<Object>();

            value.push(parsedIni);

            int start = 0;

            // Sometime '=' is apearing in value which we were assuming as the
            // key value seprator so parser is using this variable to check if
            // current position is keyvalue seprator or inside value
            boolean isvalueEnded = true;

            /***
             * Creating BufferReader to read INI from input stream
             */
            InputStreamReader iniReader = new InputStreamReader(iniInputStream);
            BufferedReader br = new BufferedReader(iniReader);
            StringBuilder ini = new StringBuilder();
            char oneChar;
            int i = 0;

            /***
             * Use stack to keep track of inner tags like '(',']','{'.Use pop
             * and push mechanism while adding data to different keys
             */

            while ((oneChar = (char) br.read()) != (char) -1) {
                ini.append(oneChar);
                if (oneChar == ' ') {
                    i++;
                    continue;
                }
                if (oneChar == KEYVALUSEPRATOR && isvalueEnded) // '='
                {
                    String key = ini.substring(start, i).trim();// Separating
                    // keys
                    keys.push(key);
                    start = i + 1;
                    isvalueEnded = false;// disable flag as we are parsing value
                    // for key
                }
                if (oneChar == COMMENT)// ';' //TODO Neglect the comment after ;
                // (according INI standard)
                {
                    isvalueEnded = true;// enable flag as we got the value and
                    // can move for next key
                    if (currentTag.size() > 0
                            && currentTag.peek() == Character
                            .valueOf(INIVALUEARRAYCLOSE)) {
                        currentTag.pop(); // if ']' tag found as last
                        // added,remove opening and closing
                        // brace from the stack to clear
                        // array loop,hence two times pop
                        currentTag.pop();
                        ArrayList<String> local = (ArrayList<String>) value
                                .pop();
                        ((IniResponseData) value.peek()).put(keys.pop(), local);

                    } else if (currentTag.size() > 0
                            && currentTag.peek() == Character
                            .valueOf(INIARRAYCLOSE)) {
                        currentTag.pop();// if ')' tag found as last
                        // added,remove opening and closing
                        // brace from the stack to clear INI
                        // array loop,hence two times pop
                        currentTag.pop();
                        IniResponseData inner_value = (IniResponseData) value
                                .pop();
                        ArrayList<IniResponseData> value_array = (ArrayList<IniResponseData>) value
                                .pop();
                        value_array.add(inner_value);
                        ((IniResponseData) value.peek()).put(keys.pop(),
                                value_array);
                    } else if (keys.size() > 0)// TODO adding as Type of object
                    // like
                    // Integer,Double....Currently
                    // adding as string and
                    // retrieving as
                    // Integer,Double...at run time
                    // as requaire
                    {
                        ((IniResponseData) value.peek()).put(keys.pop(), ini
                                .substring(start, i).trim().replace("\"", ""));
                    }
                    start = i + 1;

                } else if (oneChar == INIVALUEARRAYOPEN)// '[' ; array value
                // representation
                {

                    currentTag.push(Character.valueOf(INIVALUEARRAYOPEN));
                    start = i + 1;
                    ArrayList<String> value_array = new ArrayList<String>();// create
                    // ArrayList
                    // where
                    // each
                    // value
                    // gets
                    // added
                    // when
                    // ','
                    // apears
                    value.push(value_array);

                } else if (oneChar == INIVALUEARRAYCLOSE
                        && currentTag.peek() == Character
                        .valueOf(INIVALUEARRAYOPEN))// ']' //array value
                // close add last
                // item to ArrayList
                // here or on
                // comment ';'
                {
                    currentTag.push(Character.valueOf(INIVALUEARRAYCLOSE));
                    ((ArrayList<String>) value.peek()).add(ini.substring(start,
                            i).replace("\"", ""));
                    start = i + 1;
                } else if (oneChar == ARRAYSEPRATOR)// ',' Add data to ArrayList
                // may be value or INI
                // Structure data
                {
                    if (currentTag.peek() == INIVALUEARRAYOPEN)// '['
                    {
                        ((ArrayList<String>) value.peek()).add(ini.substring(
                                start, i).replace("\"", ""));
                        start = i + 1;
                    } else if (currentTag.peek() == INIARRAYOPEN)// '('
                    {
                        IniResponseData local = (IniResponseData) value.pop();
                        ((ArrayList<IniResponseData>) value.peek()).add(local);
                    }
                } else if (oneChar == INIARRAYOPEN)// '(' //create new INI
                // ArrayList on INI array
                // Tag
                {
                    isvalueEnded = true;// enable as we are entering INI array
                    // and can get new key
                    value.push(new ArrayList<IniResponseData>());
                    currentTag.push(Character.valueOf(INIARRAYOPEN));
                    start = i + 1;

                } else if (oneChar == INIARRAYCLOSE
                        && currentTag.peek() == Character.valueOf(INIARRAYOPEN))// ')'//close
                // INI
                // Array
                // tag
                {
                    currentTag.push(Character.valueOf(INIARRAYCLOSE));
                    start = i + 1;
                } else if (oneChar == INISTRUCTUREOPEN)// '{' //start of INI
                // Structure
                {
                    isvalueEnded = true;// enable as we are entering INI
                    // structure and can get new key
                    value.push(new IniResponseData());
                    currentTag.push(Character.valueOf(INISTRUCTUREOPEN));
                    start = i + 1;
                } else if (oneChar == INISTRUCTURECLOSE
                        && currentTag.peek() == Character
                        .valueOf(INISTRUCTUREOPEN))// '}'//end of the
                // INI structure
                {
                    currentTag.pop();
                    start = i + 1;
                }
                i++;
            }

            return (IniResponseData) value.pop();

        } catch (IOException e1) {
            RCCDFileUtil.e("Ini Response Data IOException:" + e1.getMessage());
        } catch (Exception e) {
            RCCDFileUtil.e("Ini Response Data Exception:" + e.getMessage());
        }
        return null;
    }

    /***
     *
     * @param iniFilepath
     *            :Path of the INI file on to SD card.
     * @return
     */
    protected static IniResponseData parseINIFromPath(String iniFilepath) {
        try {
            InputStream iniInputStream = new FileInputStream(iniFilepath);
            return parseINI(iniInputStream);
        } catch (FileNotFoundException e) {
            RCCDFileUtil
                    .e("IniFile Parse parseINIFromPath : FileNotFoundException:"
                            + e.getMessage());
            return null;
        }
    }
}

