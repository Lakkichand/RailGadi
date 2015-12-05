package com.railgadi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.StationNameCodeBean;
import com.railgadi.fonts.AppFonts;

import java.util.List;

public class StationAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private Context context ;
    private LayoutInflater inflater ;
    private List<StationNameCodeBean> data ;


    public StationAutoCompleteAdapter(Context context) {

        this.context    =   context ;
        this.inflater   =   (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void changeDataSet(List<StationNameCodeBean> data) {
        this.data   =   data ;
        notifyDataSetChanged() ;
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

        ViewHolder holder ;

        if(convertView == null) {

            holder                  =   new ViewHolder() ;

            convertView             =   inflater.inflate(R.layout.autocomplete_custom_row, null) ;

            holder.name             =   (TextView) convertView.findViewById(R.id.name) ;
            holder.code             =   (TextView) convertView.findViewById(R.id.code) ;

        } else {
            holder                  =   (ViewHolder) convertView.getTag() ;
        }

        holder.name.setTypeface(AppFonts.getRobotoLight(context)) ;
        holder.code.setTypeface(AppFonts.getRobotoLight(context)) ;

        StationNameCodeBean bean = (StationNameCodeBean) getItem(position);

        holder.name.setText(bean.getStationName());
        holder.code.setText(bean.getStationCode());

        return convertView ;
    }

    private static class ViewHolder {
        TextView name, code ;
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
