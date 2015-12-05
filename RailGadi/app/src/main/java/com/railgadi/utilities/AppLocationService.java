package com.railgadi.utilities;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;

import com.railgadi.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AppLocationService extends Service implements LocationListener {

    public LocationManager locationManager;
	public Location location;
	private String latitiudeTxt;
	private String longitudeTxt;
	private String addressTxt;
	private Address address;
	private HashMap<String,String> locationMap;
	private double latitude;
	private double longitude;
	private Context context;

	private Location gpsLocation;
	private Location networkLocation;

	private static final long MIN_DISTANCE_FOR_UPDATE = 10;
//	private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;
	private static final long MIN_TIME_FOR_UPDATE = 2000;

	private AppLocationService(Context context) {
		this.context    = context;
		locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
		locationMap     = new HashMap<>();
	}

    private static AppLocationService appLocationService ;

    public static AppLocationService getInstance(Context context) {
        if(appLocationService != null) {
            return appLocationService ;
        }
        else {
            appLocationService = new AppLocationService(context) ;
        }
        return appLocationService ;
    }
	
	private void checkLocation(boolean flag) {
		
		gpsLocation      = getLocation(LocationManager.GPS_PROVIDER);

		networkLocation  = getLocation(LocationManager.NETWORK_PROVIDER);
				
		if (gpsLocation != null) {
			latitude     = gpsLocation.getLatitude();
			longitude    = gpsLocation.getLongitude();
			latitiudeTxt = latitude + "";
			longitudeTxt = longitude + "";
			
			if(flag) {
				getAddressFromLocation(latitude, longitude);
				
				//return locationMap;
			} else {
				locationMap.put("latitude", latitiudeTxt);
				locationMap.put("longitude", longitudeTxt);
			}
									
		} else if (networkLocation != null) {
			
			latitude     = networkLocation.getLatitude();
			longitude    = networkLocation.getLongitude();
			latitiudeTxt = latitude + "";
			longitudeTxt = longitude + "";
			
			
			if(flag) {
				getAddressFromLocation(latitude, longitude);
				
			//	return locationMap;
			} else {
				locationMap.put("latitude", latitiudeTxt);
				locationMap.put("longitude", longitudeTxt);
			}
					
		} else {
			showSettingsAlert();		
		}
		
		
	}
	
	

	public Location getLocation(String provider) {
		if (locationManager.isProviderEnabled(provider)) {
			locationManager.requestLocationUpdates(provider,
					MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
			if (locationManager != null) {
				location = locationManager.getLastKnownLocation(provider);
				return location;
			}
		}
		return null;
	}
	
	/**
	    *  Method for show alert if location is off
	    */

		private void showSettingsAlert() {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,2);
			alertDialog.setTitle("Settings");
			alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
			//alertDialog.setCancelable(false);
			alertDialog.setPositiveButton("Settings",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					context.startActivity(intent);
				}
			});		
	
			alertDialog.show();	
	
	   }

	 /**
	    *  Method for show alert if Internet data is off
	    */
	 private void showDataAlert() {
		   AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,2);
			alertDialog.setTitle("Settings");
			alertDialog.setMessage(R.string.connection);
			//alertDialog.setCancelable(false);
			alertDialog.setPositiveButton("Settings",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Settings.ACTION_SETTINGS);
					context.startActivity(intent);
				}
			});		
	
			alertDialog.show();	
	
	   }
	 
	 /**
		 *  Get Address from Latitude and Longitude
		 * @param latitude
		 * @param longitude
		 */
		
		private void getAddressFromLocation(final double latitude,final double longitude) {
			
			Thread thread = new Thread() {
				@Override
				public void run() {
					Looper.prepare();
					Geocoder geocoder = new Geocoder(context, Locale.getDefault());
					try {
									
						List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
						if (addressList != null && addressList.size() > 0) {
						    address          = addressList.get(0);
							StringBuilder sb = new StringBuilder();
							
							for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
								sb.append(address.getAddressLine(i)).append(" ");
							}

//							sb.append(address.getLocality()).append(" ");
//							sb.append(address.getPostalCode()).append(" ");
							sb.append(address.getCountryName());
							addressTxt  = sb.toString();	
							
							locationMap.put("address", addressTxt);
							
							Looper.loop();
						}
					} catch (IOException e) {
						e.printStackTrace();
					} 	
					
				}
			};
			thread.start();
		}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
		locationManager.removeUpdates(this);
	}
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}