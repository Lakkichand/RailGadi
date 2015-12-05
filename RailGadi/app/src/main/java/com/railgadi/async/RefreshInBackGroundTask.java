package com.railgadi.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.railgadi.beans.PnrPreferenceBean;
import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.beans.RefreshPnrBean;
import com.railgadi.preferences.MyLiveJourneyPreferences;
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

public class RefreshInBackGroundTask extends AsyncTask<Void, Void, Void> {

    private Context context;

    public RefreshInBackGroundTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {

        PnrPreferencesHandler handler = new PnrPreferencesHandler((Activity) context);

        MyLiveJourneyPreferences liveHandler = new MyLiveJourneyPreferences((Activity) context) ;

        Map<String, PnrStatusNewBean> allUpcoming = handler.getAllUpcomingPnr();

        if (allUpcoming != null && allUpcoming.size() > 0) {

            List<Map.Entry<String, PnrStatusNewBean>> entryList = new ArrayList<>(allUpcoming.entrySet());

            for (Map.Entry<String, PnrStatusNewBean> entry : entryList) {

                RefreshPnrBean refreshedBean = null;

                String response = HttpServicesRailGadi.POST(ApiConstants.REFRESH_PNR, CommonInputJsonMethod.getPnrInputJson(context, entry.getKey())) ;

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (!(jsonObject.has("code") && jsonObject.has("message"))) {
                        refreshedBean = JsonResponseParsing.getRefreshPnr(jsonObject);
                    }

                } catch (Exception e) {
                    refreshedBean = null;
                }

                if (refreshedBean != null) {

                    PnrStatusNewBean pnrBean = entry.getValue();

                    pnrBean.setChartingStatus(refreshedBean.getCharting());
                    pnrBean.setPassengerList(refreshedBean.getPassengerList());
                    pnrBean.setPnrNumber(refreshedBean.getPnrNumber());
                    pnrBean.setLastTimeCheck(refreshedBean.getLastTimeCheck());

                    if(pnrBean.getChartingStatus().toLowerCase().equals("chart prepared") ||
                            pnrBean.getChartingStatus().toLowerCase().equals("charting done")) {

                        /*boolean flag = UtilsMethods.compareDepArrToCurrent(pnrBean.getTravelDate(), pnrBean.getTrainInfo().srcDepTime, pnrBean.getTrainInfo().duration) ;

                        if(flag) {
                            liveHandler.saveLiveJourneyToPref(pnrBean);
                        }*/

                        liveHandler.saveLiveJourneyToPref(pnrBean);
                    }

                    PnrPreferenceBean pnrPrefBean = new PnrPreferenceBean();

                    pnrPrefBean.setFlag(Constants.UPCOMING);
                    pnrPrefBean.setPnrNumber(refreshedBean.getPnrNumber());
                    pnrPrefBean.setPnrObject(pnrBean);

                    handler.saveToPreferences(pnrPrefBean);
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        this.cancel(true) ;
    }
}
