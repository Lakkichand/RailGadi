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
import com.railgadi.utilities.DateAndMore;

import java.util.List;

public class PnrPassengerListAdapter extends BaseAdapter {

    private Context context ;
    private List<Passenger> pList ;
    private LayoutInflater inflater ;

    private boolean chartingFlag ;

    private int lastPosition = -1 ;

    public SearchedPnrFragment searchedPnrFragment;

    public PnrPassengerListAdapter(Context context, List<Passenger> pList, boolean chartingFlag, SearchedPnrFragment searchedPnrFragment) {

        this.context = context ;
        this.pList = pList ;
        this.chartingFlag = chartingFlag ;

        this.searchedPnrFragment = searchedPnrFragment ;

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

        String coachPos = p.coachNumber;

        String nu = p.sNo ;
        String st = p.status ;
        String cnf = p.possibility ;

        String coach = p.coachPos ;

        holder.psgrNumber.setText(nu);
        holder.status.setText(st);

        if(chartingFlag) {
            holder.possibility.setText(coach);
            searchedPnrFragment.possibilityTitle.setText("COACH");
        } else {
            if(cnf == null || cnf.equals("null")) {
                holder.possibility.setText(context.getResources().getString(R.string.not_available));
            } else {
                holder.possibility.setText(cnf);
            }
        }


        if(coachPos.equalsIgnoreCase("CHART PREPARED")) {

            String lastUpdate = DateAndMore.formatDateToString(searchedPnrFragment.pnrStatus.getLastTimeCheck(), DateAndMore.SUPER_DATE_FORMAT) ;
            searchedPnrFragment.addToTrip.setText(lastUpdate+"\n"+coachPos);
            searchedPnrFragment.addToTrip.setTextSize(10);
            searchedPnrFragment.addToTrip.setClickable(false);
        }


        if(st.contains("RAC")) {
            holder.status.setBackground(context.getResources().getDrawable(R.drawable.yellow));
            searchedPnrFragment.stamp.setImageResource(R.drawable.ic_status_rac);
        } else if(st.contains("CNF") || st.contains("Conf")) {
            holder.status.setText("CNF "+p.coachNumber+" "+p.seatNumber);
            holder.status.setBackground(context.getResources().getDrawable(R.drawable.green));
            searchedPnrFragment.stamp.setImageResource(R.drawable.ic_status_cnf);
        } else if(st.contains("Can")) {
            holder.status.setBackground(context.getResources().getDrawable(R.drawable.black));
            holder.status.setTextColor(context.getResources().getColor(R.color.white)) ;
            searchedPnrFragment.stamp.setImageResource(R.drawable.ic_status_can);
        } else if(st.contains("W/L")) {
            holder.status.setBackground(context.getResources().getDrawable(R.drawable.light_red));
            searchedPnrFragment.stamp.setImageResource(R.drawable.ic_status_wl);
        } else {
            holder.status.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }

    private class ViewHolder {
        TextView psgrNumber, status, possibility ;
    }
}