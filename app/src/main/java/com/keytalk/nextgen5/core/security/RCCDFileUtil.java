package com.keytalk.nextgen5.core.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.keytalk.nextgen5.BuildConfig;
import com.keytalk.nextgen5.util.PreferenceManager;
import com.keytalk.nextgen5.view.util.AppConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static java.util.Calendar.*;

/*
 * Class  :  RCCDFileUtil
 * Description : An class for handle all rccd file operations like save,delete, modification etc
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class RCCDFileUtil {

    public RCCDFileUtil() {

    }

    public static void e(String message) {
        Log.e(SecurityConstants.TAG_PREFIX,message);
    }//String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

    public static String getTime()
    {
        String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(getInstance().getTime());
        return formattedDate;
    }
    public static void e(String tag, String message) {
        Log.e(tag,message);
    }
    public static void hideSoftInputFromWindow(Context context)
    {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
       // imm.hideSoftInputFromWindow(rccdInputEditText.getWindowToken(), 0);
    }


    protected static boolean saveLogcatToFile(Context context) {
        boolean isSucess = false;
        StringBuilder logs = new StringBuilder();
        try {
            int pid = android.os.Process.myPid();
            String pidPattern = String.format("%d):", pid);
            String deviceInfo ="KeyTalk Android Next Generation 5 client \n\n Device Details\n\n"+"Device      : "+KeyTalkUtils.getManufacturer() +"\n"
                    +"Model       : "+KeyTalkUtils.getModel()+"\n"+"SDK Version : "+KeyTalkUtils.getAndroidVersion()+"\n"
                    +"OS Version  : "+KeyTalkUtils.getAndroidOS()+"\n\nApplication Log \n\n";
            logs.append(deviceInfo);
            Process process = new ProcessBuilder().command("logcat", "-t", "200", "-v", "time").redirectErrorStream(true).start();
            InputStream in = null;
            FileWriter out = null;
            try {
                in = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(pidPattern)) {
                        logs.append(line).append("\n");
                    }
                }
                //Log file from MMC
                boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
                if(isSDPresent) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), SecurityConstants.KeyTalkLogFile);
                    if(file.exists()) {
                        file.delete();
                    }
                    out = new FileWriter(file);
                    out.write(logs.toString());
                    isSucess = true;
                } else {  //Log file from internal application storage
                    OutputStream outputStream = null;
                    try {
                        File file = new File(context.getFilesDir(), SecurityConstants.KeyTalkLogFile);
                        if(file.exists()) {
                            file.delete();
                        }
                        outputStream = context.openFileOutput(  SecurityConstants.KeyTalkLogFile, Context.MODE_WORLD_READABLE);
                        outputStream.write(logs.toString().getBytes());
                        isSucess = true;
                    } catch (Exception e) {
                    } finally {
                        if (outputStream != null) {
                            try {
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e) { }
                        }
                    }
                }
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        } catch (IOException e) {
        } finally {
            return isSucess;
        }
    }


    protected static String getLogContents(Context context) {
        boolean isSucess = false;
        StringBuilder logs = new StringBuilder();
        try {
            int pid = android.os.Process.myPid();
            String pidPattern = String.format("%d):", pid);
            String deviceInfo ="KeyTalk Android Next Generation 5 client \n\n Device Details\n\n"+"Device      : "+KeyTalkUtils.getManufacturer() +"\n"
                    +"Model       : "+KeyTalkUtils.getModel()+"\n"+"SDK Version : "+KeyTalkUtils.getAndroidVersion()+"\n"
                    +"OS Version  : "+KeyTalkUtils.getAndroidOS()+"\n\nApplication Log \n\n";
            logs.append(deviceInfo);
            Process process = new ProcessBuilder().command("logcat", "-t", "200", "-v", "time").redirectErrorStream(true).start();
            InputStream in = null;
            try {
                in = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(pidPattern)) {
                        logs.append(line).append("\n");
                    }
                }
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            }
        } catch (IOException e) {
        } finally {
            return logs.toString();
        }
    }


    protected static Uri getLogDetailsAsUri(Context context) {
        Uri uri = null;
        try {
            boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            if(isSDPresent) {
                uri = Uri.parse("file://"+ Environment.getExternalStorageDirectory().getAbsolutePath()+SecurityConstants.KEYTALK_LOGFILE_NAME);
                if(Build.VERSION.SDK_INT>=24) {
                    File file = new File(uri.getPath());
                    uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",file);
                }
            } else {
                uri = Uri.parse("file://"+context.getFilesDir().getAbsolutePath()+SecurityConstants.KEYTALK_LOGFILE_NAME);
                if(Build.VERSION.SDK_INT>=24) {
                    File file = new File(uri.getPath());
                    uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",file);
                }
            }
        } catch (Exception e) {
        } finally {
            return uri;
        }
    }

    /*
 *  Check Any RCCD file created or not.
 */
    protected boolean checkAnyRCCDFileExist(File rccdFilePath) {
        File rccdFolderPath = new File(rccdFilePath.getPath()+SecurityConstants.RCCD_FOLDER_SEPERATOR+SecurityConstants.RCCD_FILE_INTERNAL_STORAGE_FOLDER);
        if (rccdFolderPath.isDirectory() && rccdFolderPath.listFiles().length > 0) {
            return true;
        }
        return false;
    }

    /*
 * Unzip and add RCCD to file system
 */
    protected String[] addRCCDFileToInternalStorage(Context context,
                                                    InputStream inputStream, String rccdFileName) {
        String returnMessage = null;
        String rccdFolderTitle = null;
        String[] rccdFileDetails = new String[2];
        int index = 0;
        if (inputStream == null) {
            returnMessage = SecurityConstants.ERROR_EMPTY_INPUT_STREAM;
            rccdFileDetails[0] = returnMessage;
            return rccdFileDetails;
        } else if(rccdFileName == null || rccdFileName.isEmpty()) {
            returnMessage = SecurityConstants.ERROR_FILE_OPERATIONS;
            rccdFileDetails[0] = returnMessage;
            return rccdFileDetails;
        } else {
            String[] splitRCCDFileName = rccdFileName.split(SecurityConstants.RCCD_FILE_EXTENSTION);
            if(splitRCCDFileName.length <= 0 ) {
                returnMessage = SecurityConstants.ERROR_FILE_OPERATIONS;
                rccdFileDetails[0] = returnMessage;
                return rccdFileDetails;
            }
            String rccdFolderName = splitRCCDFileName[splitRCCDFileName.length - 1];
            OutputStream outputStream = null;
            try {
                String rccdCommonPath = context.getFilesDir() + SecurityConstants.RCCD_FOLDER_SEPERATOR+ SecurityConstants.RCCD_FILE_INTERNAL_STORAGE_FOLDER;
                File rccdCommonFile = new File(rccdCommonPath);
                if (!rccdCommonFile.isDirectory()) {
                    rccdCommonFile.mkdir();
                }
				/*String rccdFilePath = rccdCommonPath +RCCD_FOLDER_SEPERATOR + rccdFolderName;
				rccdCommonFile = new File(rccdFilePath);
				System.out.println("rccdFilePath..................."+rccdFilePath);
				if (rccdCommonFile.isDirectory()) {
					deleteRecursive(rccdCommonFile);
				}*/

                //new
                //Renaming folder.
                int rccdCount = rccdCommonFile.list().length;
                String tempCurrentRCCDFolderName = rccdFolderName;
                String rccdFilePath = rccdCommonPath +SecurityConstants.RCCD_FOLDER_SEPERATOR + rccdFolderName;
                rccdCommonFile = new File(rccdFilePath);
                while(rccdCommonFile.isDirectory()) {
                    index++;
                    rccdFilePath = rccdCommonPath +SecurityConstants.RCCD_FOLDER_SEPERATOR + rccdFolderName+"("+index+")";
                    tempCurrentRCCDFolderName = rccdFolderName+"("+index+")";
                    rccdCommonFile = new File(rccdFilePath);
                }
                //new coder done here
                outputStream =  DataFileHandler.createOutputFile(rccdFileName, rccdFilePath + SecurityConstants.RCCD_FOLDER_SEPERATOR);
                DataFileHandler.writeToStream(inputStream, outputStream);
                String path=rccdFilePath + SecurityConstants.RCCD_FOLDER_SEPERATOR + rccdFileName;
                String destinationPath=rccdFilePath + SecurityConstants.RCCD_FOLDER_SEPERATOR;
                PreferenceManager.put(context, AppConstants.PATH,path);
                PreferenceManager.put(context, AppConstants.DESTINATIONPATH,path);
                boolean unzipStatus = FileCompression.unzip(path, destinationPath,context,false);
                //FileInputStream openFileInput = context.openFileInput(rccdFilePath);
                rccdCommonFile = new File(rccdFilePath, rccdFileName);
                if (rccdCommonFile.exists()) {
                    rccdCommonFile.delete();
                }

                String currentFolder = rccdFilePath + SecurityConstants.RCCD_FOLDER_SEPERATOR
                        + SecurityConstants.RCCD_CONTENT_FOLDER_PATH + SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_COMCACERT;
                try {
                    File file = new File(currentFolder);
                    if (!file.exists()) {
                        File tempFile = new File(rccdFilePath);
                        if (tempFile.isDirectory()) {
                            deleteRecursive(tempFile);
                        }
                        if(!unzipStatus)
                            returnMessage = SecurityConstants.ERROR_UNZIP_OPERATIONS;
                        else
                            returnMessage = SecurityConstants.ERROR_VALIDATION_OPERATIONS;
                        rccdFileDetails[0] = returnMessage;
                        return rccdFileDetails;
                    }
                } catch (Exception e) { }
                returnMessage = SecurityConstants.SUCESS_FILE_OPERATIONS;
                //New code for service match.
                if(rccdCount > 0 && returnMessage.equals(SecurityConstants.SUCESS_FILE_OPERATIONS)) {
                    IniResponseData currentRCCDIniResponseData = readCurrentRCCDIniData(rccdFilePath,context);
                    if(currentRCCDIniResponseData == null ) {
                        File tempFile = new File(rccdFilePath);
                        if (tempFile.isDirectory()) {
                            deleteRecursive(tempFile);
                        }
                        returnMessage = SecurityConstants.ERROR_EMPTY_INPUT_STREAM;
                    } else {
                        IniData allIniResponseData = getAllIniResponseData(context, tempCurrentRCCDFolderName);
                        if(allIniResponseData == null && allIniResponseData.iniResponseData.size() > 0) {
                            File tempFile = new File(rccdFilePath);
                            if (tempFile.isDirectory()) {
                                deleteRecursive(tempFile);
                            }
                            returnMessage = SecurityConstants.ERROR_EMPTY_INPUT_STREAM;
                        } else {
                            String currentProviderName = currentRCCDIniResponseData.getProviderName();
                            int currentIndex = 0;
                            for(IniResponseData existingRCCDIniResponseData:allIniResponseData.iniResponseData)  {
                                String existingProvider = existingRCCDIniResponseData.getProviderName();
                                if(currentProviderName!= null && existingProvider!= null && currentProviderName.trim().equals(existingProvider.trim()))	{
                                    rccdFolderTitle = allIniResponseData.iniFolderName.get(currentIndex);
                                    int updatedCount = 0, importedCount = 0;
                                    ArrayList<String> currentIniServiceNames=currentRCCDIniResponseData.getServiceNames();
                                    ArrayList<String> existingIniServiceNames=existingRCCDIniResponseData.getServiceNames();
                                    ArrayList<IniResponseData> currentIniServicesData=currentRCCDIniResponseData.getServiceList();
                                    if(currentIniServiceNames!=null && existingIniServiceNames!=null) {
                                        for(String currentServideName:currentIniServiceNames) {
                                            //If service already exist then override the service
                                            if(existingIniServiceNames.contains(currentServideName)) {
                                                int existingIndex = existingIniServiceNames.indexOf(currentServideName);
                                                int iniIndex = currentIniServiceNames.indexOf(currentServideName);
                                                if(currentIniServicesData!= null) {
                                                    IniResponseData serviceData = currentIniServicesData.get(iniIndex);
                                                    existingRCCDIniResponseData.setServiceAt(existingIndex, serviceData);
                                                    updatedCount++;
                                                }
                                            } else {
                                                int iniIndex = currentIniServiceNames.indexOf(currentServideName);//If service does not exist then add the service.
                                                if(currentIniServicesData!= null) {
                                                    IniResponseData serviceData = currentIniServicesData.get(iniIndex);
                                                    existingRCCDIniResponseData.addService(serviceData);
                                                    saveINI(context, existingRCCDIniResponseData,allIniResponseData.iniFolderName.get(currentIndex));
                                                    importedCount++;
                                                }
                                            }
                                        }
                                    }
                                    File tempFile = new File(rccdFilePath);
                                    if (tempFile.isDirectory()) {
                                        deleteRecursive(tempFile);
                                    }
                                    rccdFileDetails[1] = rccdFolderTitle;
                                    break;
                                }
                                currentIndex++;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                returnMessage = SecurityConstants.ERROR_FILE_OPERATIONS;
            } catch (Exception e) {
                e.printStackTrace();
                returnMessage = SecurityConstants.ERROR_FILE_OPERATIONS;

            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        // outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            rccdFileDetails[0] = returnMessage;
            //return returnMessage;
            return rccdFileDetails;
        }
    }

    /*
 * Reading current RCCD data
 */
    private IniResponseData readCurrentRCCDIniData(final String rccdFilePath, Context context) {
        IniResponseData currentRCCDIniResponseData = null;
        FileInputStream fileInputStream = null;
        try {
            if(rccdFilePath == null || rccdFilePath.isEmpty()) {
                return currentRCCDIniResponseData;
            }
            String rccdFolderPath = rccdFilePath;
            File rccdCommonFile = new File(rccdFolderPath);
            if (!rccdCommonFile.isDirectory()) {
                return currentRCCDIniResponseData;
            }
            rccdFolderPath = rccdFolderPath +SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_USER_INI_PATH;
            rccdCommonFile = new File(rccdFolderPath);
            if (!rccdCommonFile.exists()) {
                return currentRCCDIniResponseData;
            }
            fileInputStream = new FileInputStream(rccdCommonFile);
            currentRCCDIniResponseData = IniFileParser.parseINI(fileInputStream,context );

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (fileInputStream != null) {
                try {
                    // outputStream.flush();
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return currentRCCDIniResponseData;
    }

    /*
 * delete all files and sub folders from a give folder
 */
    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

	/*
	 * Saving updated user.ini file
	 */

    private boolean saveINI(Context context, final IniResponseData resposeData,final String folderName) {
        IniResponseData iniData=null;
        boolean isUpdated = false;
        try {
            String rccdCommonPath = context.getFilesDir() + SecurityConstants.RCCD_FOLDER_SEPERATOR+ SecurityConstants.RCCD_FILE_INTERNAL_STORAGE_FOLDER;
            File rccdCommonFile = new File(rccdCommonPath);
            if (!rccdCommonFile.isDirectory()) {
                return isUpdated;
            }

            rccdCommonPath = rccdCommonPath +SecurityConstants.RCCD_FOLDER_SEPERATOR + folderName +SecurityConstants.RCCD_FOLDER_SEPERATOR+ SecurityConstants.RCCD_USER_INI_PATH;
            File file = new File(rccdCommonPath);
            if(file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fileinputstream=new FileOutputStream(file);
            fileinputstream.write(resposeData.getByteData());
            fileinputstream.flush();
            fileinputstream.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return isUpdated;
    }

    /*
 * Reading all RCCD datas
 */
    protected IniData getAllIniResponseData(Context context,final String tempCurrentRCCDFolderName) {
        IniData iniData = null;
        try {
            String rccdCommonPath = context.getFilesDir() + SecurityConstants.RCCD_FOLDER_SEPERATOR+ SecurityConstants.RCCD_FILE_INTERNAL_STORAGE_FOLDER;
            File rccdCommonFile = new File(rccdCommonPath);
            if (!rccdCommonFile.isDirectory()) {
                return iniData;
            }
            ArrayList<IniResponseData> allIniResponseData = new ArrayList<IniResponseData>();
            ArrayList<String> iniFolderName = new ArrayList<String>();
            for (File childFile : rccdCommonFile.listFiles()) {
                String childfileName = childFile.getName();
                if(!tempCurrentRCCDFolderName.equals(childfileName)) {
                    IniResponseData iniResponseData = readCurrentRCCDIniData(rccdCommonPath +SecurityConstants.RCCD_FOLDER_SEPERATOR + childfileName, context);
                    if (iniResponseData != null) {
                        allIniResponseData.add(iniResponseData);
                        iniFolderName.add(childfileName);
                    }
                }
            }
            iniData = new IniData(iniFolderName, allIniResponseData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iniData;
    }
	/*
	 * reading given rccd file content from file syste,
	 */

    protected IniResponseData readRCCDFile(Context context, String rccdFileName)  {
        FileInputStream fileInputStream = null;
        IniResponseData iniResponseData = null;
        try {
            String rccdCommonPath = context.getFilesDir() + SecurityConstants.RCCD_FOLDER_SEPERATOR+ SecurityConstants.RCCD_FILE_INTERNAL_STORAGE_FOLDER;
            File rccdCommonFile = new File(rccdCommonPath);
            if (!rccdCommonFile.isDirectory()) {
                return iniResponseData;
            }
            if(rccdFileName == null || rccdFileName.isEmpty()) {
                return iniResponseData;
            }
            String[] splitRCCDFileName = rccdFileName.split(SecurityConstants.RCCD_FILE_EXTENSTION);
            if(splitRCCDFileName.length <= 0 ) {
                return iniResponseData;
            }
            String rccdFolderName = splitRCCDFileName[splitRCCDFileName.length - 1];
            String rccdFolderPath = rccdCommonPath +SecurityConstants.RCCD_FOLDER_SEPERATOR + rccdFolderName;
            rccdCommonFile = new File(rccdFolderPath);
            if (!rccdCommonFile.isDirectory()) {
                return iniResponseData;
            }

            rccdFolderPath = rccdFolderPath +SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_USER_INI_PATH;
            rccdCommonFile = new File(rccdFolderPath);
            if (!rccdCommonFile.exists()) {
                return iniResponseData;
            }
            fileInputStream = new FileInputStream(rccdCommonFile);
            iniResponseData = IniFileParser.parseINI(fileInputStream, context);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (fileInputStream != null) {
                try {
                    // outputStream.flush();
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return iniResponseData;
    }

    /*
 * Remove all RCCD folders from file system
 */
    protected boolean revomeAllRCCDFolders(Context context) {
        try {
            String rccdPath = context.getFilesDir() + SecurityConstants.RCCD_FOLDER_SEPERATOR+ SecurityConstants.RCCD_FILE_INTERNAL_STORAGE_FOLDER;
            File rccdFolderPath = new File(rccdPath);
            deleteRecursive(rccdFolderPath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    CertificateInfo cert;

    protected boolean removeAllCertificates(Context context) {

        try {
            SharedPreferences mSettings = context.getSharedPreferences(SecurityConstants.KMSettingsPrefFileName, 0);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.clear();
            editor.commit();
            char[] test={12345};
            removeAllNativeKeyStoreInstallationStatus(context);

            String rccdPath = context.getFilesDir() + SecurityConstants.RCCD_FOLDER_SEPERATOR+ SecurityConstants.RCCD_FILE_INTERNAL_CERT_STORAGE_FOLDER;
            File rccdFolderPath = new File(rccdPath);

            //CertificateInfo cert;
            KeyStore keystore = cert.getKeyStore();
            OutputStream writeStream = new FileOutputStream(rccdPath);
            keystore.store(writeStream, test);
            writeStream.close();

            deleteRecursive(rccdFolderPath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean removeAllNativeKeyStoreInstallationStatus(Context context) {
        try {
            SharedPreferences mSettings = context.getSharedPreferences(SecurityConstants.KMSettingsPrefFileNativeStatus, 0);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.clear();
            editor.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
 *  Get all RCCD file contents
 */
    protected ArrayList<RCCDFileData> getAllRCCDFileContents(Context context) {
        File rccdFolderPath = new File(context.getFilesDir() + SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_FILE_INTERNAL_STORAGE_FOLDER);
        ArrayList<RCCDFileData> allProviderServiceData=null;
        if (rccdFolderPath.isDirectory() && rccdFolderPath.listFiles().length > 0) {
            allProviderServiceData=new ArrayList<RCCDFileData>();
            File[] rccdFiles = rccdFolderPath.listFiles();
            for(int i = 0; i<rccdFiles.length; i++) {
                RCCDFileData rccdFileData = new RCCDFileData();
                rccdFileData.setRccdFileName(rccdFiles[i].getName()+SecurityConstants.RCCD_FILE_EXTENSTION);
                rccdFileData.setRccdFilePath(rccdFiles[i].getName());
                File rccdContent = new File(rccdFiles[i].getAbsolutePath()+SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_CONTENT_FOLDER_PATH);
				/*if(rccdContent.isDirectory() && rccdContent.list().length > 0 && rccdContent.listFiles(bmpFilter).length > 0) {
					if(rccdContent.listFiles(bmpFilter)[0].exists()) {
						Bitmap providerIcon = BitmapFactory.decodeFile(rccdContent.listFiles(bmpFilter)[0].getAbsolutePath());
						rccdFileData.setProviderIcon(providerIcon);
					}
				}*/
                if(rccdContent.isDirectory() && rccdContent.list().length > 0 && rccdContent.listFiles(pngFilter).length > 0) {
                    if(rccdContent.listFiles(pngFilter)[0].exists()) {
                        Bitmap providerIcon = BitmapFactory.decodeFile(rccdContent.listFiles(pngFilter)[0].getAbsolutePath());
                        rccdFileData.setProviderIcon(providerIcon);
                    }
                }

                if(rccdContent.isDirectory() && rccdContent.list().length > 0 && rccdContent.listFiles(iniFilter).length > 0) {
                    if(rccdContent.listFiles(iniFilter)[0].exists()) {
                        FileInputStream fileInputStream = null;
                        try {
                            fileInputStream  = new FileInputStream(rccdContent.listFiles(iniFilter)[0]);
                            IniResponseData iniResponseData = IniFileParser.parseINI(fileInputStream, context);
                            rccdFileData.setServiceData(iniResponseData);
                        } catch (IOException e) {
                            e("RCCDFileUtility","Reading all rccd file IOException : "+e);
                            e.printStackTrace();
                        } catch (Exception e) {
                            e("RCCDFileUtility","Reading all rccd file Exception : "+e);
                            e.printStackTrace();
                        } finally {
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException e) {
                                    e("RCCDFileUtility","Inputstream not available : "+e);
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                allProviderServiceData.add(rccdFileData);
            }
        }
        return allProviderServiceData;
    }

    FilenameFilter pngFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            // TODO Auto-generated method stub
            return (filename.endsWith(SecurityConstants.RCCD_PNG_FILTER));
        }
    };

    FilenameFilter iniFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            // TODO Auto-generated method stub
            return (filename.endsWith(SecurityConstants.RCCD_INI_FILTER));
        }
    };

    protected void deletePFXFile(Context context, String fileName) {
        File f = new File(context.getFilesDir() + SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_FILE_INTERNAL_STORAGE_PFX_FOLDER + SecurityConstants.RCCD_FOLDER_SEPERATOR + fileName);
        if(f.exists()) {
            f.delete();
        }
    }

    protected boolean updateIniServerURL(Context context, final String rccdFilePath, final String updatedURL) {
        boolean isUpdated = false;
        InputStream inputstream = null;
        FileOutputStream fileinputstream = null;
        try
        {
            String fullPath = context.getFilesDir()  + SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_FILE_INTERNAL_STORAGE_FOLDER +
                    SecurityConstants.RCCD_FOLDER_SEPERATOR + rccdFilePath + SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_USER_INI_PATH;
            File file = new File(fullPath);
            if (file.exists()) {
                inputstream=new FileInputStream(file);
                IniResponseData iniData=IniFileParser.parseINI(inputstream, context);
                iniData.setServerUri(updatedURL);
                file.delete();
                file.createNewFile();
                fileinputstream=new FileOutputStream(file);
                fileinputstream.write(iniData.getByteData());
                isUpdated = true;
            } else {
                isUpdated = false;
            }
        } catch (Exception e)  {
            e.printStackTrace();
            isUpdated = false;
        } finally {
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileinputstream != null) {
                try {
                    fileinputstream.flush();
                    fileinputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isUpdated;
    }
    protected byte[] getPFXFile(Context context, String fileName) {
        File f = new File(context.getFilesDir() + SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_FILE_INTERNAL_STORAGE_PFX_FOLDER + SecurityConstants.RCCD_FOLDER_SEPERATOR + fileName);
        byte[] result = null;
        if(f.exists()) {
            e("getPFXFile file exist ");
            try {
                result = new byte[(int) f.length()];
                FileInputStream inputstream = new FileInputStream(f);
                inputstream.read(result);
                inputstream.close();
            } catch (FileNotFoundException e) {
                e("getPFXFile file FileNotFoundException :"+e);
            } catch (IOException e) {
                e("getPFXFile file IOException :"+e);
            }
        } else {
            e("getPFXFile file not exist ");
        }
        return result;
    }

    protected static boolean updateNativeKeyStoreInstallationStatus(Context context,String passwordURL, boolean status) {
        try {
            SharedPreferences mSettings = context.getSharedPreferences(SecurityConstants.KMSettingsPrefFileNativeStatus, 0);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(passwordURL, status);
            editor.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected static boolean getNativeKeyStoreInstallationStatus(Context context,String passwordURL) {
        try {
            SharedPreferences mSettings = context.getSharedPreferences(SecurityConstants.KMSettingsPrefFileNativeStatus, 0);
            return mSettings.getBoolean(passwordURL, false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean updateIniFile(Context context, final String rccdFilePath, final int serviceCount, final String updatedURL) {
        boolean isUpdated = false;
        InputStream inputstream = null;
        FileOutputStream fileinputstream = null;
        try
        {
            String fullPath = context.getFilesDir()  + SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_FILE_INTERNAL_STORAGE_FOLDER +
                    SecurityConstants.RCCD_FOLDER_SEPERATOR + rccdFilePath + SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_USER_INI_PATH;
            File file = new File(fullPath);
            if (file.exists()) {
                inputstream=new FileInputStream(file);
                IniResponseData iniData=IniFileParser.parseINI(inputstream, context);
                iniData.setServiceUri(serviceCount,updatedURL);
                file.delete();
                file.createNewFile();
                fileinputstream=new FileOutputStream(file);
                fileinputstream.write(iniData.getByteData());
                isUpdated = true;
            } else {
                isUpdated = false;
            }
        } catch (Exception e)  {
            e.printStackTrace();
            isUpdated = false;
        } finally {
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileinputstream != null) {
                try {
                    fileinputstream.flush();
                    fileinputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isUpdated;
    }
    protected boolean addUserNameToIniFile(Context context, final String rccdFilePath, final int serviceCount, final String updatedUserName) {
        boolean isUpdated = false;
        InputStream inputstream = null;
        FileOutputStream fileinputstream = null;
        try
        {
            String fullPath = context.getFilesDir()  + SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_FILE_INTERNAL_STORAGE_FOLDER +
                    SecurityConstants.RCCD_FOLDER_SEPERATOR + rccdFilePath + SecurityConstants.RCCD_FOLDER_SEPERATOR + SecurityConstants.RCCD_USER_INI_PATH;
            File file = new File(fullPath);
            if (file.exists()) {
                inputstream=new FileInputStream(file);
                IniResponseData iniData=IniFileParser.parseINI(inputstream,context);
                boolean isSucess = iniData.addUserName(serviceCount,updatedUserName);
                if(isSucess) {
                    file.delete();
                    file.createNewFile();
                    fileinputstream=new FileOutputStream(file);
                    fileinputstream.write(iniData.getByteData());
                    isUpdated = true;
                }
            } else {
                isUpdated = false;
            }
        } catch (Exception e)  {
            e.printStackTrace();
            isUpdated = false;
        } finally {
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileinputstream != null) {
                try {
                    fileinputstream.flush();
                    fileinputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isUpdated;
    }

}
