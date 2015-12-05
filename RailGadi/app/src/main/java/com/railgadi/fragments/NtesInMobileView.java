package com.railgadi.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.UtilsMethods;

public class NtesInMobileView extends Fragment {

    private View rootView ;

    private Dialog pd ;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView            =   inflater.inflate(R.layout.ntes_in_mobile_view, container, false) ;

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.ntes_in_mobile_view).toUpperCase());

        WebView ntes        =   (WebView) rootView.findViewById(R.id.ntes_web_view) ;
        ntes.setWebViewClient(new MyWebViewClient()) ;
        ntes.getSettings().setDomStorageEnabled(true) ;
        ntes.getSettings().setJavaScriptEnabled(true) ;

        pd                  =   UtilsMethods.getStandardProgressDialog(getActivity(), null) ;
        pd.show() ;

        ntes.loadUrl(ApiConstants.NTES_IN_MOBILE_VIEW);


        return rootView ;
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

}
