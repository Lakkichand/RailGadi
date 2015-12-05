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
import com.railgadi.fonts.AppFonts;

public class AgeAndConcessionAdapter extends BaseAdapter {

    private Context context ;
    private String [] data ;
    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public AgeAndConcessionAdapter(Context context, String [] data) {

        this.context    =   context ;
        this.data       =   data ;
        this.inflater   =   (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.age_concession_custom_row, null);
        }

        TextView text   =   (TextView) convertView.findViewById(R.id.text) ;
        text.setText(data[position]);
        text.setTypeface(AppFonts.getRobotoThin(context));

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView ;
    }
}
