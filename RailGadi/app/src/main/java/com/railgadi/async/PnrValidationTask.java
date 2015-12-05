package com.railgadi.async;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.adapters.TripPopupAdapter;
import com.railgadi.beans.PnrPreferenceBean;
import com.railgadi.beans.PnrStatusNewBean;
import com.railgadi.fragments.HomeFragment;
import com.railgadi.preferences.PnrPreferencesHandler;
import com.railgadi.utilities.ApiConstants;
import com.railgadi.utilities.CommonInputJsonMethod;
import com.railgadi.utilities.Constants;
import com.railgadi.utilities.JsonResponseParsing;
import com.railgadi.utilities.UtilsMethods;
import com.railgadi.webServices.HttpServicesRailGadi;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PnrValidationTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private List<String> pnrList;
    private List<PnrStatusNewBean> pnrStatusList;

    private HomeFragment fragment;
    private PnrPreferencesHandler preferencesHandler;

    private Dialog tripDialog;
    private ListView tripsListView;
    private TextView popupTopText;
    private ImageView popupMsgIcon, icon;
    private TextView add, cancel;

    public PnrValidationTask(Context context, List<String> pnrList, HomeFragment fragment) {

        this.context = context;
        this.pnrList = pnrList;
        this.fragment = fragment;

        pnrStatusList = new ArrayList<>();

        preferencesHandler = new PnrPreferencesHandler((Activity) context);
    }

    @Override
    protected Void doInBackground(Void... params) {

        for (String pnr : pnrList) {

            try {

                PnrStatusNewBean bean = null ;

                //String response = HttpServicesRailGadi.GET(ApiConstants.getMessagePnrStatusURL(context, pnr));
                String response = HttpServicesRailGadi.POST(ApiConstants.PNR_SMS, CommonInputJsonMethod.getPnrInputJson(context, pnr)) ;

                if(response != null) {

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("code") && jsonObject.has("message")) {
                        //error = jsonObject.getString("message");
                        //return null;
                    } else {
                        bean = JsonResponseParsing.getMessageValidationPnrStatus(context, jsonObject);
                        if(bean != null) {
                            pnrStatusList.add(bean);
                        }
                    }
                }

            } catch (Exception e) {
                pnrStatusList = null ;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        this.cancel(true);

        if (pnrStatusList != null && pnrStatusList.size() > 0) {
            openTripsPopup(pnrStatusList);
        }
    }

    public void openTripsPopup(List<PnrStatusNewBean> listPnr) {

        tripDialog = new Dialog(context);
        tripDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tripDialog.setContentView(R.layout.trips_from_mobile);
        tripDialog.setCancelable(false);

        tripsListView = (ListView) tripDialog.findViewById(R.id.msg_popup_list_view);
        popupTopText = (TextView) tripDialog.findViewById(R.id.popup_pnr_top_text);
        popupMsgIcon = (ImageView) tripDialog.findViewById(R.id.msg_popup_msg_icon);
        add = (TextView) tripDialog.findViewById(R.id.popup_add);
        icon = (ImageView) tripDialog.findViewById(R.id.msg_popup_top_rail_icon);
        cancel = (TextView) tripDialog.findViewById(R.id.popup_cancel);

        String popupTitle = "We found " + listPnr.size() + " new PNR in SMS\nWould you like to add";
        popupTopText.setText(popupTitle);
        popupMsgIcon.setImageDrawable(UtilsMethods.getSvgDrawable(context, R.drawable.msg_icon_popup));

        final TripPopupAdapter adapter = new TripPopupAdapter(context, listPnr);
        tripsListView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (adapter.getSelectedItem() == null || adapter.getSelectedItem().size() < 1) {
                    Toast.makeText(context, "Please select", Toast.LENGTH_SHORT).show();
                    return;
                }


                for (PnrStatusNewBean ps : adapter.getSelectedItem()) {

                    PnrPreferenceBean pBean = new PnrPreferenceBean();
                    pBean.setPnrNumber(ps.getPnrNumber());
                    pBean.setFlag(Constants.UPCOMING);
                    pBean.setPnrObject(ps);

                    preferencesHandler.saveToPreferences(pBean);
                }

                if (adapter.getUnselectedItem() == null || adapter.getUnselectedItem().size() < 1) {

                    if (tripDialog != null) {
                        tripDialog.dismiss();
                    }

                    if (fragment != null) {
                        fragment.setHorizontalAdapter();
                    }

                    return;
                }

                for (PnrStatusNewBean ps : adapter.getUnselectedItem()) {

                    PnrPreferenceBean pBean = new PnrPreferenceBean();
                    pBean.setPnrNumber(ps.getPnrNumber());
                    pBean.setFlag(Constants.OTHERS);
                    pBean.setPnrObject(ps);

                    preferencesHandler.saveToPreferences(pBean);
                }

                tripDialog.dismiss();
                fragment.setHorizontalAdapter();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tripDialog.dismiss();

                for (PnrStatusNewBean ps : adapter.getAll()) {

                    PnrPreferenceBean pBean = new PnrPreferenceBean();
                    pBean.setPnrNumber(ps.getPnrNumber());
                    pBean.setFlag(Constants.OTHERS);
                    pBean.setPnrObject(ps);

                    preferencesHandler.saveToPreferences(pBean);
                }
            }
        });

        tripDialog.show();
    }


}
