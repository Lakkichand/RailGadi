package com.railgadi.adapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.async.DeletePnrTask;
import com.railgadi.async.RefreshAndUpdatePnrStatusTask;
import com.railgadi.beans.PnrPreferenceBean;
import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.beans.RefreshPnrBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.comparators.PnrListComparator;
import com.railgadi.fonts.AppFonts;
import com.railgadi.fragments.HomeFragment;
import com.railgadi.interfaces.IPnrDeleteCommunicator;
import com.railgadi.interfaces.IRefreshable;
import com.railgadi.preferences.MyLiveJourneyPreferences;
import com.railgadi.preferences.PnrPreferencesHandler;
import com.railgadi.preferences.RemovablePnrPreference;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.DateAndMore;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HomeTicketAdapter extends BaseAdapter implements IRefreshable, IPnrDeleteCommunicator {

    private RefreshAndUpdatePnrStatusTask refreshPnrTask;
    private HomeFragment homeFragment;
    private static int clickedKey;
    private Context context;
    private List<Map.Entry<String, PnrStatusNewBean>> allEntries;
    private DeletePnrTask deletePnrTask ;
    private RemovablePnrPreference removablePnrPref ;


    public HomeTicketAdapter(Context context, Map<String, PnrStatusNewBean> allUpcoming, HomeFragment homeFragment) {
        this.context = context;
        this.homeFragment = homeFragment;
        this.allEntries = new ArrayList<>(allUpcoming.entrySet());
        Collections.sort(this.allEntries, new PnrListComparator());
    }


    public void changeDataSet(Map<String, PnrStatusNewBean> allUpcoming) {

        this.allEntries = new ArrayList<>(allUpcoming.entrySet());
        Collections.sort(this.allEntries, new PnrListComparator());
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return allEntries.size();
    }


    @Override
    public Object getItem(int position) {
        return allEntries.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        final ViewHolder holder;
        if (convertView == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();

            //convertView = inflater.inflate(R.layout.home_screen_ticket_row, parent, false);
            convertView = inflater.inflate(R.layout.demo_home_ticket_row, parent, false);

            holder = new ViewHolder();


            // layouts
            holder.rootLayout = (LinearLayout) convertView.findViewById(R.id.root_layout);
            holder.frontLayout = (LinearLayout) convertView.findViewById(R.id.ticket_front_layout);
            holder.backLayout = (LinearLayout) convertView.findViewById(R.id.ticket_back_layout);


            // imageviews
            holder.homeTrainIcon = (ImageView) convertView.findViewById(R.id.home_train_icon);
            holder.pantryOne = (TextView) convertView.findViewById(R.id.home_ticket_pantry);
            holder.eCatering = (TextView) convertView.findViewById(R.id.home_ticket_internet);
            holder.sharefront = (ImageView) convertView.findViewById(R.id.home_front_share);
            holder.refreshfront = (ImageView) convertView.findViewById(R.id.home_front_refresh);
            holder.shareback = (ImageView) convertView.findViewById(R.id.home_back_share);
            holder.refreshback = (ImageView) convertView.findViewById(R.id.home_back_refresh);

            holder.homeTrainIcon.setImageDrawable(UtilsMethods.getSvgDrawable(context, R.drawable.home_train_svg));

            holder.sharefront.setImageDrawable(context.getResources().getDrawable(R.drawable.home_share));
            holder.shareback.setImageDrawable(context.getResources().getDrawable(R.drawable.home_share));
            holder.refreshfront.setImageDrawable(context.getResources().getDrawable(R.drawable.home_refresh));
            holder.refreshback.setImageDrawable(context.getResources().getDrawable(R.drawable.home_refresh));

            holder.sharefront.setTag(position);
            holder.shareback.setTag(position);
            holder.refreshfront.setTag(position);
            holder.refreshback.setTag(position);

            holder.sharefront.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PnrStatusNewBean bean = allEntries.get((Integer) v.getTag()).getValue() ;
                    String forShare = bean.getPnrNumber()+"\n"+bean.getMainStatus() ;
                    UtilsMethods.openShareIntent(context, forShare);
                }
            });

            holder.shareback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PnrStatusNewBean bean = allEntries.get((Integer) v.getTag()).getValue() ;
                    String forShare = bean.getPnrNumber()+"\n"+bean.getMainStatus() ;
                    UtilsMethods.openShareIntent(context, forShare);
                }
            });

            holder.refreshback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedKey = ((Integer) v.getTag());
                    if (InternetChecking.isNetWorkOn(context)) {
                        refreshPnrTask = new RefreshAndUpdatePnrStatusTask(context, allEntries.get(clickedKey).getValue().getPnrNumber(), HomeTicketAdapter.this);
                        refreshPnrTask.execute();
                    } else {
                        InternetChecking.noInterNetToast(context);
                    }
                }
            });
            holder.refreshfront.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (InternetChecking.isNetWorkOn(context)) {
                        clickedKey = ((Integer) v.getTag());
                        refreshPnrTask = new RefreshAndUpdatePnrStatusTask(context, allEntries.get(clickedKey).getValue().getPnrNumber(), HomeTicketAdapter.this);
                        refreshPnrTask.execute();
                    } else {
                        InternetChecking.noInterNetToast(context);
                    }
                }
            });

            // textviews of ticket
            holder.fromCode = (TextView) convertView.findViewById(R.id.from_station_code_home);
            holder.toCode = (TextView) convertView.findViewById(R.id.to_station_code_home);
            holder.fromName = (TextView) convertView.findViewById(R.id.from_station_name_home);
            holder.toName = (TextView) convertView.findViewById(R.id.to_station_name_home);
            holder.depDate = (TextView) convertView.findViewById(R.id.from_station_dep_date_home);
            holder.arrDate = (TextView) convertView.findViewById(R.id.to_station_arr_date_home);
            holder.depTime = (TextView) convertView.findViewById(R.id.from_station_dep_time_home);
            holder.arrTime = (TextView) convertView.findViewById(R.id.to_station_arr_time_home);
            holder.trainNumber = (TextView) convertView.findViewById(R.id.train_number_home);
            holder.quota = (TextView) convertView.findViewById(R.id.home_quota);
            holder.pnr = (TextView) convertView.findViewById(R.id.home_pnr_number);
            holder.status = (TextView) convertView.findViewById(R.id.home_status);
            holder.duration = (TextView) convertView.findViewById(R.id.home_duration);
            holder.classType = (TextView) convertView.findViewById(R.id.home_class);
            holder.lastUpdate = (TextView) convertView.findViewById(R.id.home_last_updated_light);
            holder.charting = (TextView) convertView.findViewById(R.id.home_charting);
            holder.classLight = (TextView) convertView.findViewById(R.id.home_class_light);
            holder.durationLight = (TextView) convertView.findViewById(R.id.home_duration_light);
            holder.pnrLight = (TextView) convertView.findViewById(R.id.home_pnr_light);
            holder.quotaLight = (TextView) convertView.findViewById(R.id.home_quota_light);
            holder.servicesLight = (TextView) convertView.findViewById(R.id.home_services_light);
            holder.statusLight = (TextView) convertView.findViewById(R.id.home_status_light);

            //listviews
            holder.homeTicketPList = (ListView) convertView.findViewById(R.id.home_ticket_psgr_list);

            holder.passengerDetail = (TextView) convertView.findViewById(R.id.passenger_details_button_light);
            holder.ticketDetails = (TextView) convertView.findViewById(R.id.ticket_details_button);


            // setting typeface
            holder.fromCode.setTypeface(AppFonts.getRobotoLight(context));
            holder.toCode.setTypeface(AppFonts.getRobotoLight(context));

            holder.passengerDetail.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    setAnimation(holder.frontLayout, holder.backLayout, pos, true);
                }
            });

            holder.ticketDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    setAnimation(holder.backLayout, holder.frontLayout, pos, false);
                }
            });

            holder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    clickedKey = (Integer) v.getTag();
                    UtilsMethods.confirmDeleteDialog(context, HomeTicketAdapter.this);
                    return true;
                }
            });

            convertView.setTag(holder);
            convertView.setTag(R.id.passenger_details_button_light, holder.passengerDetail);
            convertView.setTag(R.id.ticket_details_button, holder.ticketDetails);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.passengerDetail.setTag(position);
        holder.ticketDetails.setTag(position);
        holder.rootLayout.setTag(position);

        PnrStatusNewBean ps = allEntries.get(position).getValue();

        PnrStatusNewBean.TrainInfo ti = ps.getTrainInfo();

        TimeTableNewBean timetable = ps.getTrainDetailRoute();

        try {

            holder.trainNumber.setText(ti.trainNumber);
            holder.fromCode.setText(ps.getBoardFromCode());
            holder.toCode.setText(ps.getBoardToCode());
            holder.fromName.setText(ps.getBoardFromName());
            holder.toName.setText(ps.getBoardToName());
            holder.depDate.setText(DateAndMore.formatDateToString(ti.srcDepDate, DateAndMore.DAY_DATE));
            holder.depTime.setText(ti.srcDepTime);
            holder.arrDate.setText(DateAndMore.formatDateToString(ti.destArrDate, DateAndMore.DAY_DATE));
            holder.arrTime.setText(ti.destArrTime);
            holder.duration.setText(ti.duration);
            holder.quota.setText(ps.getPassengerList().get(0).quotaType);
            holder.pnr.setText(new StringBuffer(ps.getPnrNumber()).insert(3, "-").insert(7, "-").toString());
            holder.lastUpdate.setText("Last updated @ " + DateAndMore.formatDateToString(ps.getLastTimeCheck(), DateAndMore.SUPER_DATE_FORMAT));
            holder.classType.setText(ps.getClassType());
            holder.charting.setText(ps.getChartingStatus());

            boolean[] avlPantries = timetable.getPantries();

            if(avlPantries.length == 3) {

                if (avlPantries[0]) {
                    holder.pantryOne.setBackground(context.getResources().getDrawable(R.drawable.home_pantry_rest));
                    holder.pantryOne.setText("");
                } else {
                    holder.pantryOne.setBackground(null);
                    holder.pantryOne.setText("");
                }
                if (avlPantries[2]) {
                    holder.eCatering.setBackground(context.getResources().getDrawable(R.drawable.home_internet_rest));
                    holder.eCatering.setText("");
                } else {
                    holder.eCatering.setBackground(null);
                    holder.eCatering.setText("");
                }
            }

            String statusString = ps.getMainStatus();
            holder.status.setText(statusString);

            if (statusString.contains("W/L")) {
                holder.homeTrainIcon.setImageDrawable(UtilsMethods.getSvgDrawable(context, R.drawable.home_train_wl_svg));
                holder.frontLayout.setBackground(context.getResources().getDrawable(R.drawable.wl_ticket_bg));
                holder.backLayout.setBackground(context.getResources().getDrawable(R.drawable.wl_ticket_bg));
                holder.passengerDetail.setBackground(context.getResources().getDrawable(R.drawable.wl_button_selector));
                holder.ticketDetails.setBackground(context.getResources().getDrawable(R.drawable.wl_button_selector));

                holder.fromName.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.toName.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.depDate.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.depTime.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.arrDate.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.arrTime.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.lastUpdate.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.passengerDetail.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.ticketDetails.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.statusLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.servicesLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.quotaLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.pnrLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.classLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.durationLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
            } else if (statusString.contains("CNF") || statusString.contains("Confirmed")) {
                holder.homeTrainIcon.setImageDrawable(UtilsMethods.getSvgDrawable(context, R.drawable.home_train_cnf_svg));
                holder.frontLayout.setBackground(context.getResources().getDrawable(R.drawable.cnf_ticket_bg));
                holder.backLayout.setBackground(context.getResources().getDrawable(R.drawable.cnf_ticket_bg));
                holder.passengerDetail.setBackground(context.getResources().getDrawable(R.drawable.cnf_button_selector));
                holder.ticketDetails.setBackground(context.getResources().getDrawable(R.drawable.cnf_button_selector));

                holder.fromName.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.toName.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.depDate.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.depTime.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.arrDate.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.arrTime.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.lastUpdate.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.passengerDetail.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.ticketDetails.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.statusLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.servicesLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.quotaLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.pnrLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.classLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.durationLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));

            } else if (statusString.contains("RAC")) {
                holder.homeTrainIcon.setImageDrawable(UtilsMethods.getSvgDrawable(context, R.drawable.home_train_rac_svg));
                holder.frontLayout.setBackground(context.getResources().getDrawable(R.drawable.rac_ticket_bg));
                holder.backLayout.setBackground(context.getResources().getDrawable(R.drawable.rac_ticket_bg));
                holder.passengerDetail.setBackground(context.getResources().getDrawable(R.drawable.rac_button_selector));
                holder.ticketDetails.setBackground(context.getResources().getDrawable(R.drawable.rac_button_selector));

                holder.fromName.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.toName.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.depDate.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.depTime.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.arrDate.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.arrTime.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.lastUpdate.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.passengerDetail.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.ticketDetails.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.statusLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.servicesLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.quotaLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.pnrLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.classLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.durationLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));

            } else if (statusString.contains("CAN") || statusString.contains("Can/Mod")) {
                holder.homeTrainIcon.setImageDrawable(UtilsMethods.getSvgDrawable(context, R.drawable.home_train_can_svg));
                holder.frontLayout.setBackground(context.getResources().getDrawable(R.drawable.can_ticket_bg));
                holder.backLayout.setBackground(context.getResources().getDrawable(R.drawable.can_ticket_bg));
                holder.passengerDetail.setBackground(context.getResources().getDrawable(R.drawable.can_button_selector));
                holder.ticketDetails.setBackground(context.getResources().getDrawable(R.drawable.can_button_selector));

                holder.fromName.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.toName.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.depDate.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.depTime.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.arrDate.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.arrTime.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.lastUpdate.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.passengerDetail.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.ticketDetails.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.statusLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.servicesLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.quotaLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.pnrLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.classLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));
                holder.durationLight.setTextColor(Color.argb(Constants.HOME_TICKET_ALPHA, 0, 0, 0));

            } else {
                holder.frontLayout.setBackground(context.getResources().getDrawable(R.drawable.cnf_ticket_bg));
                holder.backLayout.setBackground(context.getResources().getDrawable(R.drawable.cnf_ticket_bg));
            }

            PnrHomePsgrAdatper adapter = null;

            if (ps.getChartingStatus().equalsIgnoreCase("charting done") || ps.getChartingStatus().equalsIgnoreCase("chart prepared")) {
                adapter = new PnrHomePsgrAdatper(context, ps.getPassengerList(), true);
            } else {
                adapter = new PnrHomePsgrAdatper(context, ps.getPassengerList(), false);
            }

            holder.homeTicketPList.setAdapter(adapter);

        } catch (Exception e) {

            e.printStackTrace();
        }

        if (ps.isFirstVisible() == true) {
            holder.frontLayout.setVisibility(View.VISIBLE);
            holder.backLayout.setVisibility(View.GONE);
        } else {
            holder.frontLayout.setVisibility(View.GONE);
            holder.backLayout.setVisibility(View.VISIBLE);
        }

        return convertView;
    }


    @Override
    public void wantToDelete(boolean flag) {

        if (flag) {

            String pnr = allEntries.get(clickedKey).getValue().getPnrNumber();

            if(InternetChecking.isNetWorkOn(context)) {
                List<String> pnrList = new ArrayList<>(1) ;
                pnrList.add(pnr);
                deletePnrTask = new DeletePnrTask(context, pnrList, true) ;
                deletePnrTask.execute() ;
            } else {
                removablePnrPref = new RemovablePnrPreference((Activity) context) ;
                removablePnrPref.addToRemovable(pnr) ;
            }

            PnrPreferencesHandler preferencesHandler = new PnrPreferencesHandler((Activity) context);
            preferencesHandler.removeFromPreferences(pnr);
            allEntries.remove(clickedKey);
            notifyDataSetChanged();

            MyLiveJourneyPreferences livePref = new MyLiveJourneyPreferences((Activity) context);
            livePref.removeLiveJourneyData();

            if (allEntries.size() == 0) {
                homeFragment.setHorizontalAdapter();
            }
        }
    }


    @Override
    public void updateRefreshPnrUI(RefreshPnrBean refreshPnrBean) {

        try {

            PnrStatusNewBean selectedPnr = allEntries.get(clickedKey).getValue();
            selectedPnr.setLastTimeCheck(refreshPnrBean.getLastTimeCheck());
            selectedPnr.setPassengerList(refreshPnrBean.getPassengerList());
            selectedPnr.setPnrNumber(refreshPnrBean.getPnrNumber());
            selectedPnr.setChartingStatus(refreshPnrBean.getCharting());
            String statusString = refreshPnrBean.getPassengerList().get(refreshPnrBean.getPassengerList().size()-1).status ;
            selectedPnr.setMainStatus(statusString);

            Date pnrDate = selectedPnr.getTrainInfo().destArrDate;

            PnrPreferenceBean bean = new PnrPreferenceBean();
            bean.setPnrNumber(selectedPnr.getPnrNumber());
            bean.setPnrObject(selectedPnr);

            if (pnrDate.compareTo(new Date()) == -1) {
                bean.setFlag(Constants.COMPLETED);
            } else if (pnrDate.compareTo(new Date()) == 1) {
                bean.setFlag(Constants.UPCOMING);
            } else {
                bean.setFlag(Constants.UPCOMING);
            }

            PnrPreferencesHandler ph = new PnrPreferencesHandler((Activity) context);
            allEntries.get(clickedKey).setValue(selectedPnr);
            allEntries.get(clickedKey).getValue().setFirstVisible(true);
            ph.saveToPreferences(bean);
            notifyDataSetChanged();

        } catch (Exception e) {

        }
    }


    private static class ViewHolder {

        TextView fromName, fromCode, toName, toCode, depDate, arrDate, depTime, arrTime,
                trainNumber, quota, pnr, status, duration, classType,
                lastUpdate, charting, passengerDetail, ticketDetails,
                quotaLight, durationLight, servicesLight, statusLight, classLight, pnrLight;

        ImageView homeTrainIcon, sharefront, refreshfront, shareback, refreshback ;

        TextView pantryOne, eCatering ;

        ListView homeTicketPList;

        LinearLayout rootLayout, frontLayout, backLayout;
    }


    private void setAnimation(final View invisibleView, final View ShowView, int pos, boolean isFirstViewClicked) {

        if (isFirstViewClicked) {
            for (int i = 0; i < allEntries.size(); i++) {

                if (i == pos) {
                    allEntries.get(i).getValue().setFirstVisible(false);
                } else {
                    allEntries.get(i).getValue().setFirstVisible(true);
                }
            }
        } else {
            for (int i = 0; i < allEntries.size(); i++) {

                allEntries.get(i).getValue().setFirstVisible(true);
            }
        }

        ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(context, R.anim.flipper);
        anim.setTarget(invisibleView);
        anim.setDuration(700);
        anim.start();
        anim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                invisibleView.setVisibility(View.GONE);
                ShowView.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
    }

}
