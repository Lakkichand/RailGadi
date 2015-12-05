package com.railgadi.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.fonts.AppFonts;
import com.railgadi.preferences.IRCTCAutoLoginPreferences;
import com.railgadi.utilities.ApiConstants;


public class AutoLoginActivity extends Activity {
    private static final int BACK_BUTTON_POPUP_DIALOG_ID = 1;
    public static final String KEY_EDITTEXT_PREFERENCE_PASSWORD = "edittext_irctc_password";
    public static final String KEY_EDITTEXT_PREFERENCE_USERNAME = "edittext_irctc_username";
    public static final String PREFS_NAME = "MyPrefsFile";
    private int counter = 0;
    String desktopUrl = "https://www.services.irctc.co.in";
    String g_Msg = "Pressing the Back button when in the payment gateway may cause the transaction to be unsuccessful.\nAre you sure you want to go back? ";
    AlertDialog mBackButtonAlertDialog = null;
    public CheckBox mDontShowAgain;
    boolean mGoToDesktop = false;
    String mobileUrl = "https://www.services.irctc.co.in/mobile";
    WebView myWebView;
    TextView close, title ;
    String redirectUrl = "https://www.services.irctc.co.in/mobile";
    private boolean username_password_set = false;

    private void setUrltoLoad() {
       /* SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if ((localSharedPreferences != null) && (localSharedPreferences.getBoolean("checkbox_irctc_website_type", false)))
            this.mGoToDesktop = true;
        if (this.mGoToDesktop)
        {
            if (MainViewPagerFragmentActivity.AppBrainDesktopWeb != null)
            {
                this.redirectUrl = MainViewPagerFragmentActivity.AppBrainDesktopWeb;
                return;
            }
            this.redirectUrl = this.desktopUrl;
            return;
        }
        if (MainViewPagerFragmentActivity.AppBrainMobileWeb != null)
        {
            this.redirectUrl = MainViewPagerFragmentActivity.AppBrainMobileWeb;
            return;
        }*/

        this.redirectUrl = this.mobileUrl;
    }

    protected void launchIrctcInformation() {
        //  startActivity(new Intent(this, IrctcInformationActivity.class));
    }

    public void onBackPressed() {
        if ((this.myWebView.isFocused()) && (this.myWebView.canGoBack())) {
            if (this.counter > 8) {
                SharedPreferences localSharedPreferences = getSharedPreferences("MyPrefsFile", 1);
                if (localSharedPreferences != null) {
                    String str = localSharedPreferences.getString("IrctcSkipBackButtonMessage", null);
                    if (((str == null) || (str.equals("NOT checked"))) && (!isFinishing())) {
                        showDialog(1);
                        return;
                    }
                }
                this.myWebView.goBack();
                return;
            }
            this.myWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.auto_login_activity);

        this.close     = (TextView) findViewById(R.id.close_irctc) ;
        this.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoLoginActivity.this.finish();
            }
        });

        this.title     = (TextView) findViewById(R.id.irctc_title) ;
        this.title.setTypeface(AppFonts.getRobotoReguler(this));

        this.myWebView = ((WebView) findViewById(R.id.irctc_web_view));
        this.myWebView.setWebViewClient(new myWebClient());
        this.myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.myWebView.getSettings().setJavaScriptEnabled(true);

        setUrltoLoad();

        this.myWebView.loadUrl(this.redirectUrl);
    }

    protected Dialog onCreateDialog(int paramInt) {
        switch (paramInt) {
            default:
                return null;
            case 1:
        }
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, 2131427421));
        View localView = getLayoutInflater().inflate(R.layout.auto_login_alert_dialog, null);

        this.mDontShowAgain = ((CheckBox) localView.findViewById(R.id.auto_login_check));

        localBuilder.setView(localView);
        localBuilder.setTitle("Confirm");
        localBuilder.setIcon(R.drawable.ic_launcher);

        localBuilder.setMessage(this.g_Msg).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                String str = "NOT checked";
                if (AutoLoginActivity.this.mDontShowAgain.isChecked())
                    str = "checked";
                SharedPreferences.Editor localEditor = AutoLoginActivity.this.getSharedPreferences("MyPrefsFile", 1).edit();
                localEditor.putString("IrctcSkipBackButtonMessage", str);
                localEditor.commit();
                paramAnonymousDialogInterface.cancel();
                AutoLoginActivity.this.myWebView.goBack();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                String str = "NOT checked";
                if (AutoLoginActivity.this.mDontShowAgain.isChecked())
                    str = "checked";
                SharedPreferences.Editor localEditor = AutoLoginActivity.this.getSharedPreferences("MyPrefsFile", 1).edit();
                localEditor.putString("IrctcSkipBackButtonMessage", str);
                localEditor.commit();
                paramAnonymousDialogInterface.cancel();
            }
        });
        this.mBackButtonAlertDialog = localBuilder.create();
        return this.mBackButtonAlertDialog;
    }

