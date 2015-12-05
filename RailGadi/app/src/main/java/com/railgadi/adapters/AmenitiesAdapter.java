package com.railgadi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.javadocmd.simplelatlng.LatLng;
import com.railgadi.R;
import com.railgadi.beans.NearByAmenitiesBean;
import com.railgadi.fonts.AppFonts;

import java.util.Collections;
import java.util.List;


public class AmenitiesAdapter extends BaseAdapter {

    private Context context ;
    private List<NearByAmenitiesBean> data ;
    private LatLng source;
    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public AmenitiesAdapter(Context context, List<NearByAmenitiesBean> data, LatLng source) {

        this.context    =   context ;
        this.data       =   data ;
        this.source     =   source ;
        this.inflater   =   (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        Collections.sort(this.data);
    }

    public void changeDataSet(List<NearByAmenitiesBean> data) {
        this.data       =   data ;
        Collections.sort(this.data);
        notifyDataSetChanged() ;
    }

    @Override
    public int getCount() {
        return data.size();
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

            holder              =   new ViewHolder() ;

            convertView         =   inflater.inflate(R.layout.amenities_custom_row, null) ;

            holder.name         =   (TextView) convertView.findViewById(R.id.name) ;
            holder.distance     =   (TextView) convertView.findViewById(R.id.distance) ;
            holder.address      =   (TextView) convertView.findViewById(R.id.address) ;

            holder.name.setTypeface(AppFonts.getRobotoReguler(context));
            holder.address.setTypeface(AppFonts.getRobotoReguler(context));
            holder.distance.setTypeface(AppFonts.getRobotoReguler(context));

            convertView.setTag(holder);

        } else {
            holder  =   (ViewHolder) convertView.getTag() ;
        }

        NearByAmenitiesBean bean    =   (NearByAmenitiesBean) getItem(position);

        //LatLng destination = new LatLng(Double.valueOf(bean.getLatitude()), Double.valueOf(bean.getLongitude())) ;
        //int distance = (int) LatLngTool.distance(source, destination, LengthUnit.METER) ;
/*
        float[] arrayOfFloat = new float[3];
        Location.distanceBetween(source.getLatitude(), source.getLongitude(), destination.getLatitude(), destination.getLongitude(), arrayOfFloat);
        float distance = Float.parseFloat(String.format("%.2f", (arrayOfFloat[0] / 1000.0F))) ;
*/

        holder.name.setText(bean.getName());
        holder.address.setText(bean.getAddress());
        holder.distance.setText(bean.getDistance()+" KM");

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView ;
    }

    private static class ViewHolder {
        TextView name, distance, address ;
    }
}
