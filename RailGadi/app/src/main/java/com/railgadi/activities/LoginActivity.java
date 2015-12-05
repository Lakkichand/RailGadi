package com.railgadi.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
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
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.railgadi.R;
import com.railgadi.async.ForgotPasswordTask;
import com.railgadi.async.LoginTask;
import com.railgadi.beans.LoginResponseBean;
import com.railgadi.interfaces.ILogin;
import com.railgadi.preferences.PreferenceUtils;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

public class LoginActivity extends Activity implements ILogin, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView loginButton, skip, forgotPassword ;
    private EditText emailEdittext, pwdEdittext ;

    private Dialog forgotPasswordUI ;

    private LoginButton loginFacebookButton ;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private LoginTask loginTask ;
    private ForgotPasswordTask forgotPasswordTask ;


    private String userName, userEmail ;

    private static final int RC_SIGN_IN = 0;

    // google sign in button
    private TextView btnSignIn;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private boolean isGmail ;


    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();

            Profile profile = Profile.getCurrentProfile();
            displayMessage(profile);

/*
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            if(object != null) {
                                // get all data from object
                            }
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "name,id,first_name,last_name,gender");
            request.setParameters(parameters);
            request.executeAsync();
*/

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

            loginTask = new LoginTask(LoginActivity.this, UtilsMethods.addFacebookURL(profile.getId()), null, LoginActivity.this);
            loginTask.execute();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);


        loginButton             = (TextView) findViewById(R.id.login_button);
        skip                    = (TextView) findViewById(R.id.login_skip) ;
        forgotPassword          = (TextView) findViewById(R.id.login_forgot_password) ;
        emailEdittext           = (EditText) findViewById(R.id.login_email_edittext) ;
        pwdEdittext             = (EditText) findViewById(R.id.login_pwd_edittext) ;

        loginFacebookButton     = (LoginButton) findViewById(R.id.login_facebook_button) ;


        btnSignIn = (TextView) findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isGmail = true ;
                signInWithGplus();

            }
        });


        SpannableString content = new SpannableString(getResources().getString(R.string.forgot_password));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        forgotPassword.setText(content);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgotPassword() ;
            }
        });


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String email = emailEdittext.getText().toString() ;
                if(! UtilsMethods.fieldRequired(email)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Email", Toast.LENGTH_SHORT).show() ;
                    return  ;
                }

                String pwd = pwdEdittext.getText().toString() ;
                if(! UtilsMethods.fieldRequired(pwd)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_SHORT).show() ;
                    return  ;
                }

                loginTask   =   new LoginTask(LoginActivity.this, email, pwd, LoginActivity.this) ;
                loginTask.execute() ;
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


        loginFacebookButton.setReadPermissions("user_friends");
        loginFacebookButton.registerCallback(callbackManager, callback);
    }


    public void openForgotPassword() {

        forgotPasswordUI        =   new Dialog(LoginActivity.this) ;
        forgotPasswordUI.setContentView(R.layout.forgot_password_layout);
        forgotPasswordUI.setCancelable(true) ;
        forgotPasswordUI.setTitle(getResources().getString(R.string.forgot_password));

        final EditText enterEmail = (EditText) forgotPasswordUI.findViewById(R.id.forgot_enter_email);
        TextView forgotDone = (TextView) forgotPasswordUI.findViewById(R.id.forgot_done);

        forgotDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = enterEmail.getText().toString() ;

                if(! InternetChecking.isNetWorkOn(LoginActivity.this)) {
                    InternetChecking.noInterNetToast(LoginActivity.this);
                    return ;
                }

                if(email != null && email.length() > 0 && UtilsMethods.isEmailValid(email)) {

                    forgotPasswordTask  =   new ForgotPasswordTask(LoginActivity.this, email) ;
                    forgotPasswordTask.execute() ;

                } else {
                    Toast.makeText(getApplicationContext(), "Enter valid email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgotPasswordUI.show() ;
    }



    public void forgotPasswordMessage(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show() ;
        forgotPasswordUI.dismiss();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loginTask != null) {
            loginTask.cancel(true) ;
        }
        if(forgotPasswordTask != null) {
            forgotPasswordTask.cancel(true) ;
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
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }



    public void onConnectionFailed(ConnectionResult result) {

        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
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



    private void getProfileInformation() {

        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

                String personName = currentPerson.getDisplayName();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                loginTask = new LoginTask(LoginActivity.this, email, null, LoginActivity.this);
                loginTask.execute();

                userName = personName ;

            } else {
                Toast.makeText(getApplicationContext(),"Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

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


    @Override
    protected void onResume() {
        super.onResume();

        if(InternetChecking.isNetWorkOn(this)) {
            if(PreferenceUtils.getGcmId(this) == null || PreferenceUtils.getGcmId(this).equals("")) {
                UtilsMethods.getGcmID(this) ;
            }
        }

        if (InternetChecking.isNetWorkOn(this)) {
            if (PreferenceUtils.getGcmId(this) == null || PreferenceUtils.getGcmId(this).equals("")) {
                UtilsMethods.getGcmID(this);
            }
        }


        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
    }


    @Override
    protected void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }


    @Override
    public void updateLoginUI(LoginResponseBean bean) {

        if(bean != null) {

            if(bean.getName() != null && bean.getEmail() != null) {

                if(! bean.getEmail().contains(Constants.FACEBOOK_TAIL)) {
                    PreferenceUtils.setCurrentUserEmail(this, bean.getEmail());
                }
                PreferenceUtils.setCurrentUserName(this, bean.getName()) ;

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
            else {
                if(bean.getException() != null && (! bean.getException().isEmpty())) {
                    Toast.makeText(getApplicationContext(), bean.getException(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Email or Password id incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
