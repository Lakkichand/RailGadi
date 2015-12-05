package com.railgadi.adapters;

import android.content.Context;
import android.os.AsyncTask;
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
import com.railgadi.async.GetTimeTableTaskNew;
import com.railgadi.beans.SpecialTrainNewBean.SpecialTrains;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.fragments.RouteMapTabsFragment;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IGettingTimeTableComm;
import com.railgadi.utilities.InternetChecking;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vijay on 19-10-2015.
 */
public class SpecialTrainAdapterNew extends BaseAdapter implements IGettingTimeTableComm {

    private Context context;
    private List<SpecialTrains> data;
    private List<SpecialTrains> extraArrayList;
    public static List<SpecialTrains> updatedList;
    private LayoutInflater inflater;

    private GetTimeTableTaskNew timeTableTask;

    private int clickedKey;

    private IFragReplaceCommunicator comm;

    private int lastPosition = -1 ;

    public SpecialTrainAdapterNew(Context context, List<SpecialTrains> data) {

        this.context = context;
        this.data = data;
        extraArrayList = new ArrayList<>(data);
        updatedList = new ArrayList<>();
        comm = (MainActivity) context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public AsyncTask getTimeTableTask() {
        return this.timeTableTask;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.special_train_list_row, null);

            holder.superLayout = (LinearLayout) convertView.findViewById(R.id.super_layout);

            holder.trainName = (TextView) convertView.findViewById(R.id.train_name);
            holder.trainNumber = (TextView) convertView.findViewById(R.id.train_number);
            holder.arrText = (TextView) convertView.findViewById(R.id.arrival_text);
            holder.depText = (TextView) convertView.findViewById(R.id.departure_text);
            holder.travelTimeText = (TextView) convertView.findViewById(R.id.travel_time_text);
            holder.arrTime = (TextView) convertView.findViewById(R.id.arr_time);
            holder.depTime = (TextView) convertView.findViewById(R.id.dep_time);
            holder.travelTime = (TextView) convertView.findViewById(R.id.travel_time);
            holder.sourceText = (TextView) convertView.findViewById(R.id.source_text);
            holder.sourceName = (TextView) convertView.findViewById(R.id.source_name);
            holder.destText = (TextView) convertView.findViewById(R.id.dest_text);
            holder.destName = (TextView) convertView.findViewById(R.id.dest_name);
            holder.validFromTo = (TextView) convertView.findViewById(R.id.from_to_runs_on);
            holder.sun = (TextView) convertView.findViewById(R.id.sun);
            holder.mon = (TextView) convertView.findViewById(R.id.mon);
            holder.tue = (TextView) convertView.findViewById(R.id.tue);
            holder.wed = (TextView) convertView.findViewById(R.id.wed);
            holder.thu = (TextView) convertView.findViewById(R.id.thu);
            holder.fri = (TextView) convertView.findViewById(R.id.fri);
            holder.sat = (TextView) convertView.findViewById(R.id.sat);

            //fonts
            holder.trainName.setTypeface(AppFonts.getRobotoReguler(context));
            holder.trainNumber.setTypeface(AppFonts.getRobotoReguler(context));
            holder.arrText.setTypeface(AppFonts.getRobotoLight(context));
            holder.arrTime.setTypeface(AppFonts.getRobotoLight(context));
            holder.depText.setTypeface(AppFonts.getRobotoLight(context));
            holder.depTime.setTypeface(AppFonts.getRobotoLight(context));
            holder.travelTimeText.setTypeface(AppFonts.getRobotoLight(context));
            holder.travelTime.setTypeface(AppFonts.getRobotoLight(context));
            holder.sourceText.setTypeface(AppFonts.getRobotoReguler(context));
            holder.destText.setTypeface(AppFonts.getRobotoReguler(context));
            holder.sourceName.setTypeface(AppFonts.getRobotoReguler(context));
            holder.destName.setTypeface(AppFonts.getRobotoReguler(context));
            holder.validFromTo.setTypeface(AppFonts.getRobotoReguler(context));
            holder.sun.setTypeface(AppFonts.getRobotoLight(context));
            holder.mon.setTypeface(AppFonts.getRobotoLight(context));
            holder.tue.setTypeface(AppFonts.getRobotoLight(context));
            holder.wed.setTypeface(AppFonts.getRobotoLight(context));
            holder.thu.setTypeface(AppFonts.getRobotoLight(context));
            holder.fri.setTypeface(AppFonts.getRobotoLight(context));
            holder.sat.setTypeface(AppFonts.getRobotoLight(context));

            holder.superLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (InternetChecking.isNetWorkOn(context)) {

                        clickedKey = (Integer) v.getTag();

                        SpecialTrains selected = (SpecialTrains) getItem(clickedKey);

                        timeTableTask = new GetTimeTableTaskNew(context, selected.trainNumber, SpecialTrainAdapterNew.this);
                        timeTableTask.execute();

                    } else {
                        InternetChecking.noInterNetToast(context);
                    }
                }
            });

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.sun.setTag(position);
        holder.mon.setTag(position);
        holder.tue.setTag(position);
        holder.wed.setTag(position);
        holder.thu.setTag(position);
        holder.fri.setTag(position);
        holder.sat.setTag(position);
        holder.superLayout.setTag(position);

        SpecialTrains st = (SpecialTrains) getItem(position);

        holder.trainName.setText(st.trainName);
        holder.trainNumber.setText(st.trainNumber);
        holder.sourceName.setText(st.srcStnName);
        holder.destName.setText(st.destStnName);
        holder.arrTime.setText(st.depTime);
        holder.depTime.setText(st.arrTime);
        holder.travelTime.setText(st.travelTime);
        holder.validFromTo.setText("From " + st.validFrom + " To " + st.validTo);

        TextView[] days = {holder.sun, holder.mon, holder.tue, holder.wed, holder.thu, holder.fri, holder.sat};

        boolean[] runDays = st.runningDays;

        for (int i = 0; i < runDays.length; i++) {
            if (runDays[i]) {
                days[i].setTextColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                days[i].setTextColor(context.getResources().getColor(R.color.light_gray));
            }
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }


    private static class ViewHolder {

        TextView trainName, trainNumber, sourceName, destName, arrTime, depTime, travelTime, validFromTo,
                travelTimeText, sourceText, destText, arrText, depText, sun, mon, tue, wed, thu, fri, sat;
        LinearLayout superLayout;
    }


    public void filterData(String query) {

        query = query.toLowerCase();
        data.clear();

        if (query.isEmpty()) {
            data.addAll(extraArrayList);
        } else {

            for (SpecialTrains train : extraArrayList) {

                if (train.trainName.toLowerCase().startsWith(query) || train.trainNumber.toLowerCase().startsWith(query)) {
                    data.add(train);
                }
            }

            updatedList = data;
        }

        notifyDataSetChanged();
    }

    @Override
    public void updateException(String exception) {
        Toast.makeText(context, exception, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateTimeTableUI(TimeTableNewBean bean) {
        comm.respond(new RouteMapTabsFragment(bean));
    }
}
