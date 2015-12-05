package com.railgadi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.CoachCompositionBean;
import com.railgadi.fonts.AppFonts;

import java.util.List;

public class CoachCompositionAdapter extends BaseAdapter {

    private Context context ;
    private List<CoachCompositionBean.CoachBean> data ;
    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public CoachCompositionAdapter(Context context, List<CoachCompositionBean.CoachBean> data) {

        this.context        =   context ;
        this.data           =   data ;
        this.inflater       =   (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }

    @Override
    public int getCount() {
        return data.size() ;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null ;

        if(convertView == null) {

            holder                      =   new ViewHolder() ;

            convertView                 =   inflater.inflate(R.layout.coach_comp_list_row, null) ;

            holder.serial               =   (TextView) convertView.findViewById(R.id.serial_no) ;
            holder.code                 =   (TextView) convertView.findViewById(R.id.coach_code) ;
            holder.description          =   (TextView) convertView.findViewById(R.id.description) ;

            holder.disclaimer           =   (LinearLayout) convertView.findViewById(R.id.disclaimer_historical) ;

            holder.serial.setTypeface(AppFonts.getRobotoLight(context));
            holder.code.setTypeface(AppFonts.getRobotoLight(context));
            holder.description.setTypeface(AppFonts.getRobotoLight(context));

            convertView.setTag(holder);

        } else {

            holder                      =   (ViewHolder) convertView.getTag() ;
        }

        CoachCompositionBean.CoachBean bean   =   (CoachCompositionBean.CoachBean) getItem(position) ;

        holder.code.setTag(position);

        holder.serial.setText(bean.getSerialNo());
        holder.code.setText(bean.getCode().toUpperCase());
        holder.description.setText(bean.getDescription());
        if(bean.getBackground() != 0) {
            holder.code.setBackground(context.getResources().getDrawable(bean.getBackground()));
        }
        holder.code.setTextColor(bean.getTextColor());

        if(position == (data.size()-1)) {
            holder.disclaimer.setVisibility(View.VISIBLE);
        } else {
            holder.disclaimer.setVisibility(View.GONE);
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView ;
    }

    private static class ViewHolder {
        TextView serial, code, description ;
        LinearLayout disclaimer ;
    }
}
