package com.railgadi.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.CurrentBookingAvailabilityBean.CurrentBooking;
import com.railgadi.beans.CurrentBookingAvailabilityBean.SeatAvailability;
import com.railgadi.fonts.AppFonts;
import com.railgadi.fragments.LiveTrainInputFragment;
import com.railgadi.interfaces.IFragReplaceCommunicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CurrentBookListAdapter extends BaseAdapter {

    private Context context;

    private LayoutInflater inflater;

    private List<CurrentBooking> currBookingList;

    private IFragReplaceCommunicator comm ;

    private int lastPosition = -1 ;

    public CurrentBookListAdapter(Context context, List<CurrentBooking> currBookingList) {

        this.context = context;
        this.currBookingList = currBookingList;
        this.comm = (IFragReplaceCommunicator) context ;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void changeDataSet(List<CurrentBooking> currBookingList) {
        this.currBookingList = currBookingList;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return currBookingList.get(position);
    }

    @Override
    public int getCount() {
        return currBookingList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.curr_book_avl_list_row, null);

            holder.trainNameNumber = (TextView) convertView.findViewById(R.id.train_name_number);
            holder.goingTo = (TextView) convertView.findViewById(R.id.going_to);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.noSeatHidden = (TextView) convertView.findViewById(R.id.no_seat_avL_hidden_text);

            holder.classLayout = (LinearLayout) convertView.findViewById(R.id.avl_class_seat_layout);
            holder.superLayout = (LinearLayout) convertView.findViewById(R.id.super_layout);

            holder.superLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int clickedPosition = (Integer) v.getTag() ;

                    CurrentBooking cb = (CurrentBooking) getItem(clickedPosition) ;

                    Map<String, String> map = new HashMap<>();
                    map.put(cb.getTrainNumber(), cb.getTrainName());

                    comm.respond(new LiveTrainInputFragment(map));
                }
            });


            // setting fonts
            holder.trainNameNumber.setTypeface(AppFonts.getRobotoReguler(context));
            holder.trainNameNumber.setTextColor(context.getResources().getColor(R.color.black));
            holder.goingTo.setTypeface(AppFonts.getRobotoReguler(context));
            holder.time.setTypeface(AppFonts.getRobotoReguler(context));
            holder.time.setTextColor(context.getResources().getColor(R.color.black));
            holder.noSeatHidden.setTypeface(AppFonts.getRobotoReguler(context));

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.superLayout.setTag(position);

        holder.classLayout.removeAllViews();

        CurrentBooking train = (CurrentBooking) getItem(position);

        holder.trainNameNumber.setText(train.getTrainNumber() + " " + train.getTrainName());
        holder.goingTo.setText("Going from " + train.getSrcStationName() + " to " + train.getDestStationName());
        holder.time.setText(train.getDeptTime());

        List<SeatAvailability> seatList = train.getSeatAvlList();

        if (seatList != null && seatList.size() > 0) {

            for (SeatAvailability entry : seatList) {

                TextView classButton = new TextView(context);
                classButton.setGravity(Gravity.CENTER);
                classButton.setTypeface(AppFonts.getRobotoReguler(context));
                LinearLayout.LayoutParams layoutParamsOne = new LinearLayout.LayoutParams(65, 65);
                classButton.setLayoutParams(layoutParamsOne);
                classButton.setTextSize(10);
                classButton.setBackgroundColor(context.getResources().getColor(R.color.white));
                classButton.setTextColor(context.getResources().getColor(R.color.white));
                classButton.setBackground(context.getResources().getDrawable(R.drawable.round_red_selected));
                classButton.setText(entry.keyClass);

                holder.classLayout.addView(classButton);

                TextView avlSeat = new TextView(context);
                avlSeat.setGravity(Gravity.CENTER);
                avlSeat.setTypeface(AppFonts.getRobotoReguler(context));
                LinearLayout.LayoutParams layoutParamsTwo = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                avlSeat.setLayoutParams(layoutParamsTwo);
                avlSeat.setTextSize(12);
                avlSeat.setPadding(5, 0, 25, 0);
                avlSeat.setTextColor(context.getResources().getColor(R.color.light_gray));
                if (entry.valueAvl.equals("0") || entry.valueAvl.equals("NA")) {
                    avlSeat.setText(entry.valueAvl);
                    avlSeat.setTextColor(context.getResources().getColor(R.color.red));
                } else {
                    avlSeat.setText(entry.valueAvl + " Seats");
                    avlSeat.setTextColor(context.getResources().getColor(R.color.green));
                }

                holder.classLayout.addView(avlSeat);
            }

            holder.classLayout.setVisibility(View.VISIBLE);
            holder.noSeatHidden.setVisibility(View.GONE);
        } else {
            holder.classLayout.setVisibility(View.GONE);
            holder.noSeatHidden.setVisibility(View.VISIBLE);
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }


    private class ViewHolder {
        TextView trainNameNumber, goingTo, time, noSeatHidden;
        LinearLayout classLayout, superLayout;
    }
}
