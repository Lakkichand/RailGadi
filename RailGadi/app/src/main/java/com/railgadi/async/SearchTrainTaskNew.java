package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.SearchTrainInputBean;
import com.railgadi.beans.SearchTrainResultBean;
import com.railgadi.interfaces.IFindTrainComm;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

public class SearchTrainTaskNew extends AsyncTask<Void, Void, Void> {

    private Context context ;
    private SearchTrainInputBean input ;
    private IFindTrainComm fragment ;

    private Dialog pro ;

    private String error ;
    private SearchTrainResultBean  bean ;

    public SearchTrainTaskNew(Context context, SearchTrainInputBean input, IFindTrainComm fragment) {

        this.context        =   context ;
        this.input          =   input ;
        this.fragment       =   fragment ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pro = UtilsMethods.getStandardProgressDialog(context, SearchTrainTaskNew.this) ;
        pro.show() ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String response = HttpServicesRailGadi.POST(ApiConstants.SEARCH_TRAIN, CommonInputJsonMethod.getSearchTrainInputJson(context, input)) ;

        try {

            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.has("code") && jsonObject.has("message")) {
                error = jsonObject.getString("message") ;
                return null ;
            } else {
                bean = JsonResponseParsing.getSearchTrain(jsonObject);
            }

            if(bean == null) {
                error = "No Data Found" ;
            }

        } catch( Exception e) {
            bean = null ;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        pro.dismiss();

        if (error != null) {
            fragment.updateException(error);
        } else {
            if(bean == null) {
                fragment.updateException("No Data Found");
            } else {
                fragment.updateSearchTrainListUI(bean, input) ;
            }
        }
    }
}
