package com.railgadi.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.DivertedListAdapterNew;
import com.railgadi.adapters.FullyCancelledTrainAdapter;
import com.railgadi.adapters.PartiallyCancelledTrainAdapter;
import com.railgadi.adapters.RescheduledListAdapterNew;
import com.railgadi.async.CancelledTrainTaskNew;
import com.railgadi.async.DivertedTrainTaskNew;
import com.railgadi.async.RescheduledTrainTaskNew;
import com.railgadi.beans.CancelledTrainBean;
import com.railgadi.beans.DivertedTrainBean;
import com.railgadi.beans.RescheduledTrainBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IResCanDivMarker;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.InternetChecking;

import java.util.List;

public class ResDivCanFragmentNew extends Fragment {

    private View rootView;

    private static String NO_DATA_FOUND = "NO DATA FOUND";

    private TextView res, can, div, yesterday, today, tomorrow, emptyTextView ;
    private LinearLayout resLayout, canLayout, divLayout ;
    private LinearLayout fullPartialLayout ;
    private View resView, canView, divView ;
    private RadioButton fullyRadio, partiallyRadio ;
    private RadioGroup radioGroup ;
    private ListView listview ;

    private RescheduledTrainBean resBean;
    private CancelledTrainBean canBean;
    private DivertedTrainBean divBean;

    private RescheduledListAdapterNew resAdapter;
    private DivertedListAdapterNew divAdapter;
    private FullyCancelledTrainAdapter fullyCancelledAdapter;
    private PartiallyCancelledTrainAdapter partiallyCancelledAdapter;

    private RescheduledTrainTaskNew rescheduledTask;
    private DivertedTrainTaskNew divertedTask;
    private CancelledTrainTaskNew cancelledTask;

    private String selectedType;
    private boolean isCancel;

    private byte day = 1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (InternetChecking.isNetWorkOn(getActivity())) {
            selectedType = Constants.RES;
            rescheduledTask = new RescheduledTrainTaskNew(getActivity(), ResDivCanFragmentNew.this);
            rescheduledTask.execute();
        } else {
            InternetChecking.noInterNetToast(getActivity());
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (resBean != null) {
            selectedType = Constants.RES;
            if (resBean.getTodayList() != null && resBean.getTodayList().size() > 0) {
                updateRescheduleAdapter(resBean.getTodayList());
                updateTodayTomorrow(today, tomorrow, yesterday);
                updateViews();
            }
        }
    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        if (rescheduledTask != null) {
            rescheduledTask.cancel(true);
        }
        if (divertedTask != null) {
            divertedTask.cancel(true);
        }
        if (cancelledTask != null) {
            cancelledTask.cancel(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.res_can_div_frag_new, container, false);

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.rec_div_can).toUpperCase());

        initializeAllViews();
        setDataOnViews();

