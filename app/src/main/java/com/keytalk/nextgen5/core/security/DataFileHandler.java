package com.keytalk.nextgen5.core.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * Class  :  DataFileHandler
 * Description : Support class for file operations
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class DataFileHandler {

    protected static FileOutputStream createOutputFile(String fileName, String path)
            throws IOException {
        StringBuilder fullPath = new StringBuilder();
        if (path != null && path.length() > 0) {
            if (!path.startsWith(SecurityConstants.FRONT_SLASH)) {
                fullPath.append(SecurityConstants.FRONT_SLASH);
            }
            fullPath.append(path);
            if (!path.endsWith(SecurityConstants.FRONT_SLASH)) {
                fullPath.append(SecurityConstants.FRONT_SLASH);
            }
        }
        File fileDir = new File(fullPath.toString());
        createDirIfNotExists(path);
        File dataFile = new File(fileDir, fileName);
        return new FileOutputStream(dataFile);
    }

    /**
     * Writes data from input stream to output stream
     *
     * @param in
     *            InputStream
     * @param out
     *            OutputStream
     * @return long - total amounts of bytes written
     * @throws IOException
     */
    protected static long writeToStream(InputStream in, OutputStream out)
            throws IOException {
        byte data[] = new byte[1024];
        long total = 0;
        int count;
        while ((count = in.read(data)) != -1) {
            total += count;
            out.write(data, 0, count);
        }
        out.flush();
        out.close();
        in.close();
        return total;
    }

    private static boolean createDirIfNotExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return false;
            }
        }
        return true;
    }

    protected static String readFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }
}

