package com.keytalk.nextgen5.core.security;

import com.keytalk.nextgen5.core.Data;

/*
 * Class  :  RCCDFileImportCommand
 * Description : An subclass which pass downloadd rccd request to communication thread
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class RCCDFileImportCommand<R extends Data, S extends Data> extends BaseCommand<R, S> {

    protected RCCDFileImportCommand(Class<? extends R> requestClass, Class<? extends S> responseClass) {
        super(requestClass, responseClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute() {
        if (request.getAction() == ServiceActions.MLS_RCCD_FILE_IMPORT_FROM_SERVER) {
            RCCDFileRequestData requestData = (RCCDFileRequestData) request.getData();
            boolean isSucess = processRCCDFileImportRequest(requestData.getURL());
            if (isSucess) {
                response.setMessageType(prepareResponseType());
                response.setAction(request.getAction());
                addRCCDFileToInternalStorage(response);
                processResponseData(response);
            }
        }
    }

    private ResponseType prepareResponseType() {
        RCCDFileResponseData rccdFileImportResponseData = (RCCDFileResponseData) response.getData();
        if (rccdFileImportResponseData != null) {
            ResponseHeader responseHeader = rccdFileImportResponseData.getResponseHeader();
            if (responseHeader != null && responseHeader.isSuccess()) {
                return ResponseType.RESPONSE_SUCCESS;
            }
            return ResponseType.RESPONSE_FAILURE;
        }
        return ResponseType.RESPONSE_FAILURE;
    }

    private void addRCCDFileToInternalStorage(Response<S> responseData) {
        if (responseData != null && responseData.getMessageType().equals( ResponseType.RESPONSE_SUCCESS)) {
            RCCDFileResponseData rccdFileImportResponseData = (RCCDFileResponseData) responseData.getData();
            if (rccdFileImportResponseData.getResponseHeader().isSuccess()) {
                // Successfully received the RCCD file from server
                RCCDFileUtil rccdFileUtil = new RCCDFileUtil();
                RCCDFileUtil.e("RCCDFileImportCommand","Downloaded RCCD file status 1:"+ rccdFileImportResponseData.getFileName());
                String[] fileOperationStatus = rccdFileUtil.addRCCDFileToInternalStorage(contex, rccdFileImportResponseData.getEmailRCCDFileInputStream(), rccdFileImportResponseData.getFileName());
                rccdFileImportResponseData.getResponseHeader().setFileOperationStatus(fileOperationStatus[0]);
                RCCDFileUtil.e("RCCDFileImportCommand","Downloaded RCCD file status :"+ fileOperationStatus[0]);
                if (fileOperationStatus[1] != null && !fileOperationStatus[1].isEmpty() && !fileOperationStatus[1].equals("")) {
                    rccdFileImportResponseData.setIiniResponseData(rccdFileUtil.readRCCDFile(contex, fileOperationStatus[1]));
                } else {
                    rccdFileImportResponseData.setIiniResponseData(rccdFileUtil.readRCCDFile(contex, rccdFileImportResponseData.getFileName()));
                }
            }
        }
    }

}
