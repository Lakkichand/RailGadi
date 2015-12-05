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
import com.railgadi.beans.SeatFareBeanNew;
import com.railgadi.fonts.AppFonts;

import java.util.List;

public class FareAdapterFindTrain extends BaseAdapter {

    private Context context ;
    private List<SeatFareBeanNew.FareBreakup.FareDetails> fareList ;
    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public FareAdapterFindTrain(Context context, List<SeatFareBeanNew.FareBreakup.FareDetails> fareList) {

        this.context = context ;
        this.fareList = fareList ;

        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }

    @Override
    public int getCount() {
        return fareList.size();
    }

    @Override
    public Object getItem(int position) {
        return fareList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {

            convertView =   inflater.inflate(R.layout.fare_breakup_custom_row, null) ;
        }

        TextView type       = (TextView) convertView.findViewById(R.id.fare_type) ;
        TextView amount     = (TextView) convertView.findViewById(R.id.fare_amount) ;

        type.setTypeface(AppFonts.getRobotoReguler(context));
        amount.setTypeface(AppFonts.getRobotoReguler(context));

        SeatFareBeanNew.FareBreakup.FareDetails entry = fareList.get(position) ;

        type.setText(entry.key);
        amount.setText(context.getResources().getString(R.string.rs_symbol)+" "+entry.value);

        if(entry.key.equals(context.getResources().getString(R.string.net_amount))) {
            type.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            amount.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            type.setTextColor(context.getResources().getColor(R.color.black));
            amount.setTextColor(context.getResources().getColor(R.color.black));
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }
}
