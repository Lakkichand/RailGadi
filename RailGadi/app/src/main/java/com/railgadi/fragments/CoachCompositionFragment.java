package com.railgadi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.async.AutoSuggestionTask;
import com.railgadi.async.CoachCompositionTask;
import com.railgadi.beans.CoachCompositionBean;
import com.railgadi.beans.TrainDataBean;
import com.railgadi.dbhandlers.TrainDBHandler;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IAutoSuggestion;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.utilities.InternetChecking;

import java.util.List;
import java.util.Map;

public class CoachCompositionFragment extends Fragment implements IAutoSuggestion {

    private View rootView ;

    private IFragReplaceCommunicator comm ;

    private CoachCompositionTask coachCompositionTask ;
    private AutoSuggestionTask autoSuggestionTask ;

    //views
    private TextView getCompositionButton ;
    private LinearLayout autoSearchLayout ;
    private AutoCompleteTextView enterTrainNumberEditText ;

    private List<Map.Entry<String, String>> suggestionEntries;


    private String trainNumber;
    private String [] suggestionArray = {"please search..."} ;
    private List<TrainDataBean> allSuggestionList ;
    private TrainDBHandler trainDBHandler ;
    private ArrayAdapter autoSuggestionAdapter ;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView        =   inflater.inflate(R.layout.coach_composition_frag, container, false) ;

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.coach_composition).toUpperCase());

        comm            =   (IFragReplaceCommunicator) getActivity() ;

        trainNumber     =   null ;

        initializeAllViews() ;
        setTypeface();

        return rootView ;
    }


    @Override
    public void onResume() {
        super.onResume();

        //MyApplication.getInstance().trackScreenView(CoachCompositionFragment.this.getClass().getName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(coachCompositionTask != null) {
            coachCompositionTask.cancel(true) ;
        }
        if(autoSuggestionTask != null) {
            autoSuggestionTask.cancel(true) ;
        }
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

                autoSuggestionTask = new AutoSuggestionTask(getActivity(), searchTerm, CoachCompositionFragment.this);
                autoSuggestionTask.execute();
            }
        });

        enterTrainNumberEditText = (AutoCompleteTextView) rootView.findViewById(R.id.enter_train_number_edittext);
        enterTrainNumberEditText.setTypeface(AppFonts.getRobotoLight(getActivity()));
        enterTrainNumberEditText.setFocusable(false);
        enterTrainNumberEditText.setAdapter(null);
        enterTrainNumberEditText.setThreshold(1);
        //enterTrainNumberEditText.setClearButton(getActivity().getResources().getDrawable(R.drawable.com_facebook_close));

        autoSuggestionAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, suggestionArray);
        enterTrainNumberEditText.setAdapter(autoSuggestionAdapter);

        enterTrainNumberEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                enterTrainNumberEditText.setText("");
                autoSearchLayout.setVisibility(View.GONE);
                trainNumber = null ;
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

                trainNumber = allSuggestionList.get(position).getTrainNumber();
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

                getAutoSuggestion(userInput.toString());
            }
        });



        /*enterTrainNumberEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                enterTrainNumberEditText.setText("");
                return true ;
            }
        });

        enterTrainNumberEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

                autoSuggestionTask = new AutoSuggestionTask(getActivity(), s.toString(), CoachCompositionFragment.this);
                autoSuggestionTask.execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        */

        getCompositionButton    =   (TextView) rootView.findViewById(R.id.get_composition_button) ;

        getCompositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(trainNumber == null) {
                    Toast.makeText(getActivity(), "Select Train", Toast.LENGTH_SHORT).show() ;
                    return ;
                }

                if(InternetChecking.isNetWorkOn(getActivity())) {
                    coachCompositionTask = new CoachCompositionTask(getActivity(), trainNumber, CoachCompositionFragment.this);
                    coachCompositionTask.execute();
                    trainNumber = null ;
                } else {
                    InternetChecking.noInterNetToast(getActivity());
                }
            }
        });
    }


    private void getAutoSuggestion(String userInput) {

        if(userInput.toString().length() < 1) {
            autoSearchLayout.setVisibility(View.GONE);
            trainNumber = null ;
            return ;
        }

        if(trainDBHandler == null) {
            trainDBHandler = new TrainDBHandler(getActivity()) ;
        }

        List<TrainDataBean> suggestionList = trainDBHandler.getSearchedItemList(userInput.toString()) ;

        updateAutoSuggestion(suggestionList);
    }



    private void setTypeface() {

        enterTrainNumberEditText.setTypeface(AppFonts.getRobotoThin(getActivity()));

        getCompositionButton.setTypeface(AppFonts.getRobotoReguler(getActivity()));
    }

    public void updateException(String exception) {
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show() ;
        autoSearchLayout.setVisibility(View.GONE);
        enterTrainNumberEditText.setText("");
    }


    @Override
    public void updateAutoSuggestion(List<TrainDataBean> suggestionList) {

        if(suggestionList == null || suggestionList.size() < 1) {
            enterTrainNumberEditText.setAdapter(null) ;
            autoSearchLayout.setVisibility(View.VISIBLE);
            enterTrainNumberEditText.dismissDropDown();
            return ;
        }

        autoSearchLayout.setVisibility(View.GONE);

        allSuggestionList = suggestionList ;

        suggestionArray = new String[allSuggestionList.size()] ;

        for(int i=0 ; i<allSuggestionList.size() ; i++) {
            TrainDataBean bean = allSuggestionList.get(i) ;
            suggestionArray[i] =  bean.getTrainNumber()+" - "+bean.getTrainName() ;
        }

        // update the adapter
        autoSuggestionAdapter.notifyDataSetChanged();
        autoSuggestionAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, suggestionArray);
        enterTrainNumberEditText.setAdapter(autoSuggestionAdapter);
        autoSuggestionAdapter.notifyDataSetChanged();
        enterTrainNumberEditText.showDropDown();
    }


    @Override
    public void updateAutoSuggestionException(String exception) {
        enterTrainNumberEditText.setAdapter(null);
        autoSearchLayout.setVisibility(View.GONE);
    }


    public void updateComposition(CoachCompositionBean bean) {

        if(autoSuggestionTask != null) {
            autoSuggestionTask.cancel(true) ;
        }
        enterTrainNumberEditText.setText("");
        autoSearchLayout.setVisibility(View.GONE);
        comm.respond(new CoachCompToNextFrag(bean));
    }
}

