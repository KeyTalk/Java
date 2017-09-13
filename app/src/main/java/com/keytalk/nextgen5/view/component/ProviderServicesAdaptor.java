package com.keytalk.nextgen5.view.component;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keytalk.nextgen5.R;
import com.keytalk.nextgen5.core.security.IniResponseData;
import com.keytalk.nextgen5.core.security.RCCDFileData;
import com.keytalk.nextgen5.view.util.AppConstants;

import java.util.ArrayList;

/*
 * Class  :  ProviderServicesAdaptor
 * Description : Adapter class for showing service from RCCD in the list view
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class ProviderServicesAdaptor extends BaseExpandableListAdapter {
    private Context _context;
    private ArrayList<RCCDFileData> providerServiceList;
    TextView urlChild;

    public ProviderServicesAdaptor(Context context,ArrayList<RCCDFileData> providerServiceList) {
        this._context = context;
        this.providerServiceList = providerServiceList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        if (childPosititon == 0) {
            return null;
            //add code for the url
        } else {
            IniResponseData iniResponseData = providerServiceList.get(groupPosition).getServiceData();
            IniResponseData providerData = iniResponseData.getIniArrayValue(AppConstants.INI_FILE_PROVIDER_TEXT).get(0);
            IniResponseData servicedata = providerData.getIniArrayValue(AppConstants.INI_FILE_PROVIDER_SERVICE_TEXT).get(childPosititon - 1);
            return servicedata;
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (childPosition == 0) {
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.providerservice_list_item, null);
            }
            LinearLayout borderlayout = (LinearLayout) convertView.findViewById(R.id.keytalk_border_layout);
            borderlayout.setBackgroundResource(R.drawable.servicelist_background_url);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) borderlayout.getLayoutParams();
            params.setMargins(0, 0, 0, 5); //substitute parameters for left, top, right, bottom
            borderlayout.setLayoutParams(params);
            //add code here
            ImageView keytalk_icon = (ImageView) convertView.findViewById(R.id.service_icon);
            keytalk_icon.setVisibility(View.GONE);
            urlChild = (TextView) convertView.findViewById(R.id.keytalk_listitem);
            urlChild.setGravity(Gravity.CENTER);
            IniResponseData iniResponseData = providerServiceList.get(groupPosition).getServiceData();
            IniResponseData providerData = iniResponseData.getIniArrayValue(AppConstants.INI_FILE_PROVIDER_TEXT).get(0);
            urlChild.setText(providerData.getStringValue("Server"));
            return convertView;
        } else {
            Log.d("KEYTALK", "groupPosition" + groupPosition + "childPosition=" + childPosition);
            final IniResponseData childText = (IniResponseData) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.providerservice_list_item, null);
            }
            LinearLayout borderlayout = (LinearLayout) convertView.findViewById(R.id.keytalk_border_layout);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) borderlayout.getLayoutParams();
            params.setMargins(0, 0, 0, 0); //substitute parameters for left, top, right, bottom
            borderlayout.setLayoutParams(params);
            if (getChildrenCount(groupPosition) == 2) {
                borderlayout.setBackgroundResource(R.drawable.servicelist_background_middle);
            } else if (childPosition - 1 == 0) {
                borderlayout.setBackgroundResource(R.drawable.servicelist_background_top);
            } else if (isLastChild) {
                borderlayout.setBackgroundResource(R.drawable.servicelist_background_bottom);
            } else {
                borderlayout.setBackgroundResource(R.drawable.servicelist_background);
            }
            TextView txtListChild = (TextView) convertView.findViewById(R.id.keytalk_listitem);
            txtListChild.setText(childText.getStringValue(AppConstants.INI_FILE_SERVICE_NAME_TEXT));
            txtListChild.setGravity(Gravity.LEFT);
            ImageView keytalk_icon = (ImageView) convertView.findViewById(R.id.service_icon);
            keytalk_icon.setImageBitmap(providerServiceList.get(groupPosition).getProviderIcon());
            keytalk_icon.setVisibility(View.VISIBLE);
            return convertView;
        }

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        IniResponseData providerServiceData = this.providerServiceList.get(groupPosition).getServiceData();
        IniResponseData providerData = providerServiceData.getIniArrayValue(AppConstants.INI_FILE_PROVIDER_TEXT).get(0);
        ArrayList<IniResponseData> serviceData = providerData.getIniArrayValue(AppConstants.INI_FILE_PROVIDER_SERVICE_TEXT);
        return (serviceData.size() + 1);//add extra one count for the url
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.providerServiceList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.providerServiceList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = providerServiceList.get(groupPosition)
                .getServiceData().getIniArrayValue(AppConstants.INI_FILE_PROVIDER_TEXT).get(0)
                .getStringValue(AppConstants.INI_FILE_PROVIDER_NAME_TEXT);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(
                    R.layout.providerservice_list_group, null);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.group_header_text);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

}
