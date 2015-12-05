package com.railgadi.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.PnrPredictorAdapter;
import com.railgadi.async.PnrStatusTaskNew;
import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.interfaces.IFragReplaceCommunicator;
import com.railgadi.interfaces.IPnrStatusComm;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

import java.util.ArrayList;

public class PnrConfPredictorFragment extends Fragment implements IPnrStatusComm {

    private View rootView;

    private IFragReplaceCommunicator comm ;

    private PnrStatusTaskNew getPnrStatusTask;

    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;

    private PnrPredictorAdapter pnrPredictorAdapter ;

    private Dialog dialog ;

    // views
    private EditText enterPnrEdit;
    private ImageView searchVoice;
    private ListView predictor_list;
    private TextView passenger, status, possibility;


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(getPnrStatusTask != null) {
            getPnrStatusTask.cancel(true) ;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView        =   inflater.inflate(R.layout.pnr_conf_predictor, container, false);

        MainActivity.toolbar.setVisibility(View.VISIBLE) ;
        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.pnr_conf_predictor).toUpperCase()) ;

        comm            =   (IFragReplaceCommunicator) getActivity() ;

        initializeAllViews();
        setDataOnViews();
        setTypeface();

        return rootView;
    }


    private void initializeAllViews() {

        enterPnrEdit = (EditText) rootView.findViewById(R.id.enter_pnr_edittext);
        searchVoice = (ImageView) rootView.findViewById(R.id.search_voice_btn);
        predictor_list = (ListView) rootView.findViewById(R.id.predictor_list);
        passenger = (TextView) rootView.findViewById(R.id.passenger_text);
        status = (TextView) rootView.findViewById(R.id.status_text);
        possibility = (TextView) rootView.findViewById(R.id.possibility_text);
    }

    private void setDataOnViews() {

        enterPnrEdit.setFocusable(false);
        enterPnrEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                enterPnrEdit.setFocusableInTouchMode(true);
                return false ;
            }
        });

        enterPnrEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String pnr = s.toString();
                if (pnr.length() == 10) {

                    if (InternetChecking.isNetWorkOn(getActivity())) {

                        getPnrStatusTask = new PnrStatusTaskNew(getActivity(), pnr, PnrConfPredictorFragment.this);
                        getPnrStatusTask.execute();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(enterPnrEdit.getWindowToken(), 0);

                    } else {
                        InternetChecking.noInterNetToast(getActivity());
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
        speechRecognizer.setRecognitionListener(new SpeechListener());

        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName());
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, true);

        searchVoice = (ImageView) rootView.findViewById(R.id.search_voice_btn);
        searchVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = UtilsMethods.openRecording(getActivity(), speechRecognizer) ;
                dialog.show() ;
                speechRecognizer.startListening(speechIntent);
            }
        });
    }


    private void setTypeface() {

        enterPnrEdit.setTypeface(AppFonts.getRobotoThin(getActivity()));
        passenger.setTypeface(AppFonts.getRobotoLight(getActivity()));
        status.setTypeface(AppFonts.getRobotoLight(getActivity()));
        possibility.setTypeface(AppFonts.getRobotoLight(getActivity()));
    }


    @Override
    public void updateException(String exception) {

        enterPnrEdit.setText("");
        Toast.makeText(getActivity(), exception, Toast.LENGTH_SHORT).show() ;
    }

    @Override
    public void updatePnrUI(PnrStatusNewBean ps) {

        enterPnrEdit.setText("");

        if(pnrPredictorAdapter != null) {
            pnrPredictorAdapter.changeDataSet(ps) ;
        } else {
            pnrPredictorAdapter = new PnrPredictorAdapter(getActivity(), ps);
            predictor_list.setAdapter(pnrPredictorAdapter);
        }
    }


    private class SpeechListener implements RecognitionListener {

        // following eight method are for voice search
        @Override
        public void onReadyForSpeech(Bundle params) {
            searchVoice.setEnabled(false);
        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            dialog.dismiss();
            searchVoice.setEnabled(true);
        }

        @Override
        public void onError(int error) {
            dialog.dismiss();
            searchVoice.setEnabled(true);
            String errorText = UtilsMethods.getErrorText(error) ;
            Toast.makeText(getActivity(), errorText, Toast.LENGTH_SHORT).show() ;
        }

        public void onResults(Bundle results) {

            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            String text1 = matches.get(0);
            String text = "";
            for (int i = 0; i < text1.length(); i++) {
                if (text1.charAt(i) != ' ') {
                    text = text + text1.charAt(i);
                }
            }
            String regex = "[0-9]+";
            if (text.length() > 10) {
                text = text.substring(0, 10);
            }

            try {
                for (int k = 0; k < text.length(); k++) {
                    if (Character.isDigit(text.charAt(k))) {
                        if (k == text.length() - 1) {
                            // Toast.makeText(getApplicationContext(),"Speaking over",Toast.LENGTH_LONG).show();
                            enterPnrEdit.setText(text);
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Please speak only numeric value", Toast.LENGTH_LONG).show();
                        break;
                    }

                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }

}
