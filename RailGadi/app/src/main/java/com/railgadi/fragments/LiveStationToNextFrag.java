package com.railgadi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.LiveStationListAdapter;
import com.railgadi.beans.LiveStationNewBean;
import com.railgadi.interfaces.IFragReplaceCommunicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveStationToNextFrag extends Fragment {

    private View rootView ;

    private LiveStationListAdapter adapter ;

    private IFragReplaceCommunicator comm ;

    //views
    private ListView liveStationListView ;
    private TextView trainInNext, stationName ;

    private List<LiveStationNewBean.LiveStationData> allTrainList ;
    private String station ;
    private String time ;
    private String toolBarTitle ;

    public LiveStationToNextFrag(LiveStationNewBean bean , String station, String time, String toolBarTitle) {

        this.allTrainList   =   bean.getLiveStationList() ;
        this.station = station ;
        this.time = time ;
        this.toolBarTitle = toolBarTitle ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView        =   inflater.inflate(R.layout.live_station_next_frag, container, false) ;

        MainActivity.toolbar.setTitle(toolBarTitle.toUpperCase());

        comm            =   (IFragReplaceCommunicator) getActivity() ;

        initializeAllViews() ;
        setDataOnViews() ;

        return rootView ;
    }


    public void initializeAllViews() {

        liveStationListView     =   (ListView) rootView.findViewById(R.id.live_station_listview) ;

        trainInNext             =   (TextView) rootView.findViewById(R.id.train_in_next) ;
        stationName             =   (TextView) rootView.findViewById(R.id.station_name) ;

        adapter                 =   new LiveStationListAdapter(getActivity(), allTrainList ) ;
        trainInNext.setText("Train in next : "+time+" hr");
        stationName.setText(station);
    }


    private void setDataOnViews() {

        liveStationListView.setAdapter(adapter);

        liveStationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LiveStationNewBean.LiveStationData clickedData =
                        (LiveStationNewBean.LiveStationData) adapter.getItem(position) ;

                Map<String, String> map = new HashMap<>() ;
                map.put(clickedData.trainNumber, clickedData.trainName) ;

                comm.respond(new LiveTrainInputFragment(map)) ;
            }
        });
    }

}
