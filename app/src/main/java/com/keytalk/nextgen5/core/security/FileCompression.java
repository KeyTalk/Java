package com.keytalk.nextgen5.core.security;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/*
 * Class  :  FileCompression
 * Description : An file support class that has zip and unzip operations
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class FileCompression {

    protected static void zip(String path, String[] uncompressedFiles,
                              String zipFile) throws IOException {
        if (!path.endsWith(SecurityConstants.FRONT_SLASH))
            path += SecurityConstants.FRONT_SLASH;

        for (int i = 0; i < uncompressedFiles.length; i++)
            uncompressedFiles[i] = path + uncompressedFiles[i];

        BufferedInputStream origin = null;
        FileOutputStream dest = DataFileHandler.createOutputFile(zipFile, path);
        ZipOutputStream out = new ZipOutputStream(
                new BufferedOutputStream(dest));

        byte data[] = new byte[SecurityConstants.BUFFER];

        for (int i = 0; i < uncompressedFiles.length; i++) {
            FileInputStream fi = new FileInputStream(uncompressedFiles[i]);
            origin = new BufferedInputStream(fi, SecurityConstants.BUFFER);
            ZipEntry entry = new ZipEntry(
                    uncompressedFiles[i].substring(uncompressedFiles[i]
                            .lastIndexOf(SecurityConstants.FRONT_SLASH) + 1));
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, SecurityConstants.BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
        }

        out.close();
    }

    protected static boolean unzip(String path, String dest) {
        String filePath = path;
        String destinationPath = dest;

        File archive = new File(filePath);
        try {
            ZipFile zipfile = new ZipFile(archive);
            for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                unzipEntry(zipfile, entry, destinationPath);
            }
        } catch (Exception e) {
            RCCDFileUtil.e("FileCompression", "Unzip Exception : "+e.toString());
            return false;
        }
        return true;
    }

    private static void unzipEntry(ZipFile zipfile, ZipEntry entry,
                                   String outputDir) throws IOException {

        if (entry.isDirectory()) {
            createDir(new File(outputDir, entry.getName()));
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()) {
            createDir(outputFile.getParentFile());
        }
        BufferedInputStream inputStream = new BufferedInputStream(
                zipfile.getInputStream(entry));
        BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(outputFile));

        DataFileHandler.writeToStream(inputStream, outputStream);
    }

    private static void createDir(File dir) {
        if (dir.exists()) {
            return;
        }
        if (!dir.mkdirs()) {
            throw new RuntimeException("Can not create dir " + dir);
        }
    }
}
