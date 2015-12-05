package com.railgadi.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.fonts.AppFonts;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.UtilsMethods;

public class TermsAndConditionActivity extends Activity {


    private TextView closeTC, titleTC ;
    private WebView webViewTC ;

    private Dialog pd ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);

        closeTC     =   (TextView) findViewById(R.id.close_t_and_c) ;
        closeTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TermsAndConditionActivity.this.finish();
            }
        });

        titleTC     =   (TextView) findViewById(R.id.t_and_c_title) ;
        titleTC.setTypeface(AppFonts.getRobotoReguler(this));
        titleTC.setText(getResources().getString(R.string.terms_and_conditions));

        webViewTC   =   (WebView) findViewById(R.id.t_and_c_web) ;
        webViewTC.setWebViewClient(new MyWebViewClient()) ;
        webViewTC.getSettings().setDomStorageEnabled(true) ;
        webViewTC.getSettings().setJavaScriptEnabled(true) ;

        pd                  =   UtilsMethods.getStandardProgressDialog(this, null) ;
        pd.show() ;

        webViewTC.loadUrl(ApiConstants.TERMS_AND_CONDITION);

    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);

            if (!pd.isShowing()) {
                pd.show();
            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            if (pd.isShowing()) {
                pd.dismiss();
            }

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish() ;
    }
}
