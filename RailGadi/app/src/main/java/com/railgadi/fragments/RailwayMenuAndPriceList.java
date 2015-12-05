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
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.UtilsMethods;

public class RailwayMenuAndPriceList extends Fragment {

    private View rootView ;

    private WebView webView ;

    private Dialog pd ;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView        =   inflater.inflate(R.layout.railway_menu_price_list, container, false) ;

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.railway_menu_price).toUpperCase());



        if(InternetChecking.isNetWorkOn(getActivity())) {

            pd  = UtilsMethods.getStandardProgressDialog(getActivity(), null) ;
            pd.show();

            webView = (WebView) rootView.findViewById(R.id.railway_menu_webview);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(Constants.GOOGLE_DOCS_URL + Constants.RAILWAY_MENU_URL);
            webView.setWebViewClient(new MyWebViewClient());

        }
        else {
            //Toast.makeText(getActivity(), "Connect Internet and Try Again", Toast.LENGTH_SHORT).show() ;
            InternetChecking.noInterNetToast(getActivity());
        }

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
