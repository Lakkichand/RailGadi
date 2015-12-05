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
import com.railgadi.beans.LiveStationNewBean;
import com.railgadi.fonts.AppFonts;

import java.util.List;

/**
 * Created by Vijay on 27-07-2015.
 */
public class LiveStationListAdapter extends BaseAdapter {

    private Context context ;
    private LayoutInflater inflater ;

    private List<LiveStationNewBean.LiveStationData> allTrains ;

    private int lastPosition = -1 ;

    public LiveStationListAdapter(Context context, List<LiveStationNewBean.LiveStationData> allTrains) {

        this.context    =   context ;
        this.allTrains  =   allTrains ;

        this.inflater   =   (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }


    @Override
    public int getCount() {
        return allTrains.size() ;
    }

    @Override
    public Object getItem(int position) {
        return allTrains.get(position) ;
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

            convertView         =   inflater.inflate(R.layout.live_station_list_row, null) ;

            holder.trainName    =   (TextView) convertView.findViewById(R.id.train_name) ;
            holder.trainNumber  =   (TextView) convertView.findViewById(R.id.train_number) ;
            holder.pltNo        =   (TextView) convertView.findViewById(R.id.plt_no) ;
            holder.trainType    =   (TextView) convertView.findViewById(R.id.train_type) ;
            holder.sta          =   (TextView) convertView.findViewById(R.id.sta) ;
            holder.std          =   (TextView) convertView.findViewById(R.id.std) ;
            holder.delay        =   (TextView) convertView.findViewById(R.id.delay) ;
            holder.eta          =   (TextView) convertView.findViewById(R.id.eta) ;
            holder.etd          =   (TextView) convertView.findViewById(R.id.etd) ;
            holder.srcTxt       =   (TextView) convertView.findViewById(R.id.source_text) ;
            holder.srcName      =   (TextView) convertView.findViewById(R.id.source_name) ;
            holder.destTxt      =   (TextView) convertView.findViewById(R.id.dest_text) ;
            holder.destName     =   (TextView) convertView.findViewById(R.id.dest_name) ;

            holder.trainName.setTypeface(AppFonts.getRobotoReguler(context));
            holder.trainNumber.setTypeface(AppFonts.getRobotoReguler(context));
            holder.pltNo.setTypeface(AppFonts.getRobotoReguler(context));
            holder.sta.setTypeface(AppFonts.getRobotoThin(context));
            holder.std.setTypeface(AppFonts.getRobotoThin(context));
            holder.delay.setTypeface(AppFonts.getRobotoThin(context));
            holder.eta.setTypeface(AppFonts.getRobotoThin(context));
            holder.etd.setTypeface(AppFonts.getRobotoThin(context));
            holder.srcTxt.setTypeface(AppFonts.getRobotoThin(context));
            holder.srcName.setTypeface(AppFonts.getRobotoReguler(context));
            holder.destTxt.setTypeface(AppFonts.getRobotoThin(context));
            holder.destName.setTypeface(AppFonts.getRobotoReguler(context));

            convertView.setTag(holder);
        }
        else {
            holder  =   (ViewHolder) convertView.getTag() ;
        }

        LiveStationNewBean.LiveStationData liveStn_trn = allTrains.get(position) ;

        holder.trainName.setText(liveStn_trn.trainName);
        holder.trainNumber.setText(liveStn_trn.trainNumber);
        holder.pltNo.setText("PF no: "+liveStn_trn.platform);
        holder.trainType.setText(("Train Type : "+liveStn_trn.trainType).toUpperCase());
        holder.sta.setText(liveStn_trn.sta);
        holder.std.setText(liveStn_trn.std);
        holder.delay.setText(liveStn_trn.delay);
        holder.eta.setText(liveStn_trn.eta);
        holder.etd.setText(liveStn_trn.etd);
        holder.srcName.setText(liveStn_trn.srcStationName);
        holder.destName.setText(liveStn_trn.destStationName);

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView ;
    }


    private class ViewHolder {

        TextView trainName, trainNumber, pltNo, trainType, sta, std, delay, eta, etd, srcTxt, srcName, destTxt, destName ;
    }
}
