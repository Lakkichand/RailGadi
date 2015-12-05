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

public class QuotaAdapter extends BaseAdapter {

    private Context context ;
    private String[] qCodes, qNames ;
    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public QuotaAdapter(Context context, String [] qCodes, String [] qNames) {
        this.context = context ;

        this.qCodes = qCodes ;
        this.qNames = qNames ;

        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }

    @Override
    public int getCount() {
        return qCodes.length ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return null ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.quota_dialog_custom_row, null) ;
        }

        TextView code = (TextView) convertView.findViewById(R.id.quota_code) ;
        TextView name = (TextView) convertView.findViewById(R.id.quota_name) ;

        code.setTypeface(AppFonts.getRobotoLight(context));
        name.setTypeface(AppFonts.getRobotoLight(context));

        code.setText(qCodes[position]);
        name.setText(qNames[position]);

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }
}
