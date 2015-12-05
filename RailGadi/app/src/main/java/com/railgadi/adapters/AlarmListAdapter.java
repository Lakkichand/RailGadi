package com.railgadi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.AlarmDataBean;
import com.railgadi.fonts.AppFonts;

import java.util.List;

public class AlarmListAdapter extends BaseAdapter {

    private Context context ;
    private List<AlarmDataBean> savedAlarm ;

    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public AlarmListAdapter(Context context, List<AlarmDataBean> savedAlarms) {

        this.context        =   context ;
        this.savedAlarm     =   savedAlarms ;
        this.inflater       =   (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }

    public void changeDataSet(List<AlarmDataBean> savedAlarms) {
        this.savedAlarm     =   savedAlarms ;
        notifyDataSetChanged() ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public Object getItem(int position) {
        return savedAlarm.get(position);
    }

    @Override
    public int getCount() {
        return savedAlarm.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder ;

        if(convertView == null) {

            holder              =   new ViewHolder() ;

            convertView         =   inflater.inflate(R.layout.alarm_main_list_row, null) ;

            holder.cityIcon     =   (ImageView) convertView.findViewById(R.id.city_icon) ;
            holder.alarmBell    =   (ImageView) convertView.findViewById(R.id.alarm_bell_icon) ;

            holder.code         =   (TextView) convertView.findViewById(R.id.station_code) ;
            holder.name         =   (TextView) convertView.findViewById(R.id.station_name) ;
            holder.beforeTime   =   (TextView) convertView.findViewById(R.id.before_time) ;
            holder.trainNumber  =   (TextView) convertView.findViewById(R.id.train_number) ;

            holder.code.setTypeface(AppFonts.getRobotoLight(context));
            holder.name.setTypeface(AppFonts.getRobotoLight(context));
            holder.beforeTime.setTypeface(AppFonts.getRobotoLight(context));
            holder.trainNumber.setTypeface(AppFonts.getRobotoLight(context));

            convertView.setTag(holder);

        } else {
            holder  =   (ViewHolder) convertView.getTag() ;
        }

        AlarmDataBean adb = savedAlarm.get(position) ;

        holder.trainNumber.setText(adb.getTrainNumber());
        holder.code.setText(adb.getStationCode());
        holder.name.setText(adb.getStationName());

        if(adb.getDistance() < 1) {
            holder.beforeTime.setText("On Station");
        } else {
            holder.beforeTime.setText("Before: "+((int)adb.getDistance())+" km");
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView ;
    }

    private class ViewHolder {

        ImageView cityIcon, alarmBell ;
        TextView code, name, beforeTime, trainNumber ;
    }
}
