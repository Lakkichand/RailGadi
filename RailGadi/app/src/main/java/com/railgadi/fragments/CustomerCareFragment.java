package com.railgadi.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.CustomerCareAdapter;

public class CustomerCareFragment extends Fragment {

    private View rootView ;

    private CustomerCareAdapter adapter ;
    private String [] number ;
    private String [] name ;

    private ListView listView ;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView        =   inflater.inflate(R.layout.customer_care_fragment, container, false) ;

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.indian_rail_customer_care).toUpperCase());

        initializeAllViews();

        return rootView ;
    }

    private void initializeAllViews() {

        name      =   getActivity().getResources().getStringArray(R.array.railway_name) ;
        number    =   getActivity().getResources().getStringArray(R.array.railway_numbers) ;

        adapter             =   new CustomerCareAdapter(getActivity(), name, number) ;

        listView            =   (ListView) rootView.findViewById(R.id.all_phone_numbers) ;
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + number[position]));
                getActivity().startActivity(intent);
            }
        });
    }
}
