package com.grampower.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by samdroid on 7/6/17.
 */

public class GridViewAdapter extends BaseAdapter {

    String[] menuList;
    int[]  iconList;
     Context context;
    public GridViewAdapter(Context con, String[] menu, int[] icons) {
        context = con;
        menuList=menu;
        iconList=icons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            grid=new View(context);
            grid=inflater.inflate(R.layout.grid_child_view,null);
            ImageView mIcon=(ImageView)grid.findViewById(R.id.icon);
            TextView mField=(TextView)grid.findViewById(R.id.textField);
             mIcon.setImageResource(iconList[position]);
             mField.setText(menuList[position]);

        } else {
            grid = (View) convertView;
        }
        return grid;
    }

    @Override
    public int getCount() {
        return menuList.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}
