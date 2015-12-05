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
import com.railgadi.beans.DivertedTrainBean.DivertedData;

import java.util.List;

public class DivertedListAdapterNew extends BaseAdapter {

    private Context context ;
    private List<DivertedData> data ;

    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public DivertedListAdapterNew(Context context, List<DivertedData> data) {

        this.context = context ;
        this.data = data ;

        this.inflater = LayoutInflater.from(context) ;
    }

    public void changeDataSet(List<DivertedData> list) {
        this.data   =   list ;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size() ;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null ;

        if(convertView == null) {

            holder                  =   new ViewHolder() ;

            convertView             =   inflater.inflate(R.layout.diverted_list_row, null) ;

            holder.trainName        =   (TextView) convertView.findViewById(R.id.train_name) ;
            holder.trainNumber      =   (TextView) convertView.findViewById(R.id.train_number) ;
            holder.trainType        =   (TextView) convertView.findViewById(R.id.train_type) ;
            holder.startTime        =   (TextView) convertView.findViewById(R.id.start_time) ;
            holder.divertedFrom     =   (TextView) convertView.findViewById(R.id.diverted_from) ;
            holder.divertedTo       =   (TextView) convertView.findViewById(R.id.diverted_to) ;
            holder.srcStation       =   (TextView) convertView.findViewById(R.id.src_name) ;
            holder.destStation      =   (TextView) convertView.findViewById(R.id.dest_name) ;

            convertView.setTag(holder) ;

        } else {

            holder                  =   (ViewHolder) convertView.getTag() ;
        }

        DivertedData bean = (DivertedData) getItem(position) ;

        holder.trainName.setText(bean.trainName) ;
        holder.trainNumber.setText(bean.trainNo) ;
        holder.trainType.setText(bean.trainType.toUpperCase()) ;
        holder.startTime.setText(bean.startTime) ;
        holder.divertedFrom.setText(bean.divertedFrom) ;
        holder.divertedTo.setText(bean.divertedTo) ;
        holder.srcStation.setText(bean.srcStationName) ;
        holder.destStation.setText(bean.destStationName) ;

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView ;
    }

    private static class ViewHolder {
        TextView trainName, trainNumber, trainType, startTime, divertedFrom, divertedTo, srcStation, destStation ;
    }

}
