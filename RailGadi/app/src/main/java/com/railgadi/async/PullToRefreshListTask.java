package com.railgadi.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;

import com.railgadi.beans.PnrPreferenceBean;
import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.beans.RefreshPnrBean;
import com.railgadi.fragments.UpcomingPnrTab;
import com.railgadi.preferences.PnrPreferencesHandler;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PullToRefreshListTask extends AsyncTask<Void, Void, Void> {

    private Activity activity ;
    private Map<String, PnrStatusNewBean> all ;
    private UpcomingPnrTab uFragment ;

    private SwipeRefreshLayout pullRefresh ;

    public PullToRefreshListTask(Activity activity, Map<String,PnrStatusNewBean> all, UpcomingPnrTab uFragment, SwipeRefreshLayout pullRefresh) {

        this.activity = activity ;
        this.all = all ;
        this.uFragment = uFragment ;

        this.pullRefresh = pullRefresh ;
    }

    @Override
    protected Void doInBackground(Void... params) {

        PnrPreferencesHandler pre = new PnrPreferencesHandler(activity) ;

        List<Map.Entry<String, PnrStatusNewBean>> allPnr = new ArrayList<>(all.entrySet()) ;

        for(Map.Entry<String, PnrStatusNewBean> entry : allPnr) {

            String pnr = entry.getKey() ;

            RefreshPnrBean bean = null ;

            String response = HttpServicesRailGadi.POST(ApiConstants.REFRESH_PNR, CommonInputJsonMethod.getPnrInputJson(activity, pnr)) ;

            try {

                JSONObject jsonObject = new JSONObject(response) ;

                if( ! (jsonObject.has("code") && jsonObject.has("message") ) ) {
                    bean = JsonResponseParsing.getRefreshPnr(jsonObject) ;
                }

            } catch( Exception e ) {
                bean = null ;
            }

            if(bean != null) {

                PnrStatusNewBean pnrBean = entry.getValue() ;

                pnrBean.setChartingStatus(bean.getCharting());
                pnrBean.setPassengerList(bean.getPassengerList());
                pnrBean.setPnrNumber(bean.getPnrNumber());
                pnrBean.setLastTimeCheck(bean.getLastTimeCheck());
                String statusString = bean.getPassengerList().get(bean.getPassengerList().size()-1).status ;
                pnrBean.setMainStatus(statusString) ;

                PnrPreferenceBean pnrPrefBean = new PnrPreferenceBean() ;

                pnrPrefBean.setFlag(Constants.UPCOMING);
                pnrPrefBean.setPnrNumber(bean.getPnrNumber());
                pnrPrefBean.setPnrObject(pnrBean);

                pre.saveToPreferences(pnrPrefBean);
            }
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pullRefresh.setRefreshing(false);
        uFragment.refreshUpcomingAdapter();
    }
}
