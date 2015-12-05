package com.railgadi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.railgadi.R;
import com.railgadi.adapters.MessageAdapter;
import com.railgadi.beans.PnrPreferenceBean;
import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.comparators.PnrListComparator;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IPnrDeleteCommunicator;
import com.railgadi.preferences.PnrPreferencesHandler;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.UtilsMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OthersPnrTab extends Fragment implements IPnrDeleteCommunicator {

    private View rootView;
    private ListView listview;
    public Map<String, PnrStatusNewBean> allOthers;

    private MessageAdapter adapter;

    private PnrPreferencesHandler preferencesHandler;
    private IFragReplaceCommunicator comm;


    public OthersPnrTab() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.others_messages_layout, container, false);

        comm = (IFragReplaceCommunicator) getActivity();

        preferencesHandler = new PnrPreferencesHandler(getActivity());

        initializeView();
        refreshOthersAdapter();

        return rootView;
    }


    private void initializeView() {

        listview = (ListView) rootView.findViewById(R.id.others_msg_list);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Map.Entry<String, PnrStatusNewBean>> list = new ArrayList<>(allOthers.entrySet());
                Collections.sort(list, new PnrListComparator());
                comm.respond(new ShowExistingPnr(Constants.OTHERS, list.get(position).getValue()));
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                List<Map.Entry<String, PnrStatusNewBean>> entrySet = new ArrayList<>(allOthers.entrySet());
                Collections.sort(entrySet, new PnrListComparator());

                PnrPreferenceBean bean = new PnrPreferenceBean();
                bean.setPnrNumber(entrySet.get(position).getKey());
                bean.setPnrObject(entrySet.get(position).getValue());
                bean.setFlag(Constants.COMPLETED);

                selectedBean = bean;

                //new DeletePnrConfirmDialog(getActivity(), OthersPnrTab.this);
                UtilsMethods.confirmDeleteDialog(getActivity(), OthersPnrTab.this);

                return true;
            }
        });
    }

    private PnrPreferenceBean selectedBean;

    // method of PnrDeleteCommunicator
    @Override
    public void wantToDelete(boolean flag) {

        if (flag) {
            allOthers.remove(selectedBean.getPnrNumber());
            preferencesHandler.removeFromPreferences(selectedBean);
            refreshOthersAdapter();
        }
    }

    public void refreshOthersAdapter() {

        allOthers = preferencesHandler.getAllOthersPnr();

        if (allOthers == null) {
            listview.setAdapter(null);
        } else {
            if (adapter != null) {
                adapter.changeDataSet(allOthers);
                listview.setAdapter(adapter);
            } else {
                adapter = new MessageAdapter(getActivity(), allOthers, Constants.OTHERS);
                listview.setAdapter(adapter);
            }
        }
    }
}
