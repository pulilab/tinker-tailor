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
    private Ator a = null;
    private ArrayList<Listener> sensors = new ArrayList<Listener>();

    public Loc(Context c, Ator a_) {
	ctx = c;
	a = a_;
	lm = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
	Iterator i = lm.getAllProviders().iterator();
	while(i.hasNext()) {
	    String provider = (String)i.next();
	    if(provider != "passive") {
		evalLoc(lm.getLastKnownLocation(provider));
		sensors.add(new Listener(provider));
		lm.requestLocationUpdates(provider,0,0,sensors.get(sensors.size()-1));
	    }
	}
    }

    public void finalize() { close(); }

    public void close() {
	Iterator i = sensors.iterator();
	while(i.hasNext()) { lm.removeUpdates((Listener)i.next()); }
    }

    public Location ation(){return loc;}

    public void evalLoc(Location l) {
	if(loc == null){loc = l; return;}

	Float dr = loc.distanceTo(l);
	Long dt = l.getTime() - loc.getTime();
	Float da = l.getAccuracy() - loc.getAccuracy();

	if(da < 0 ||
	   (dt > 1000 && da < 5) ||
	   (dt > 120000 && da < 5) ||
	   (dr > 2)) {
	    loc = l;
	    a.refined(l);
	} else a.dismissed(l);
    }

    class Listener implements LocationListener {
	private String provider = "";
	public Listener(String p){provider = p;}
	public void onLocationChanged(Location l){evalLoc(l);}
	public void onStatusChanged(String p,int s,Bundle e){}
	public void onProviderEnabled(String p) {}
	public void onProviderDisabled(String p) {}
    }

    public interface Ator {
	public void refined(Location l);
	public void dismissed(Location l);
    }
}
