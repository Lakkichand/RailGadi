package com.railgadi.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.railgadi.R;
import com.railgadi.async.UserSignUpTask;
import com.railgadi.beans.SignUpRequestBean;
import com.railgadi.fonts.AppFonts;
import com.railgadi.preferences.PreferenceUtils;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

public class SignUpActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    private TextView skip, signInButton, loginTextButton, clickHereFor, termsAndCondition ;
    private EditText fullNameEdit, emailEdit, passwordEdit ;
    private LoginButton loginButton ;

    private String userName, userEmail ;

    private static final int RC_SIGN_IN = 0;

    // google sign in button
    private TextView btnSignIn;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private SignUpRequestBean signUpRequestBean ;

    private UserSignUpTask userSignUpTask ;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private boolean isGmail ;


    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();

            Profile profile = Profile.getCurrentProfile();
            displayMessage(profile);

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };


    private void displayMessage(Profile profile){

        if(profile != null) {

            SignUpRequestBean bean = new SignUpRequestBean();
            bean.setFullName(profile.getName());
            bean.setEmail(UtilsMethods.addFacebookURL(profile.getId()));
            bean.setDevId(PreferenceUtils.getDeviceID(getApplicationContext()));
            bean.setDevType(Constants.DEVICE_TYPE_ANDROID);
            bean.setGender(Constants.DEFAULT_GENDER);

            userSignUpTask = new UserSignUpTask(SignUpActivity.this, bean);
            userSignUpTask.execute();

            userName = bean.getFullName();
        }
    }


    public void onConnectionFailed(ConnectionResult result) {

        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(isGmail) {
            if (requestCode == RC_SIGN_IN) {
                if (resultCode != RESULT_OK) {
                    mSignInClicked = false;
                }

                mIntentInProgress = false;

                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
            }
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(InternetChecking.isNetWorkOn(this)) {
            if(PreferenceUtils.getGcmId(this) == null || PreferenceUtils.getGcmId(this).equals("")) {
                UtilsMethods.getGcmID(this) ;
            }
        }

        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(userSignUpTask != null) {
            userSignUpTask.cancel(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_sign_up);

        isGmail = false ;

        btnSignIn = (TextView) findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isGmail = true ;
                signInWithGplus();

            }
        });

        //1
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        initializeViews();
        setDataOnViews();

    }


    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    private void initializeViews() {

        loginButton             =   (LoginButton) findViewById(R.id.login_button);

        fullNameEdit            =   (EditText) findViewById(R.id.full_name_edit) ;
        emailEdit               =   (EditText) findViewById(R.id.email_edit) ;
        passwordEdit            =   (EditText) findViewById(R.id.password_edit) ;

        skip                    =   (TextView) findViewById(R.id.sign_up_skip) ;
        signInButton            =   (TextView) findViewById(R.id.sign_in_button) ;

        loginTextButton         =   (TextView) findViewById(R.id.login_text_button) ;
        loginTextButton.setTypeface(AppFonts.getRobotoLight(this));
        SpannableString content = new SpannableString(getResources().getString(R.string.existing_user_login));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        loginTextButton.setText(content);

        clickHereFor = (TextView) findViewById(R.id.click_here_for) ;
        termsAndCondition = (TextView) findViewById(R.id.terms_and_condition) ;
        clickHereFor.setTypeface(AppFonts.getRobotoLight(this));
        termsAndCondition.setTypeface(AppFonts.getRobotoLight(this));
        SpannableString cont = new SpannableString(getResources().getString(R.string.terms_and_conditions));
        cont.setSpan(new UnderlineSpan(), 0, cont.length(), 0);
        termsAndCondition.setText(cont);
        termsAndCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(InternetChecking.isNetWorkOn(SignUpActivity.this)) {
                    startActivity(new Intent(SignUpActivity.this, TermsAndConditionActivity.class));
                } else {
                    InternetChecking.noInterNetToast(SignUpActivity.this);
                }
            }
        });

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

                displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();


        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, callback);

    }


    private void signInWithGplus() {

         if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    private void resolveSignInError() {

        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }


    //3
    private void getProfileInformation() {

        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

                String personName = currentPerson.getDisplayName();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                SignUpRequestBean bean = new SignUpRequestBean();
                bean.setFullName(personName);
                bean.setEmail(email);
                bean.setDevId(PreferenceUtils.getDeviceID(getApplicationContext()));
                bean.setDevType(Constants.DEVICE_TYPE_ANDROID);
                bean.setGender(Constants.DEFAULT_GENDER);

                userSignUpTask = new UserSignUpTask(SignUpActivity.this, bean);
                userSignUpTask.execute();

                userName = bean.getFullName();

            } else {
                Toast.makeText(getApplicationContext(),"Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //2
    @Override
    public void onConnected(Bundle arg0) {

        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        getProfileInformation();

        // Update the UI after signin

    }


    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    private void setDataOnViews() {

        fullNameEdit.setTypeface(AppFonts.getRobotoLight(this));
        emailEdit.setTypeface(AppFonts.getRobotoLight(this));
        passwordEdit.setTypeface(AppFonts.getRobotoLight(this));
        skip.setTypeface(AppFonts.getRobotoLight(this));
        signInButton.setTypeface(AppFonts.getRobotoLight(this));
        loginTextButton.setTypeface(AppFonts.getRobotoLight(this));

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                finish();
            }
        });

        loginTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(performValidation()) {

                    if(InternetChecking.isNetWorkOn(SignUpActivity.this)) {
                        userSignUpTask = new UserSignUpTask(SignUpActivity.this, signUpRequestBean);
                        userSignUpTask.execute();
                    } else {
                        InternetChecking.noInterNetToast(SignUpActivity.this);
                    }
                }
            }
        });
    }


    private boolean performValidation() {

        String fullName = fullNameEdit.getText().toString().trim() ;
        if(! UtilsMethods.fieldRequired(fullName)) {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show() ;
            return false ;
        }

        userEmail= emailEdit.getText().toString().trim() ;
        if(! UtilsMethods.fieldRequired(userEmail)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show() ;
            return false ;
        } else {
            if(! UtilsMethods.isEmailValid(userEmail)) {
                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_LONG).show() ;
                return false ;
            }
        }

        String pass = passwordEdit.getText().toString().trim() ;
        if(! UtilsMethods.fieldRequired(pass)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show() ;
            return false ;
        }

        if(PreferenceUtils.getDeviceID(this) == null || PreferenceUtils.getDeviceID(this).equals("")) {
            UtilsMethods.getDeviceID(this);
        }

        signUpRequestBean   =   new SignUpRequestBean() ;

        signUpRequestBean.setDevId(PreferenceUtils.getDeviceID(this));
        signUpRequestBean.setFullName(fullName) ;
        signUpRequestBean.setDevType("android");
        signUpRequestBean.setEmail(userEmail);
        signUpRequestBean.setGender("m");
        signUpRequestBean.setFullName(userName);
        signUpRequestBean.setPassword(pass);

        return true ;
    }



    private void clearFields() {

        fullNameEdit.setText("");
        emailEdit.setText("");
        passwordEdit.setText("");

    }


    public void regResponse(boolean responseFlag) {

        if(responseFlag) {
            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
            PreferenceUtils.setCurrentUserName(this, userName) ;
            PreferenceUtils.setCurrentUserEmail(this, userEmail) ;
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration Unsuccessful try again", Toast.LENGTH_SHORT).show();
            clearFields() ;
        }
    }

}
