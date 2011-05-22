package fi.ruisrock2011.android.gps;

import fi.ruisrock2011.android.MapActivity;
import android.location.Location;
import android.location.LocationListener;
import android.location.GpsStatus.Listener;
import android.os.Bundle;

/**
 * GPS Location Listener.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class GPSLocationListener implements LocationListener {
	
	private MapActivity mapActivity;
	
	public GPSLocationListener(MapActivity mapActivity) {
		this.mapActivity = mapActivity;
	}

	@Override
	public void onLocationChanged(Location location) {
		mapActivity.updateGpsLocation(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO: Should we do something?
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO: Should we do something?
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	
	

}