/*    public boolean onCreateOptionsMenu(Menu paramMenu)
    {
*//*        getMenuInflater().inflate(2131558402, paramMenu);
        super.onCreateOptionsMenu(paramMenu);*//*
        return true;
    }*/

    protected void onDestroy() {
        super.onDestroy();
    }

/*    public boolean onOptionsItemSelected(MenuItem paramMenuItem)
    {
        switch (paramMenuItem.getItemId())
        {
            default:
                return super.onOptionsItemSelected(paramMenuItem);
            case 16908332:
                Intent localIntent = new Intent(this, MainViewPagerFragmentActivity.class);
                localIntent.addFlags(67108864);
                startActivity(localIntent);
                return true;
            case 2131099951:
        }
        launchIrctcInformation();
        return true;
    }*/

    public class myWebClient extends WebViewClient {

        private String autologin_str = "AutoLogin in Progress. Please wait...";
        private ProgressDialog dialog;
        private String loading_str = "Loading. Please wait...";
        private String redirect_str = "Redirecting to IRCTC Website.\nThis may take a while.\nPlease wait...";

        public myWebClient() {
        }

        public void onPageFinished(WebView paramWebView, String paramString) {
            super.onPageFinished(paramWebView, paramString);
            if (this.dialog != null) ;
            try {
                this.dialog.dismiss();
                this.dialog = null;

                if (AutoLoginActivity.this.counter == 1) {

                    IRCTCAutoLoginPreferences autoLoginPreferences = new IRCTCAutoLoginPreferences(AutoLoginActivity.this);

                    String savedUserName = autoLoginPreferences.getUserName();
                    String savedPassword = autoLoginPreferences.getPassword();

                    if ((savedUserName != null) && (savedPassword != null)) {

                        AutoLoginActivity.this.username_password_set = true;

                        AutoLoginActivity.this.myWebView.loadUrl(ApiConstants.getIrctcAutoLoginURL(savedUserName, savedPassword));
                    }
                }

                return;

            } catch (Exception localException) {

            }
        }

        public void onPageStarted(WebView paramWebView, String paramString, Bitmap paramBitmap) {
            //   MyLog.d("DEBUG", "Counter Value: " + IrctcBookTicket.this.counter);
/*            if (((this.dialog == null) || (!this.dialog.isShowing())) && (!AutoLoginActivity.this.isFinishing()))
            {
                if (AutoLoginActivity.this.counter != 0)
                    //break label108;
                    this.dialog = ProgressDialog.show(AutoLoginActivity.this, "", this.redirect_str, true);
            }
            while (true)
            {
                AutoLoginActivity localIrctcBookTicket = AutoLoginActivity.this;
                localIrctcBookTicket.counter = (1 + localIrctcBookTicket.counter);
                return;
                label108: if ((AutoLoginActivity.this.counter == 1) && (AutoLoginActivity.this.username_password_set))
                // this.dialog = ProgressDialog.show(AutoLoginActivity.this, "", this.autologin_str, true);
                else
                this.dialog = ProgressDialog.show(AutoLoginActivity.this, "", this.loading_str, true);
            }       super.onPageStarted(paramWebView, paramString, paramBitmap);*/

        }

        public void onReceivedError(WebView paramWebView, int paramInt, String paramString1, String paramString2) {
            super.onReceivedError(paramWebView, paramInt, paramString1, paramString2);
            AutoLoginActivity localIrctcBookTicket = AutoLoginActivity.this;
            localIrctcBookTicket.counter = (-1 + localIrctcBookTicket.counter);
        }

        public boolean shouldOverrideUrlLoading(WebView paramWebView, String paramString) {
            paramWebView.loadUrl(paramString);
            return true;
        }
    }
}

/* Location:           /Users/goutam/Desktop/APP/apk & jar/Indian Rail/IndianRail v3.5_dex2jar.jar
 * Qualified Name:     com.coolapps.indianrail.IrctcBookTicket
 * JD-Core Version:    0.6.2
 */