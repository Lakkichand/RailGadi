package com.railgadi.async;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.RouteChildBean;
import com.railgadi.beans.RouteGroupBean;
import com.railgadi.beans.TimeTableNewBean;
import com.railgadi.interfaces.IUpdateStationBean;
import com.railgadi.utilities.UtilsMethods;

import java.util.ArrayList;
import java.util.List;

public class ParseStationsTask extends AsyncTask<Void, Void, Void> {

    private List<RouteChildBean> beans ;
    private TimeTableNewBean bean ;
    private IUpdateStationBean fragment ;
    private Context context ;
    private Dialog pro ;

    public ParseStationsTask(Context context, TimeTableNewBean bean, IUpdateStationBean fragment) {
        this.context = context ;
        this.bean = bean ;
        this.fragment = fragment ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pro = UtilsMethods.getStandardProgressDialog(context, ParseStationsTask.this) ;
        pro.show() ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        beans = new ArrayList<>();
        for (RouteGroupBean rgb : bean.getGroupBeans()) {
            for (RouteChildBean cb : rgb.getChildList()) {
                beans.add(cb);
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pro.dismiss();
        fragment.updateStationBeans(beans); ;
    }
}
