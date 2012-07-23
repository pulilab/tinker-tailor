package com.pulilab.loc_ation;

import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.*;

class Loc {
    private Context ctx = null;
    private LocationManager lm = null;
    private Location loc = null;

    public Loc(Context c) {
	ctx = c;
	lm = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
	Iterator i = lm.getAllProviders().iterator();
	while(i.hasNext()) {
	    String provider = (String)i.next();
	    if(provider != "passive") {
		evalLoc(lm.getLastKnownLocation(provider));
		lm.requestLocationUpdates(provider,0,0,new Listener());
	    }
	}
    }

    public Location ation(){return loc;}

    public void evalLoc(Location l) {
	if(loc == null){
	    loc = l;
	    return;}

	Float dr = loc.distanceTo(l);
	Long dt = l.getTime() - loc.getTime();
	Float da = l.getAccuracy() - loc.getAccuracy();

	if(da < 0 ||
	   (dt > 1000 && da < 5) ||
	   (dt > 120000 && da < 5) ||
	   (dr > 2)) {
	    loc = l;
	    refined(l);
	} else dismissed(l);
    }

    public void refined(Location l){
	Log.d("Loc.ation", "Using new location from " + l.getProvider());
    }
    public void dismissed(Location l){
	Log.d("Loc.ation", "Dismissed location from " + l.getProvider());
    }

    class Listener implements LocationListener {
	public void onLocationChanged(Location l){evalLoc(l);}
	public void onStatusChanged(String p,int s,Bundle e){}
	public void onProviderEnabled(String p) {}
	public void onProviderDisabled(String p) {}
    }
}
