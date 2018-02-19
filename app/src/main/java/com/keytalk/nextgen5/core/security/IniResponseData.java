package com.keytalk.nextgen5.core.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/*
 * Class  :  IniResponseData
 * Description : Support class various ini file operations
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

@SuppressWarnings("unchecked")
public class IniResponseData
{

    private LinkedHashMap<String, Object> ini = null;
    private static final String INI_FILE_PROVIDER_TEXT="Providers";
    private static final String INI_FILE_PROVIDER_SERVICE_TEXT="Services";
    private static final String INI_FILE_PROVIDER_NAME_TEXT="Name";
    private static final String INI_FILE_SERVICE_NAME_TEXT="Name";
    /**
     * Create LinkedHashMap(Used to maintain Insertion order iteration)
     */
    protected IniResponseData()
    {
        i=0;
        SPACE="";
        ini = new LinkedHashMap<String, Object>();
    }

    /***
     *
     * @param key
     *            : key
     * @param object
     *            : object may be Array,INI Structure Itself,String value
     */
    protected void put(String key, Object object)
    {
        ini.put(key, object);
    }

    /***
     * get value as Object
     *
     * @param key
     * @return String
     */
    private Object get(String key) {

        try {
            return ini.get(key);
        } catch(Exception e) {
            return null;
        }


    }

    /***
     * get value as String
     *
     * @param key
     * @return String
     */
    public String getStringValue(String key) {
        try {
            return get(key).toString();
        } catch(Exception e) {
            return null;
        }
    }

    /***
     * get value as Byte
     *
     * @param key
     * @return Byte
     */
    protected Byte getByteValue(String key) {
        return Byte.valueOf(get(key).toString());
    }

    /***
     * get value as Integer
     *
     * @param key
     * @return Integer
     */
    protected Integer getIntegerValue(String key) {
        return Integer.valueOf(get(key).toString());
    }

    /***
     * get value as Double
     *
     * @param key
     * @return Double
     */
    protected Double getDoubleValue(String key) {
        return Double.valueOf(get(key).toString());
    }

    /***
     * get value as Float
     *
     * @param key
     * @return Float
     */
    protected Float getFloatValue(String key) {
        return Float.valueOf(get(key).toString());
    }

    /***
     * get value as Ini
     *
     * @param key
     * @return Ini
     */
    protected IniResponseData getIniValue(String key) {
        return ((IniResponseData) get(key));
    }

    /***
     * get value as StringArray
     *
     * @param key
     * @return StringArray
     */
    protected ArrayList<String> getStringArrayValue(String key) {
        return (ArrayList<String>) get(key);
    }

    /***
     * get value as IniArray
     *
     * @param key
     * @return IniArray
     */

    public ArrayList<IniResponseData> getIniArrayValue(String key) {
        return (ArrayList<IniResponseData>) get(key);
    }

    //Making static as we are using these in recursive method, method is referring to old value in scope
    private static String SPACE="";
    private static int i=0;
    /***
     *Add Space while printing IniResponseData class
     */
    protected void addSpace()
    {
        if(i > 1)
        {
            SPACE=SPACE+"  ";
        }
        i++;
    }

    /***
     *Remove Space while printing IniResponseData class
     */
    protected void removeSpace()
    {
        try
        {   //Even though we have guard we are putting catch here
            if(SPACE.length()>=2)
            {
                SPACE=SPACE.substring(0,SPACE.length()-2);
            }
            if(i>0)
            {
                i--;
            }
        }catch (Exception e)
        {

        }

    }

    /****
     * This recursive function which will print INI file from IniResponseData class
     */
    @Override
    public String toString()
    {

        addSpace();
        String ini_string="";
        if(i!=0)
            ini_string="\n";
        ini_string+= SPACE+"{";
        addSpace();
        Iterator<Entry<String, Object>> it = ini.entrySet().iterator();

        while (it.hasNext())
        {
            Map.Entry<String, Object> pairs = (Map.Entry<String, Object>) it.next();
            Object value = pairs.getValue();
            if (value instanceof IniResponseData)
            {
                ini_string += "\n"+SPACE+ pairs.getKey() + "=\n" + value+";";
            }else if(value instanceof ArrayList<?>)
            {
                Object obj=((ArrayList<?>)value).get(0);

                if(obj instanceof IniResponseData)
                {
                    //replace is not working because of recursive method its removing all the occurrences '[' ']' in final result so this long process,to string calls
                    //again toString() of internal IniResponseData class
                    //String don't have setChar so we need use long run SubString();
                    //Use String builder
                    StringBuilder local =new StringBuilder(value.toString());
                    int index1 = local.indexOf("[");
                    local.setCharAt(index1,' ');
                    int index2 = local.lastIndexOf("]");
                    local.setCharAt(index2,' ');
                    ini_string += "\n"+SPACE + pairs.getKey() + "=(\n" +local+");\n";

                }else if(obj instanceof String)
                {

                    StringBuilder local =new StringBuilder(value.toString());
                    int index1 = local.indexOf("[");
                    local.setCharAt(index1,' ');
                    int index2 = local.lastIndexOf("]");
                    local.setCharAt(index2,' ');

                    String splitData[]=local.toString().split(",");

                    String formatedString="";

                    for(String indivisival: splitData)
                    {
                        if(formatedString.trim().length()==0)
                        {
                            formatedString="\""+indivisival.trim()+"\"";
                        }else
                        {
                            formatedString+=",\""+indivisival.trim()+"\"";
                        }
                    }

                    ini_string += "\n"+SPACE + pairs.getKey() + "=["+formatedString+"];";
                }

            } else if(value instanceof String)
            {
                String local=(String)value;
                if(com.keytalk.nextgen5.core.security.StringUtil.isBoolean(local))
                {
                    ini_string += "\n"+SPACE+ pairs.getKey() + "=" + local+";";

                }else if(com.keytalk.nextgen5.core.security.StringUtil.isNumeric(local))
                {
                    ini_string += "\n"+SPACE+ pairs.getKey() + "=" + local+";";
                }else
                {
                    ini_string += "\n"+SPACE+ pairs.getKey() + "=\"" +local.trim()+"\";";
                }


            }
        }
        removeSpace();
        ini_string+="\n"+SPACE+"}";
        removeSpace();

        return  ini_string;
    }

    /***
     * Return the iniData in Byte form so can be saved to file or InputStream
     * @return
     */
    protected byte[] getByteData()
    {
        byte byteData[]=null;

        //String don't have setChar so we need use long run SubString();
        //Use String builder

        StringBuilder local=new StringBuilder(toString());
        int index1 = local.indexOf("{");
        local.setCharAt(index1,' ');
        int index2 = local.lastIndexOf("}");
        local.setCharAt(index2,' ');
        String byteString=local.toString().trim();

        byteData=byteString.getBytes();
        return byteData;
    }


    /***
     * Replaces uri with passes String,We can implement same method for all values.
     * @param index which Service need to set
     * @param uri Uri to be replaced
     */
    protected void setServiceUri(int index,String uri)
    {
        ArrayList<IniResponseData> provider_list=getIniArrayValue(INI_FILE_PROVIDER_TEXT);
        if(provider_list != null && provider_list.size() > 0)
        {
            ArrayList<IniResponseData> services_list=provider_list.get(0).getIniArrayValue(INI_FILE_PROVIDER_SERVICE_TEXT);
            if(services_list != null && services_list.size() > index)
            {
                IniResponseData service=services_list.get(index);
                String URI=service.getStringValue("Uri");
                service.put("Uri",uri);
            }
        }
    }

    protected void setServerUri(String ServerUrl) {
        ArrayList<IniResponseData> providerlist=getIniArrayValue(INI_FILE_PROVIDER_TEXT);
        if(providerlist!=null && providerlist.size() > 0) {
            IniResponseData providerData = providerlist.get(0);
            if(providerData!=null)  {
                providerData.put("Server",ServerUrl);
            }
        }
    }
    /***
     * Add the User Name to the list of Usernames
     *
     * @param index
     *            which Service need to be updated
     * @param username
     *            to be updated
     */
    protected boolean addUserName(int index, String username) {
        ArrayList<IniResponseData> provider_list = getIniArrayValue(INI_FILE_PROVIDER_TEXT);
        if (provider_list != null && provider_list.size() > 0) {
            ArrayList<IniResponseData> services_list = provider_list.get(0)
                    .getIniArrayValue(INI_FILE_PROVIDER_SERVICE_TEXT);
            if (services_list != null && services_list.size() > index) {
                IniResponseData service = services_list.get(index);
                ArrayList<String> usernameList = service.getStringArrayValue("Users");// getStringArrayValue(String// key)
                if (usernameList != null && !usernameList.contains(username)) {
                    boolean isNew = true;
                    for(int i = 0; i<usernameList.size(); i++ ) {
                        if(usernameList.get(i).trim().equals(username.trim())) {
                            isNew = false;
                            break;
                        }
                    }
                    if(isNew) {
                        RCCDFileUtil.e("IniResponseDate","Adding new user name :"+username);
                        usernameList.add(username);
                        if (usernameList.size() > 1) {
                            int blankIndex = -1;
                            if ((blankIndex = indexOfBlank(usernameList)) != -1) {
                                usernameList.remove(blankIndex);
                            }
                        }
                        service.put("Users", usernameList);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Contains method is not working some cases so using this fix fo find blank
     * string
     *
     * @param locallist
     * @return
     */
    private int indexOfBlank(ArrayList<String> locallist) {
        int index = -1;
        for (int i = 0; i < locallist.size(); i++) // To get index using old
        // style for loop
        {
            if (locallist.get(i).trim().length() == 0) {
                index = i;
                break;
            }
        }
        return index;
    }

    protected String getProviderName() {
        String providerName = null;
        ArrayList<IniResponseData> providerList = getIniArrayValue(INI_FILE_PROVIDER_TEXT);
        if (providerList != null && providerList.size() > 0) {
            IniResponseData providerData = providerList.get(0);
            providerName = providerData.getStringValue(INI_FILE_PROVIDER_NAME_TEXT);
        }
        return providerName;
    }

    /***
     * @return Name of the All Services Present as the ArrayList
     */
    protected ArrayList<String> getServiceNames() {
        ArrayList<String> serviceNames=null;
        ArrayList<IniResponseData> providerList = getIniArrayValue(INI_FILE_PROVIDER_TEXT);
        if (providerList != null && providerList.size() > 0) {
            IniResponseData providerData = providerList.get(0);
            if (providerData != null) {
                ArrayList<IniResponseData> servicesList = providerData
                        .getIniArrayValue(INI_FILE_PROVIDER_SERVICE_TEXT);
                if (servicesList != null && servicesList.size()>0) {
                    serviceNames = new ArrayList<String>();
                    for(IniResponseData serviceData:servicesList)
                        if (serviceData != null) {
                            String	serviceName = serviceData
                                    .getStringValue(INI_FILE_SERVICE_NAME_TEXT);
                            if(serviceName != null && serviceName.trim().length()> 0)
                                serviceNames.add(serviceName.trim());
                        }
                }
            }
        }
        return serviceNames;
    }
    /***
     * provide all Services present in current .ini as ArrayList Here We are
     * assuming that that only one provider exist per .ini file.
     *
     * @return all Services Present in current RCCD
     */
    protected ArrayList<IniResponseData> getServiceList() {
        ArrayList<IniResponseData> servicesList = null;
        ArrayList<IniResponseData> providerList = getIniArrayValue(INI_FILE_PROVIDER_TEXT);
        if (providerList != null && providerList.size() > 0) {
            IniResponseData providerData = providerList.get(0);
            servicesList = providerData
                    .getIniArrayValue(INI_FILE_PROVIDER_SERVICE_TEXT);
            ;
        }
        return servicesList;
    }

    /***
     * @update the service at the particular index
     */
    protected boolean setServiceAt(int index,IniResponseData serviceData) {
        ArrayList<IniResponseData> providerList = getIniArrayValue(INI_FILE_PROVIDER_TEXT);
        if (providerList != null && providerList.size() > 0) {
            IniResponseData providerData = providerList.get(0);
            if (providerData != null) {
                ArrayList<IniResponseData> servicesList = providerData
                        .getIniArrayValue(INI_FILE_PROVIDER_SERVICE_TEXT);
                if (servicesList != null && servicesList.size() > index) {

                    servicesList.set(index,serviceData);
                    return true;
                }
            }
        }
        return false;
    }

    /***
     * @update add new service data to existing iniData
     */
    protected boolean addService(IniResponseData serviceData) {
        ArrayList<IniResponseData> providerList = getIniArrayValue(INI_FILE_PROVIDER_TEXT);
        if (providerList != null && providerList.size() > 0) {
            IniResponseData providerData = providerList.get(0);
            if (providerData != null) {
                ArrayList<IniResponseData> servicesList = providerData
                        .getIniArrayValue(INI_FILE_PROVIDER_SERVICE_TEXT);
                if(servicesList!=null)
                {
                    servicesList.add(serviceData);
                    return true;
                }
            }
        }
        return false;
    }
}
