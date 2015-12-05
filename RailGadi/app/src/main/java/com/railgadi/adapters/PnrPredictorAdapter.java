package com.railgadi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.beans.PnrStatusNewBean.Passenger;
import com.railgadi.fonts.AppFonts;

import java.util.List;

public class PnrPredictorAdapter extends BaseAdapter {

    private Context context;
    private PnrStatusNewBean ps;
    private List<Passenger> psList;
    private LayoutInflater inflater;


    public PnrPredictorAdapter(Context context, PnrStatusNewBean ps) {

        this.context = context;
        this.ps = ps;
        this.psList = ps.getPassengerList();
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void changeDataSet(PnrStatusNewBean ps) {
        this.ps = ps;
        this.psList = ps.getPassengerList();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return psList.size();
    }

    @Override
    public Object getItem(int position) {
        return psList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.pnr_predictor_list_row, null);

            holder.passenger = (TextView) convertView.findViewById(R.id.passenger);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.possibility = (TextView) convertView.findViewById(R.id.possibility);

            holder.passenger.setTypeface(AppFonts.getRobotoLight(context));
            holder.status.setTypeface(AppFonts.getRobotoLight(context));
            holder.possibility.setTypeface(AppFonts.getRobotoLight(context));

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        Passenger p = psList.get(position);

        String nu = p.sNo ;
        String st = p.status +" "+ p.coachNumber +" "+ p.seatNumber ;
        String cnf = p.possibility ;

        String coach = p.coachPos ;

        holder.passenger.setText(nu);
        holder.status.setText(st);

        boolean chartingFlag;

        if (ps.getChartingStatus().equalsIgnoreCase("charting done") || ps.getChartingStatus().equalsIgnoreCase("chart prepared")) {
            chartingFlag = true;
        } else {
            chartingFlag = false;
        }

        if (chartingFlag) {
            holder.possibility.setText(coach);
        } else {
            if (cnf == null || cnf.equals("null")) {
                holder.possibility.setText(context.getResources().getString(R.string.not_available));
            } else {
                holder.possibility.setText(cnf);
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView passenger, status, possibility;
    }
}
