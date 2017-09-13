package com.keytalk.nextgen5.core.security;

import com.keytalk.nextgen5.core.Data;

import java.io.Serializable;
import java.security.PublicKey;

/*
 * Class  :  SelectedRCCDFileRequestData
 * Description : Class which holding selected rccd file request details
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class SelectedRCCDFileRequestData implements Data, Serializable {

    private static final long serialVersionUID = 1L;

    private String rccdFolderPath;

    private String configVersion;
    private String latestProvider;
    private String latestService;
    private String providersName;
    private String providersContentVersion;
    private String providersCAs;
    private String providersLogLevel;
    private String providersServer;
    private String servicesCertValidPercent;
    private String servicesCertFormat;
    private String servicesCertChain;
    private String servicesName;
    private String servicesUri;
    private String servicesProxySettings;
    private String servicesKeyAgreement;
    private String servicesUsers;
    private String servicesNewUri = null;

    private PublicKey serverCommunicationPublicKey;
    private IniResponseData iniResponseData;
    private int groupPosition;
    private int childPosition;
    /**
     * @return the configVersion
     */
    protected String getConfigVersion() {
        return configVersion;
    }
    /**
     * @param configVersion the configVersion to set
     */
    protected void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }
    /**
     * @return the latestProvider
     */
    protected String getLatestProvider() {
        return latestProvider;
    }
    /**
     * @param latestProvider the latestProvider to set
     */
    protected void setLatestProvider(String latestProvider) {
        this.latestProvider = latestProvider;
    }
    /**
     * @return the latestService
     */
    protected String getLatestService() {
        return latestService;
    }
    /**
     * @param latestService the latestService to set
     */
    protected void setLatestService(String latestService) {
        this.latestService = latestService;
    }
    /**
     * @return the providersName
     */
    protected String getProvidersName() {
        return providersName;
    }
    /**
     * @param providersName the providersName to set
     */
    protected void setProvidersName(String providersName) {
        this.providersName = providersName;
    }
    /**
     * @return the providersContentVersion
     */
    protected String getProvidersContentVersion() {
        return providersContentVersion;
    }
    /**
     * @param providersContentVersion the providersContentVersion to set
     */
    protected void setProvidersContentVersion(String providersContentVersion) {
        this.providersContentVersion = providersContentVersion;
    }
    /**
     * @return the providersCAs
     */
    protected String getProvidersCAs() {
        return providersCAs;
    }
    /**
     * @param providersCAs the providersCAs to set
     */
    protected void setProvidersCAs(String providersCAs) {
        this.providersCAs = providersCAs;
    }
    /**
     * @return the providersLogLevel
     */
    protected String getProvidersLogLevel() {
        return providersLogLevel;
    }
    /**
     * @param providersLogLevel the providersLogLevel to set
     */
    protected void setProvidersLogLevel(String providersLogLevel) {
        this.providersLogLevel = providersLogLevel;
    }
    /**
     * @return the providersServer
     */
    protected String getProvidersServer() {
        return providersServer;
    }
    /**
     * @param providersServer the providersServer to set
     */
    protected void setProvidersServer(String providersServer) {
        this.providersServer = providersServer;
    }
    /**
     * @return the servicesCertValidPercent
     */
    protected String getServicesCertValidPercent() {
        return servicesCertValidPercent;
    }
    /**
     * @param servicesCertValidPercent the servicesCertValidPercent to set
     */
    protected void setServicesCertValidPercent(String servicesCertValidPercent) {
        this.servicesCertValidPercent = servicesCertValidPercent;
    }
    /**
     * @return the servicesCertFormat
     */
    protected String getServicesCertFormat() {
        return servicesCertFormat;
    }
    /**
     * @param servicesCertFormat the servicesCertFormat to set
     */
    protected void setServicesCertFormat(String servicesCertFormat) {
        this.servicesCertFormat = servicesCertFormat;
    }
    /**
     * @return the servicesCertChain
     */
    protected String getServicesCertChain() {
        return servicesCertChain;
    }
    /**
     * @param servicesCertChain the servicesCertChain to set
     */
    protected void setServicesCertChain(String servicesCertChain) {
        this.servicesCertChain = servicesCertChain;
    }
    /**
     * @return the servicesName
     */
    protected String getServicesName() {
        return servicesName;
    }
    /**
     * @param servicesName the servicesName to set
     */
    protected void setServicesName(String servicesName) {
        this.servicesName = servicesName;
    }
    /**
     * @return the servicesUri
     */
    protected String getServicesUri() {
        return servicesUri;
    }
    /**
     * @param servicesUri the servicesUri to set
     */
    protected void setServicesUri(String servicesUri) {
        this.servicesUri = servicesUri;
    }
    /**
     * @return the servicesProxySettings
     */
    protected String getServicesProxySettings() {
        return servicesProxySettings;
    }
    /**
     * @param servicesProxySettings the servicesProxySettings to set
     */
    protected void setServicesProxySettings(String servicesProxySettings) {
        this.servicesProxySettings = servicesProxySettings;
    }
    /**
     * @return the servicesKeyAgreement
     */
    protected String getServicesKeyAgreement() {
        return servicesKeyAgreement;
    }
    /**
     * @param servicesKeyAgreement the servicesKeyAgreement to set
     */
    protected void setServicesKeyAgreement(String servicesKeyAgreement) {
        this.servicesKeyAgreement = servicesKeyAgreement;
    }
    /**
     * @return the servicesUsers
     */
    protected String getServicesUsers() {
        return servicesUsers;
    }
    /**
     * @param servicesUsers the servicesUsers to set
     */
    protected void setServicesUsers(String servicesUsers) {
        this.servicesUsers = servicesUsers;
    }
    /**
     * @return the rccdFolderPath
     */
    protected String getRccdFolderPath() {
        return rccdFolderPath;
    }
    /**
     * @param rccdFolderPath the rccdFolderPath to set
     */
    protected void setRccdFolderPath(String rccdFolderPath) {
        this.rccdFolderPath = rccdFolderPath;
    }
    /**
     * @return the serverCommunicationPublicKey
     */
    protected PublicKey getServerCommunicationPublicKey() {
        return serverCommunicationPublicKey;
    }
    /**
     * @param serverCommunicationPublicKey the serverCommunicationPublicKey to set
     */
    protected void setServerCommunicationPublicKey(
            PublicKey serverCommunicationPublicKey) {
        this.serverCommunicationPublicKey = serverCommunicationPublicKey;
    }
    /**
     * @return the servicesNewUri
     */
    protected String getServicesNewUri() {
        return servicesNewUri;
    }
    /**
     * @param servicesNewUri the servicesNewUri to set
     */
    protected void setServicesNewUri(String servicesNewUri) {
        this.servicesNewUri = servicesNewUri;
    }
    protected IniResponseData getIniResponseData() {
        return iniResponseData;
    }
    protected void setIniResponseData(IniResponseData iniResponseData) {
        this.iniResponseData = iniResponseData;
    }
    protected int getGroupPosition() {
        return groupPosition;
    }
    protected void setGroupPosition(int groupPosition) {
        this.groupPosition = groupPosition;
    }
    protected int getChildPosition() {
        return childPosition;
    }
    protected void setChildPosition(int childPosition) {
        this.childPosition = childPosition;
    }
}

