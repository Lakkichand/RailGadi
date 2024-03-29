package com.railgadi.utilities;

import android.location.Location;

public class CLocation extends Location {

	private boolean bUseMetricUnits = false;
	

	public CLocation(Location location) {
		// TODO Auto-generated constructor stub
		super(location);
	}
	
	
	public boolean getUseMetricUnits()
	{
		return this.bUseMetricUnits;
	}
	
	public void setUseMetricunits(boolean bUseMetricUntis)
	{
		this.bUseMetricUnits = bUseMetricUntis;
	}

	@Override
	public float distanceTo(Location dest) {
		// TODO Auto-generated method stub
		float nDistance = super.distanceTo(dest);
		if(!this.getUseMetricUnits())
		{
			//Convert meters to feet
			nDistance = nDistance * 3.28083989501312f;
		}
		return nDistance;
	}

	@Override
	public float getAccuracy() {
		// TODO Auto-generated method stub
		float nAccuracy = super.getAccuracy();
		if(!this.getUseMetricUnits())
		{
			//Convert meters to feet
			nAccuracy = nAccuracy * 3.28083989501312f;
		}
		return nAccuracy;
	}

	@Override
	public double getAltitude() {
		// TODO Auto-generated method stub
		double nAltitude = super.getAltitude();
		if(!this.getUseMetricUnits())
		{
			//Convert meters to feet
			nAltitude = nAltitude * 3.28083989501312d;
		}
		return nAltitude;
	}

	@Override
	public float getSpeed() {
		// TODO Auto-generated method stub
		float nSpeed = super.getSpeed();
		if(!this.getUseMetricUnits())
		{
			//Convert meters/second to miles/hour
			nSpeed = nSpeed * 2.2369362920544f;
		}
		return nSpeed;
	}
	
	
	
}
