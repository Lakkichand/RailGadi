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
import com.railgadi.beans.RescheduledTrainBean;

import java.util.List;

public class RescheduledListAdapterNew extends BaseAdapter {

    private Context context ;
    private List<RescheduledTrainBean.RescheduledData> data ;

    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public RescheduledListAdapterNew(Context context, List<RescheduledTrainBean.RescheduledData> data) {

        this.context = context ;
        this.data = data ;

        this.inflater = LayoutInflater.from(context) ;
    }

    public void changeDataSet(List<RescheduledTrainBean.RescheduledData> list) {
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

            convertView             =   inflater.inflate(R.layout.rescheduled_list_row, null) ;

            holder.trainName        =   (TextView) convertView.findViewById(R.id.train_name) ;
            holder.trainNumber      =   (TextView) convertView.findViewById(R.id.train_number) ;
            holder.trainType        =   (TextView) convertView.findViewById(R.id.train_type) ;
            holder.startTime        =   (TextView) convertView.findViewById(R.id.start_time) ;
            holder.resTime          =   (TextView) convertView.findViewById(R.id.reschedule_time) ;
            holder.delayTime        =   (TextView) convertView.findViewById(R.id.delay_hrs) ;
            holder.srcStation       =   (TextView) convertView.findViewById(R.id.src_name) ;
            holder.destStation      =   (TextView) convertView.findViewById(R.id.dest_name) ;

            convertView.setTag(holder) ;

        } else {

            holder                  =   (ViewHolder) convertView.getTag() ;
        }

        RescheduledTrainBean.RescheduledData bean = (RescheduledTrainBean.RescheduledData) getItem(position) ;

        holder.trainName.setText(bean.trainName) ;
        holder.trainNumber.setText(bean.trainNo) ;
        holder.trainType.setText(bean.trainType.toUpperCase()) ;
        holder.startTime.setText(bean.startDateTime) ;
        holder.resTime.setText(bean.reschDateTime) ;
        holder.delayTime.setText(bean.delay) ;
        holder.srcStation.setText(bean.srcStationName) ;
        holder.destStation.setText(bean.destStationName) ;

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView ;
    }

    private static class ViewHolder {
        TextView trainName, trainNumber, trainType, startTime, resTime, delayTime, srcStation, destStation ;
    }

}
