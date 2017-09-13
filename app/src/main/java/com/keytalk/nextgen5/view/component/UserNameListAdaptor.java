package com.keytalk.nextgen5.view.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.keytalk.nextgen5.R;

/*
 * Class  :  UserNameListAdaptor
 * Description : Adapter class for showing drop down list of saved user names  in user screen
 *
 * Created by : KeyTalk IT Security BV on 2017
 * All rights reserved @ keytalk.com
 */

public class UserNameListAdaptor extends ArrayAdapter<String>
{
    private Context _context;
    private String[] userNamesArray;

    public UserNameListAdaptor(Context _context, int resource, String[] userNamesArray)
    {
        super(_context,resource,userNamesArray);
        this.userNamesArray=userNamesArray;
        this._context=_context;
    }
    /***
     * Get the number of users present
     */
    @Override
    public int getCount()
    {
        if(userNamesArray!=null)
            return userNamesArray.length;
        return 0;
    }

    /***
     * Return Item as the string position is Index of the item in the array
     */
    @Override
    public String getItem(int position)
    {
        String userName=null;
        if(userNamesArray!=null && userNamesArray.length > position)//to avoid array index out of bound exception
        {
            userName=userNamesArray[position];
        }
        return userName;
    }

    /***
     * Returns the view of the Item according to the position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.usernamelist_item, null);
        }
        if (userNamesArray.length == 1)
        {
            convertView.setBackgroundResource(R.drawable.servicelist_background_middle);
        } else if (position == 0)
        {
            convertView
                    .setBackgroundResource(R.drawable.servicelist_background_top);
        } else if (position == getCount()-1) {
            convertView
                    .setBackgroundResource(R.drawable.servicelist_background_bottom);
        } else {
            convertView
                    .setBackgroundResource(R.drawable.servicelist_background);
        }
        TextView textview = (TextView) convertView.findViewById(R.id.username_listitem);
        textview.setText(userNamesArray[position]);
        return convertView;

    }

}
