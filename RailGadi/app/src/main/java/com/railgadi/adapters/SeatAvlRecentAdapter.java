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
import com.railgadi.beans.TimeTableNewBean;

import java.util.List;

/**
 * Created by Vijay on 21-11-2015.
 */

public class SeatAvlRecentAdapter extends BaseAdapter {

    private Context context ;
    private List<TimeTableNewBean> allSaved ;
    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public SeatAvlRecentAdapter(Context context, List<TimeTableNewBean> allSaved) {

        this.context            =   context ;
        this.allSaved           =   allSaved ;
        this.inflater           =   LayoutInflater.from(context) ;
    }

    public void removeItem(int position) {
        allSaved.remove(position) ;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return allSaved.size() ;
    }

    @Override
    public Object getItem(int position) {
        return allSaved.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder   =   null ;

        if(convertView == null) {

            holder                  =   new ViewHolder() ;

            convertView             =   inflater.inflate(R.layout.timetable_recent_row, null) ;

            holder.number           =   (TextView) convertView.findViewById(R.id.train_number) ;
            holder.name             =   (TextView) convertView.findViewById(R.id.train_name) ;

            convertView.setTag(holder) ;

        } else {

            holder                  =   (ViewHolder) convertView.getTag() ;
        }

        TimeTableNewBean bean      =   (TimeTableNewBean) getItem(position) ;

        holder.name.setText(bean.getTrainName()) ;
        holder.number.setText(bean.getTrainNumber()) ;

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView ;
    }

    private static class ViewHolder {
        TextView number, name ;
    }
}
