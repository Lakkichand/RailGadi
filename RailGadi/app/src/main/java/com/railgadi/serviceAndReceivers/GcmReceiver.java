package com.railgadi.serviceAndReceivers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ComponentName comp = new ComponentName(context.getPackageName(),
							 GcmMessageHandler.class.getName());
		
		startWakefulService(context, intent.setComponent(comp));

		//setResultCode(Activity.RESULT_OK);	
	}
}
