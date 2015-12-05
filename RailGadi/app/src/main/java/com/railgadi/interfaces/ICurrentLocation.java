package com.railgadi.interfaces;

import android.location.Location;

public interface ICurrentLocation {
    public void currentLocationFound(Location cLocation) ;
    public void locationNotFound() ;
}
