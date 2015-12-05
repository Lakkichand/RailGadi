package com.railgadi.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.FavoritesTimeTableAdapter;
import com.railgadi.async.AutoSuggestionTask;
import com.railgadi.async.GetTimeTableTaskNew;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.beans.TrainDataBean;
import com.railgadi.customUi.CustomAutoCompleteView;
import com.railgadi.dbhandlers.TrainDBHandler;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IAutoSuggestion;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IGettingTimeTableComm;
import com.railgadi.interfaces.IPnrDeleteCommunicator;
import com.railgadi.preferences.TimeTablePreferencesHandler;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimeTableInputFragment extends Fragment implements IPnrDeleteCommunicator, IGettingTimeTableComm, IAutoSuggestion {

    private View rootView;

    private IFragReplaceCommunicator comm;

    private FavoritesTimeTableAdapter adapter;

    private GetTimeTableTaskNew getTimeTableTask;
    private AutoSuggestionTask autoSuggestionTask;

    private TimeTablePreferencesHandler timeTablePreferencesHandler;

    private List<TimeTableNewBean> savedTimetableList;
    private TimeTableNewBean selectedBean;

    private ArrayAdapter<String> autoSuggestionAdapter ;

    // views
    private CustomAutoCompleteView enterTrainNumberEditText ;
    private ListView favouriteTrainsListView;
    private LinearLayout autoSearchLayout ;
    private TextView getTimeTableButton, recentTrainText, clearAllText, noFavTrains;
    private LinearLayout favLayout;

    private List<Map.Entry<String, String>> suggestionEntries;
    private String selectedTrain;

    private String [] suggestionArray = {"please search..."} ;
    private List<TrainDataBean> allSuggestionList ;
    private TrainDBHandler trainDBHandler ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (getTimeTableTask != null) {
            getTimeTableTask.cancel(true);
        }
        if (autoSuggestionTask != null) {
            autoSuggestionTask.cancel(true);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.time_table_input_fragment, container, false);

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.train_timetable).toUpperCase());

        comm = (IFragReplaceCommunicator) getActivity();

        timeTablePreferencesHandler = new TimeTablePreferencesHandler(getActivity());

        initializeAllViews();
        setDataOnViews();
        setAdapterOnFavourites();

        selectedTrain = null;

        return rootView;
    }


    private void initializeAllViews() {


        autoSearchLayout = (LinearLayout) rootView.findViewById(R.id.auto_search_layout) ;
        autoSearchLayout.setVisibility(View.GONE);
        autoSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchTerm = enterTrainNumberEditText.getText().toString() ;
                if(searchTerm == null || searchTerm.length() < 1){
                    Toast.makeText(getActivity(), "Please enter text", Toast.LENGTH_LONG).show() ;
                    return ;
                }

                if(autoSuggestionTask != null) {
                    autoSuggestionTask.cancel(true) ;
                    autoSuggestionTask = null ;
                }

                autoSuggestionTask = new AutoSuggestionTask(getActivity(), searchTerm, TimeTableInputFragment.this);
                autoSuggestionTask.execute();
            }
        });

        enterTrainNumberEditText = (CustomAutoCompleteView) rootView.findViewById(R.id.enter_train_number_edittext);
        enterTrainNumberEditText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        enterTrainNumberEditText.setFocusable(false);
        enterTrainNumberEditText.setAdapter(null);
        enterTrainNumberEditText.setThreshold(1);

        autoSuggestionAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, suggestionArray);
        enterTrainNumberEditText.setAdapter(autoSuggestionAdapter);

        enterTrainNumberEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                enterTrainNumberEditText.setText("");
                selectedTrain = null ;
                return true ;
            }
        });

        enterTrainNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                enterTrainNumberEditText.setFocusableInTouchMode(true);
                return false;
            }
        });

        enterTrainNumberEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedTrain = allSuggestionList.get(position).getTrainNumber() ;
                autoSearchLayout.setVisibility(View.GONE);
            }
        });

        enterTrainNumberEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {


                getAutoSuggestion(userInput.toString()); ;

            }
        });


        /*enterTrainNumberEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedTrain = suggestionEntries.get(position).getKey();
            }
        });
        enterTrainNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                enterTrainNumberEditText.setFocusableInTouchMode(true);
                return false;
            }
        });
        enterTrainNumberEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(autoSuggestionTask != null) {
                    autoSuggestionTask.cancel(true) ;
                    autoSuggestionTask = null ;
                }

                autoSuggestionTask = new AutoSuggestionTask(getActivity(), s.toString(), TimeTableInputFragment.this);
                autoSuggestionTask.execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        favouriteTrainsListView = (ListView) rootView.findViewById(R.id.recent_trains_listview);

        getTimeTableButton = (TextView) rootView.findViewById(R.id.get_timetable_button);
        recentTrainText = (TextView) rootView.findViewById(R.id.recent_trains_text);
        clearAllText = (TextView) rootView.findViewById(R.id.clear_all_button);
        noFavTrains = (TextView) rootView.findViewById(R.id.no_fav_trains_textview);

        favLayout = (LinearLayout) rootView.findViewById(R.id.fav_layout);
    }


    private void getAutoSuggestion(String userInput) {

        if(userInput.toString().length() < 1) {
            autoSearchLayout.setVisibility(View.GONE);
            selectedTrain = null ;
            return ;
        }

        if(trainDBHandler == null) {
            trainDBHandler = new TrainDBHandler(getActivity()) ;
        }

        List<TrainDataBean> suggestionList = trainDBHandler.getSearchedItemList(userInput.toString()) ;
        updateAutoSuggestion(suggestionList);
    }


    private void setDataOnViews() {

        getTimeTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedTrain == null || selectedTrain.isEmpty()) {
                    Toast.makeText(getActivity(), "Select Train", Toast.LENGTH_SHORT).show();
                    return;
                }

                TimeTableNewBean savedTimeTable = timeTablePreferencesHandler.getTimeTableFromPref(selectedTrain);

                if (savedTimeTable != null) {

                    //updateTimeTableUI(savedTimeTable);
                    comm.respond(new RouteMapTabsFragment(savedTimeTable));
                    closeKeyBoard();

                } else {
                    if (InternetChecking.isNetWorkOn(getActivity())) {

                        getTimeTableTask = new GetTimeTableTaskNew(getActivity(), selectedTrain, TimeTableInputFragment.this);
                        getTimeTableTask.execute();
                        closeKeyBoard();

                    } else {
                        InternetChecking.noInterNetToast(getActivity());
                        closeKeyBoard();
                    }
                }
            }
        });


        favouriteTrainsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TimeTableNewBean ttc = savedTimetableList.get(position);
                enterTrainNumberEditText.setText("");
                comm.respond(new RouteMapTabsFragment(ttc));
            }
        });


        favouriteTrainsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                selectedBean = savedTimetableList.get(position);
                //new DeletePnrConfirmDialog(getActivity(), TimeTableInputFragment.this);
                UtilsMethods.confirmDeleteDialog(getActivity(), TimeTableInputFragment.this);

                return true;
            }
        });

        clearAllText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTablePreferencesHandler.removeAllSavedTimetable();
                setAdapterOnFavourites();
            }
        });
    }

    private void closeKeyBoard() {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(enterTrainNumberEditText.getWindowToken(), 0);
    }

    private void setAdapterOnFavourites() {

        savedTimetableList = timeTablePreferencesHandler.getAllSavedTimeTable();

        if (savedTimetableList != null) {

            List<TimeTableNewBean> visibleData = new ArrayList<>() ;
            for(TimeTableNewBean t : savedTimetableList) {
                if(t.isVisible()) {
                    visibleData.add(t);
                }
            }

            if(visibleData.size() > 0) {
                adapter = new FavoritesTimeTableAdapter(getActivity(), visibleData);
                favouriteTrainsListView.setAdapter(adapter);
                favLayout.setVisibility(View.VISIBLE);
                noFavTrains.setVisibility(View.GONE);
            } else {
                favouriteTrainsListView.setAdapter(null);
                favLayout.setVisibility(View.GONE);
                favLayout.setGravity(Gravity.CENTER);
                noFavTrains.setVisibility(View.VISIBLE);
            }

        }
        else {
            favouriteTrainsListView.setAdapter(null);
            favLayout.setVisibility(View.GONE);
            favLayout.setGravity(Gravity.CENTER);
            noFavTrains.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void updateException(String exception) {

        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void updateTimeTableUI(TimeTableNewBean bean) {

        comm.respond(new RouteMapTabsFragment(bean));
        enterTrainNumberEditText.setText("");
        if(autoSuggestionTask != null) {
            autoSuggestionTask.cancel(true) ;
        }
        if(getTimeTableTask != null) {
            getTimeTableTask.cancel(true) ;
        }
    }

    @Override
    public void wantToDelete(boolean flag) {

        if (flag) {
            timeTablePreferencesHandler.removeFromPreferences(selectedBean.getTrainNumber());
            setAdapterOnFavourites();
        }
    }

    @Override
    public void updateAutoSuggestion(List<TrainDataBean> suggestionEntries) {

        try {

            if (suggestionEntries == null || suggestionEntries.size() < 1) {
                enterTrainNumberEditText.setAdapter(null);
                autoSearchLayout.setVisibility(View.VISIBLE);
                enterTrainNumberEditText.dismissDropDown();
                return;
            }

            autoSearchLayout.setVisibility(View.GONE);

            allSuggestionList = suggestionEntries;
            suggestionArray = new String[allSuggestionList.size()];

            for (int i = 0; i < allSuggestionList.size(); i++) {
                TrainDataBean bean = allSuggestionList.get(i);
                suggestionArray[i] = bean.getTrainNumber() + " - " + bean.getTrainName();
            }

            // update the adapater
            autoSuggestionAdapter.notifyDataSetChanged();
            autoSuggestionAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, suggestionArray);
            enterTrainNumberEditText.setAdapter(autoSuggestionAdapter);
            autoSuggestionAdapter.notifyDataSetChanged();
            enterTrainNumberEditText.showDropDown();

        } catch( Exception e ) {

        }
    }

    @Override
    public void updateAutoSuggestionException(String exception) {
        enterTrainNumberEditText.setAdapter(null);
        autoSearchLayout.setVisibility(View.GONE);
    }
}
