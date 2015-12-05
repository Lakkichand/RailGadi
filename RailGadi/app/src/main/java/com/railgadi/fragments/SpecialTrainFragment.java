package com.railgadi.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.SpecialTrainAdapterNew;
import com.railgadi.async.SpecialTrainTask;
import com.railgadi.beans.SpecialTrainNewBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IGettingTimeTableComm;

public class SpecialTrainFragment extends Fragment implements IGettingTimeTableComm {

    private View rootView;

    private SpecialTrainAdapterNew adapter;

    private SpecialTrainTask specialTrainTask;

    private IFragReplaceCommunicator comm;

    private ListView specialTrainListView;
    private EditText enterTrainNumber;
    private LinearLayout upperLayout;

    private SpecialTrainNewBean.SpecialTrains selectedTrain;

    private SpecialTrainNewBean bean;


    public SpecialTrainFragment() {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(adapter != null) {
            if(adapter.getTimeTableTask() != null) {
                adapter.getTimeTableTask().cancel(true) ;
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        enterTrainNumber.setText("") ;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        specialTrainTask = new SpecialTrainTask(getActivity(), SpecialTrainFragment.this);
        specialTrainTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (bean != null) {
            updateSpecialTrainUI(bean);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.special_train_main, container, false);

        comm = (IFragReplaceCommunicator) getActivity();

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.special_trains).toUpperCase());

        initializeAllViews();

        return rootView;
    }


    public void initializeAllViews() {

        specialTrainListView = (ListView) rootView.findViewById(R.id.special_train_listview);

        enterTrainNumber = (EditText) rootView.findViewById(R.id.enter_train_number_edittext);

        upperLayout = (LinearLayout) rootView.findViewById(R.id.upper_layout);

        specialTrainListView.setTextFilterEnabled(true);

/*

        specialTrainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (SpecialTrainAdapterNew.updatedList != null || StationNameCodeAdapter.updatedList.size() > 0) {
                    selectedTrain = SpecialTrainAdapterNew.updatedList.get(position);
                } else {
                    selectedTrain = (SpecialTrainNewBean.SpecialTrains) adapter.getItem(position);
                }

                Toast.makeText(getActivity(), selectedTrain.trainName, Toast.LENGTH_SHORT).show();
            }
        });
*/

        enterTrainNumber.setFocusable(false);
        enterTrainNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                enterTrainNumber.setFocusableInTouchMode(true);
                return false;
            }
        });

        enterTrainNumber.setTypeface(AppFonts.getRobotoLight(getActivity()));
        enterTrainNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (adapter != null) {
                    adapter.filterData(s.toString());
                }
            }


            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    specialTrainListView.setAdapter(adapter);
                }
            }
        });
    }


    public void updateSpecialTrainUI(SpecialTrainNewBean bean) {
        this.bean = bean;
        upperLayout.setVisibility(View.VISIBLE);

        Toast.makeText(getActivity(), bean.getSpecialTrainsList().size()+" Special Train Found", Toast.LENGTH_SHORT).show(); ;

        adapter = new SpecialTrainAdapterNew(getActivity(), bean.getSpecialTrainsList());
        specialTrainListView.setAdapter(adapter);
    }


    public void updateSpecialTrainException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show();
        specialTrainListView.setAdapter(null);
    }


    @Override
    public void updateException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void updateTimeTableUI(TimeTableNewBean bean) {
        comm.respond(new RouteMapTabsFragment(bean));
    }
}
