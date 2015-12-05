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
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.async.LiveStationTaskNew;
import com.railgadi.async.StationInformationTask;
import com.railgadi.beans.LiveStationNewBean;
import com.railgadi.beans.NearByStationBean;
import com.railgadi.beans.StationInformationBean;
import com.railgadi.dbhandlers.StationsDBHandler;
import com.railgadi.fonts.AppFonts;
import com.railgadi.fragments.LiveStationToNextFrag;
import com.railgadi.fragments.ShowStationInformationFragment;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.ILiveStationInterface;
import com.railgadi.interfaces.IStationInformationInterface;
import com.railgadi.utilities.InternetChecking;

import java.util.List;
import java.util.Map;

public class NearByStationAdapter extends BaseAdapter implements ILiveStationInterface, IStationInformationInterface {

    private Context context;
    private List<Map.Entry<String, NearByStationBean>> allStations;
    private LayoutInflater inflater;

    private StationInformationTask informationTask ;

    private IFragReplaceCommunicator comm ;

    private StationInformationTask stationInformationTask ;

    private String selectedViaName ;
    private String from , to , time ;

    private LiveStationTaskNew liveStationTask ;

    private int lastPosition = -1 ;

    private StationsDBHandler dbHandler ;


    public NearByStationAdapter(Context context, List<Map.Entry<String, NearByStationBean>> allStations) {

        this.context = context;
        this.allStations = allStations;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.dbHandler  =   new StationsDBHandler(context) ;

        comm = ((MainActivity)context ) ;
    }

    public void changeDataSet(List<Map.Entry<String, NearByStationBean>> allStations) {
        this.allStations = allStations ;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return allStations.get(position);
    }

    @Override
    public int getCount() {
        return allStations.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder ;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.near_by_station_list_row, null);

            holder.station = (TextView) convertView.findViewById(R.id.station_name);
            holder.distance = (TextView) convertView.findViewById(R.id.km_distance);
            holder.liveStation = (TextView) convertView.findViewById(R.id.live_station);
            holder.stationInfo  =  (TextView) convertView.findViewById(R.id.station_information) ;
            holder.disclaimerLayout = (LinearLayout) convertView.findViewById(R.id.disclaimer_historical) ;

            holder.stationInfo.setTypeface(AppFonts.getRobotoLight(context));
            holder.station.setTypeface(AppFonts.getRobotoLight(context));
            holder.distance.setTypeface(AppFonts.getRobotoLight(context));
            holder.liveStation.setTypeface(AppFonts.getRobotoLight(context));

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        NearByStationBean bean = allStations.get(position).getValue();

        holder.station.setText(bean.getStationName());
        holder.distance.setText(((int) bean.getDistance()) + " km");

        holder.liveStation.setTag(position);
        holder.liveStation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int clickedPosition = (Integer)v.getTag() ;
                NearByStationBean clicked = allStations.get(clickedPosition).getValue() ;

                from = clicked.getStationCode() ;
                to = null ;
                time = "2" ;

                selectedViaName = new StationsDBHandler(context).getStationNameFromCode(from) ;

                if(InternetChecking.isNetWorkOn(context)) {
                    liveStationTask = new LiveStationTaskNew(context, from, to, time, NearByStationAdapter.this);
                    liveStationTask.execute();
                } else {
                    InternetChecking.noInterNetToast(context);
                }
            }
        });


        holder.stationInfo.setTag(position);
        holder.stationInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int clickedPosition = (Integer) v.getTag() ;

                String inputSrcCode = ((Map.Entry<String, NearByStationBean>) getItem(clickedPosition)).getKey() ;

                if(InternetChecking.isNetWorkOn(context)) {
                    informationTask = new StationInformationTask(context, inputSrcCode, NearByStationAdapter.this) ;
                    informationTask.execute() ;
                } else {
                    InternetChecking.noInterNetToast(context);
                }
            }
        });


        if(position == (getCount()-1)) {
            holder.disclaimerLayout.setVisibility(View.VISIBLE);
        } else {
            holder.disclaimerLayout.setVisibility(View.GONE);
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }

    private class ViewHolder {
        TextView station, distance, liveStation, stationInfo;
        LinearLayout disclaimerLayout ;
    }

    @Override
    public void updateStationInformationException(String exception) {
        updateException(exception) ;
    }

    @Override
    public void updateStationInformationUI(StationInformationBean bean) {
        comm.respond(new ShowStationInformationFragment(bean));
    }

    @Override
    public void updateException(String exception) {
        Toast.makeText(context, exception, Toast.LENGTH_SHORT).show() ;
    }

    @Override
    public void updateLiveStationUI(LiveStationNewBean bean) {
        comm.respond(new LiveStationToNextFrag(bean, selectedViaName, time, context.getResources().getString(R.string.live_station)));
    }
}
