package com.keytalk.nextgen5.core.security;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.keytalk.nextgen5.core.CommunicationCommand;
import com.keytalk.nextgen5.core.Data;
import com.keytalk.nextgen5.core.LocalHandler;

import java.io.InputStream;

/*
 * Class  :  BaseCommand
 * Description : Class which handling all request to server through thread queue
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class BaseCommand<R extends Data, S extends Data> implements CommunicationCommand {

    protected Request<R> request;
    protected Response<S> response;
    protected Handler handler;
    protected long delayInMillis = 0;
    protected boolean putAtFrontOfQueue = false;
    private Class<? extends R> requestClass;
    private Class<? extends S> responseClass;
    private boolean handleUsingsThread = true;
    protected Context contex;

    public void execute() {
    };

    protected void notifySuccess() {
        Message msg = Message.obtain((Handler) handler, 1, response);
        if (handleUsingsThread) {
            handler.sendMessage(msg);
           // RCCDAuthenticationCommand.storeRequest();
        } else {
            ((LocalHandler) handler).handleLocalMessage(response);

        }
    }

    protected void notifyError() {
        Message msg = Message.obtain((Handler) handler, 1, response);
        handler.sendMessage(msg);
    }

    @SuppressWarnings("unchecked")
    protected BaseCommand(Class<? extends R> requestClass,
                          Class<? extends S> responseClass) {
        this.requestClass = requestClass;
        this.responseClass = responseClass;
        request = new Request<R>();
        response = new Response<S>();
        request.setData((R) getInstance(this.requestClass));
        response.setData((S) getInstance(this.responseClass));
    }

    protected Data processResponseData(Response<S> response) {
        this.response = response;
        this.response.setAction(request.getAction());
        this.response.setMessage("");
        if (this.response.getMessageType() == ResponseType.RESPONSE_SUCCESS ||
                this.response.getMessageType() == ResponseType.AUTH_RESPONCE_REQUIRE_CREDENTIALS||
                this.response.getMessageType() == ResponseType.AUTH_RESPONCE_RELOAD_PAGE ||
                this.response.getMessageType() == ResponseType.AUTH_REQUEST_DELAY) {
            notifySuccess();
        } else {
            notifyError();
        }
        return this.response.getData();
    }

    protected Object getInstance(Class<?> aClass) {
        try {
            if (!aClass.isInterface()) {
                return aClass.newInstance();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    protected void setHandleUsingsThread(boolean handleUsingsThread) {
        this.handleUsingsThread = handleUsingsThread;
    }

    protected boolean isHandleUsingsThread() {
        return handleUsingsThread;
    }

    @SuppressWarnings("unchecked")
    protected boolean processRCCDFileImportRequest(String requestURL) {
        try {
            InputStream httpResponse = null;
            ServiceDataSource processor = new ServiceDataSource();
            httpResponse = processor.processRCCDFileImportRequest(requestURL, contex);
            if (httpResponse != null) {
                String[] path = requestURL.split(SecurityConstants.FRONT_SLASH);
                String rccdFileName = path[path.length - 1];
                RCCDFileResponseData rccdDFileImportResponseData = new RCCDFileResponseData();
                rccdDFileImportResponseData.setFileName(rccdFileName);
                rccdDFileImportResponseData.setEmailRCCDFileInputStream(httpResponse);
                ResponseHeader responseHeader = new ResponseHeader();
                responseHeader.setSuccess(true);
                rccdDFileImportResponseData.setResponseHeader(responseHeader);
                response.setData((S) rccdDFileImportResponseData);
                RCCDFileUtil.e("BaseCommand","RCCD request successful");
                return true;
            } else {
                RCCDFileUtil.e("BaseCommand","RCCD request failed ");
                response.setMessageType(ResponseType.RESPONSE_FAILURE);
                response.setAction(request.getAction());
                response.setMessage(null);
                notifyError();
                return false;
            }
        } catch (ServiceException e) {
            response.setMessageType(ResponseType.RESPONSE_FAILURE);
            RCCDFileUtil.e("BaseCommand","RCCD request failed :"+response.getMessageType());
            response.setAction(request.getAction());
            response.setMessage(e.getLocalizedMessage());
            notifyError();
            return false;
        } catch (Exception e) {
            RCCDFileUtil.e("BaseCommand","RCCD request failed exception :"+e);
            response.setMessageType(ResponseType.RESPONSE_FAILURE);
            response.setAction(request.getAction());
            response.setMessage(e.getLocalizedMessage());
            notifyError();
            return false;
        }
    }

}
