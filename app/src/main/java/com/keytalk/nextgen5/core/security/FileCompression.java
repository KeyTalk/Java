package com.keytalk.nextgen5.core.security;

import android.content.Context;
import android.content.Intent;
import android.security.KeyChain;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

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

    public static boolean unzip(String path, String dest, Context context,Boolean saveCA) {
        String filePath = path;
        String destinationPath = dest;

        File archive = new File(filePath);
        try {
            ZipFile zipfile = new ZipFile(archive);
            for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                unzipEntry(zipfile, entry, destinationPath, context,saveCA);
            }
        } catch (Exception e) {
            RCCDFileUtil.e("FileCompression", "Unzip Exception : "+e.toString());
            return false;
        }

        return true;
    }

    private static boolean installCertificate(String entry, Context context, String name) {

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            String comcacertPath = entry;
            File rccdCommonFile = new File(comcacertPath);
            if (!rccdCommonFile.exists()) {
                return false;
            }
            InputStream caInputStream = new BufferedInputStream(new FileInputStream(comcacertPath));


            // now I get the X509 certificate from the PEM string
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(caInputStream);
            String alias = "alias";

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null);
            trustStore.setCertificateEntry(alias, certificate);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(trustStore, null);
            KeyManager[] keyManagers = kmf.getKeyManagers();

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(trustStore);
            TrustManager[] trustManagers = tmf.getTrustManagers();

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(keyManagers, trustManagers, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            Intent installIntent = KeyChain.createInstallIntent();
            installIntent.putExtra(KeyChain.EXTRA_CERTIFICATE, certificate.getEncoded());
            installIntent.putExtra(KeyChain.EXTRA_NAME, name.substring(8));
            context.startActivity(installIntent);
          //  startActivityForResult((Activity) context,installIntent,0,null);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return false;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//       // logger.debug("onActivityResult called with requestCode {}", requestCode);
//
//        switch(requestCode)
//        {
//            case 0:
//            {
//                if(resultCode == RESULT_OK)
//                {
//                    // Go to success web page
//                }
//                else //RESULT_CANCELED
//                {
//                    // Go to canceled/failure web page
//                }
//                break;
//            }
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    private static void unzipEntry(ZipFile zipfile, ZipEntry entry,
                                   String outputDir, Context context, Boolean saveCA) throws IOException {

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
        if(saveCA) {
            if (entry.getName().contains(".der") || entry.getName().contains(".pem"))// || entry.getName().contains(".crt"))
            {
                installCertificate(outputDir + entry.getName(), context, entry.getName());

            }
        }
       /* if(entry.getName().contains("pcacert"))
        {
            installCertificate(outputDir +entry.getName(), context,entry.getName());

        }*/
    }

    private static void createDir(File dir) {
        if (dir.exists()) {
            return;
        }
        if (!dir.mkdirs()) {
            throw new RuntimeException("Can not create directory " + dir);
        }
    }
}
