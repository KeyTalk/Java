package com.keytalk.nextgen5.core.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.keytalk.nextgen5.R;
import com.keytalk.nextgen5.view.util.AppConstants;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

/*
 * Class  :  KeyTalkSettings
 * Description : An util class for various certification operations
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */
public class KeyTalkSettings {

    private Context mContext;
    SharedPreferences mSettings = null;

    private static String userDefinedKeyStorePassword = "12345";

    public KeyTalkSettings()
    {}
    protected KeyTalkSettings(Context c) {
        mContext = c;
        mSettings = mContext.getSharedPreferences(ProtocolConstants.KMSettingsPrefFileName, 0);
    }

    protected boolean isHotUrl(String url) {
        return true;
    }


    public boolean validCertAvailable(String url, String serviceName) {
        boolean result = false;
        try {
            KeyStore ks = readKeyStore(url,serviceName).getKeyStore();
            X509Certificate cert = getCertificateForServiceName(ks,serviceName);
            Date practicalEndTime = calcPracticalEndTime(cert.getNotBefore(),cert.getNotAfter(), getPracticalEndTimePercentage(serviceName,cert.getNotBefore(),cert.getNotAfter()));
            Date currentTime = new Date();
            result = currentTime.before(practicalEndTime);
        } catch (FileNotFoundException e) {
            Log.e("",e.getMessage());
            // do nothing, result is already false
        } catch (KeyTalkNoCertificateException e) {
            Log.e("",e.getMessage());
            // do nothing, result is already false
        }
        catch (Exception e) {
            Log.e("",e.getMessage());
            // do nothing, result is already false
        }
        return result;
    }

    private X509Certificate getCertificateForServiceName(KeyStore ks, String serviceName) throws KeyTalkNoCertificateException {
        try {
            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                //RCCDFileUtil.e("KeyTalkSettings","alias  :  "+alias);
                X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
               /* String baseServiceName = getNsBaseServiceName(cert);
                if (baseServiceName.equals(serviceName)) {
                    return cert;
                }*/
               if(cert != null)
                   return cert;
            }
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
        throw new KeyTalkNoCertificateException(mContext.getString(R.string.no_cert_found_service)  + serviceName);
    }

