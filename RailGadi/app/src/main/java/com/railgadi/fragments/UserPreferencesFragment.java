package com.railgadi.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.preferences.IRCTCAutoLoginPreferences;
import com.railgadi.preferences.PreferenceUtils;

public class UserPreferencesFragment extends Fragment {

    private View rootView ;

    private LinearLayout uNameClickLayout, passClickLayout ;
    private TextView userNameText, passwordText ;

    private Dialog dialog ;
    private TextView dialogTitle, cancel, save ;
    private EditText enterUserPass ;
    private CheckBox muteNotification ;

    private IRCTCAutoLoginPreferences autoLoginPreferences ;

    private boolean flag = true ;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView    =   inflater.inflate(R.layout.user_preference_fragment, container, false) ;

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.settings).toUpperCase());

        autoLoginPreferences    =   new IRCTCAutoLoginPreferences(getActivity()) ;

        initializeAllViews() ;

        getFromPreferences();

        return rootView ;
    }


    private void initializeAllViews() {

        uNameClickLayout            =   (LinearLayout) rootView.findViewById(R.id.user_name_layout) ;
        passClickLayout             =   (LinearLayout) rootView.findViewById(R.id.password_layout) ;

        userNameText                =   (TextView) rootView.findViewById(R.id.irctc_username_textview) ;
        passwordText                =   (TextView) rootView.findViewById(R.id.irctc_password_textview) ;

        muteNotification            =   (CheckBox) rootView.findViewById(R.id.mute_notification_check) ;
        if(PreferenceUtils.isMuteNotificationFlag(getActivity())) {
            muteNotification.setChecked(true) ;
        } else {
            muteNotification.setChecked(false) ;
        }


        uNameClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true ;
                openDialog();
            }
        });

        passClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false ;
                openDialog();
            }
        });

        muteNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtils.setMuteNotificationFlag(getActivity(), isChecked);
            }
        });
    }

    private void getFromPreferences() {

        String savedUsername = autoLoginPreferences.getUserName() ;
        String savedPassword = autoLoginPreferences.getPassword() ;

        if(savedUsername != null ) {
            userNameText.setText(savedUsername);
        }

        if (savedPassword != null ) {

            StringBuffer sb = new StringBuffer() ;
            for(int i=0 ; i<savedPassword.length() ; i++) {
                sb.append("*") ;
            }
            passwordText.setText(sb.toString());
        }
    }

    private void openDialog() {

        dialog      =   new Dialog(getActivity()) ;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        dialog.setContentView(R.layout.irctc_user_pass_layout);
        dialog.setCancelable(true);

        enterUserPass   =   (EditText) dialog.findViewById(R.id.enter_user_pass_edittext) ;
        dialogTitle     =   (TextView) dialog.findViewById(R.id.dialog_title) ;
        cancel          =   (TextView) dialog.findViewById(R.id.cancel) ;
        save            =   (TextView) dialog.findViewById(R.id.save) ;

        if(flag) {

            dialogTitle.setText(getActivity().getResources().getString(R.string.enter_user_name));
            enterUserPass.setInputType(InputType.TYPE_CLASS_TEXT);
            enterUserPass.setSelection(enterUserPass.getText().length());

        } else {

            dialogTitle.setText(getActivity().getResources().getString(R.string.enter_password));
            enterUserPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            enterUserPass.setSelection(enterUserPass.getText().length());

        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(enterUserPass.getText().toString().length() > 0) {

                    closeKeyBoard();

                    if(flag) {
                        autoLoginPreferences.saveIrctcUsername(enterUserPass.getText().toString());
                    }
                    else {
                        autoLoginPreferences.saveIrctcPassword(enterUserPass.getText().toString());
                    }
                    getFromPreferences();

                    dialog.dismiss();

                } else {

                    if(flag) {
                        Toast.makeText(getActivity(), "Enter Username", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Enter Password", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                closeKeyBoard();
            }
        });

        dialog.show() ;
    }

    private void closeKeyBoard() {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(enterUserPass.getWindowToken(), 0);
    }

}
