package com.keytalk.nextgen5.core.security;

import com.keytalk.nextgen5.core.KeyTalkCertificateConsumer;
import com.keytalk.nextgen5.core.KeyTalkCredentialsConsumer;
import com.keytalk.nextgen5.core.KeyTalkExpiredCredentialConsumer;
import com.keytalk.nextgen5.core.security.KeyTalkProtocol.AuthResult;

/*
 * Class  :  KeyTalkAsyncCertificateRequest
 * Description : An support class for handle request's to server
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class KeyTalkAsyncCertificateRequest {

    private final KeyTalkProtocol protocol;
    private final KeyTalkCertificateConsumer client;
    private boolean started = false;
    private String selectedUserName = null;
    private String[] serverMsg = null;
    private String[] serviceURIArray = null;


    protected KeyTalkAsyncCertificateRequest(KeyTalkProtocol protocol,KeyTalkCertificateConsumer client) {
        this.protocol = protocol;
        this.client = client;
    }

    /**
     * Initiate the certificate retrieval
     * Will call back to the KeyTalkProtocolClient instance provided in the
     * constructor as the retrieval happens, and/or more information is needed.
     */
    protected void start() {
        assert (!started);
        started = true;
        new HandshakeUntilAuthentication().execute();
    }

    // Hand Shaking process Starting here

    private class HandshakeUntilAuthentication extends ErrorHandlingAsyncTask<Void, Void, Void> {
        @Override
        protected Void realDoInBackground(Void... params) throws Exception {
            RCCDFileUtil.e("KeyTalk","Phase 1 Hello request is getting starting here");
            protocol.phase1HandshakeHello();
            RCCDFileUtil.e("KeyTalk","Phase1 Handshake request is getting starting here");
            protocol.phase1Handshake();
            return null;
        }

        @Override
        protected void onSuccess(Void result) throws Exception {
            RCCDFileUtil.e("KeyTalk","Phase1 Handshaking completed sucessfully");
            new RequestAuthenticationChallenge().execute();
        }

        @Override
        protected void onError(Throwable t) {
            RCCDFileUtil.e("KeyTalk","Phase1 hand shaking has : "+t.getMessage());
            client.errorOccurred(t);
        }
    }

    // ----------------------------------------------------------------------

    private class RequestAuthenticationChallenge extends ErrorHandlingAsyncTask<Void, Void, KeyTalkCredentials> implements KeyTalkCredentialsConsumer {
        @Override
        protected KeyTalkCredentials realDoInBackground(Void... params) throws Exception {
            RCCDFileUtil.e("KeyTalk","Phase 2 (authentication) started");
            serviceURIArray = null;
            String reqCredentials = protocol.phase2AuthRequirements();
            return new KeyTalkCredentials(reqCredentials);
        }

        @Override
        protected void onSuccess(KeyTalkCredentials result) throws Exception {
            RCCDFileUtil.e("KeyTalk","Phase 2 Auth-Requirements started");
            serviceURIArray = result.getServiceURIArray();
            if (result.isAnyRequested()) {
                RCCDFileUtil.e("KeyTalk","Credential Requested");
                // Request credentials on main thread
                client.requestCredentials(result, this);
            } else {
                // No credentials requested -- immediately continue with the next step
                supplyCredentials(result);
            }
        }

        @Override
        protected void onError(Throwable t) {
            RCCDFileUtil.e("KeyTalk","OnError called in Phase 2 Auth-Requirements :"+t.getMessage());
            client.errorOccurred(t);
        }

        @Override
        public void supplyCredentials(KeyTalkCredentials credentials) {
            // Start the next part of the chain
            RCCDFileUtil.e("KeyTalk","supplyCredentials() got credentials from user");
            selectedUserName = credentials.getUsername();
            new SupplyCredentials().execute(credentials);
        }
    }

    // ----------------------------------------------------------------------

    private class SupplyCredentials extends ErrorHandlingAsyncTask<KeyTalkCredentials, Void, AuthResult>
            implements Runnable,KeyTalkExpiredCredentialConsumer {
        private KeyTalkCredentials suppliedCreds;

        @Override
        protected AuthResult realDoInBackground(KeyTalkCredentials... params) throws Exception {
            assert (params.length == 1);
            suppliedCreds = params[0];
            RCCDFileUtil.e("KeyTalk","Phase2 sending user credentials ");
            return protocol.phase2SupplyAuthentication(suppliedCreds);
        }

        @Override
        protected void onSuccess(AuthResult result) throws Exception {
            if (result.isOK()) {
				if(result.getExpirySeconds() >= 0 && result.getExpirySeconds() < 7 ) {
					RCCDFileUtil.e("KeyTalkAysncCertificate","SupplyCredentials sucessfully completed but user can reset password "+result.getExpirySeconds());
					suppliedCreds.setExpiryDate(result.getExpirySeconds());
					client.resetCredentialOption(suppliedCreds, this);
				} else {
					RCCDFileUtil.e("KeyTalk","SupplyCredentials sucessfully completed "+result.getExpirySeconds());
					new GetMessages().execute();
				}
            }
            if (result.isDelay()) {
                RCCDFileUtil.e("KeyTalk","Supy credentials completed, but send wrong credentials ");
                client.invalidCredentialsDelay(result.getSeconds(), this);
            }

            if (result.isLocked()) {
                RCCDFileUtil.e("KeyTalk","Authentication successfully completed but user got locked");
                throw new KeyTalkUserLockedOutException("User locked out of system");
            }
            if(result.isExpired()) {
                RCCDFileUtil.e("KeyTalk","Authentication successfully completed but user password got expired");
                suppliedCreds.setNewPasswordRequested(true);
                client.requestResetCredentials(suppliedCreds, this);
            }
            if(result.isChallenge()) {
                RCCDFileUtil.e("KeyTalk","Phase3 successfully completed but server sending challenge");
                suppliedCreds.setChallengeRequested(true);
                suppliedCreds.setChallengeData(result.getChallengeData());
                client.requestChallengeCredentials(suppliedCreds, this);
                //throw new KeyTalkUserLockedOutException("Authentication completed, but server required some challenges which not supporting in this android build");
            }
            if(result.isAuthReqChallenge()) {
                RCCDFileUtil.e("KeyTalk","Phase2 sucessfully completed challenge "+result.getAuthReqChallengeData());
                throw new KeyTalkUserLockedOutException("Authentication completed, but server required some challenges which not supporting in this android build");
            }
            assert (false); // Should never happen




			/*




			if(result.isChallenge()) {
				RCCDFileUtil.e("KeyTalk","Phase3 sucessfully completed but server sending challenge");
				suppliedCreds.setChallengeRequested(true);
				suppliedCreds.setChallengeData(result.getChallengeData());
				client.requestChallengeCredentials(suppliedCreds, this);
			}

			if(result.isAuthReqChallenge()) {
				RCCDFileUtil.e("KeyTalk","Phase3 sucessfully completed challenge "+result.getAuthReqChallengeData());
				String actualData = result.getAuthReqChallengeData();
				if (actualData.startsWith(ProtocolConstants.CRED_RESPONSE)) {
					actualData = actualData.substring(ProtocolConstants.CRED_RESPONSE.length());

				}
				String[] responseDataArray = actualData.split(":");
				String[] challengArray = responseDataArray[0].split(",");
				String[] responseArray = responseDataArray[1].split(",");
				for(int i = 0; i<challengArray.length;i++) {
					StringBuffer temBuffer = new StringBuffer();
					temBuffer =temBuffer.append(new String(Base64.decode(challengArray[i].split(ProtocolConstants.CRED_HASH)[0].getBytes(), Base64.NO_WRAP)).trim());
					if(challengArray[i].split(ProtocolConstants.CRED_HASH).length == 2) {
						temBuffer = temBuffer.append(ProtocolConstants.CRED_HASH);
						temBuffer =temBuffer.append(new String(Base64.decode(challengArray[i].split(ProtocolConstants.CRED_HASH)[1].getBytes(), Base64.NO_WRAP)).trim());
					}
					challengArray[i] = temBuffer.toString();
				}
				for(int i = 0; i<responseArray.length;i++) {
					StringBuffer temBuffer = new StringBuffer();
					temBuffer =temBuffer.append(new String(Base64.decode(responseArray[i].split("#")[0].getBytes(), Base64.NO_WRAP)).trim());
					if(responseArray[i].split("#").length == 2) {
						temBuffer = temBuffer.append("#");
						temBuffer =temBuffer.append(new String(Base64.decode(responseArray[i].split("#")[1].getBytes(), Base64.NO_WRAP)).trim());
					}
					responseArray[i] = temBuffer.toString();
				}

				ArrayList<String[]> challengeData = new ArrayList<String[]>(2);
				challengeData.add(challengArray);
				challengeData.add(responseArray);
				suppliedCreds.setNewAuthReqChallengeRequested(true);
				suppliedCreds.setNewAuthReqChallengeData(challengeData);
				client.requestChallengeCredentials(suppliedCreds, this);
			}
			assert (false); // Should never happen*/
        }

        @Override
        protected void onError(Throwable t) {
            RCCDFileUtil.e("KeyTalk","Phase2 getting error :"+t.getMessage());
            client.errorOccurred(t);
        }

        /**
         * This function will be invoked by the client after a delay challenge
         * Re-request an authentication challenge from the server.
         */
        @Override
        public void run() {
            RCCDFileUtil.e("KeyTalk","Phase2 request after delay ");
            new RequestAuthenticationChallenge().execute();
        }

        @Override
        public void supplyNewCredentials(KeyTalkCredentials credentials) {
            // TODO Auto-generated method stub
            // Start the next part of the chain
            RCCDFileUtil.e("KeyTalk","Phase3 credentials request with new credentials");
            selectedUserName = credentials.getUsername();
            new ExpiredCredentials().execute(credentials);
        }

        @Override
        public void isPasswordShouldReset(boolean isResetPassword) {
            // TODO Auto-generated method stub
            if(isResetPassword) {
                RCCDFileUtil.e("KeyTalk","Phase3 user request for password should change");
                suppliedCreds.setNewPasswordRequested(true);
                client.requestResetCredentials(suppliedCreds, this);
            } else {
                RCCDFileUtil.e("KeyTalk","Phase3 credentials password change postponed");
                new GetMessages().execute();
            }

        }

        @Override
        public void supplyChallengeCredentials(KeyTalkCredentials credentials) {
            // TODO Auto-generated method stub
            RCCDFileUtil.e("KeyTalk","Phase2 credentials request with new challenge");
            selectedUserName = credentials.getUsername();
            new SupplyCredentials().execute(credentials);
        }
    }


    //New password async task

    private class ExpiredCredentials extends ErrorHandlingAsyncTask<KeyTalkCredentials, Void, AuthResult> {
        private KeyTalkCredentials suppliedCreds;

        @Override
        protected AuthResult realDoInBackground(KeyTalkCredentials... params)
                throws Exception {
            assert (params.length == 1);
            suppliedCreds = params[0];
            RCCDFileUtil.e("KeyTalk","Phase3 reset password request async");
            return protocol.phase2ResetExpiredPassword(suppliedCreds);
        }

        @Override
        protected void onSuccess(AuthResult result) throws Exception {
            if (result.isLocked()) {
                RCCDFileUtil.e("KeyTalk","ExpiredCredentials request sucess isLocked ");
                throw new KeyTalkUserLockedOutException("User locked out of system");
            }

            if (result.isDelay()) {
                RCCDFileUtil.e("KeyTalk","ExpiredCredentials request sucess delay ");
                client.requestResetCredentialsDelay(result.getSeconds());
            }

            if (result.isOK()) {
                RCCDFileUtil.e("KeyTalk","ExpiredCredentials request sucess");
                new RequestAuthenticationChallenge().execute();
            }

            assert (false); // Should never happen
        }

        @Override
        protected void onError(Throwable t) {
            RCCDFileUtil.e("KeyTalk","ExpiredCredentials request error : "+t.getMessage());
            client.errorOccurred(t);
        }
    }

    //New Password async task over
    //Get All messages from server

    private class GetMessages extends ErrorHandlingAsyncTask<Void, Void, String[]> {
        @Override
        protected String[] realDoInBackground(Void... params) throws Exception {
            RCCDFileUtil.e("KeyTalk","Phase3 getting messages");
            return protocol.getPhase3LastMessageFromServer();
        }

        @Override
        protected void onSuccess(String[] msgresult) throws Exception {
            RCCDFileUtil.e("KeyTalk","Phase3 GetMessages Sucess");
            serverMsg = msgresult;
            new HandshakeAfterAuthentication().execute();
        }

        @Override
        protected void onError(Throwable t) {
            RCCDFileUtil.e("KeyTalk","Phase3 GetMessages Error :"+t.getMessage());
            client.errorOccurred(t);
        }
    }


    //Message Async over

    // ----------------------------------------------------------------------

    private class HandshakeAfterAuthentication extends ErrorHandlingAsyncTask<Void, Void, CertificateInfo> {
        @Override
        protected CertificateInfo realDoInBackground(Void... params)
                throws Exception {
            // FIXME: Implement phase 4
            RCCDFileUtil.e("KeyTalk","Phase3 certificate request starting here");
            return protocol.getCertificate();
        }

        @Override
        protected void onSuccess(CertificateInfo result) throws Exception {
            if(serviceURIArray != null && serviceURIArray.length > 0) {
                result.setURL(true);
                result.setUrlString(serviceURIArray[0]);
            }
           client.certificateRetrieved(result ,selectedUserName,serverMsg);
        }

        @Override
        protected void onError(Throwable t) {
            RCCDFileUtil.e("KeyTalk","Phase5 Certiciate error : "+t.getMessage());
            client.errorOccurred(t);
        }
    }
    // ----------------------------------------------------------------------
}