        return rootView;
    }

    private void initializeAllViews() {

        res = (TextView) rootView.findViewById(R.id.rescheduled_text);
        can = (TextView) rootView.findViewById(R.id.cancelled_text);
        div = (TextView) rootView.findViewById(R.id.diverted_text);

        yesterday = (TextView) rootView.findViewById(R.id.yesterday);
        today = (TextView) rootView.findViewById(R.id.today);
        tomorrow = (TextView) rootView.findViewById(R.id.tomorrow);

        emptyTextView = (TextView) rootView.findViewById(R.id.empty_text_view) ;

        res.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        can.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        div.setTypeface(AppFonts.getRobotoMedium(getActivity()));
        emptyTextView.setTypeface(AppFonts.getRobotoMedium(getActivity()));

        radioGroup = (RadioGroup) rootView.findViewById(R.id.cancelled_radio_group);

        fullyRadio = (RadioButton) rootView.findViewById(R.id.fully_radio);
        partiallyRadio = (RadioButton) rootView.findViewById(R.id.partially_radio);

        resLayout = (LinearLayout) rootView.findViewById(R.id.rescheduled_layout);
        canLayout = (LinearLayout) rootView.findViewById(R.id.cancelled_layout);
        divLayout = (LinearLayout) rootView.findViewById(R.id.diverted_layout);

        fullPartialLayout = (LinearLayout) rootView.findViewById(R.id.full_partial_layout);

        resView = rootView.findViewById(R.id.rescheduled_view);
        canView = rootView.findViewById(R.id.cancelled_view);
        divView = rootView.findViewById(R.id.diverted_view);

        listview = (ListView) rootView.findViewById(R.id.res_can_div_list);
    }


    private void setDataOnViews() {

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {

                    case R.id.fully_radio: {

                        switch (day) {
                            case 0: {

                                setFullyCancelledAdapter(canBean.getYesterday().fullyCancelledList);

                                /*if (canBean.getYesterday().fullyCancelledList != null) {
                                    setFullyCancelledAdapter(canBean.getYesterday().fullyCancelledList);
                                } else {
                                    Toast.makeText(getActivity(), Constants.NO_FULLY_CANCELLED_TRAIN_FOUND, Toast.LENGTH_SHORT).show();
                                }*/
                                break;
                            }
                            case 1: {

                                setFullyCancelledAdapter(canBean.getToday().fullyCancelledList);

                                /*if (canBean.getToday().fullyCancelledList != null) {
                                    setFullyCancelledAdapter(canBean.getToday().fullyCancelledList);
                                } else {
                                    Toast.makeText(getActivity(), Constants.NO_FULLY_CANCELLED_TRAIN_FOUND, Toast.LENGTH_SHORT).show();
                                }*/
                                break;
                            }
                            case 2: {

                                setFullyCancelledAdapter(canBean.getTomorrow().fullyCancelledList);

                                /*if (canBean.getTomorrow().fullyCancelledList != null) {
                                    setFullyCancelledAdapter(canBean.getTomorrow().fullyCancelledList);
                                } else {
                                    Toast.makeText(getActivity(), Constants.NO_FULLY_CANCELLED_TRAIN_FOUND, Toast.LENGTH_SHORT).show();
                                }*/
                                break;
                            }
                        }
                        break;
                    }
                    case R.id.partially_radio: {

                        switch (day) {
                            case 0: {

                                setPartiallyCancelledAdapter(canBean.getYesterday().partiallyCancelledList);

                                /*if (canBean.getYesterday().partiallyCancelledList != null) {
                                    setPartiallyCancelledAdapter(canBean.getYesterday().partiallyCancelledList);
                                } else {
                                    Toast.makeText(getActivity(), Constants.NO_PARTIALLY_CANCELLED_TRAIN_FOUND, Toast.LENGTH_SHORT).show();
                                }*/
                                break;
                            }
                            case 1: {

                                setPartiallyCancelledAdapter(canBean.getToday().partiallyCancelledList);

                                /*if (canBean.getToday().partiallyCancelledList != null) {
                                    setPartiallyCancelledAdapter(canBean.getToday().partiallyCancelledList);
                                } else {
                                    Toast.makeText(getActivity(), Constants.NO_PARTIALLY_CANCELLED_TRAIN_FOUND, Toast.LENGTH_SHORT).show();
                                }*/
                                break;
                            }
                            case 2: {

                                setPartiallyCancelledAdapter(canBean.getTomorrow().partiallyCancelledList);

                                /*if (canBean.getTomorrow().partiallyCancelledList != null) {
                                    setPartiallyCancelledAdapter(canBean.getTomorrow().partiallyCancelledList);
                                } else {
                                    Toast.makeText(getActivity(), Constants.NO_PARTIALLY_CANCELLED_TRAIN_FOUND, Toast.LENGTH_SHORT).show();
                                }*/
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        });

        resLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCancel = false;
                listview.setAdapter(null);
                selectedType = Constants.RES;
                if (resBean != null) {
                    if (resBean.getTodayList() != null) {
                        updateUI(resBean);
                        return;
                    }
                }
                rescheduledTask = new RescheduledTrainTaskNew(getActivity(), ResDivCanFragmentNew.this);
                rescheduledTask.execute();
            }
        });

        canLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                day = 1;
                isCancel = true;
                fullyRadio.setChecked(true);
                partiallyRadio.setChecked(false);

                listview.setAdapter(null);
                selectedType = Constants.CAN;
                if (canBean != null) {
                    updateUI(canBean);
                    return;
                }

                cancelledTask = new CancelledTrainTaskNew(getActivity(), ResDivCanFragmentNew.this);
                cancelledTask.execute();
            }
        });

        divLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCancel = false;
                listview.setAdapter(null);
                selectedType = Constants.DIV;
                if (divBean != null) {
                    if (divBean.getTodayList() != null) {
                        updateUI(divBean);
                        return;
                    }
                }

                divertedTask = new DivertedTrainTaskNew(getActivity(), ResDivCanFragmentNew.this);
                divertedTask.execute();
            }
        });

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                day = 1;

                switch (selectedType) {

                    case Constants.RES: {

                        List<RescheduledTrainBean.RescheduledData> todayList = resBean.getTodayList();
                        /*if (todayList != null && todayList.size() > 0) {
                            updateRescheduleAdapter(todayList);
                            updateTodayTomorrow(today, tomorrow, yesterday);
                        } else {
                            Toast.makeText(getActivity(), NO_DATA_FOUND, Toast.LENGTH_SHORT).show();
                        }*/

                        updateRescheduleAdapter(todayList);
                        updateTodayTomorrow(today, tomorrow, yesterday);

                        break;
                    }
                    case Constants.CAN: {

                        if (isCancel) {
                            fullyRadio.setChecked(true);
                            partiallyRadio.setChecked(false);
                        }

                        List<CancelledTrainBean.FullyCancelled> todayList = canBean.getToday().fullyCancelledList;
                        /*if (todayList != null && todayList.size() > 0) {
                            setFullyCancelledAdapter(todayList);
                            updateTodayTomorrow(today, yesterday, tomorrow);
                        } else {
                            Toast.makeText(getActivity(), NO_DATA_FOUND, Toast.LENGTH_SHORT).show();
                        }*/

                        setFullyCancelledAdapter(todayList);
                        updateTodayTomorrow(today, yesterday, tomorrow);

                        break;
                    }
                    case Constants.DIV: {

                        List<DivertedTrainBean.DivertedData> todayList = divBean.getTodayList();
                        /*if (todayList != null && todayList.size() > 0) {
                            updateDivertedAdapter(todayList);
                            updateTodayTomorrow(today, tomorrow, yesterday);
                        } else {
                            Toast.makeText(getActivity(), NO_DATA_FOUND, Toast.LENGTH_SHORT).show();
                        }*/

                        updateDivertedAdapter(todayList);
                        updateTodayTomorrow(today, tomorrow, yesterday);

                        break;
                    }
                }
            }
        });

        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                day = 2;

                switch (selectedType) {

                    case Constants.RES: {

                        if (res != null) {

                            List<RescheduledTrainBean.RescheduledData> tomorrowList = resBean.getTomorrowList();
                            /*if (tomorrowList != null && tomorrowList.size() > 0) {
                                updateRescheduleAdapter(tomorrowList);
                                updateTodayTomorrow(tomorrow, today, yesterday);
                            } else {
                                Toast.makeText(getActivity(), NO_DATA_FOUND, Toast.LENGTH_SHORT).show();
                            }*/

                            updateRescheduleAdapter(tomorrowList);
                            updateTodayTomorrow(tomorrow, today, yesterday);
                        }

                        break;
                    }
                    case Constants.CAN: {

                        if (isCancel) {
                            fullyRadio.setChecked(true);
                            partiallyRadio.setChecked(false);
                        }

                        if (canBean != null) {

                            List<CancelledTrainBean.FullyCancelled> tomorrowList = canBean.getTomorrow().fullyCancelledList;
                            /*if (tomorrowList != null && tomorrowList.size() > 0) {
                                setFullyCancelledAdapter(tomorrowList);
                                updateTodayTomorrow(tomorrow, yesterday, today);
                            } else {
                                Toast.makeText(getActivity(), NO_DATA_FOUND, Toast.LENGTH_SHORT).show();
                            }*/

                            setFullyCancelledAdapter(tomorrowList);
                            updateTodayTomorrow(tomorrow, yesterday, today);
                        }
                        break;
                    }
                    case Constants.DIV: {

                        if (divBean != null) {
                            List<DivertedTrainBean.DivertedData> tomorrowList = divBean.getTomorrowList();
                            /*if (tomorrowList != null && tomorrowList.size() > 0) {
                                updateDivertedAdapter(tomorrowList);
                                updateTodayTomorrow(tomorrow, today, yesterday);
                            } else {
                                Toast.makeText(getActivity(), NO_DATA_FOUND, Toast.LENGTH_SHORT).show();
                            }*/

                            updateDivertedAdapter(tomorrowList);
                            updateTodayTomorrow(tomorrow, today, yesterday);
                        }

                        break;
                    }
                }
            }
        });

        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                day = 0;

                switch (selectedType) {

                    case Constants.RES: {

                        List<RescheduledTrainBean.RescheduledData> yesterdayList = resBean.getYesterdayList();
                        updateRescheduleAdapter(yesterdayList);
                        updateTodayTomorrow(yesterday, today, tomorrow);

                        /*if(yesterdayList != null && yesterdayList.size() > 0) {
                            updateRescheduleAdapter(yesterdayList) ;
                            updateTodayTomorrow(yesterday, today, tomorrow);
                        } else {
                            Toast.makeText(getActivity(), NO_DATA_FOUND, Toast.LENGTH_SHORT).show() ;
                        }*/

                        break;
                    }
                    case Constants.CAN: {

                        if (isCancel) {
                            fullyRadio.setChecked(true);
                            partiallyRadio.setChecked(false);
                        }

                        List<CancelledTrainBean.FullyCancelled> yesterdayList = canBean.getYesterday().fullyCancelledList;
                        setFullyCancelledAdapter(yesterdayList);
                        updateTodayTomorrow(yesterday, today, tomorrow);

                        /*if (yesterdayList != null && yesterdayList.size() > 0) {
                            setFullyCancelledAdapter(yesterdayList);
                            updateTodayTomorrow(yesterday, today, tomorrow);
                        } else {
                            Toast.makeText(getActivity(), NO_DATA_FOUND, Toast.LENGTH_SHORT).show();
                        }*/

                        break;
                    }
                    case Constants.DIV: {

                        List<DivertedTrainBean.DivertedData> yesterdayList = divBean.getYesterdayList();
                        updateDivertedAdapter(yesterdayList);
                        updateTodayTomorrow(yesterday, today, tomorrow);

                        /*if (yesterdayList != null && yesterdayList.size() > 0) {
                            updateDivertedAdapter(yesterdayList);
                            updateTodayTomorrow(yesterday, today, tomorrow);
                        } else {
                            Toast.makeText(getActivity(), NO_DATA_FOUND, Toast.LENGTH_SHORT).show();
                        }*/

                        break;
                    }
                }
            }
        });
    }


    private void updateTodayTomorrow(TextView one, TextView two, TextView three) {

        one.setBackground(getActivity().getResources().getDrawable(R.drawable.find_train_button_selector));
        one.setTextColor(getActivity().getResources().getColor(R.color.white));
        two.setBackground(null);
        two.setTextColor(getActivity().getResources().getColor(R.color.black));
        three.setBackground(null);
        three.setTextColor(getActivity().getResources().getColor(R.color.black));
    }


    public void updateException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show();
    }


    public void updateUI(IResCanDivMarker bean) {

        try {
            switch (selectedType) {

                case Constants.RES: {
                    this.resBean = (RescheduledTrainBean) bean;
                    updateRescheduleAdapter(((RescheduledTrainBean) bean).getTodayList());
                    updateTodayTomorrow(today, tomorrow, yesterday);
                    updateViews();
                    break;
                }
                case Constants.DIV: {
                    this.divBean = (DivertedTrainBean) bean;
                    updateDivertedAdapter(((DivertedTrainBean) bean).getTodayList());
                    updateTodayTomorrow(today, tomorrow, yesterday);
                    updateViews();
                    break;
                }
                case Constants.CAN: {
                    this.canBean = (CancelledTrainBean) bean;
                    setFullyCancelledAdapter(this.canBean.getToday().fullyCancelledList);
                    updateTodayTomorrow(today, tomorrow, yesterday);
                    updateViews();
                    break;
                }
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), NO_DATA_FOUND, Toast.LENGTH_SHORT).show();
        }
    }


    private void updateRescheduleAdapter(List<RescheduledTrainBean.RescheduledData> list) {

        /*fullPartialLayout.setVisibility(View.GONE);

        if (resAdapter == null) {
            resAdapter = new RescheduledListAdapterNew(getActivity(), list);
            listview.setAdapter(resAdapter);
        } else {
            resAdapter.changeDataSet(list);
            listview.setAdapter(resAdapter);
        }

        Toast.makeText(getActivity(), list.size() + " train found", Toast.LENGTH_SHORT).show();*/

        if(list != null && list.size() > 0) {
            listview.setVisibility(View.VISIBLE);
            if (resAdapter == null) {
                resAdapter = new RescheduledListAdapterNew(getActivity(), list);
                listview.setAdapter(resAdapter);
            } else {
                resAdapter.changeDataSet(list);
                listview.setAdapter(resAdapter);
            }
        } else {
            //Toast.makeText(getActivity(), NO_DATA_FOUND, Toast.LENGTH_SHORT).show() ;
            listview.setAdapter(null);
            listview.setVisibility(View.GONE);
        }
    }

    private void updateDivertedAdapter(List<DivertedTrainBean.DivertedData> list) {

        /*fullPartialLayout.setVisibility(View.GONE);

        if (divAdapter == null) {
            divAdapter = new DivertedListAdapterNew(getActivity(), list);
            listview.setAdapter(divAdapter);
        } else {
            divAdapter.changeDataSet(list);
            listview.setAdapter(divAdapter);
        }

        Toast.makeText(getActivity(), list.size() + " train found", Toast.LENGTH_SHORT).show();*/

        fullPartialLayout.setVisibility(View.GONE);

        if(list != null && list.size() > 0) {
            listview.setVisibility(View.VISIBLE);
            if (divAdapter == null) {
                divAdapter = new DivertedListAdapterNew(getActivity(), list);
                listview.setAdapter(divAdapter);
            } else {
                divAdapter.changeDataSet(list);
                listview.setAdapter(divAdapter);
            }
        }
        else {
            listview.setAdapter(null) ;
            listview.setVisibility(View.GONE);
        }
    }


    private void setFullyCancelledAdapter(List<CancelledTrainBean.FullyCancelled> data) {

        /*fullPartialLayout.setVisibility(View.VISIBLE);

        if (fullyCancelledAdapter != null) {
            fullyCancelledAdapter.changeDataSet(data);
            listview.setAdapter(fullyCancelledAdapter);
        } else {
            fullyCancelledAdapter = new FullyCancelledTrainAdapter(getActivity(), data);
            listview.setAdapter(fullyCancelledAdapter);
        }

        Toast.makeText(getActivity(), data.size() + " train found", Toast.LENGTH_SHORT).show();*/

        fullPartialLayout.setVisibility(View.VISIBLE);

        if(data != null && data.size() > 0) {
            listview.setVisibility(View.VISIBLE);
            if (fullyCancelledAdapter != null) {
                fullyCancelledAdapter.changeDataSet(data);
                listview.setAdapter(fullyCancelledAdapter);
            } else {
                fullyCancelledAdapter = new FullyCancelledTrainAdapter(getActivity(), data);
                listview.setAdapter(fullyCancelledAdapter);
            }
        }
        else {
            listview.setAdapter(null) ;
            listview.setVisibility(View.GONE);
        }
    }


    private void setPartiallyCancelledAdapter(List<CancelledTrainBean.PartiallyCancelled> data) {

        /*fullPartialLayout.setVisibility(View.VISIBLE);

        if (partiallyCancelledAdapter != null) {
            partiallyCancelledAdapter.changeDataSet(data);
            listview.setAdapter(partiallyCancelledAdapter);
        } else {
            partiallyCancelledAdapter = new PartiallyCancelledTrainAdapter(getActivity(), data);
            listview.setAdapter(partiallyCancelledAdapter);
        }

        Toast.makeText(getActivity(), data.size() + " train found", Toast.LENGTH_SHORT).show();*/


        fullPartialLayout.setVisibility(View.VISIBLE);

        if(data != null && data.size() > 0) {
            listview.setVisibility(View.VISIBLE);
            if (partiallyCancelledAdapter != null) {
                partiallyCancelledAdapter.changeDataSet(data);
                listview.setAdapter(partiallyCancelledAdapter);
            } else {
                partiallyCancelledAdapter = new PartiallyCancelledTrainAdapter(getActivity(), data);
                listview.setAdapter(partiallyCancelledAdapter);
            }
        } else {
            //Toast.makeText(getActivity(), data.size() + " train found", Toast.LENGTH_SHORT).show();
            listview.setAdapter(null) ;
            listview.setVisibility(View.GONE);
        }
    }


    private void updateViews() {

        switch (selectedType) {

            case Constants.RES: {

                resAdapter = null;

                resLayout.setClickable(false);
                canLayout.setClickable(true);
                divLayout.setClickable(true);

                resView.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                canView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                divView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));

                res.setTextColor(getActivity().getResources().getColor(R.color.white));
                can.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));
                div.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));

                break;
            }

            case Constants.CAN: {

                fullyCancelledAdapter = null;
                partiallyCancelledAdapter = null;

                resLayout.setClickable(true);
                canLayout.setClickable(false);
                divLayout.setClickable(true);

                resView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                canView.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                divView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));

                res.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));
                can.setTextColor(getActivity().getResources().getColor(R.color.white));
                div.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));

                break;
            }

            case Constants.DIV: {

                divAdapter = null;

                resLayout.setClickable(true);
                canLayout.setClickable(true);
                divLayout.setClickable(false);

                resView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                canView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                divView.setBackgroundColor(getActivity().getResources().getColor(R.color.white));

                res.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));
                can.setTextColor(getActivity().getResources().getColor(R.color.text_tab_unselected));
                div.setTextColor(getActivity().getResources().getColor(R.color.white));

                break;
            }
        }
    }
}
