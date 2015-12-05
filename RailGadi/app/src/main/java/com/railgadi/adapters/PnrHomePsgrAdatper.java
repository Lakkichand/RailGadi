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
import com.railgadi.beans.PnrStatusNewBean.Passenger;
import com.railgadi.fragments.SearchedPnrFragment;

import java.util.List;

public class PnrHomePsgrAdatper extends BaseAdapter {

    private Context context ;
    private List<Passenger> pList ;
    private LayoutInflater inflater ;

    private boolean chartingFlag ;

    private int lastPosition = -1 ;

    public static SearchedPnrFragment searchedPnrFragment;

    public PnrHomePsgrAdatper(Context context, List<Passenger> pList, boolean chartingFlag ) {

        this.context = context ;
        this.pList = pList ;

        this.chartingFlag = chartingFlag ;

        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }

    @Override
    public int getCount() {
        return pList.size() ;
    }

    @Override
    public Object getItem(int position) {
        return pList.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null ;

        if(convertView == null) {

            holder = new ViewHolder() ;

            convertView         =   inflater.inflate(R.layout.pnr_frag_psgr_row, null) ;

            holder.psgrNumber   =   (TextView) convertView.findViewById(R.id.passenger_number) ;
            holder.status       =   (TextView) convertView.findViewById(R.id.status) ;
            holder.possibility  =   (TextView) convertView.findViewById(R.id.possibility) ;

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag() ;
        }

        Passenger p = pList.get(position) ;

        String coachPos = p.coachNumber ;

        String nu = p.sNo ;
        String st = p.status ;
        String cnf = p.possibility ;

        String coach = p.coachPos ;

        holder.psgrNumber.setText(nu);
        holder.status.setText(st);

        if(chartingFlag) {
            holder.possibility.setText(coach);
        } else {
            if(cnf == null || cnf.equals("null")) {
                holder.possibility.setText(context.getResources().getString(R.string.not_available));
            } else {
                holder.possibility.setText(cnf);
            }
        }

        if(st.contains("CNF") || st.contains("Conf")) {
            holder.status.setText("CNF "+p.coachNumber+","+p.seatNumber);
        }

        holder.psgrNumber.setTextColor(context.getResources().getColor(R.color.home_dark));
        holder.status.setTextColor(context.getResources().getColor(R.color.home_dark));
        holder.possibility.setTextColor(context.getResources().getColor(R.color.home_dark));

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }

    private class ViewHolder {
        TextView psgrNumber, status, possibility ;
    }
}