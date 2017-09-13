package com.keytalk.nextgen5.core.security;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Class  :  KeyTalkCredentials
 * Description : Holding all user credential details while client-server communication
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class KeyTalkCredentials implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private boolean usernameRequested = false;
    private String  username = "";

    private boolean passwordRequested = false;
    private String  password = "";
    private String passwordText = "";

    private boolean pinRequested = false;
    private String  pin = "";

    private String  challenge = "";
    private String  response = "";
    private boolean  responseRequested = false;


    private String  hardwareFormula = "";
    private String  hardwareSignature = "";
    private boolean hardwareFormulaRequested = false;

    private boolean newPasswordRequested = false;
    private String  newPassword = "";

    private boolean challengeRequested = false;
    private String[] challengeData = null;

    private String[] serviceURIArray = null;

    private boolean resolveServieURISRequested = false;
    private boolean calcServieURIDigestSRequested = false;

    private boolean newAuthReqChallengeRequested = false;
    private ArrayList<String[]> newAuthReqChallengeData = null;
    private String  newResponse = "";



    private int expiryDate = -1;


    /**
     * Create a new KMCredentials object with the given field values
     */
    protected KeyTalkCredentials(boolean usernameRequested, boolean passwordRequested, boolean pinRequested,
                                 String challenge, String hardwareFormula) {
        this.usernameRequested = usernameRequested;
        this.passwordRequested = passwordRequested;
        this.pinRequested      = pinRequested;
        this.challenge         = challenge;
        this.hardwareFormula   = hardwareFormula;
    }

    /**
     * Create a new KMCredentials object from the given set of string credential requirements
     */
    protected KeyTalkCredentials(Iterable<String> requestedCredentials) {
        for (String c : requestedCredentials) {
            if (c.equals(ProtocolConstants.CRED_USERID)) {
                usernameRequested = true;
                continue;
            }
            //System.out.println("Password prompt   : "+c);

            if (c.equals(ProtocolConstants.CRED_PASSWD)) {
                passwordRequested = true;
                continue;
            } else if(c.startsWith(ProtocolConstants.CRED_PASSWD)) {
                passwordRequested = true;
                passwordText = new String(Base64.decode(c.substring(7), Base64.DEFAULT));
                //System.out.println("Password prompt2   : "+c+","+passwordText);
                continue;
            }

			/*if (c.equals(CRED_PASSWD) || c.startsWith(CRED_PASSWD)) {
				passwordRequested = true;
				continue;
			}*/


            if (c.equals(ProtocolConstants.CRED_PIN)) {
                pinRequested = true;
                continue;
            }
            if (c.startsWith(ProtocolConstants.CRED_RESPONSE)) {
                challenge = c.substring(ProtocolConstants.CRED_RESPONSE.length());
                continue;
            }
            if (c.startsWith(ProtocolConstants.CRED_HWSIG)) {
                hardwareFormula = c.substring(ProtocolConstants.CRED_HWSIG.length());
                continue;
            }

            Log.w("KMCredentials", "Unsupported credential key: " + c);
        }
    }

    protected KeyTalkCredentials(String requestedCredentials) {
        Log.e("KMCredentials", "Constructor ");
        try {
            JSONObject serverMessageJSONObject = new JSONObject(requestedCredentials);
            if(serverMessageJSONObject.has("credential-types")) {
                JSONArray credArray = serverMessageJSONObject.getJSONArray("credential-types");
                for(int i=0; i<credArray.length(); i++) {
                    if (credArray.getString(i).equals(ProtocolConstants.CRED_USERID)) {
                        usernameRequested = true;
                    }
                    if (credArray.getString(i).equals(ProtocolConstants.CRED_PASSWD)) {
                        passwordRequested = true;
                    }
                    if (credArray.getString(i).equals(ProtocolConstants.CRED_PIN)) {
                        pinRequested = true;
                    }
                    if (credArray.getString(i).equals(ProtocolConstants.CRED_RESPONSE)) {
                        responseRequested = true;
                    }
                    if (credArray.getString(i).equals(ProtocolConstants.CRED_HWSIG)) {
                        hardwareFormulaRequested = true;
                    }
                }
            }
            if(serverMessageJSONObject.has(ProtocolConstants.hwsig_formula)) {
                hardwareFormula = serverMessageJSONObject.getString(ProtocolConstants.hwsig_formula);
            }
            if(serverMessageJSONObject.has(ProtocolConstants.password_prompt)) {
                passwordText = serverMessageJSONObject.getString(ProtocolConstants.password_prompt);
            }
            if(serverMessageJSONObject.has(ProtocolConstants.resolve_service_uris)) {
                resolveServieURISRequested = serverMessageJSONObject.getBoolean(ProtocolConstants.resolve_service_uris);
            }
            if(serverMessageJSONObject.has(ProtocolConstants.calc_service_uris_digest)) {
                calcServieURIDigestSRequested = serverMessageJSONObject.getBoolean(ProtocolConstants.calc_service_uris_digest);
            }
            if(serverMessageJSONObject.has(ProtocolConstants.service_uris)) {
                JSONArray credArray = serverMessageJSONObject.getJSONArray(ProtocolConstants.service_uris);
                serviceURIArray = new String[credArray.length()];
                for(int i=0; i<credArray.length(); i++) {
                    serviceURIArray[i] = credArray.getString(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("KMCredentials", "Val "+usernameRequested+","+passwordRequested+","+pinRequested+","+responseRequested+","+hardwareFormulaRequested+","+passwordText+","+resolveServieURISRequested+","+calcServieURIDigestSRequested);
        Log.e("KMCredentials", "hardwareFormula "+hardwareFormula);
        for(int i=0; i<serviceURIArray.length; i++) {
            Log.e("KMCredentials", "uri "+serviceURIArray[i]);
        }

    }

    public String[] getServiceURIArray() {
        return serviceURIArray;
    }
    /**
     * Returns whether any of the input fields are requested
     */
    protected boolean isAnyRequested() {
        return isUsernameRequested() || isPasswordRequested() || isPinRequested() || isHardwareSignatureRequested() || isResponseRequested();
    }

    /**
     * Returns whether user input is required
     *
     * User input is required for everything except hardware signature
     */
    protected boolean isUserInputRequested() {
        return isUsernameRequested() || isPasswordRequested() || isPinRequested() || isResponseRequested();
    }

    protected boolean isUsernameRequested() {
        return usernameRequested;
    }

    protected boolean isPasswordRequested() {
        return passwordRequested;
    }

    protected boolean isChallengeRequested() {
        return challengeRequested;
    }

    protected void setChallengeRequested(boolean challengeRequested) {
        this.challengeRequested = challengeRequested;
    }

    protected String[] getChallengeData() {
        return challengeData;
    }

    protected void setChallengeData(String[] challengeData) {
        this.challengeData = challengeData;
    }

    protected boolean isNewPasswordRequested() {
        return newPasswordRequested;
    }

    protected void setNewPasswordRequested(boolean newPasswordRequested) {
        this.newPasswordRequested = newPasswordRequested;
    }


    protected boolean isPinRequested() {
        return pinRequested;
    }

    protected boolean isResponseRequested() {
        return !challenge.equals("");
    }

    protected String getChallenge() {
        return challenge;
    }

    protected boolean isHardwareSignatureRequested() {
        //return !hardwareFormula.equals("");
        return hardwareFormulaRequested;
    }

    protected String getHardwareFormula() {
        return hardwareFormula;
    }

    protected String getUsername() {
        return username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    protected String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected String getNewPassword() {
        return newPassword;
    }

    protected void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    protected String getPin() {
        return pin;
    }

    protected void setPin(String pin) {
        this.pin = pin;
    }

    protected String getResponse() {
        return response;
    }

    protected void setResponse(String response) {
        this.response = response;
    }

    protected String getHardwareSignature() {
        return hardwareSignature;
    }

    protected void setHardwareSignature(String hardwareSignature) {
        this.hardwareSignature = hardwareSignature;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((challenge == null) ? 0 : challenge.hashCode());
        result = prime * result
                + ((hardwareFormula == null) ? 0 : hardwareFormula.hashCode());
        result = prime
                * result
                + ((hardwareSignature == null) ? 0 : hardwareSignature
                .hashCode());
        result = prime * result
                + ((password == null) ? 0 : password.hashCode());
        result = prime * result + (passwordRequested ? 1231 : 1237);
        result = prime * result + ((pin == null) ? 0 : pin.hashCode());
        result = prime * result + (pinRequested ? 1231 : 1237);
        result = prime * result
                + ((response == null) ? 0 : response.hashCode());
        result = prime * result
                + ((username == null) ? 0 : username.hashCode());
        result = prime * result + (usernameRequested ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KeyTalkCredentials other = (KeyTalkCredentials) obj;
        if (challenge == null) {
            if (other.challenge != null)
                return false;
        } else if (!challenge.equals(other.challenge))
            return false;
        if (hardwareFormula == null) {
            if (other.hardwareFormula != null)
                return false;
        } else if (!hardwareFormula.equals(other.hardwareFormula))
            return false;
        if (hardwareSignature == null) {
            if (other.hardwareSignature != null)
                return false;
        } else if (!hardwareSignature.equals(other.hardwareSignature))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (passwordRequested != other.passwordRequested)
            return false;
        if (pin == null) {
            if (other.pin != null)
                return false;
        } else if (!pin.equals(other.pin))
            return false;
        if (pinRequested != other.pinRequested)
            return false;
        if (response == null) {
            if (other.response != null)
                return false;
        } else if (!response.equals(other.response))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        if (usernameRequested != other.usernameRequested)
            return false;
        return true;
    }

    protected int getExpiryDate() {
        return expiryDate;
    }

    protected void setExpiryDate(int expiryDate) {
        this.expiryDate = expiryDate;
    }

    protected String getPasswordText() {
        return passwordText;
    }

    protected void setPasswordText(String passwordText) {
        this.passwordText = passwordText;
    }

    protected boolean isNewAuthReqChallengeRequested() {
        return newAuthReqChallengeRequested;
    }

    protected void setNewAuthReqChallengeRequested(boolean newAuthReqChallengeRequested) {
        this.newAuthReqChallengeRequested = newAuthReqChallengeRequested;
    }

    protected ArrayList<String[]> getNewAuthReqChallengeData() {
        return newAuthReqChallengeData;
    }

    protected void setNewAuthReqChallengeData(ArrayList<String[]> newAuthReqChallengeData) {
        this.newAuthReqChallengeData = newAuthReqChallengeData;
    }

    protected String getNewResponse() {
        return newResponse;
    }

    protected void setNewResponse(String newResponse) {
        this.newResponse = newResponse;
    }
}
