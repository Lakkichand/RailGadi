package com.railgadi.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.StationListDataHolder;
import com.railgadi.beans.StationNameCodeBean;
import com.railgadi.dbhandlers.StationsDBHandler;
import com.railgadi.fonts.AppFonts;

import java.util.ArrayList;
import java.util.List;


public class StationNameCodeAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<StationListDataHolder> continentList;
    private ArrayList<StationListDataHolder> originalList;

    private List<StationListDataHolder> updatedList;

    public StationNameCodeAdapter(Context context, List<StationListDataHolder> continentList) {

        this.context = context;
        this.continentList = new ArrayList<>();
        this.continentList.addAll(continentList);
        this.originalList = new ArrayList<>();
        this.originalList.addAll(continentList);
    }

    public void setUpdatedNull() {
        updatedList = null ;
    }


    public Object getSelectedItemGroup(int groupPosition) {

        if (updatedList != null && updatedList.size() > groupPosition) {
            return updatedList.get(groupPosition);
        } else {
            return originalList.get(groupPosition);
        }
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {

        List<StationNameCodeBean> countryList = continentList.get(groupPosition).getNameCodeData();

        return countryList.get(childPosition);
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        StationNameCodeBean data = (StationNameCodeBean) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.station_code_name_listrow, null);
        }

        TextView code = (TextView) view.findViewById(R.id.station_code);
        code.setTypeface(AppFonts.getRobotoLight(context));
        TextView name = (TextView) view.findViewById(R.id.station_name);
        name.setTypeface(AppFonts.getRobotoLight(context));
        ImageView icon = (ImageView) view.findViewById(R.id.station_icon);

        code.setText(data.getStationCode().trim());
        name.setText(data.getStationName().trim());

        if (data.getStationIcon() == 0) {
            icon.setImageResource(R.drawable.shape_logo);
        } else {
            //icon.setImageResource(R.drawable.shape_logo);
            icon.setImageResource(data.getStationIcon());
            //icon.setImageDrawable(UtilsMethods.getSvgDrawable(context, data.getStationIcon()));
        }

        return view;
    }


    @Override
    public int getChildrenCount(int groupPosition) {

        List<StationNameCodeBean> countryList = continentList.get(groupPosition).getNameCodeData();
        return countryList.size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return continentList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return continentList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        StationListDataHolder continent = (StationListDataHolder) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.expand_list_group_row, null);
        }

        TextView heading = (TextView) view.findViewById(R.id.heading);
        heading.setTypeface(AppFonts.getRobotoReguler(context));
        heading.setText(continent.getName().trim());

        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void filterData(String query) {

        query = query.toLowerCase();
        continentList.clear();

        if (query.isEmpty()) {
            continentList.addAll(originalList);
        } else {

            for (StationListDataHolder continent : originalList) {

                List<StationNameCodeBean> countryList = continent.getNameCodeData();
                ArrayList<StationNameCodeBean> newList = new ArrayList<>();
                for (StationNameCodeBean country : countryList) {
                    if (country.getStationCode().toLowerCase().startsWith(query.toLowerCase()) ||
                            country.getStationName().toLowerCase().startsWith(query.toLowerCase())) {
                        newList.add(country);
                    }
                }
                if (newList.size() > 0) {

                    StationListDataHolder nContinent = new StationListDataHolder();
                    nContinent.setName(continent.getName());
                    nContinent.setNameCodeData(newList);

                    continentList.add(nContinent);
                }

                else {

                    StationsDBHandler dbHandler = new StationsDBHandler(context);
                    List<StationNameCodeBean> fromDB = dbHandler.getDataWithSearchTerm(query);

                    if (fromDB != null && fromDB.size() > 0) {
                        StationListDataHolder sh = new StationListDataHolder();
                        sh.setName(context.getResources().getString(R.string.all_station));
                        sh.setNameCodeData(fromDB);

                        continentList.add(sh);

                        updatedList = continentList;
                        break;
                    }
                }
                //updatedList = continentList;
            }
            updatedList = continentList;
        }

        Log.v("MyListAdapter", String.valueOf(continentList.size()));
        notifyDataSetChanged();

    }

}