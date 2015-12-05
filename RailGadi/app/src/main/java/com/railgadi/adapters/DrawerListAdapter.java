package com.railgadi.adapters ;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.NavigationChildBean;
import com.railgadi.beans.NavigationGroupBean;
import com.railgadi.customUi.AnimatedExpandableListView;
import com.railgadi.fonts.AppFonts;
import com.railgadi.utilities.UtilsMethods;

import java.util.List;

public class DrawerListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private LayoutInflater inflater;
    private Context context ;
    private List<NavigationGroupBean> allData;

    public DrawerListAdapter(Context context, List<NavigationGroupBean> allData) {

        this.context        =   context ;
        this.allData        =   allData ;
        this.inflater       =   LayoutInflater.from(context);
    }


    @Override
    public NavigationChildBean getChild(int groupPosition, int childPosition) {
        return allData.get(groupPosition).getChildData().get(childPosition) ;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildHolder holder;

        NavigationChildBean item = getChild(groupPosition, childPosition);

        if (convertView == null) {

            holder = new ChildHolder();

            convertView = inflater.inflate(R.layout.drawer_menu_items_row , parent, false);

            holder.childText            =   (TextView) convertView.findViewById(R.id.drawer_child_text);
            holder.childIcon            =   (ImageView) convertView.findViewById(R.id.drawer_child_icon) ;

            holder.childText.setTypeface(AppFonts.getRobotoReguler(context));

            convertView.setTag(holder);

        } else {

            holder = (ChildHolder) convertView.getTag();
        }

        holder.childText.setText(item.getMenu());

        try {
            holder.childIcon.setImageDrawable(UtilsMethods.getSvgDrawable(context, item.getIcon()));
        } catch(Exception e) {
            holder.childIcon.setImageResource(item.getIcon());
        }

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return allData.get(groupPosition).getChildData().size();
    }

    @Override
    public NavigationGroupBean getGroup(int groupPosition) {
        return allData.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return allData.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        GroupHolder holder;

        NavigationGroupBean item = getGroup(groupPosition);

        if (convertView == null) {

            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.drawer_menu_heading_row, parent, false);

            holder.groupText        =   (TextView) convertView.findViewById(R.id.drawer_group_text);
            holder.indicator        =   (ImageView) convertView.findViewById(R.id.drawer_group_indicator);
            holder.groupIcon        =   (ImageView) convertView.findViewById(R.id.drawer_group_icon);

            holder.groupText.setTypeface(AppFonts.getRobotoReguler(context));

            convertView.setTag(holder);

        } else {

            holder = (GroupHolder) convertView.getTag();
        }

        holder.groupText.setText(item.getGroupName());
        if(item.isExpandable()) {
            holder.indicator.setImageResource(item.getGroupIndicator());
            holder.indicator.setVisibility(View.VISIBLE);
        } else {
            holder.indicator.setVisibility(View.GONE);
        }

        try {
            holder.groupIcon.setImageDrawable(UtilsMethods.getSvgDrawable(context, item.getIcon()));
        } catch(Exception e) {
            holder.groupIcon.setImageResource(item.getIcon()) ;
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show() ;
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    public static class ChildHolder {
        public TextView childText ;
        public ImageView childIcon ;
    }

    public static class GroupHolder {
        public ImageView groupIcon ;
        public TextView groupText ;
        public ImageView indicator ;
    }

}
