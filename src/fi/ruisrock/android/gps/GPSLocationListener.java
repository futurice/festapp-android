package fi.ruisrock.android.gps;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import fi.ruisrock.android.MapActivity;

/**
 * GPS Location Listener.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class GPSLocationListener implements LocationListener, GpsStatus.Listener {
	
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
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onGpsStatusChanged(int event) {
		mapActivity.gpsStatusChanged(event);
	}

}
