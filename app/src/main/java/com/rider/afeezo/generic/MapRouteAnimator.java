package com.rider.afeezo.generic;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.view.animation.LinearInterpolator;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.List;

public class MapRouteAnimator {
    /* access modifiers changed from: private */
    public Polyline a;
    private AnimatorSet b;

    public interface Callback {
        void a(Polyline polyline);
    }

    private class RouteEvaluator implements TypeEvaluator<LatLng> {
        LatLngInterpolator a;

        RouteEvaluator(LatLngInterpolator latLngInterpolator) {
            this.a = latLngInterpolator;
        }

        /* renamed from: a */
        public LatLng evaluate(float f, LatLng latLng, LatLng latLng2) {
            return this.a.a(f, latLng, latLng2);
        }
    }

    public AnimatorSet animateRoute(GoogleMap googleMap, List<LatLng> list, long j, int i, float f, LatLngInterpolator latLngInterpolator, final Callback callbacks) {
        AnimatorSet animatorSet = this.b;
        if (animatorSet == null) {
            this.b = new AnimatorSet();
        } else {
            animatorSet.removeAllListeners();
            this.b.end();
            this.b.cancel();
            this.b = new AnimatorSet();
        }
        Polyline polyline = this.a;
        if (polyline != null) {
            polyline.remove();
        }
        PolylineOptions geodesic = new PolylineOptions().add(list.get(0)).color(i).width(f).geodesic(true);
        geodesic.zIndex(2.0f);
        this.a = googleMap.addPolyline(geodesic);
        ObjectAnimator ofObject = ObjectAnimator.ofObject(this, "routeIncreaseForward", new RouteEvaluator(latLngInterpolator), list.toArray());
        ofObject.setInterpolator(new LinearInterpolator());
        ofObject.setDuration(j);
        ofObject.addListener(new Animator.AnimatorListener() {
            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                Callback callback = callbacks;
                if (callback != null) {
                    callback.a(MapRouteAnimator.this.a);
                }
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }
        });
        this.b.play(ofObject);
        this.b.start();
        return this.b;
    }

    public void setRouteIncreaseForward(LatLng latLng) {
        List<LatLng> points = this.a.getPoints();
        points.add(latLng);
        this.a.setPoints(points);
    }
}
