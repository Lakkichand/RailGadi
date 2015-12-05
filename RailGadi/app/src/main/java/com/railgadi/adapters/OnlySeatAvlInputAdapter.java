package com.railgadi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.TimeTableNewBean.AllStations;

import java.util.List;

public class OnlySeatAvlInputAdapter extends BaseAdapter {

    private Context context ;
    private List<AllStations> data ;

    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public OnlySeatAvlInputAdapter(Context context, List<AllStations> data) {

        this.context = context ;
        this.data = data ;

        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }

    @Override
    public int getCount() {
        return data.size() ;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView     =   inflater.inflate(R.layout.station_code_name_listrow, null) ;
        }

        TextView code   = (TextView) convertView.findViewById(R.id.station_code);
        TextView name   = (TextView) convertView.findViewById(R.id.station_name);

        AllStations station = data.get(position) ;

        code.setText(station.stnCode) ;
        name.setText(station.stnName) ;

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }
}
