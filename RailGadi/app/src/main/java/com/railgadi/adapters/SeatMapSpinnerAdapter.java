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
import com.railgadi.beans.SeatMapDataBean;

import java.util.List;

public class SeatMapSpinnerAdapter extends BaseAdapter {

    private Context context ;
    private List<SeatMapDataBean> data ;
    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public SeatMapSpinnerAdapter(Context context, List<SeatMapDataBean> data ) {

        this.context    =   context ;
        this.data       =   data ;
        inflater        =   (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.seat_map_spin_row, null) ;
        }

        TextView textItem = (TextView) convertView.findViewById(R.id.seat_map_spin_text) ;

        textItem.setText(data.get(position).getName());

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }
}
