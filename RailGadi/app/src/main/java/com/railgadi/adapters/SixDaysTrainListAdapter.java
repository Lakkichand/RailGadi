package com.railgadi.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.AutoLoginActivity;
import com.railgadi.activities.MainActivity;
import com.railgadi.async.FareSeatSingleTrainTask;
import com.railgadi.async.GetTimeTableTaskNew;
import com.railgadi.beans.FareEnqInput;
import com.railgadi.beans.SearchTrainInputBean;
import com.railgadi.beans.SearchTrainResultBean;
import com.railgadi.beans.SeatFareBeanNew;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.beans.TrainClassTypeHolder;
import com.railgadi.fonts.AppFonts;
import com.railgadi.fragments.FareBreakupFragmentFT;
import com.railgadi.fragments.RouteMapTabsFragment;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IGettingTimeTableComm;
import com.railgadi.interfaces.ISeatAvlFare;
import com.railgadi.preferences.TimeTablePreferencesHandler;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SixDaysTrainListAdapter extends BaseAdapter implements ISeatAvlFare, IGettingTimeTableComm {

    private LayoutInflater inflater;

    private FareEnqInput fareInput;

    private Context context;

    private SearchTrainInputBean inputData;

    private List<SearchTrainResultBean.SearchTrain> searchTrainList;

    private FareSeatSingleTrainTask fareSeatSingleTrainTask;

    private int clickedPosition;

    private List<TrainClassTypeHolder> buttonList;

    private GetTimeTableTaskNew timeTableTask;

    private IFragReplaceCommunicator comm;

    private TimeTablePreferencesHandler preferencesHandler;

    private int lastPosition = -1 ;

    public SixDaysTrainListAdapter(Context context, SearchTrainInputBean input, List<SearchTrainResultBean.SearchTrain> searchTrainList) {

        this.context = context;
        this.inputData = input;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.searchTrainList = new ArrayList<>();
        for (SearchTrainResultBean.SearchTrain st : searchTrainList) {
            List<String> classes = st.avlClasses;
            if (classes != null && classes.size() > 0) {
                st.activeClass = classes.get(classes.size() - 1);
            } else {
                st.activeClass = "";
            }
            this.searchTrainList.add(st);
        }

        comm = (MainActivity) context;
        preferencesHandler = new TimeTablePreferencesHandler((Activity) context);
    }

    @Override
    public int getCount() {
        return searchTrainList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchTrainList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void sortDataSet() {
        Collections.sort(searchTrainList);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.avaiable_train_list_row, null);

            holder = new ViewHolder();

            holder.popupMenu = (ImageView) convertView.findViewById(R.id.popup_menu_button);
            holder.popupMenu.setOnClickListener(new ClickOnPopUp());

            holder.superLayout = (LinearLayout) convertView.findViewById(R.id.super_layout);
            holder.hiddenLayout = (LinearLayout) convertView.findViewById(R.id.hidden_station_layout);
            holder.classLayout = (LinearLayout) convertView.findViewById(R.id.class_list_layout);

            holder.tNameNumber = (TextView) convertView.findViewById(R.id.train_name_and_number);
            holder.src = (TextView) convertView.findViewById(R.id.via_station_code);
            holder.dest = (TextView) convertView.findViewById(R.id.to_station_code);
            holder.depArrTravelTime = (TextView) convertView.findViewById(R.id.arr_dep_travel_time);
            holder.trainType = (TextView) convertView.findViewById(R.id.train_type);

            holder.midArrow = (ImageView) convertView.findViewById(R.id.mid_arrow_trainlist);
            holder.midArrow.setImageAlpha(125);

            holder.sun = (TextView) convertView.findViewById(R.id.sun);
            holder.mon = (TextView) convertView.findViewById(R.id.mon);
            holder.tue = (TextView) convertView.findViewById(R.id.tue);
            holder.wed = (TextView) convertView.findViewById(R.id.wed);
            holder.thu = (TextView) convertView.findViewById(R.id.thu);
            holder.fri = (TextView) convertView.findViewById(R.id.fri);
            holder.sat = (TextView) convertView.findViewById(R.id.sat);

            holder.totalFareTextView = (TextView) convertView.findViewById(R.id.total_fare_textview);

            holder.dOne = (TextView) convertView.findViewById(R.id.date_one);
            holder.dTwo = (TextView) convertView.findViewById(R.id.date_two);
            holder.dThree = (TextView) convertView.findViewById(R.id.date_three);
            holder.dFour = (TextView) convertView.findViewById(R.id.date_four);
            holder.dFive = (TextView) convertView.findViewById(R.id.date_five);
            holder.dSix = (TextView) convertView.findViewById(R.id.date_six);

            holder.sOne = (TextView) convertView.findViewById(R.id.status_one);
            holder.sTwo = (TextView) convertView.findViewById(R.id.status_two);

            holder.sThree = (TextView) convertView.findViewById(R.id.status_three);
            holder.sFour = (TextView) convertView.findViewById(R.id.status_four);
            holder.sFive = (TextView) convertView.findViewById(R.id.status_five);
            holder.sSix = (TextView) convertView.findViewById(R.id.status_six);

            holder.next = (TextView) convertView.findViewById(R.id.next_six_days);
            holder.prev = (TextView) convertView.findViewById(R.id.prev_six_days);

            holder.next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (InternetChecking.isNetWorkOn(context)) {

                        SearchTrainResultBean.SearchTrain select = (SearchTrainResultBean.SearchTrain) getItem((Integer) v.getTag());

                        FareEnqInput nextInput = new FareEnqInput();

                        nextInput.setClassCode(fareInput.getClassCode());
                        nextInput.setMonth(select.seatFareBeanNew.getSeatAvl().next.nextMonth);
                        nextInput.setDay(select.seatFareBeanNew.getSeatAvl().next.nextDay);
                        nextInput.setAge(fareInput.getAge());
                        nextInput.setConcCode(fareInput.getConcCode());
                        nextInput.setDestCode(fareInput.getDestCode());
                        nextInput.setQuota(fareInput.getQuota());
                        nextInput.setSrcCode(fareInput.getSrcCode());
                        nextInput.setTrainNo(fareInput.getTrainNo());


                        fareSeatSingleTrainTask = new FareSeatSingleTrainTask(context, nextInput, SixDaysTrainListAdapter.this);
                        fareSeatSingleTrainTask.execute();
                    } else {
                        InternetChecking.noInterNetToast(context);
                    }
                }
            });

            holder.prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (InternetChecking.isNetWorkOn(context)) {

                        SearchTrainResultBean.SearchTrain select = (SearchTrainResultBean.SearchTrain) getItem((Integer) v.getTag());

                        FareEnqInput prevInput = new FareEnqInput();

                        prevInput.setClassCode(fareInput.getClassCode());
                        prevInput.setMonth(select.seatFareBeanNew.getSeatAvl().previous.nextMonth);
                        prevInput.setDay(select.seatFareBeanNew.getSeatAvl().previous.nextDay);
                        prevInput.setAge(fareInput.getAge());
                        prevInput.setConcCode(fareInput.getConcCode());
                        prevInput.setDestCode(fareInput.getDestCode());
                        prevInput.setQuota(fareInput.getQuota());
                        prevInput.setSrcCode(fareInput.getSrcCode());
                        prevInput.setTrainNo(fareInput.getTrainNo());

                        fareSeatSingleTrainTask = new FareSeatSingleTrainTask(context, prevInput, SixDaysTrainListAdapter.this);
                        fareSeatSingleTrainTask.execute();

                    } else {
                        InternetChecking.noInterNetToast(context);
                    }
                }
            });

            // setting typeface on holder mambers
            holder.tNameNumber.setTypeface(AppFonts.getRobotoLight(context));
            holder.src.setTypeface(AppFonts.getRobotoReguler(context));
            holder.dest.setTypeface(AppFonts.getRobotoReguler(context));
            holder.depArrTravelTime.setTypeface(AppFonts.getRobotoReguler(context));
            holder.sun.setTypeface(AppFonts.getRobotoReguler(context));
            holder.mon.setTypeface(AppFonts.getRobotoReguler(context));
            holder.tue.setTypeface(AppFonts.getRobotoReguler(context));
            holder.wed.setTypeface(AppFonts.getRobotoReguler(context));
            holder.thu.setTypeface(AppFonts.getRobotoReguler(context));
            holder.fri.setTypeface(AppFonts.getRobotoReguler(context));
            holder.sat.setTypeface(AppFonts.getRobotoReguler(context));
            holder.totalFareTextView.setTypeface(AppFonts.getRobotoReguler(context));
            holder.dOne.setTypeface(AppFonts.getRobotoReguler(context));
            holder.dTwo.setTypeface(AppFonts.getRobotoReguler(context));
            holder.dThree.setTypeface(AppFonts.getRobotoReguler(context));
            holder.dFour.setTypeface(AppFonts.getRobotoReguler(context));
            holder.dFive.setTypeface(AppFonts.getRobotoReguler(context));
            holder.dSix.setTypeface(AppFonts.getRobotoReguler(context));
            holder.sOne.setTypeface(AppFonts.getRobotoReguler(context));
            holder.sTwo.setTypeface(AppFonts.getRobotoReguler(context));
            holder.sThree.setTypeface(AppFonts.getRobotoReguler(context));
            holder.sFour.setTypeface(AppFonts.getRobotoReguler(context));
            holder.sFive.setTypeface(AppFonts.getRobotoReguler(context));
            holder.sSix.setTypeface(AppFonts.getRobotoReguler(context));

            convertView.setTag(holder);

            holder.superLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    clickedPosition = (Integer) v.getTag();

                    SearchTrainResultBean.SearchTrain searchTrain = (SearchTrainResultBean.SearchTrain) getItem(clickedPosition);

                    if (searchTrain.isSelected) {
                        buttonList = null;
                        searchTrain.activeClass = searchTrain.avlClasses.get(searchTrain.avlClasses.size() - 1);
                        searchTrain.isSelected = false;
                        notifyDataSetChanged();
                    } else {
                        startSeatAvlTask();
                    }
                }
            });

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.popupMenu.setTag(position);
        holder.superLayout.setTag(position);
        holder.next.setTag(position);
        holder.prev.setTag(position);

        SearchTrainResultBean.SearchTrain result = (SearchTrainResultBean.SearchTrain) getItem(position);

        // setting data on views ;

        boolean[] runningDays = result.runningDays;

        TextView[] daysArr = {holder.mon, holder.tue, holder.wed, holder.thu, holder.fri, holder.sat, holder.sun};

        for (int i = 0; i < runningDays.length && i < daysArr.length; i++) {

            if (runningDays[i]) {
                daysArr[i].setTextColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                daysArr[i].setTextColor(context.getResources().getColor(R.color.light_gray));
            }
        }

        String tNameNumber = result.trainNo + " - " + result.trainName;
        String srcName = result.srcStnName;
        String destName = result.destStnName;
        String depArrTime = result.deptTime + " - " + result.arrTime;
        String trainType = result.trainType;
        String duration = result.travelTime;

        holder.tNameNumber.setText(tNameNumber);
        holder.src.setText(srcName);
        holder.dest.setText(destName);
        holder.depArrTravelTime.setText(depArrTime + " ( " + duration + " )");
        holder.trainType.setText(trainType.toUpperCase());

        if (result.isSelected) {

            holder.popupMenu.setVisibility(View.VISIBLE);

            List<String> classList = result.avlClasses;

            buttonList = new ArrayList<>();

            if (classList != null && classList.size() > 0) {

                holder.classLayout.removeAllViews();

                for (int i = 0; i < classList.size(); i++) {

                    TextView classButton = new TextView(context);
                    classButton.setGravity(Gravity.CENTER);
                    classButton.setTypeface(AppFonts.getRobotoLight(context));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(75, 75);
                    layoutParams.setMargins(0, 0, 50, 0);
                    classButton.setLayoutParams(layoutParams);
                    classButton.setTextSize(15);
                    classButton.setBackgroundColor(context.getResources().getColor(R.color.white));
                    classButton.setText(classList.get(i));
                    classButton.setTextColor(context.getResources().getColor(R.color.light_gray));
                    classButton.setBackground(null);
                    classButton.setTextColor(context.getResources().getColor(R.color.black));
                    classButton.setOnClickListener(new ClickOnClasses(fareInput));

                    TrainClassTypeHolder cHolder = null;

                    if (result.activeClass.equals(classList.get(i))) {
                        classButton.setBackground(context.getResources().getDrawable(R.drawable.round_red_selected));
                        classButton.setTextColor(context.getResources().getColor(R.color.white));
                    } else {
                        classButton.setBackground(null);
                        classButton.setTextColor(context.getResources().getColor(R.color.black));
                    }

                    holder.classLayout.addView(classButton);

                    buttonList.add(cHolder);
                }
            }

            SeatFareBeanNew seatFareBeanNew = result.seatFareBeanNew;
            SeatFareBeanNew.FareBreakup fareBreakup = seatFareBeanNew.getFareBreakup();
            SeatFareBeanNew.SeatAvl seatAvl = seatFareBeanNew.getSeatAvl();

            List<SeatFareBeanNew.SeatAvl.AvlStatus> avlStatusList = seatAvl.avlStatusList;

            if (fareBreakup.netAmount == null || fareBreakup.netAmount.isEmpty()) {
                holder.totalFareTextView.setText(context.getResources().getString(R.string.rs_symbol) + " " + fareBreakup.totalAmount);
            } else {
                holder.totalFareTextView.setText(context.getResources().getString(R.string.rs_symbol) + " " + fareBreakup.netAmount);
            }

            TextView[] dates = {holder.dOne, holder.dTwo, holder.dThree, holder.dFour, holder.dFive, holder.dSix};
            TextView[] status = {holder.sOne, holder.sTwo, holder.sThree, holder.sFour, holder.sFive, holder.sSix};

            for (int i = 0; i < avlStatusList.size(); i++) {

                SeatFareBeanNew.SeatAvl.AvlStatus avlStatus = avlStatusList.get(i);

                dates[i].setText(avlStatus.date);
                status[i].setText(avlStatus.status[0] + " " + avlStatus.status[1]);

                if ((status[i]).getText().toString().contains("DEPART")) {
                    (status[i]).setBackground(context.getResources().getDrawable(R.drawable.black));
                    (status[i]).setText("Departed");
                } else if ((status[i]).getText().toString().contains("NOT AVAIL")) {
                    status[i].setText("NA");
                    (status[i]).setBackground(context.getResources().getDrawable(R.drawable.black));
                } else if ((status[i]).getText().toString().contains("AVAIL")) {
                    (status[i]).setBackground(context.getResources().getDrawable(R.drawable.green));
                    (status[i]).setText("AVL " + avlStatus.status[1]);
                } else if ((status[i]).getText().toString().contains("NOT AV")) {
                    (status[i]).setBackground(context.getResources().getDrawable(R.drawable.black));
                    (status[i]).setText("NA");
                } else if ((status[i]).getText().toString().contains("WL")) {
                    (status[i]).setBackground(context.getResources().getDrawable(R.drawable.light_red));
                } else if ((status[i]).getText().toString().contains("RAC")) {
                    (status[i]).setBackground(context.getResources().getDrawable(R.drawable.yellow));
                } else if ((status[i]).getText().toString().contains("CANCEL")) {
                    (status[i]).setBackground(context.getResources().getDrawable(R.drawable.black));
                } else if ((status[i]).getText().toString().toUpperCase().contains("CHARTING DONE") || (status[i]).getText().toString().toUpperCase().contains("CHART PREPARED")) {
                    (status[i]).setBackground(context.getResources().getDrawable(R.drawable.black));
                } else {
                    (status[i]).setTextColor(context.getResources().getColor(R.color.black));
                }
            }

            if (seatAvl.next.nextDay == null || seatAvl.next.nextDay.isEmpty()) {
                holder.next.setClickable(false);
                holder.next.setTextColor(context.getResources().getColor(R.color.light_gray));
            } else {
                holder.next.setClickable(true);
                holder.next.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }

            if (seatAvl.previous.nextDay == null || seatAvl.previous.nextDay.isEmpty()) {
                holder.prev.setClickable(false);
                holder.prev.setTextColor(context.getResources().getColor(R.color.light_gray));
            } else {
                holder.prev.setClickable(true);
                holder.prev.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }

            holder.hiddenLayout.setVisibility(View.VISIBLE);

        } else {

            holder.popupMenu.setVisibility(View.GONE);
            holder.hiddenLayout.setVisibility(View.GONE);
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }


    private static class ViewHolder {

        ImageView popupMenu, midArrow;
        LinearLayout superLayout, hiddenLayout, classLayout;
        TextView tNameNumber, src, dest, depArrTravelTime, trainType;
        TextView sun, mon, tue, wed, thu, fri, sat;
        TextView totalFareTextView;
        TextView dOne, dTwo, dThree, dFour, dFive, dSix;
        TextView sOne, sTwo, sThree, sFour, sFive, sSix;
        TextView next, prev;
    }


    public void startSeatAvlTask() {

        SearchTrainResultBean.SearchTrain selected = (SearchTrainResultBean.SearchTrain) getItem(clickedPosition);

        fareInput = new FareEnqInput();

        fareInput.setTrainNo(selected.trainNo);

        if(inputData.getDay() == null || inputData.getDay().isEmpty() || inputData.getMonth() == null || inputData.getMonth().isEmpty()) {
            String [] nextRunDay = UtilsMethods.getNextRunningDay(selected.runningDays) ;
            if(nextRunDay != null && nextRunDay.length > 0) {
                fareInput.setDay(nextRunDay[0]);
                fareInput.setMonth(nextRunDay[1]);
            }
        } else {
            fareInput.setDay(inputData.getDay());
            fareInput.setMonth(inputData.getMonth());
        }

        fareInput.setSrcCode(selected.srcStnCode);
        fareInput.setDestCode(selected.destStnCode);
        fareInput.setQuota(inputData.getQuota());
        if (selected.avlClasses != null && selected.avlClasses.size() > 0) {
            fareInput.setClassCode(selected.avlClasses.get(selected.avlClasses.size() - 1));
        } else {
            updateException("No Classes Found");
            return;
        }

        if(InternetChecking.isNetWorkOn(context)) {
            fareSeatSingleTrainTask = new FareSeatSingleTrainTask(context, fareInput, SixDaysTrainListAdapter.this);
            fareSeatSingleTrainTask.execute();
        } else {
            InternetChecking.noInterNetToast(context);
        }
    }


    @Override
    public void updateException(String exceptionMsg) {
        Toast.makeText(context, exceptionMsg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void updateSearchTrainUi(SeatFareBeanNew seatFareBeanNew, FareEnqInput fei) {

        for (int i = 0; i < searchTrainList.size(); i++) {
            if (i == clickedPosition) {
                ((SearchTrainResultBean.SearchTrain) getItem(i)).activeClass = fei.getClassCode();
                ((SearchTrainResultBean.SearchTrain) getItem(i)).isSelected = true;
                ((SearchTrainResultBean.SearchTrain) getItem(i)).seatFareBeanNew = seatFareBeanNew;
            } else {
                ((SearchTrainResultBean.SearchTrain) getItem(i)).isSelected = false;
                ((SearchTrainResultBean.SearchTrain) getItem(i)).seatFareBeanNew = null;
            }
        }

        /*if (buttonList != null) {

        }*/

        this.fareInput = fei;
        notifyDataSetChanged();
    }


    @Override
    public void updateTimeTableUI(TimeTableNewBean bean) {
        if (bean != null) {
            comm.respond(new RouteMapTabsFragment(bean));
        }
    }


    private class ClickOnPopUp implements View.OnClickListener {

        @Override
        public void onClick(final View v) {

            PopupMenu popup = new PopupMenu(context, v);
            popup.getMenuInflater().inflate(R.menu.avaiable_train_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    String title = item.getTitle().toString();
                    SearchTrainResultBean.SearchTrain searchTrain = (SearchTrainResultBean.SearchTrain) getItem((Integer) v.getTag());
                    if (title.equals(context.getResources().getString(R.string.time_table))) {
                        if (preferencesHandler.isAlreadyExists(searchTrain.trainNo)) {
                            TimeTableNewBean ttContainer = preferencesHandler.getTimeTableFromPref(searchTrain.trainNo);
                            comm.respond(new RouteMapTabsFragment(ttContainer));
                        } else {
                            if (InternetChecking.isNetWorkOn(context)) {
                                timeTableTask = new GetTimeTableTaskNew(context, searchTrain.trainNo, SixDaysTrainListAdapter.this);
                                timeTableTask.execute();
                            } else {
                                InternetChecking.noInterNetToast(context);
                            }
                        }
                    } else if (title.equals(context.getResources().getString(R.string.fare_breakup))) {
                        comm.respond(new FareBreakupFragmentFT(searchTrain.seatFareBeanNew.fareBreakup));
                    } else if (title.equals(context.getResources().getString(R.string.irctc_booking))) {
                        context.startActivity(new Intent(context, AutoLoginActivity.class));
                    }

                    return true ;
                }
            });
            popup.show();
        }
    }



    private class ClickOnClasses implements View.OnClickListener {

        FareEnqInput input;

        ClickOnClasses(FareEnqInput input) {
            this.input = input;
        }

        @Override
        public void onClick(View v) {

            if(InternetChecking.isNetWorkOn(context)) {

                String selectedClass = ((TextView) v).getText().toString();

                if(input == null) {
                    input = new FareEnqInput() ;
                }

                input.setClassCode(selectedClass);

                fareSeatSingleTrainTask = new FareSeatSingleTrainTask(context, input, SixDaysTrainListAdapter.this);
                fareSeatSingleTrainTask.execute();
            } else {
                InternetChecking.noInterNetToast(context) ;
            }
        }
    }
}
