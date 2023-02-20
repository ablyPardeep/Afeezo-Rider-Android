package com.rider.afeezo.generic;

import com.google.android.gms.maps.model.LatLng;

public interface LatLngInterpolator {

    public static class LinearFixed implements LatLngInterpolator {
        public LatLng a(float f, LatLng latLng, LatLng latLng2) {
            double d = latLng2.latitude;
            double d2 = latLng.latitude;
            double d3 = (double) f;
            double d4 = ((d - d2) * d3) + d2;
            double d5 = latLng2.longitude - latLng.longitude;
            if (Math.abs(d5) > 180.0d) {
                d5 -= Math.signum(d5) * 360.0d;
            }
            return new LatLng(d4, (d5 * d3) + latLng.longitude);
        }
    }

    LatLng a(float f, LatLng latLng, LatLng latLng2);
}
    