package com.railgadi.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.LiveTrainNewBean;
import com.railgadi.beans.MiniTimeTableBean.TrainRoute;
import com.railgadi.fonts.AppFonts;
import com.railgadi.utilities.DateAndMore;
import com.railgadi.utilities.UtilsMethods;

import java.util.Date;
import java.util.List;


public class LiveTrainAdapter extends BaseAdapter {

    private Context context;
    private List<TrainRoute> allStationList;
    private LiveTrainNewBean liveTrainNewBean;

    private LayoutInflater inflater;

    public LiveTrainAdapter(Context context, LiveTrainNewBean liveTrainNewBean) {

        this.context = context;
        this.liveTrainNewBean = liveTrainNewBean;
        this.allStationList = liveTrainNewBean.getMiniTimeTableBean().getTrainRouteList();
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return allStationList.size();
    }

    @Override
    public Object getItem(int position) {
        return allStationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        Log.d("Enter Live Rows: ", DateAndMore.formatDateToString(new Date(), DateAndMore.SUPER_DATE_FORMAT)) ;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.live_train_list_row, null);

            holder = new ViewHolder();

            holder.aboveView = convertView.findViewById(R.id.top_view);
            holder.belowView = convertView.findViewById(R.id.bottom_view);
            holder.stationImage = (ImageView) convertView.findViewById(R.id.station_image);
            holder.currentTrain = (ImageView) convertView.findViewById(R.id.current_train);
            holder.hiddenLayout = (LinearLayout) convertView.findViewById(R.id.hidden_layout);
            holder.stationName = (TextView) convertView.findViewById(R.id.station_name);
            holder.depTime = (TextView) convertView.findViewById(R.id.dep_time);
            holder.arrTime = (TextView) convertView.findViewById(R.id.arr_time);
            holder.interMediateStation = (TextView) convertView.findViewById(R.id.intermediate_station_name);
            holder.trainIsHere = (TextView) convertView.findViewById(R.id.train_is_here);

            holder.stationName.setTypeface(AppFonts.getRobotoLight(context));
            holder.depTime.setTypeface(AppFonts.getRobotoReguler(context));
            holder.arrTime.setTypeface(AppFonts.getRobotoReguler(context));
            holder.trainIsHere.setTypeface(AppFonts.getRobotoLight(context));
            holder.interMediateStation.setTypeface(AppFonts.getRobotoLight(context));
            holder.currentTrain.startAnimation(getAnimation());

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TrainRoute route = (TrainRoute) getItem(position);

        if(position == 0) {
            holder.aboveView.setVisibility(View.INVISIBLE) ;
        } else {
            holder.aboveView.setVisibility(View.VISIBLE) ;
        }

        if(position == (getCount()-1)) {
            holder.belowView.setVisibility(View.INVISIBLE) ;
        } else {
            holder.belowView.setVisibility(View.VISIBLE) ;
        }

        if (liveTrainNewBean.trainPosition == position) {

            if (liveTrainNewBean.isMain) {

                holder.hiddenLayout.setVisibility(View.GONE);
                holder.stationImage.setImageResource(R.drawable.train);
                holder.stationImage.setAnimation(getAnimation());

            } else {

                holder.interMediateStation.setText("(Intermediate station) " + liveTrainNewBean.getLastStation().stnName);
                holder.hiddenLayout.setVisibility(View.VISIBLE);
                holder.currentTrain.setAnimation(getAnimation());
            }

        } else {

            holder.stationImage.setImageResource(R.drawable.green_circle);
            holder.stationImage.setAnimation(null);
            holder.hiddenLayout.setVisibility(View.GONE);
        }

        if (position > liveTrainNewBean.trainPosition) {

            holder.aboveView.setBackgroundColor(context.getResources().getColor(R.color.black));
            holder.belowView.setBackgroundColor(context.getResources().getColor(R.color.black));

        } else {

            holder.aboveView.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
            holder.belowView.setBackgroundColor(context.getResources().getColor(R.color.light_gray));

        }

        holder.stationName.setText(route.stationName);

        Date instanceDate = liveTrainNewBean.getInputDateInstance() ;

        switch(route.day) {

            case "2" : {
                instanceDate    =   UtilsMethods.addRemoveDayFromDate(instanceDate, 1) ;
                break ;
            }
            case "3" : {
                instanceDate    =   UtilsMethods.addRemoveDayFromDate(instanceDate, 2) ;
                break ;
            }
            case "4" : {
                instanceDate    =   UtilsMethods.addRemoveDayFromDate(instanceDate, 3) ;
                break ;
            }
            case "5" : {
                instanceDate    =   UtilsMethods.addRemoveDayFromDate(instanceDate, 4) ;
                break ;
            }
        }

        holder.depTime.setText(route.depTime + " " + DateAndMore.formatDateToString(instanceDate, DateAndMore.DATE_MONTH));
        holder.arrTime.setText(route.arrTime + " " + DateAndMore.formatDateToString(instanceDate, DateAndMore.DATE_MONTH));

        Log.d("Exit Live Rows: ", DateAndMore.formatDateToString(new Date(), DateAndMore.SUPER_DATE_FORMAT)) ;


        return convertView;
    }


    private static class ViewHolder {
        private View aboveView;
        private View belowView;
        private ImageView stationImage, currentTrain;
        private LinearLayout hiddenLayout;
        private TextView stationName, depTime, arrTime, trainIsHere, interMediateStation;
    }


    private Animation getAnimation() {

        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        return animation;
    }
}
