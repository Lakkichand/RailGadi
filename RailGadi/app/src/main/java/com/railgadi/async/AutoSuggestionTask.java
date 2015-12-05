package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.railgadi.beans.TrainDataBean;
import com.railgadi.interfaces.IAutoSuggestion;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.InternetChecking;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import java.util.List;

public class AutoSuggestionTask extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private String inputQuery ;
    private IAutoSuggestion fragment ;

    private List<TrainDataBean> suggestionList;

    private Dialog pro ;

    private String error ;

    public AutoSuggestionTask(Context context, String inputQuery, IAutoSuggestion fragment) {

        this.context        =   context ;
        this.inputQuery     =   inputQuery ;
        this.fragment       =   fragment ;
        suggestionList =   null ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro = UtilsMethods.getStandardProgressDialog(context, AutoSuggestionTask.this) ;
        pro.show() ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        if(InternetChecking.isNetWorkOn(context)) {

            String response = HttpServicesRailGadi.POST(ApiConstants.AUTO_SUGGESTION, CommonInputJsonMethod.getAutoSuggestionInputJson(context, inputQuery));

            suggestionList = JsonResponseParsing.getAutoSuggestion(response);

        } else {
            error = "check network settings" ;
        }

        if(isCancelled() && pro.isShowing()) {
            pro.dismiss();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        pro.dismiss();

        if(suggestionList != null) {
            fragment.updateAutoSuggestion(suggestionList);
        } else if (error != null) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show() ;
        } else {
            fragment.updateAutoSuggestionException("No Trains");
        }
    }
}
