package com.railgadi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.RouteChildBean;
import com.railgadi.beans.RouteGroupBean;
import com.railgadi.fonts.AppFonts;

import java.util.List;

public class TimeTableExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<RouteGroupBean> groupList;


    public TimeTableExpandableAdapter(Context context, List<RouteGroupBean> groupList) {

        this.context = context;
        this.groupList = groupList;

    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return groupList.get(groupPosition).getChildList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return groupList.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {

        RouteGroupBean rgb = (RouteGroupBean) getGroup(groupPosition);

        GroupHolder holder = null ;

        if (view == null) {

            holder = new GroupHolder() ;

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.train_time_table_group_row, null);

            holder.heading = (TextView) view.findViewById(R.id.time_table_heading) ;
            holder.heading.setTypeface(AppFonts.getRobotoMedium(context));
            view.setTag(holder);

        } else {
            holder = (GroupHolder) view.getTag() ;
        }

        holder.heading.setText("DAY "+rgb.getDayGroup());

        return view ;
    }

    class GroupHolder {
        TextView heading ;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {

        RouteChildBean rcb = (RouteChildBean) getChild(groupPosition, childPosition);

        ChildHolder holder = null ;

        if (view == null) {

            holder = new ChildHolder() ;

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.train_timetable_child_row, null);

            holder.arrival      =   (TextView) view.findViewById(R.id.time_table_arrival) ;
            holder.departure    =   (TextView) view.findViewById(R.id.time_table_departure) ;
            holder.stName       =   (TextView) view.findViewById(R.id.time_table_station) ;
            holder.stCode       =   (TextView) view.findViewById(R.id.time_table_station_code) ;
            holder.distance     =   (TextView) view.findViewById(R.id.time_table_distance) ;
            holder.platform     =   (TextView) view.findViewById(R.id.time_table_platform) ;
            holder.adt          =   (TextView) view.findViewById(R.id.time_table_adt) ;
            holder.disclaimer   =   (LinearLayout) view.findViewById(R.id.disclaimer_historical) ;

            holder.arrival.setTypeface(AppFonts.getRobotoMedium(context));
            holder.departure.setTypeface(AppFonts.getRobotoMedium(context));
            holder.stCode.setTypeface(AppFonts.getRobotoMedium(context));
            holder.adt.setTypeface(AppFonts.getRobotoMedium(context));
            holder.stName.setTypeface(AppFonts.getRobotoMedium(context));
            holder.distance.setTypeface(AppFonts.getRobotoMedium(context));
            holder.platform.setTypeface(AppFonts.getRobotoMedium(context));

            view.setTag(holder);

        } else {
            holder = (ChildHolder) view.getTag() ;
        }

        holder.arrival.setText(rcb.getArrival());
        holder.departure.setText(rcb.getDeparture());
        holder.stName.setText(rcb.getStationName());
        holder.stCode.setText(rcb.getStationCode());
        holder.adt.setText(rcb.getAdt());
        holder.distance.setText(rcb.getDistance()+" Km");
        holder.platform.setText(rcb.getPlatform());

        holder.adt.setTextColor(rcb.getAdtColor());

        if(rcb.isArrivalSelected()) {
            holder.arrival.setBackgroundColor(context.getResources().getColor(R.color.red));
            holder.arrival.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.arrival.setBackground(null);
            holder.arrival.setTextColor(context.getResources().getColor(R.color.black));
        }

        if(rcb.isDepartureSelected()) {
            holder.departure.setBackgroundColor(context.getResources().getColor(R.color.red));
            holder.departure.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.departure.setBackground(null);
            holder.departure.setTextColor(context.getResources().getColor(R.color.black));
        }

        if(groupPosition == getGroupCount()-1 &&  childPosition == getChildrenCount(groupPosition)-1 ) {
            holder.disclaimer.setVisibility(View.VISIBLE);
        } else {
            holder.disclaimer.setVisibility(View.GONE);
        }

        /*if(rcb.isMain()) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }*/

        return view;
    }

    class ChildHolder {
        TextView arrival, departure, stCode, stName, adt, distance, platform ;
        LinearLayout disclaimer ;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