    /**
     * Loads a KeyStore from the internal storage (which is hidden to both the
     * user and other applications).<br>
     * <br>
     * <b>TODO: This is not a final implementation: you can read different
     * KeyStores (using different filenames), but this implementation only
     * understands one password.</b>
     *
     * @param url
     *            the URL for which the KeyStore must be read
     * @throws FileNotFoundException
     *
     * @see KeyStore
     */
    protected CertificateInfo readKeyStore(String url, String serviceName) throws FileNotFoundException {
        //String filename = getServiceNameFromUrl(url);
        String filename = serviceName;
        if (filename == null) {
            throw new FileNotFoundException("File for url: " + url + " does not exist");
        }
        KeyStore ks = null;
        //New Code
        File certPath = new File(mContext.getFilesDir() + ProtocolConstants.RCCD_FOLDER_SEPERATOR + ProtocolConstants.RCCD_FILE_INTERNAL_CERT_STORAGE_FOLDER);
        if (!certPath.isDirectory())
            throw new FileNotFoundException("File for url: " + url + " does not exist");
        certPath = new File(mContext.getFilesDir() + ProtocolConstants.RCCD_FOLDER_SEPERATOR + ProtocolConstants.RCCD_FILE_INTERNAL_CERT_STORAGE_FOLDER + ProtocolConstants.RCCD_FOLDER_SEPERATOR + filename);
        if(!certPath.exists())
            throw new FileNotFoundException("File for url: " + url + " does not exist");
        FileInputStream fin = new FileInputStream(certPath);
        //New code end
        X509Certificate[] certificateChain = new X509Certificate[1];
        try {
            try {
                ks = KeyStore.getInstance("PKCS12", "BC");
                ks.load(fin, getKeyStorePassword(url).toCharArray());
                Enumeration<String> alias = ks.aliases();
                certificateChain[0] = (X509Certificate) ks.getCertificate(alias.nextElement());
            } finally {
                fin.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new CertificateInfo(certificateChain, ks, getKeyStorePassword(url));
    }

    /**
     * Saves a KeyStore to the internal storage (which is hidden to both the
     * user and other applications).<br>
     * <br>
     * <b>TODO: This is not a final implementation: you can store different
     * KeyStores (using different filenames), but this implementation only
     * remembers one password.</b>
     *
     * @param cert
     *            the certificate to write to disk
     *
     * @see KeyStore
     */

    protected void writeKeyStore(CertificateInfo cert, String passwordURL, String serviceName) {
        try {
            //String filename = getNsBaseServiceName(cert.getCertificateChain()[0]);
            String filename = serviceName;
            String certCommonPath = mContext.getFilesDir() + ProtocolConstants.RCCD_FOLDER_SEPERATOR+ ProtocolConstants.RCCD_FILE_INTERNAL_CERT_STORAGE_FOLDER;
            File certCommonFile = new File(certCommonPath);
            if (!certCommonFile.isDirectory()) {
                certCommonFile.mkdir();
            }
            certCommonFile = new File(certCommonPath + ProtocolConstants.RCCD_FOLDER_SEPERATOR + filename);
            if (certCommonFile.exists()) {
                certCommonFile.delete();
            }
            FileOutputStream fout =  new FileOutputStream(certCommonPath + ProtocolConstants.RCCD_FOLDER_SEPERATOR + filename);
            try {
                KeyStore ks = cert.getKeyStore();
                ks.store(fout, cert.getCertPassword().toCharArray());
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(passwordURL, cert.getCertPassword());
                editor.commit();
                RCCDFileUtil.e("KeyTalk","certificate added to system");
                RCCDFileUtil.updateNativeKeyStoreInstallationStatus(mContext,passwordURL,false);
            } finally {
                fout.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void createPFXFile(CertificateInfo cert, String serviceName) {
        try {
            //String filename = getNsBaseServiceName(cert.getCertificateChain()[0]);
            String filename = serviceName;
            String rccdCommonPath = mContext.getFilesDir() +ProtocolConstants.PFX_PATH;
            File rccdCommonFile = new File(rccdCommonPath);
            if (!rccdCommonFile.isDirectory()) {
                rccdCommonFile.mkdir();
            }
            String rccdFilePath = rccdCommonPath +ProtocolConstants.RCCD_FOLDER_SEPERATOR+filename+".pfx";
            rccdCommonFile = new File(rccdFilePath);
            if(rccdCommonFile.exists()) {
                rccdCommonFile.delete();
            }
            FileOutputStream fout = new FileOutputStream(rccdCommonFile);
            try {
                KeyStore ks = cert.getKeyStore();
                //	ks.store(fout, "12345".toCharArray());
                ks.store(fout, userDefinedKeyStorePassword.toCharArray());
                RCCDFileUtil.e("KeyTalk","Added certificate to native keytstore ");
            } finally {
                fout.close();
            }
        } catch (Exception e) {
            RCCDFileUtil.e("KeyTalk","native keytstore exception :"+e);
        }
    }

    protected void deleteCertificate(String url, String serviceName) {
        //String filename = getServiceNameFromUrl(url);
        String filename = serviceName;
        //New Code
        File f = new File(mContext.getFilesDir() + ProtocolConstants.RCCD_FOLDER_SEPERATOR + ProtocolConstants.RCCD_FILE_INTERNAL_CERT_STORAGE_FOLDER + ProtocolConstants.RCCD_FOLDER_SEPERATOR + filename);
        if (f.exists()) {
            Boolean success = f.delete();
            Log.v(ProtocolConstants.TAG, "delete file succeeded: " + success.toString());
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(url, "");
            editor.commit();
            RCCDFileUtil.updateNativeKeyStoreInstallationStatus(mContext,url,false);
        }
    }

    private int getPracticalEndTimePercentage(String serviceName, Date notBefore, Date notAfter) {
       // IniResponseData parsedIni = new IniResponseData();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String certValidity = pref.getString(AppConstants.CERT_VALIDITY+serviceName,"");
        String certValidPercent = pref.getString(AppConstants.CERT_VALID_PERCENT+serviceName,"");
        int value=75;
        if(certValidPercent!=null&&certValidPercent!="")
            value=Integer.parseInt(certValidPercent);
        else if(certValidity!=null&&certValidity!="")
        {
            long validityWindow = notAfter.getTime() - notBefore.getTime();
            long before=Integer.parseInt(certValidity.substring(0,certValidity.length()-1))*100000;
            value=Integer.parseInt(String.valueOf(before/validityWindow));
        }
        return 100-value;
    }

    /**
     * Calculates the "practical end time" of a certificate, which is defined
     * as: "certificate start time" + "x% from certificate validity window"
     * e.g.: if a certificate is valid from 12:00 - 14:00 and the practical end
     * percentage is 75%, the certificate can be used until 13:30 (12:00 + (75%
     * * 2:00))
     *
     * @param validFrom
     *            time on which the certificate starts to be valid
     * @param validUntil
     *            time on which the certificate stops to be valid
     * @param percentage
     *            practical end percentage of certificate
     * @return practical end time
     */
    private Date calcPracticalEndTime(Date validFrom, Date validUntil,
                                      int percentage) {
        long validityWindow = validUntil.getTime() - validFrom.getTime();
        return new Date(validFrom.getTime()
                + (validityWindow * percentage / 100));
    }


/*	private String getServiceNameFromUrl(String url) {
		String serviceName = null;
		if (KeyTalkUtils.equalUrls(url, "https://spdemo.keytalk.com/")) {
			//serviceName = "IOS_DEMO_SERVICE";
			serviceName = "KT_AD";
		}
		return serviceName;
	}*/

/*
    private String getNsBaseServiceName(X509Certificate cert) {
        String nsBaseUrlOID = "2.16.840.1.113730.1.2";
        byte[] value = cert.getExtensionValue(nsBaseUrlOID);
        assert(value != null);
        ByteArrayInputStream inStream = new ByteArrayInputStream(value);
        ASN1InputStream asnInputStream = new ASN1InputStream(inStream);
        DERObject derObject;
        try {
            derObject = asnInputStream.readObject();
            assert(derObject instanceof DEROctetString);

            DEROctetString derOctetString = (DEROctetString) derObject;

            ByteArrayInputStream inStream1 = new ByteArrayInputStream(derOctetString.getOctets());
            ASN1InputStream asnInputStream1 = new ASN1InputStream(inStream1);
            DERObject derObject1 = asnInputStream1.readObject();

            assert(derObject1 instanceof DERIA5String);
            DERIA5String s = DERIA5String.getInstance(derObject1);
            return s.getString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
*/

    private String getKeyStorePassword(String passwordURL) {
        return mSettings.getString(passwordURL, "");
    }

    protected void setUserConfig(){}
    protected void setUca(){}
    protected void setPca(){}
    protected void setSca(){}
    protected void setIcon(){}
    protected void setLogo(){}

    protected String getUserDefinedKeyStorePassword() {
        return userDefinedKeyStorePassword;
    }

    protected static void setUserDefinedKeyStorePassword(String keyStorePasswords) {
        userDefinedKeyStorePassword = keyStorePasswords;
    }
}

