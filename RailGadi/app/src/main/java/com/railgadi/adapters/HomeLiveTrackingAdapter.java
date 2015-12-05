package com.railgadi.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.async.LiveTrainNewTask;
import com.railgadi.beans.LiveTrainNewBean;
import com.railgadi.beans.MiniTimeTableBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.interfaces.ILiveTrainInterface;
import com.railgadi.interfaces.IPnrDeleteCommunicator;
import com.railgadi.preferences.LiveTrainTrackingPreferences;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.List;

public class HomeLiveTrackingAdapter extends BaseAdapter implements ILiveTrainInterface, IPnrDeleteCommunicator {

    private Context context ;
    private LayoutInflater inflater ;
    private List<LiveTrainNewBean> data ;

    private LiveTrainNewTask liveTrainNewTask ;

    private int clickedKey, longClickedKey ;

    private int lastPosition = -1 ;

    public HomeLiveTrackingAdapter(Context context, List<LiveTrainNewBean> allTrackInstance) {

        this.context        =   context ;
        this.data           =   allTrackInstance ;
        this.inflater       =   LayoutInflater.from(context) ;
    }

    public void cancelTask() {
        if(liveTrainNewTask != null) {
            liveTrainNewTask.cancel(true) ;
        }
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
        return position ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null ;

        if(convertView == null) {

            holder                      =   new ViewHolder() ;

            convertView                 =   inflater.inflate(R.layout.demo_home_tracking_row, null) ;

            holder.superLayout          =   (LinearLayout) convertView.findViewById(R.id.super_layout) ;

            holder.srcIcon              =   (ImageView) convertView.findViewById(R.id.last_image) ;
            holder.destIcon             =   (ImageView) convertView.findViewById(R.id.next_image) ;
            holder.trainIcon            =   (ImageView) convertView.findViewById(R.id.current_image) ;
            holder.refreshIcon          =   (ImageView) convertView.findViewById(R.id.refresh_live_tracking) ;

            holder.liveStatusTitle      =   (TextView) convertView.findViewById(R.id.live_status_text) ;
            holder.lastDelay            =   (TextView) convertView.findViewById(R.id.last_delay_factor) ;
            holder.currentDelay         =   (TextView) convertView.findViewById(R.id.current_delay_factor) ;
            holder.distanceAhead        =   (TextView) convertView.findViewById(R.id.next_distance_ahead) ;
            holder.srcName              =   (TextView) convertView.findViewById(R.id.last_station_name) ;
            holder.destName             =   (TextView) convertView.findViewById(R.id.next_station_name) ;
            holder.currentStation       =   (TextView) convertView.findViewById(R.id.current_station) ;
            holder.srcTimePlat          =   (TextView) convertView.findViewById(R.id.last_station_time_pf) ;
            holder.destTimePlat         =   (TextView) convertView.findViewById(R.id.next_station_time_pf) ;
            holder.lastUpdated          =   (TextView) convertView.findViewById(R.id.last_updated) ;

            holder.superLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    longClickedKey      =   (Integer) v.getTag() ;

                    UtilsMethods.confirmDeleteDialog(context, HomeLiveTrackingAdapter.this);

                    return true ;
                }
            });

            holder.refreshIcon.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(InternetChecking.isNetWorkOn(context)) {

                        clickedKey      =   (Integer) v.getTag() ;

                        LiveTrainNewBean bean = (LiveTrainNewBean) getItem(clickedKey) ;

                        liveTrainNewTask = new LiveTrainNewTask(context, bean.getMiniTimeTableBean().getTrainNumber(), bean,  HomeLiveTrackingAdapter.this) ;
                        liveTrainNewTask.execute() ;

                    } else {
                        InternetChecking.noInterNetToast(context) ;
                    }
                }
            });

            convertView.setTag(holder);

        } else {

            holder                      =   (ViewHolder) convertView.getTag() ;
        }

        holder.superLayout.setTag(position) ;
        holder.refreshIcon.setTag(position) ;

        LiveTrainNewBean live           =   (LiveTrainNewBean) getItem(position) ;

        LiveTrainNewBean.LastStation ls =   live.getLastStation() ;
        LiveTrainNewBean.NextStation ns =   live.getNextStation() ;

        holder.liveStatusTitle.setText("Live Status : "+live.getMiniTimeTableBean().getTrainName());

        if(ns == null) {
            holder.destName.setText("Train on dest.");
            holder.distanceAhead.setText("0");
        } else {
            holder.destName.setText(ns.stnName);
            holder.distanceAhead.setText(ns.nextDistAhead);
        }

        MiniTimeTableBean.TrainRoute prevStn = live.getMiniTimeTableBean().getTrainRouteList().get(live.prevStnPosition) ;

        holder.srcName.setText(prevStn.stationName);
        holder.srcTimePlat.setText(prevStn.arrTime);

        String [] arr= ls.delay.split(":") ;
        if("00".equals(arr[0]) && "00".equals(arr[1])) {
            holder.lastDelay.setText("On time");
            holder.lastDelay.setTextColor(context.getResources().getColor(R.color.green));
            holder.srcIcon.setImageResource(R.drawable.green_circle);
        } else {
            holder.lastDelay.setText(arr[0] + " hr " + arr[1] + " min Delay");
            holder.lastDelay.setTextColor(context.getResources().getColor(R.color.red));
            holder.srcIcon.setImageResource(R.drawable.red_circle);
        }

        holder.currentStation.setVisibility(View.VISIBLE);
        holder.currentStation.setText("Current Station : "+ls.stnName);

        /*if(live.isMain) {
            holder.currentStation.setVisibility(View.VISIBLE);
            holder.currentStation.setText("Current Station : "+ls.stnName);
            MiniTimeTableBean.TrainRoute tr = live.getMiniTimeTableBean().getTrainRouteList().get(live.trainPosition) ;
            holder.srcName.setText(tr.stationName) ;
        } else {
            holder.currentStation.setVisibility(View.GONE);
            holder.srcName.setText(ls.stnName);
        }*/

        holder.lastUpdated.setText("Last Updated @ "+live.getLastUpdatedTime());

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView ;
    }

    private static class ViewHolder {
        LinearLayout superLayout ;
        ImageView srcIcon, destIcon, trainIcon, refreshIcon ;
        TextView liveStatusTitle, lastDelay, currentDelay, distanceAhead, currentStation ;
        TextView srcName, destName, srcTimePlat, destTimePlat, lastUpdated ;
    }


    @Override
    public void updateException(String exception) {
        Toast.makeText(context, exception, Toast.LENGTH_SHORT).show() ;
    }

    @Override
    public void updateLiveTrainData(LiveTrainNewBean bean, TimeTableNewBean timeTableNewBean) {
        if(bean != null) {
            data.set(clickedKey, bean) ;
            notifyDataSetChanged();

            LiveTrainTrackingPreferences trackingPref = new LiveTrainTrackingPreferences((Activity)context) ;
            trackingPref.saveLiveTrackingToPref(bean) ;
        }
    }

    @Override
    public void wantToDelete(boolean flag) {

        if(flag) {

            LiveTrainNewBean longClicked = (LiveTrainNewBean) getItem(longClickedKey) ;

            LiveTrainTrackingPreferences livePref = new LiveTrainTrackingPreferences((Activity)context) ;
            livePref.removeTrackingFromPref(longClicked.getMiniTimeTableBean().getTrainNumber());

            data.remove(longClickedKey) ;

            notifyDataSetChanged() ;
        }
    }
}
