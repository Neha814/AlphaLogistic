package functions;

/**
 * Created by nehabh on 2/15/2016.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import alphalogistics.com.alphalogistics.MainActivity;

public class GPSTracker extends Service implements LocationListener {
    // private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10L;
//	  private static final long MIN_TIME_BW_UPDATES = 60000L;
    boolean canGetLocation = false;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    double latitude;
    Location location;
    protected LocationManager locationManager;
    double longitude;
    private final Context mContext;

    public GPSTracker(Context paramContext) {
        this.mContext = paramContext;
        getLocation();
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public double getLatitude() {
        if (this.location != null) {
            this.latitude = this.location.getLatitude();
        }
        return this.latitude;
    }

    public Location getLocation() {
        try {
            this.locationManager = ((LocationManager) this.mContext.getSystemService(Context.LOCATION_SERVICE));
            this.isGPSEnabled = this.locationManager.isProviderEnabled("gps");
            this.isNetworkEnabled = this.locationManager.isProviderEnabled("network");
            if ((this.isGPSEnabled) || (this.isNetworkEnabled)) {
                this.canGetLocation = true;
                if (this.isNetworkEnabled) {
                    this.locationManager.requestLocationUpdates("network", 60000L, 10.0F, this);
                    Log.d("Network", "Network");
                    if (this.locationManager != null) {
                        this.location = this.locationManager.getLastKnownLocation("network");
                        if (this.location != null) {
                            this.latitude = this.location.getLatitude();
                            this.longitude = this.location.getLongitude();
                        }
                    }
                }
                if ((this.isGPSEnabled) && (this.location == null)) {
                    this.locationManager.requestLocationUpdates("gps", 60000L, 10.0F, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (this.locationManager != null) {
                        this.location = this.locationManager.getLastKnownLocation("gps");
                        if (this.location != null) {
                            this.latitude = this.location.getLatitude();
                            this.longitude = this.location.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception localException) {
            for (; ; ) {
                localException.printStackTrace();
            }
        }
        return this.location;
    }

    public double getLongitude() {
        if (this.location != null) {
            this.longitude = this.location.getLongitude();
        }
        return this.longitude;
    }

    public IBinder onBind(Intent paramIntent) {
        return null;
    }

    public void onLocationChanged(Location paramLocation) {
    }

    public void onProviderDisabled(String paramString) {
    }

    public void onProviderEnabled(String paramString) {
    }

    public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {
    }

    public void showSettingsAlert() {
        Builder localBuilder = new Builder(this.mContext);
        localBuilder.setTitle("GPS is settings");
        localBuilder.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        localBuilder.setPositiveButton("Settings", new OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                ((Activity) GPSTracker.this.mContext).finish();
                Intent localIntent1 = new Intent(GPSTracker.this.mContext, MainActivity.class);
                startActivity(localIntent1);
                Intent localIntent2 = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
                GPSTracker.this.mContext.startActivity(localIntent2);
            }
        });
        localBuilder.setNegativeButton("Cancel", new OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.cancel();
            }
        });
        localBuilder.show();
    }

    public void stopUsingGPS() {
        if (this.locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            this.locationManager.removeUpdates(this);
        }
    }
}
