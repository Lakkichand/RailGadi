package com.railgadi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.MiniTimeTableBean.TrainRoute;
import com.railgadi.fonts.AppFonts;

import java.util.List;

/**
 * Created by Vijay on 29-07-2015.
 */

public class LiveTrainStationAdapter extends BaseAdapter {

    private Context context ;
    private List<TrainRoute> allStations ;
    private LayoutInflater inflater ;

    public LiveTrainStationAdapter(Context context, List<TrainRoute> allStations) {

        this.context        =   context ;
        this.allStations    =   allStations ;
        this.inflater       =   (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return allStations.get(position);
    }

    @Override
    public int getCount() {
        return allStations.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null ;

        if(convertView == null) {

            holder                  =   new ViewHolder() ;

            convertView             =   inflater.inflate(R.layout.live_trains_station_row, null) ;

            holder.code             =   (TextView) convertView.findViewById(R.id.code) ;
            holder.name             =   (TextView) convertView.findViewById(R.id.name) ;

            holder.code.setTypeface(AppFonts.getRobotoLight(context));
            holder.name.setTypeface(AppFonts.getRobotoLight(context));

            convertView.setTag(holder) ;

        } else {

            holder                  =   (ViewHolder) convertView.getTag() ;
        }

        TrainRoute route = (TrainRoute) getItem(position);

        holder.code.setText(route.stationCode);
        holder.name.setText(route.stationName);

        return convertView ;
    }

    private static class ViewHolder {
        TextView code, name ;
    }
}
