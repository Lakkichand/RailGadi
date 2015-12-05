package com.railgadi.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.comparators.PnrListComparator;
import com.railgadi.fonts.AppFonts;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.DateAndMore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class MessageAdapter extends BaseAdapter {

    private Context context;
    private List<Map.Entry<String,PnrStatusNewBean>> dataList;
    private LayoutInflater inflater;
    private String flag ;

    private int lastPosition = -1 ;

    public MessageAdapter(Context context, Map<String, PnrStatusNewBean> dataMap, String flag) {

        this.context = context;
        this.flag       =   flag ;
        this.dataList = new ArrayList<>(dataMap.entrySet());
        Collections.sort(this.dataList, new PnrListComparator());
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void changeDataSet(Map<String, PnrStatusNewBean> dataMap) {

        this.dataList = new ArrayList<>(dataMap.entrySet());
        Collections.sort(this.dataList, new PnrListComparator());
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.pnr_status_custom_row_new, null);
            holder = new ViewHolder();

            holder.fromCode         =   (TextView) convertView.findViewById(R.id.pnr_from_station_code) ;
            holder.toCode           =   (TextView) convertView.findViewById(R.id.pnr_to_station_code) ;
            holder.fromName         =   (TextView) convertView.findViewById(R.id.pnr_from_station_name) ;
            holder.toName           =   (TextView) convertView.findViewById(R.id.pnr_to_station_name) ;
            holder.status           =   (TextView) convertView.findViewById(R.id.pnr_status_textview) ;
            holder.pnrNumber        =   (TextView) convertView.findViewById(R.id.pnr_number) ;
            holder.trainName        =   (TextView) convertView.findViewById(R.id.pnr_train_name) ;
            holder.date             =   (TextView) convertView.findViewById(R.id.pnr_date_of_journey) ;
            holder.trainType        =   (TextView) convertView.findViewById(R.id.train_type) ;
            holder.nuOfPassenger    =   (TextView) convertView.findViewById(R.id.pnr_number_of_passenger) ;

            holder.fromCode.setTypeface(AppFonts.getRobotoLight(context));
            holder.toCode.setTypeface(AppFonts.getRobotoLight(context));
            holder.fromName.setTypeface(AppFonts.getRobotoReguler(context));
            holder.toName.setTypeface(AppFonts.getRobotoReguler(context)) ;
            holder.status.setTypeface(AppFonts.getRobotoMedium(context)) ;
            holder.pnrNumber.setTypeface(AppFonts.getRobotoReguler(context));
            holder.trainType.setTypeface(AppFonts.getRobotoReguler(context));
            holder.trainName.setTypeface(AppFonts.getRobotoReguler(context));
            holder.date.setTypeface(AppFonts.getRobotoReguler(context));
            holder.nuOfPassenger.setTypeface(AppFonts.getRobotoReguler(context));

            if(Constants.COMPLETED.equals(flag)) {

                holder.fromCode.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.toCode.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.fromName.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.toName.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.status.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.pnrNumber.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.trainName.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.date.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.nuOfPassenger.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
            }

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map.Entry<String, PnrStatusNewBean> entry = (Map.Entry<String, PnrStatusNewBean>) getItem(position) ;

        PnrStatusNewBean model = entry.getValue() ;

        PnrStatusNewBean.TrainInfo trainInfo = model.getTrainInfo() ;

        try {

            holder.fromCode.setText(model.getBoardFromCode());
            holder.fromName.setText(model.getBoardFromName());
            holder.toCode.setText(model.getBoardToCode());
            holder.toName.setText(model.getBoardToName());

            holder.pnrNumber.setText(new StringBuffer(model.getPnrNumber()).insert(3,"-").insert(7,"-").toString());
            holder.trainName.setText(trainInfo.trainName);
            holder.trainType.setText(trainInfo.trainType.toUpperCase());
            holder.date.setText(DateAndMore.formatDateToString(trainInfo.srcDepDate, DateAndMore.DAY_DATE_MONTH)+" "+ trainInfo.srcDepTime);

            holder.nuOfPassenger.setText(model.getPassengerList().size()+" Passenger");

            String status = model.getMainStatus() ;
            holder.status.setText(status);

            if(status.contains("W/L")) {
                holder.status.setText("WL");
                holder.status.setTextColor(context.getResources().getColor(R.color.white));
                holder.status.setBackground(context.getResources().getDrawable(R.drawable.light_red));
            }
            else if(status.contains("CNF") || status.contains("Conf")) {
                holder.status.setText("CONFIRM");
                holder.status.setBackground(context.getResources().getDrawable(R.drawable.green));
            }
            else if(status.contains("RAC")) {
                holder.status.setText("RAC");
                holder.status.setBackground(context.getResources().getDrawable(R.drawable.yellow));
            }
            else if(status.contains("CAN") || status.contains("Can")) {
                holder.status.setText("CANCELED");
                holder.status.setTextColor(context.getResources().getColor(R.color.white)) ;
                holder.status.setBackground(context.getResources().getDrawable(R.drawable.black));
            }
            else {
                holder.status.setTextColor(context.getResources().getColor(R.color.black));
            }

        } catch(Exception e) {

            e.printStackTrace();
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }

    private class ViewHolder {

        TextView fromCode, toCode, fromName, toName, status, pnrNumber, trainName, trainType, date, nuOfPassenger ;
    }
}
