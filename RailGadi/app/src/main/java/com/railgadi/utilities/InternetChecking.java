package com.railgadi.utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.railgadi.R;
import com.railgadi.fonts.AppFonts;

public class InternetChecking {

    private static Dialog dialog ;

	public static boolean isNetWorkOn(Context cont) {
		
		ConnectivityManager conMgr = (ConnectivityManager) cont.getSystemService(Context.CONNECTIVITY_SERVICE);
	
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		
		if (i == null)
			return false;
		if (!i.isConnected())
			return false;
		if (!i.isAvailable())
			return false;

		return true;
	}



    public static void showNoInternetPopup(final Context context) {
/*
        dialog  =   new Dialog(context) ;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        dialog.setContentView(R.layout.no_internet_popup);

        ImageView image = (ImageView) dialog.findViewById(R.id.no_internet_imageview) ;
        image.setImageDrawable(UtilsMethods.getSvgDrawable(context, R.drawable.no_internet));

        TextView seems = (TextView) dialog.findViewById(R.id.seems_you_are_no) ;
        TextView okButton = (TextView) dialog.findViewById(R.id.ok) ;

        seems.setTypeface(MainActivity.typeface.ROBOTO_LIGHT);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offAlert();
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(intent);
            }
        });

        dialog.show();*/

        Toast toast = Toast.makeText(context, "It seems internet is not connected\nor very slow please try again", Toast.LENGTH_SHORT) ;
        toast.show();

    }


    public static void showLocationAlert(final Context context) {

        dialog = new Dialog(context) ;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        dialog.setContentView(R.layout.custom_alert_dialog);

        TextView title = (TextView) dialog.findViewById(R.id.title) ;
        TextView msg = (TextView) dialog.findViewById(R.id.data_alert_msg) ;
        msg.setText(context.getResources().getString(R.string.enable_location_msg));

        title.setTypeface(AppFonts.getRobotoReguler(context));
        msg.setTypeface(AppFonts.getRobotoLight(context));

        Button button = (Button) dialog.findViewById(R.id.data_alert_btn);
        button.setTypeface(AppFonts.getRobotoLight(context));

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(intent);
            }
        });

        dialog.show();
    }


    public static void noInterNetToast(Context context) {
        Toast toast = Toast.makeText(context, "It seems internet is not connected\nor very slow please try again", Toast.LENGTH_SHORT) ;
        toast.show();
    }


    public static void offAlert() {
        if(dialog != null) {
            dialog.dismiss();
        }
    }
}
