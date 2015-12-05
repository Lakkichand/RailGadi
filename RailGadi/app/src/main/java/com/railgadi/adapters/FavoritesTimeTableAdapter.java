package com.railgadi.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.fragments.RouteMapTabsFragment;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.preferences.TimeTablePreferencesHandler;

import java.util.List;

public class FavoritesTimeTableAdapter extends BaseAdapter {

    private Context context ;

    private List<TimeTableNewBean> data ;

    private TimeTablePreferencesHandler timeTablePreferencesHandler ;

    private IFragReplaceCommunicator comm ;

    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public FavoritesTimeTableAdapter(Context context, List<TimeTableNewBean> data) {

        this.context                        =   context ;
        this.comm                           =   (IFragReplaceCommunicator) context ;
        this.data                           =   data ;
        this.timeTablePreferencesHandler    =   new TimeTablePreferencesHandler((Activity)context) ;
        this.inflater                       =   (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }

    public void changeDataSet(List<TimeTableNewBean> data) {
        this.data = data ;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null ;

        if(convertView == null) {

            holder                  =   new ViewHolder() ;

            convertView             =   inflater.inflate(R.layout.timetable_recent_row, null) ;

            holder.number           =   (TextView) convertView.findViewById(R.id.train_number) ;
            holder.name             =   (TextView) convertView.findViewById(R.id.train_name) ;
            holder.superLayout      =   (LinearLayout) convertView.findViewById(R.id.super_layout) ;

            holder.superLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int clicked = (Integer) v.getTag() ;
                    String trainNumber = ((TimeTableNewBean)getItem(clicked)).getTrainNumber() ;

                    if(timeTablePreferencesHandler.isAlreadyExists(trainNumber)) {
                        TimeTableNewBean bean = timeTablePreferencesHandler.getTimeTableFromPref(trainNumber) ;
                        comm.respond(new RouteMapTabsFragment(bean));
                    }
                }
            });

            convertView.setTag(holder);
        }
        else {
            holder            =   (ViewHolder) convertView.getTag() ;
        }

        holder.superLayout.setTag(position) ;

        TimeTableNewBean bean = (TimeTableNewBean) getItem(position) ;

        holder.number.setText(bean.getTrainNumber());
        holder.name.setText(bean.getTrainName());

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }

    private static class ViewHolder {
        TextView number, name ;
        LinearLayout superLayout ;
    }
}
