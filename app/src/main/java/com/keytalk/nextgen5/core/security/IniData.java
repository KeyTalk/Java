package com.keytalk.nextgen5.core.security;

import java.util.ArrayList;

/*
 * Class  :  IniData
 * Description : Support class for rccd file systems
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class IniData {

    protected ArrayList<IniResponseData> iniResponseData;
    protected ArrayList<String> iniFolderName;

    protected IniData(ArrayList<String> iniFolderName,	ArrayList<IniResponseData> iniResponseData) {
        this.iniFolderName = iniFolderName;
        this.iniResponseData = iniResponseData;
    }

    /**
     * @return the iniResponseData
     */
    protected ArrayList<IniResponseData> getIniResponseData() {
        return iniResponseData;
    }

    /**
     * @return the iniFolderName
     */
    protected ArrayList<String> getIniFolderName() {
        return iniFolderName;
    }

    protected String getFileNameAtIndex(int index) {
        if (iniFolderName != null && iniFolderName.size() > index) {
            return iniFolderName.get(index);
        }
        return null;
    }

}
