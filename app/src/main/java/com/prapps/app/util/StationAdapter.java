package com.prapps.app.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.prapps.app.activities.R;
import com.prapps.app.activities.SearchActivity;
import com.prapps.app.model.Station;

/**
 * Created by pratik on 24/2/17.
 */

public class StationAdapter extends ArrayAdapter<Station> {
    int index;
    Context ctx;
    public StationAdapter(Context context, int index, Station[] stations) {
        super(context, index, stations);
        this.index = index;
        this.ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        TextView txtTo = null;
        if (convertView == null) {
            LayoutInflater inflater = ((SearchActivity) ctx).getLayoutInflater();
            convertView = inflater.inflate(index, parent, false);

            txtTo = (TextView) convertView.findViewById(R.id.txtTo);

            convertView.setTag(txtTo);
        } else {
            txtTo = (TextView) convertView.getTag();
        }

        Station item = getItem(position);
        if (item!= null) {
            // My layout has only one TextView
            // do whatever you want with your string and long
            txtTo.setText(item.getName());
        }

        return convertView;
    }
}
