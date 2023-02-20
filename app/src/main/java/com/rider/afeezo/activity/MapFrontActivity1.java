/*
package com.rider.afeezo.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rider.afeezo.R;
import com.rider.afeezo.adapter.FavLocationSearchAdapter;
import com.rider.afeezo.adapter.GooglePlacesAutocompleteAdapter;
import com.rider.afeezo.generic.CircleImageView;
import com.rider.afeezo.generic.Constant;
import com.rider.afeezo.generic.DataHolder;
import com.rider.afeezo.generic.Genric;
import com.rider.afeezo.generic.Utility;
import com.rider.afeezo.interfaces.OnSelectedPlaceListener;
import com.rider.afeezo.interfaces.ResponseHandler;
import com.rider.afeezo.interfaces.onFindLocation;
import com.rider.afeezo.model.AddFavLocation;
import com.rider.afeezo.model.Common;
import com.rider.afeezo.model.Drivers;
import com.rider.afeezo.model.FavLocation;
import com.rider.afeezo.model.RideDetails;
import com.rider.afeezo.model.RideStatuses;
import com.rider.afeezo.model.User;
import com.rider.afeezo.model.Vehicles;
import com.rider.afeezo.service.MyFirebaseMessagingService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Response;

import static com.google.android.gms.maps.model.JointType.ROUND;

//import android.support.annotation.NonNull;
//import android.support.annotation.RequiresApi;

public class MapFrontActivity1 extends BaseActivity implements
        OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener, ValueEventListener, ResponseHandler, GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener, OnSelectedPlaceListener, onFindLocation {
    // Data initlization
    private static final int REQUEST_LOCATION = 0;
    public String SEARCH_TYPE = Constant.SEARCH_SOURCE;
    public String FAV_TYPE = "FAV_TYPE";
    DecimalFormat value = new DecimalFormat("###.##");
    EditText editSrcLocation, editDestLocation;
    ViewGroup content;
    Utility utility;
    Activity activity;
    RideDetails.RideDetail tempDetails;
    double longitude, latitude;
    TextView carName, carNumber, driverName, driverRating, rideOtp, totalFare, etaLabel;
    LinearLayout ratingLt, otpLayout, etaLayout;
    CircleImageView driverImage;
    ImageView driverCarImage, starImageView;
    String coupon = "";
    TextView setup_txt_payment, txt_applyCoupon, estimatedTimeArrival, cancelChargeMsg;
    Vehicles vehicles;
    LinearLayout confirmLayout, searchLayout, driverDataLayout, shareDetails, bookRideLt, hsvLayout, callDriverLt, cancelRideLt, endRideLt, couponLt, paymentLt, findingRideLt;
    ArrayList<TextView> carTypes;
    ArrayList<TextView> fares;
    ArrayList<ImageView> imagesList;
    ImageView carImage;
    ResponseHandler listener;
    MapFragment mapFragment;
    ArrayList<LatLng> MarkerPoints;
    LatLng startPoint = null;
    Genric genric;
    IntentFilter filter;
    BroadcastReceiver receiver;
    Bundle bundle;
    Button bookBtn, confirmBookBtn, cancelRequestBtn;
    RotateLoading rotateLoading;
    LatLng endPoint;
    String endLocName, CancelCharge, CancelText, endLocAddress, startLocName,wallet = "0";
    LatLng driverPos;
    ArrayList<FavLocation.Locations> locationData = new ArrayList<>();
    ArrayList<RideStatuses.RideStatus> rideStatus = new ArrayList<>();
    String selectedVehicleId = "", driverMobile;
    Timer timer;
    FrameLayout sosBtn;
    String locationTypeFav;
    ArrayList<Marker> markers = new ArrayList<>();
    ImageView ivLocationPin;
    private boolean isFavClick = false;
    private int lastCarSelectedCarId = -1;
    */
/* public void getNearByPlaces(LatLng origin, String placeName) {
         mMap.clear();
         if (mk != null) {
             mk.remove();
         }
         String url = genric.getUrl(origin.latitude, origin.longitude, placeName);
         Object[] DataTransfer = new Object[2];
         DataTransfer[0] = mMap;
         DataTransfer[1] = url;
         Log.d("onClick", url);
         GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(this, placeName);
         getNearbyPlacesData.execute(DataTransfer);
     }*//*

    String startLocAddress;

    boolean firstTimeSameLoc = false;
    boolean rideAccepted = false;
    Marker marker;
    LatLng startPosition, endPosition, driverlastPosition;
    Polyline polyline;
    String requestUrl;
    PolylineOptions polylineOptions, blackPolylineOptions;
    Polyline greyPolyLine, blackPolyline;
    ArrayList<LatLng> polyLineList = new ArrayList<>();
    EditText edtToolSearch;
    Marker locationMarker;
    Bitmap pinBitmap;
    CountDownTimer rideCancelTimer;
    String requestCancelFrom;
    boolean isSrcFilled = false;
    String carType = "";
    String estimatedTime = "";
    int i = 0;
    Marker carMarker = null, destinationMarker = null;
    ValueAnimator valueAnimator;
    Timer timerss;
    TimerTask timerTask;
    int count = 0;
    Handler handler = new Handler();
    String statusValue = "";
    ArrayList<Marker> pinPointMarker = new ArrayList<>();
    ListView list_favourites;
    boolean isSelectedLocManually = false;
    String payment_mode, payment_card;
    private GoogleMap mMap;
    private DatabaseReference mFirebaseDatabaseRef;
    private FirebaseDatabase mFirebaseInstance;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private int markerCount = 0;
    private String TAG = MapFrontActivity.class.getSimpleName();
    private boolean isFirstTime = true;
    private boolean isSrcFav = false, isDestFav = false;
    private String favSrcId = "", favDestId = "";
    ImageView favouriteSrcBtn, favouriteDestBtn;

    LinearLayout viewSrc, viewDest;
    */
/*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.school:
                bookRide();
//                getNearByPlaces(point, "school");
                break;
            case R.id.atm:
//                getNearByPlaces(point, "atm");
                break;
            case R.id.restaurant:
//                getNearByPlaces(point, "restaurant");
                break;
            case R.id.hospital:
//                getNearByPlaces(point, "hospital");
                break;
        }
        return super.onOptionsItemSelected(item);
    }*//*

    private Dialog toolbarSearchDialog;
    private boolean placesResult = true;

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;
        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }
        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    public static void setDynamicHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        //check adapter if null
        if (adapter == null) {
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = height + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();
    }

    */
/**
     * method used to show retry dialog
     *//*

    public void showReTryDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_ride_cancel, null, false);
        DialogPlus dialogPlus = DialogPlus.newDialog(this).setContentHolder(new ViewHolder(view))
                .setBackgroundColorResId(Color.TRANSPARENT)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.reTryBtn:
                                dialog.dismiss();
                                addRideRequest();
                                break;
                            case R.id.cancelBtn:
                                if (rideCancelTimer != null) rideCancelTimer.cancel();
                                findingRideLt.setVisibility(View.GONE);
                                searchLayout.setVisibility(View.VISIBLE);
                                editDestLocation.setText("");
                                endPoint = null;
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .setGravity(Gravity.BOTTOM).setCancelable(false)  // This will enable the expand
                // feature, (similar to android L share dialog)
                .create();
        dialogPlus.show();
    }

    @Override
    public void onBackPressed() {
        if (confirmLayout.getVisibility() == View.VISIBLE) {
            SEARCH_TYPE = Constant.SEARCH_DESTINATION;
            if (SEARCH_TYPE == Constant.SEARCH_SOURCE)
                viewSrc.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_selected_bg));
            else if (SEARCH_TYPE == Constant.SEARCH_DESTINATION)
                viewDest.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_selected_bg));
            confirmLayout.setVisibility(View.GONE);
            hsvLayout.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.VISIBLE);
            editSrcLocation.setEnabled(true);
            editDestLocation.setEnabled(true);

            ivLocationPin.setVisibility(View.VISIBLE);
            coupon = "";
            mMap.clear();
            getNearByVehicles();
            txt_applyCoupon.setText(getString(R.string.apply_coupon));
        } else if (!DataHolder.getInstance().getRideRequestId().isEmpty()) {
            cancelRideRequest("Back");
            super.onBackPressed();
        } else
            super.onBackPressed();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                */
/*  .addApi(Places.GEO_DATA_API)
                  .addApi(Places.PLACE_DETECTION_API)*//*

                .addApi(LocationServices.API).build();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content = findViewById(R.id.relLayout);
        getLayoutInflater().inflate(R.layout.activity_main, content, true);
        if (!DataHolder.getInstance().getStore(this).getString(Constant.AUTHORIZE).contentEquals("1")) {
            startActivity(new Intent(this, UserRegistration.class));
            finish();
            return;
        }
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        utility = new Utility(this);
        activity = this;
        mLocationRequest = LocationRequest.create();

        initView();
        setMylocationBtn();

        bundle = getIntent().getExtras();
        filter = new IntentFilter("RIDEDETAILS");
        try {
            if (bundle != null) {
                String mBody = bundle.getString("Body", "");
                String mTitle = bundle.getString("Title", "");
                Utility.showMapScreenDialog(this, mTitle, mBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        registerReceiver(receiver, filter);
        if (bundle != null) {
            if (timer != null) timer.cancel();
            callRiderDetail(bundle);
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundleReceived = intent.getExtras();
                if (timer != null) timer.cancel();

                if (MyFirebaseMessagingService.notificationManager != null) {
                    MyFirebaseMessagingService.notificationManager.cancelAll();
                }
                if (bundleReceived != null) {
                    if (bundleReceived.getString("type").equals(Constant.RIDE_REQUEST_DECLINED_ALL_DRIVERS)) {
                        showDialog(MapFrontActivity.this, bundleReceived.getString("message"), true);
                    } else if (bundleReceived.getString("type").equals(Constant.RIDE_CANCELLED)) {
                        rideAccepted = false;
                        showDialog(MapFrontActivity.this, bundleReceived.getString("message"), false);
                    } else
                        callRiderDetail(bundleReceived);
                }
            }
        };
      */
/*if (utility.isMyServiceRunning(CabService.class)) {
            goneViews();
        }*//*


        getRideStatuses();
        getNearByVehicles();

    }

    */
/**
     * method used to bounce markers
     *//*

    public void bounceMarker() {
        final Handler handler = new Handler();

        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;

        Projection proj = mMap.getProjection();
        final LatLng markerLatLng = locationMarker.getPosition();
        Point startPoint = proj.toScreenLocation(markerLatLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                locationMarker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    */
/**
     * method used to show dialog
     * @param context
     * @param msg
     * @param decline
     *//*

    public void showDialog(Context context, String msg, final boolean decline) {
        AlertDialog.Builder b = new AlertDialog.Builder(context, R.style.MaterialThemeDialog);
        b.setTitle(context.getString(R.string.message));
        b.setMessage(msg);
        b.setCancelable(false);
        b.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (MyFirebaseMessagingService.notificationManager != null)
                    MyFirebaseMessagingService.notificationManager.cancelAll();

                if (decline) {
                    cancelRideRequest("Dialog");
                }
                DataHolder.getInstance().setRideId("");
                DataHolder.getInstance().setRideRequestId("");
                finish();
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                dialog.cancel();
            }
        });
        AlertDialog alert11 = b.create();
        alert11.show();
    }

    */
/**
     * method used to show confirmation end ride
     * @param msg
     *//*

    public void ConfirmationEndRide(String msg) {
        AlertDialog.Builder b = new AlertDialog.Builder(this, R.style.MaterialThemeDialog);
        b.setTitle(this.getString(R.string.message));
        b.setMessage(msg);
        b.setCancelable(false);
        b.setPositiveButton(this.getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                endRide();
            }
        });
        b.setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert11 = b.create();
        alert11.show();
    }

    */
/**
     * method used to show sos confirmation dialog
     *//*

    public void showSosConfirmation() {
        AlertDialog.Builder b = new AlertDialog.Builder(this, R.style.MaterialThemeDialog);
        b.setTitle(getString(R.string.are_you_sure));
        b.setMessage(R.string.emergency_message);
        b.setCancelable(true);
        b.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                triggerEmergencyContacts();
                dialog.dismiss();
            }
        });
        b.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert11 = b.create();
        alert11.show();
    }

    */
/**
     * method used to fetch rider details
     * @param bundle
     *//*

    private void callRiderDetail(Bundle bundle) {
        System.out.println("bundle.getString(\"type\") = " + bundle.getString("type"));
        try {
            if (bundle.getString("type").equalsIgnoreCase(Constant.RIDE_STARTED)) {
                cancelRideLt.setVisibility(View.GONE);
                endRideLt.setVisibility(View.GONE);
//                endRideLt.setVisibility(View.VISIBLE); //previous it was visible
                otpLayout.setVisibility(View.GONE);
                statusValue = "STARTED";
                firstTimeSameLoc = false;
                getRideDetail(bundle.getString("recordId"));
                sosBtn.setVisibility(View.VISIBLE);
            } else if (bundle.getString("type").equalsIgnoreCase(Constant.RIDE_REQUEST_COMPLETED)) {
//                rideRequestComplete = true;
                statusValue = "BOOKED";
                getRideDetail(bundle.getString("recordId"));
            } else if (bundle.getString("type").equalsIgnoreCase(Constant.RIDE_COMPLETED)) {
                rideAccepted = false;
                DataHolder.getInstance().setRideId("");
                statusValue = "COMPLETED";
                startActivity(new Intent(this, RideCompleteActivity.class).putExtra(Constant.RIDE_ID, bundle.getString("recordId")));
            } else if (bundle.getString("type").equalsIgnoreCase(Constant.RIDE_READY_FOR_PICKUP)) {
                if (mMap != null)
                    mMap.clear();
                statusValue = "PICKUP_READY";
                getRideDetail(bundle.getString("recordId"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
/**
     * method used to fetch address data
     *//*

    public void getAddressData() {

        if (locationData != null && locationData.size() > 0)
            locationData.clear();

        Type type = new TypeToken<List<FavLocation.Locations>>() {
        }.getType();
        Gson gson = new Gson();

        String data = DataHolder.getInstance().getStore(this).getString(Constant.FAV_LOCATION);
        if (!data.equals("")) {
            ArrayList<FavLocation.Locations> arrayList = gson.fromJson(data, type);
            locationData.addAll(arrayList);
        }
        loadToolBarSearch();

        */
/*if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this);
            return;
        }
        utility.startProgressRotate(rotateLoading, this);
        DataHolder.getInstance().getUserFavoriteLocations(Constant.GET_FAV_LOCATION, DataHolder.getInstance().getToken(),
                listener);*//*

    }

    */
/**
     * method used to call share ride details API
     *//*

    public void shareRideDetails() {
        if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this);
            return;
        }
        utility.startProgressRotate(rotateLoading, this);
        DataHolder.getInstance().getRideShareUrl(Constant.SHARE_RIDE_DETAIL, DataHolder.getInstance().getToken(),
                tempDetails.getRide_id(), listener);
    }

    @Override
    public void onSuccess(int tag, Response response) {
        if (rotateLoading != null)
            utility.stopProgressRotate(rotateLoading, this);
        if (tag == Constant.GET_NEARBY_DRIVERS) {
            Drivers driverData = (Drivers) response.body();
            Bitmap carIcon = null;
            if (driverData != null && driverData.getStatus().contentEquals("1")) {
                if (mMap != null) {
                    mMap.clear();
                    if (markers != null)
                        markers.clear();
                }
                int height = 100;
                int width = 60;
                carIcon = genric.getBitmapFromVectorDrawable(activity, R.mipmap.icon_car);
              */
/*if(selectedVehicleId.isEmpty())
                    carIcon = genric.getBitmapFromVectorDrawable(this, R.drawable.ic_hatchback_gray);
                    else {
                    imagesList.get(selectedCarIndex).buildDrawingCache();
                    carIcon = imagesList.get(selectedCarIndex).getDrawingCache();
                }*//*

                Bitmap smallMarker = Bitmap.createScaledBitmap(carIcon, width, height, false);
                if (driverData.getVehicles() != null) {
                    setBookBtn(true);
                    for (int i = 0; i < driverData.getVehicles().size(); i++) {
                        if (mMap != null) {
                            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(driverData.getVehicles().get(i).getLatitude()), Double.parseDouble(driverData.getVehicles().get(i).getLongitude())))
                                    .icon(BitmapDescriptorFactory.fromBitmap((smallMarker))));
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(new LatLng(Double.parseDouble(driverData.getVehicles().get(i).getLatitude()), Double.parseDouble(driverData.getVehicles().get(i).getLongitude())));
                            markers.add(marker);
                        }
                    }
//                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                  */
/*  for (Marker marker : markers) {
                        builder.include(marker.getPosition());
                        System.out.println("marker.getPosition() = " + marker.getPosition());
                    }
                    LatLngBounds bounds = builder.build();
                    int padding = 30; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.moveCamera(cu);*//*

                } else
                    setBookBtn(false);

            } else if (driverData != null && driverData.getStatus().contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(activity);
            } else if (driverData != null)
                Utility.showToast(activity, driverData.getMsg());
            else
                Utility.showToast(activity, getString(R.string.error_something_wrong));

        } else if (tag == Constant.GET_VEHICLES) {
            vehicles = (Vehicles) response.body();
            if (vehicles != null && vehicles.getStatus().contentEquals("1")) {
                confirmLayout.setVisibility(View.GONE);
                editSrcLocation.setEnabled(true);
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                carTypes = new ArrayList<>();
                fares = new ArrayList<>();
                imagesList = new ArrayList<>();
                bookRideLt.removeAllViews();
                if (vehicles.getVehiclesTypes() != null) {
                    for (int i = 0; i < vehicles.getVehiclesTypes().size(); i++) {
                        final View view = layoutInflater.inflate(R.layout.car_layout, null);
                        TextView baseFare = view.findViewById(R.id.baseFare);
                        TextView carType = view.findViewById(R.id.carType);
                        carImage = view.findViewById(R.id.carImage);
                        Glide.with(getApplicationContext()).load(vehicles.getVehiclesTypes().get(i).getIcon()).apply(new
                                RequestOptions().override(150, 150)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.image_placeholder)).into(carImage);
                        carType.setText(vehicles.getVehiclesTypes().get(i).getName());
                        baseFare.setText(vehicles.getCurrencySymbol() + value.format(Float.parseFloat(vehicles
                                .getVehiclesTypes().get
                                        (i).getVehicleTypeEstimateArr().getBase_fare())) + "*");

                        ColorMatrix matrix = new ColorMatrix();
                        matrix.setSaturation(0);

                        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                        carImage.setColorFilter(filter);
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                1.0f
                        );
                        view.setId(Integer.parseInt(vehicles.getVehiclesTypes().get(i).getId()));
                        view.setLayoutParams(param);
                        view.setOnClickListener(this);
                        imagesList.add(carImage);
                        carTypes.add(carType);
                        fares.add(baseFare);
                        bookRideLt.addView(view);
                    }
                }
                if (bookRideLt.getChildCount() > 0)
                    hsvLayout.setVisibility(View.VISIBLE);
                else
                    hsvLayout.setVisibility(View.GONE);
            } else if (vehicles != null && vehicles.getStatus().contentEquals(Constant.SESSION_EXPIRED))
                utility.sessionExpire(this);
            else if (vehicles != null)
                Utility.showLongToast(this, vehicles.getMsg(), true);
            else
                Utility.showToast(this, getString(R.string.error_something_wrong));

        } else if (tag == Constant.ADD_RIDE_REQUEST) {


            Common common = (Common) response.body();
            if (common != null && common.getStatus().contentEquals("1")) {
                String rideRequestId = common.getRideRequestId();
                DataHolder.getInstance().setRideRequestId(rideRequestId);
                confirmLayout.setVisibility(View.GONE);
                findingRideLt.setVisibility(View.VISIBLE);

                setRideRespondTime(Integer.parseInt(common.getRideRequestAutoCancelledInterval()) * 60);
            } else if (common != null && common.getStatus().contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this);
            } else if (common != null && common.getRequestId() != null) {
//                Utility.showToast(this, common.getMsg());
                DataHolder.getInstance().setRideRequestId(common.getRequestId());
                cancelRideRequest("AddRide");
                addRideRequest();
            } else if (common != null)
                Utility.showToast(this, common.getMsg());
            else
                Utility.showToast(this, getString(R.string.error_something_wrong));
        } else if (tag == Constant.RIDE_DETAIL) {
            RideDetails rideDetails = (RideDetails) response.body();
            if (rideDetails != null && rideDetails.getStatus().contentEquals("1")) {
                DataHolder.getInstance().setRideId(rideDetails.getRideDetail().getRide_id());
                endLocName = rideDetails.getRideDetail().getRide_end_location();
                CancelCharge = rideDetails.getRideDetail().getCancellation_charges();
                CancelText = rideDetails.getRideDetail().getRide_cancel_msg_text();
                Constant.CURRENCY = rideDetails.getCurrencySymbol();
                if (rideCancelTimer != null) rideCancelTimer.cancel();
                setUpDriverProfile(rideDetails.getRideDetail());
//                if (rideRequestComplete) {

                if (statusValue.equals("BOOKED")) {
                    if (mMap != null)
                        mMap.clear();
                    drawPathPolyline(driverPos, rideDetails.getRideDetail().getRide_start_location
                            (), new LatLng(Float.parseFloat(rideDetails.getRideDetail().getSrc_lat()),
                            Float.parseFloat(rideDetails.getRideDetail().getSrc_lng())));
                } else if (statusValue.equals("PICKUP_READY") || statusValue.equals("STARTED")) {
                    if (mMap != null)
                        mMap.clear();
                    drawPathPolyline(driverPos, rideDetails.getRideDetail().getRide_end_location(), new LatLng(Float
                            .parseFloat(rideDetails.getRideDetail().getDest_lat()), Float.parseFloat(rideDetails.getRideDetail()
                            .getDest_lng())));
                } */
/*else if (statusValue.equals("STARTED")) {
                    drawPathPolyline(driverPos, rideDetails.getRideDetail().getRide_end_location
                            (), new LatLng(Float.parseFloat(rideDetails.getRideDetail().getDest_lat()),
                            Float.parseFloat(rideDetails.getRideDetail().getDest_lng())));
                }*//*

//                }
            } else if (rideDetails != null && rideDetails.getStatus().contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this);
            } else if (rideDetails != null && rideDetails.getStatus().contentEquals("-1")) {
                new Utility(this).showSnackBar(rideDetails.getMsg());
            } else
                new Utility(this).showSnackBar(getString(R.string.server_error));
        } else if (tag == Constant.SAVE_FIREBASE_TOKEN) {
            Common common = (Common) response.body();
            if (common != null && common.getStatus().contentEquals("1")) {
                DataHolder.getInstance().getStore(this).saveBoolean(Constant.ONE_TIME_CALL, false);
            } else if (common != null && common.getStatus().contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this);
            } else if (common != null)
                Utility.showToast(this, common.getMsg());
            else
                Utility.showToast(this, getString(R.string.error_something_wrong));
        } else if (tag == Constant.RIDE_STATUSES) {
            RideStatuses statuses = (RideStatuses) response.body();
            if (statuses != null && statuses.getStatus().contentEquals("1")) {
                rideStatus.addAll(statuses.getRideStatus());
                getActiveRide();
            } else if (statuses != null && statuses.getStatus().contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this);
            } else
                Utility.showToast(this, getString(R.string.error_something_wrong));
        } else if (tag == Constant.GET_ACTIVE_RIDE) {
            RideDetails rideDetails = (RideDetails) response.body();
            if (rideDetails.getAmount() != null && Double.parseDouble(rideDetails.getAmount()) > 0) {
                cancelChargeMsg.setVisibility(View.VISIBLE);
                cancelChargeMsg.setText(rideDetails.getPrevChargeText());
            }
            if (rideDetails != null && rideDetails.getStatus().contentEquals("1")) {
                for (RideStatuses.RideStatus status : rideStatus) {
                    if (status.getId().equals(rideDetails.getRideDetail().getRide_status()))
                        statusValue = status.getName();
                }
                selectedVehicleId = rideDetails.getRideDetail().getVtype_id();
                DataHolder.getInstance().setRideId(rideDetails.getRideDetail().getRide_id());
                endLocName = rideDetails.getRideDetail().getRide_end_location();
                CancelCharge = rideDetails.getRideDetail().getCancellation_charges();
                CancelText = rideDetails.getRideDetail().getRide_cancel_msg_text();
                Constant.CURRENCY = rideDetails.getCurrencySymbol();
                if (!statusValue.isEmpty()) {
                    setUpDriverProfile(rideDetails.getRideDetail());
                    if (statusValue.equals("BOOKED")) {
                        viewSrc.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_bg));
                        viewDest.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_bg));
                        SEARCH_TYPE = Constant.SEARCH_EMPTY;
                        ivLocationPin.setVisibility(View.GONE);
                        if (timer != null) timer.cancel();

                        if (mMap != null)
                            mMap.clear();
//                        drawPathPolyline(driverPos, rideDetails.getRideDetail().getRide_start_location());
                        drawPathPolyline(driverPos, rideDetails.getRideDetail().getRide_start_location
                                (), new LatLng(Float.parseFloat(rideDetails.getRideDetail().getSrc_lat()),
                                Float.parseFloat(rideDetails.getRideDetail().getSrc_lng())));
                    } else if (statusValue.equals("PICKUP_READY")) {
                        viewSrc.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_bg));
                        viewDest.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_bg));
                        SEARCH_TYPE = Constant.SEARCH_EMPTY;
                        ivLocationPin.setVisibility(View.GONE);
                        if (timer != null) timer.cancel();
//                        startPoint = searchedLtlng(rideDetails.getRideDetail().getRide_start_location());
                        startPoint = new LatLng(Float.parseFloat(rideDetails.getRideDetail().getSrc_lat()),
                                Float.parseFloat(rideDetails.getRideDetail().getSrc_lng()));
                        if (mMap != null)
                            mMap.clear();
//                        drawPathPolyline(startPoint, rideDetails.getRideDetail().getRide_end_location());
                        drawPathPolyline(driverPos, rideDetails.getRideDetail().getRide_end_location
                                (), new LatLng(Float.parseFloat(rideDetails.getRideDetail().getDest_lat()),
                                Float.parseFloat(rideDetails.getRideDetail().getDest_lng())));
                    } else if (statusValue.equals("STARTED")) {
                        viewSrc.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_bg));
                        viewDest.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_bg));
                        SEARCH_TYPE = Constant.SEARCH_EMPTY;
                        ivLocationPin.setVisibility(View.GONE);
                        if (timer != null) timer.cancel();
                        sosBtn.setVisibility(View.VISIBLE);
//                        startPoint = searchedLtlng(rideDetails.getRideDetail().getRide_start_location());
                        startPoint = new LatLng(Float.parseFloat(rideDetails.getRideDetail().getSrc_lat()),
                                Float.parseFloat(rideDetails.getRideDetail().getSrc_lng()));
                        if (mMap != null)
                            mMap.clear();
//                        drawPathPolyline(startPoint, rideDetails.getRideDetail().getRide_end_location());
                        drawPathPolyline(driverPos, rideDetails.getRideDetail().getRide_end_location
                                (), new LatLng(Float.parseFloat(rideDetails.getRideDetail().getDest_lat()),
                                Float.parseFloat(rideDetails.getRideDetail().getDest_lng())));
                        cancelRideLt.setVisibility(View.GONE);
                        otpLayout.setVisibility(View.GONE);
                        endRideLt.setVisibility(View.GONE);
//                        endRideLt.setVisibility(View.VISIBLE); //Previous it was visible
                    }
                }
            } else if (rideDetails != null && rideDetails.getStatus().contentEquals("-1")) {
                System.out.println("rideDetails.getMsg() = " + rideDetails.getMsg());

                getNearByDrivers(latitude + "", longitude + "");
//                Utility.showToast(this, rideDetails.getMsg());
            } else if (rideDetails != null && rideDetails.getStatus().contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this);
            } else
                Utility.showToast(this, getString(R.string.error_something_wrong));
        } else if (tag == Constant.CANCEL_RIDE_REQUEST) {
            Common common = (Common) response.body();


            lastCarSelectedCarId = -1;
            editSrcLocation.setEnabled(true);
            editDestLocation.setEnabled(true);
            if (mMap != null)
                mMap.clear();
            ivLocationPin.setVisibility(View.VISIBLE);
            SEARCH_TYPE = Constant.SEARCH_DESTINATION;
            if (SEARCH_TYPE == Constant.SEARCH_SOURCE)
                viewSrc.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_selected_bg));
            else if (SEARCH_TYPE == Constant.SEARCH_DESTINATION)
                viewDest.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_selected_bg));

            if (common != null && common.getStatus() != null && common.getStatus().contentEquals
                    ("1")) {
                DataHolder.getInstance().setRideRequestId("");
                if (requestCancelFrom != null) {
                    if (requestCancelFrom.equalsIgnoreCase("Timer")) {
                        showReTryDialog();
                    } else if (!requestCancelFrom.equalsIgnoreCase("AddRide")) {
                        if (rideCancelTimer != null) rideCancelTimer.cancel();
                        findingRideLt.setVisibility(View.GONE);
                        searchLayout.setVisibility(View.VISIBLE);
//                        editDestLocation.setText("");
//                        endPoint = null;
                    }
                }
            } else if (common != null && common.getStatus() != null && common.getStatus()
                    .contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this);
            } */
/*else if (common != null)
                Utility.showToast(this, common.getMsg());*//*
 */
/*else
                Utility.showToast(this, getString(R.string.error_something_wrong));*//*

        } else if (tag == Constant.END_RIDE) {
            Common common = (Common) response.body();
            if (common != null && common.getStatus().contentEquals("1")) {
                DataHolder.getInstance().setRideRequestId("");
                DataHolder.getInstance().getStore(this).saveString(Constant.COMPLETE_RIDE,
                        DataHolder.getInstance().getRideId());
                statusValue = "COMPLETED";
                startActivity(new Intent(this, RideCompleteActivity.class).putExtra(Constant.RIDE_ID, DataHolder.getInstance().getRideId()));
            } else if (common != null && common.getStatus().contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this);
            } else if (common != null)
                Utility.showToast(this, common.getMsg());
            else
                Utility.showToast(this, getString(R.string.error_something_wrong));
        } else if (tag == Constant.ADD_FAV_LOCATION) {
            AddFavLocation addFavLocation = (AddFavLocation) response.body();
            if (addFavLocation != null && addFavLocation.getStatus().contentEquals("1")) {
                FavLocation.Locations locations = new FavLocation.Locations();
                locations.setId(addFavLocation.getData().getUfavpointId());
                locations.setDate_added(addFavLocation.getData().getUfavpointAddedDate());
                locations.setGoogle_loc_name(addFavLocation.getData().getUfavpointGoogleName());
                locations.setLatitude(addFavLocation.getData().getUfavpointLatitude());
                locations.setLongitude(addFavLocation.getData().getUfavpointLongitude());
                locations.setName(addFavLocation.getData().getUfavpointName());

                ArrayList<FavLocation.Locations> arrayList = new ArrayList<>();
                Type type = new TypeToken<List<FavLocation.Locations>>() {
                }.getType();
                Gson gson = new Gson();

                String data = DataHolder.getInstance().getStore(this).getString(Constant.FAV_LOCATION);

                if (!data.equals("")) {
                    arrayList = gson.fromJson(data, type);

                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i).getId().equals(locations.getId())) {
                            arrayList.remove(i);
                            break;
                        }
                    }

                    arrayList.add(locations);
                } else {
                    arrayList.add(locations);
                }

                String list = gson.toJson(arrayList);
                DataHolder.getInstance().getStore(this).saveString(Constant.FAV_LOCATION, list);
                Utility.showToast(this, addFavLocation.getMsg());

                if (FAV_TYPE == Constant.FAV_SOURCE) {
                    favSrcId = addFavLocation.getData().getUfavpointId();
                    favouriteSrcBtn.setImageResource(R.drawable.ic_favorite_red_700_24dp);
                    isSrcFav = true;
                } else if (FAV_TYPE == Constant.FAV_DESTINATION) {
                    favDestId = addFavLocation.getData().getUfavpointId();
                    favouriteDestBtn.setImageResource(R.drawable.ic_favorite_red_700_24dp);
                    isDestFav = true;
                }

            } else if (addFavLocation != null && addFavLocation.getStatus().contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this);
            } else if (addFavLocation != null) {
                Utility.showToast(this, addFavLocation.getMsg());
            } else
                Utility.showToast(this, getString(R.string.error_something_wrong));
        } else if (tag == Constant.GET_FAV_LOCATION) {

            FavLocation favLocation = (FavLocation) response.body();
            if (favLocation != null && favLocation.getStatus().contentEquals("1")) {
                if (favLocation.getLocations() != null)
                    locationData = favLocation.getLocations();
                loadToolBarSearch();
            } else if (favLocation != null && favLocation.getStatus().contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this);
            } else
                Utility.showToast(this, getString(R.string.error_something_wrong));
        } else if (tag == Constant.SHARE_RIDE_DETAIL) {
            Common common = (Common) response.body();
            if (common != null && common.getStatus().contentEquals("1")) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, common.getMsg());
                share.putExtra(Intent.EXTRA_TEXT, common.getMsg());
                activity.startActivity(Intent.createChooser(share, getString(R.string.share)));
            } else if (common != null && common.getStatus().contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this);
            } else
                Utility.showToast(this, getString(R.string.error_something_wrong));
        } else if (tag == Constant.TRIGGER_CONTACTS) {
            Common common = (Common) response.body();
            if (common != null && common.getStatus().contentEquals("1")) {
                Utility.showToast(this, common.getMsg());
            } else if (common != null && common.getStatus().contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this);
            } else if (common != null) {
                Utility.showToast(this, common.getMsg());
            } else
                Utility.showToast(this, getString(R.string.error_something_wrong));
        }
    }

    */
/**
     * method used to get rides status from API
     *//*

    public void getRideStatuses() {
        if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this);
            return;
        }
        utility.startProgressRotate(rotateLoading, this);
        DataHolder.getInstance().getConfigRideStatuses(Constant.RIDE_STATUSES, DataHolder.getInstance().getToken(), listener);
    }

    */
/**
     * method used to get active rides through API
     *//*

    public void getActiveRide() {
        if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this);
            return;
        }
        utility.startProgressRotate(rotateLoading, this);
        DataHolder.getInstance().getActiveRide(Constant.GET_ACTIVE_RIDE, DataHolder.getInstance().getToken(), listener);
    }

    */
/**
     * method used to set book button
     * @param enable
     *//*

    public void setBookBtn(boolean enable) {
        if (enable) {
            bookBtn.setEnabled(true);
            bookBtn.setBackgroundResource(R.drawable.button_bg);
            bookBtn.setText(getString(R.string.book_now) + " " + carType);
        } else {
            bookBtn.setEnabled(false);
            bookBtn.setBackgroundResource(R.drawable.button_bg_grey);
            bookBtn.setText(getString(R.string.no_cab_available));
        }
    }

    @Override
    public void onError(Throwable throwable) {
        utility.stopProgressRotate(rotateLoading, this);
        // utility.showSnackBar(throwable.getMessage());
        utility.showSnackBar(getResources().getString(R.string.error_internet_connection));
    }

    public void sendRegistrationIdToBackend() {
        final String regId = FirebaseInstanceId.getInstance().getToken();
        System.out.println("regId = " + regId);
        if (regId == null || regId.equalsIgnoreCase("null") || regId.length() == 0) {
            return;
        }
        DataHolder.getInstance().saveFirebaseToken(Constant.SAVE_FIREBASE_TOKEN, regId, DataHolder.getInstance().getToken(), listener);
    }

    */
/**
     * method used to set up driver profile
     * @param rideDetails
     *//*

    public void setUpDriverProfile(RideDetails.RideDetail rideDetails) {
        try {
            rideAccepted = true;
            if (MyFirebaseMessagingService.notificationManager != null)
                MyFirebaseMessagingService.notificationManager.cancelAll();
            tempDetails = rideDetails;
            carName.setText(rideDetails.getRide_vehicle_make());
            driverMobile = rideDetails.getRide_driver_phone();
            carNumber.setText(rideDetails.getRide_vehicle_number());
            driverName.setText(rideDetails.getRide_driver_name());
            DecimalFormat value = new DecimalFormat("#.#");
            if (rideDetails.getDriverRating() != null) {
                starImageView.setImageResource(R.drawable.ic_star_yellow_24dp);
                driverRating.setText(value.format(Float.parseFloat(rideDetails.getDriverRating())) + "");
            } else {
                starImageView.setImageResource(R.drawable.ic_empty_star);
                driverRating.setText(getString(R.string.not_applicable));
            }
            rideOtp.setText(rideDetails.getRide_otp());
            Glide.with(getApplicationContext()).load(rideDetails.getDriver_image()).apply(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.user_placeholder)
                    .skipMemoryCache(true)).into(driverImage);
            Glide.with(getApplicationContext()).load(rideDetails.getIcon()).apply(new
                    RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.image_placeholder)
                    .skipMemoryCache(true)).into(driverCarImage);

            DataHolder.getInstance().setRideRequestId("");
            DataHolder.getInstance().setRideId(rideDetails.getRide_id());
            driverDataLayout.setVisibility(View.VISIBLE);
            confirmLayout.setVisibility(View.GONE);
            findingRideLt.setVisibility(View.GONE);
            searchLayout.setVisibility(View.GONE);
            driverPos = new LatLng(Double.parseDouble(rideDetails.getDriver_lat()), Double.parseDouble
                    (rideDetails.getDriver_lng()));

           */
/* if (!driverPos.equals(startPoint) || ) {
                rideAccepted = true;
            }*//*

//            cancelChargeMsg reachedRideLimitMsg.setSelected(true);
            mFirebaseDatabaseRef.child(rideDetails.getRide_driver_id()).addValueEventListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
/**
     * method used to end ride status from API
     *//*

    public void endRide() {
        if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this);
            return;
        }
        endLocName = getAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        utility.startProgressRotate(rotateLoading, this);
        DataHolder.getInstance().endRide(Constant.END_RIDE, tempDetails.getRide_id(), String.valueOf(latitude), String.valueOf(longitude), endLocName, DataHolder.getInstance().getToken(), listener);
    }
    */
/**
     * method used to get emergency contacts from API
     *//*

    public void triggerEmergencyContacts() {
        if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this);
            return;
        }
        utility.startProgressRotate(rotateLoading, this);
        DataHolder.getInstance().triggerEmergencyContacts(Constant.TRIGGER_CONTACTS, tempDetails.getRide_id(), DataHolder.getInstance().getToken(), listener);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        isSelectedLocManually = false;
        //setSourceLoc();
        if (startPoint != null && startPoint != new LatLng(latitude, longitude) && endPoint != null
                && DataHolder.getInstance().getRideRequestId().isEmpty()) {
            if (DataHolder.getInstance().getRideId().isEmpty()) {
                getVehicleType(latitude + "", longitude
                        + "", endPoint.latitude + "", endPoint.longitude + "");
            }
        }
        return false;
    }

    */
/**
     * method used to set Location
     *//*

    public void setLocation() {
        try {
            AsyncTask task = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    if (SEARCH_TYPE == Constant.SEARCH_SOURCE)
                        getAddressNew(startPoint.latitude, startPoint.longitude);
                    else
                        getAddressNew(endPoint.latitude, endPoint.longitude);
                    return "";
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    System.out.println("startLocAddress async = " + startLocAddress);
                    if (startLocAddress != null || endLocAddress != null) {
                        if (SEARCH_TYPE == Constant.SEARCH_SOURCE) {
                            editSrcLocation.setText(startLocAddress);
                            isSrcFilled = true;
                            isFirstTime = false;
                            Log.e("Under Set Location:: " , ""+isFirstTime);
                        } else {
                            editDestLocation.setText(endLocAddress);
                        }

                    } else {
                        Toast.makeText(MapFrontActivity.this, getString(R.string.pls_select_start_loc) + "" +
                                "", Toast
                                .LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }
            };
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
/**
     * method used to set  source location
     *//*

    public void setSourceLoc() {
        try {
            AsyncTask task = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    if (isSelectedLocManually)
                        getAddress(startPoint.latitude, startPoint.longitude);
                    else
                        getAddress(latitude, longitude);
                    return "";
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    System.out.println("startLocAddress async = " + startLocAddress);
                    if (startLocAddress != null) {
                        editSrcLocation.setText(startLocAddress);
                        isSrcFilled = true;
                        isFirstTime = false;
                        Log.e("Under SrcLoc Location" , ""+isFirstTime);

                    } else {
                        Toast.makeText(MapFrontActivity.this, getString(R.string.pls_select_start_loc) + "" +
                                "", Toast
                                .LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }
            };
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this);
            return;
        }
        switch (view.getId()) {
            case R.id.bookBtn:

                System.out.println("selectedVehicleId = " + selectedVehicleId);
                DataHolder.getInstance().getStore(this).saveString(Constant.COMPLETE_RIDE, "");
                if (!selectedVehicleId.isEmpty()) {
                    timer.cancel();
                    timer.purge();
                    SEARCH_TYPE = Constant.SEARCH_EMPTY;
                    confirmLayout.setVisibility(View.VISIBLE);
                    editSrcLocation.setEnabled(false);
                    editDestLocation.setEnabled(false);
                    hsvLayout.setVisibility(View.GONE);
                    viewSrc.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_bg));
                    viewDest.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_bg));

                    ivLocationPin.setVisibility(View.GONE);
                    mMap.addMarker(new MarkerOptions().position(startPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_source)));
                    mMap.addMarker(new MarkerOptions().position(endPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination)));

//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(endPoint, 15.5f));
                } else {
                    Utility.showToast(this, getString(R.string.please_select_vehicle));
                }
                break;
            case R.id.confirmBookBtn:
                searchLayout.setVisibility(View.GONE);
                addRideRequest();
                break;
            case R.id.editSrcLocation:
                viewSrc.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_selected_bg));
                viewDest.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_bg));
                ivLocationPin.setVisibility(View.GONE);
                if (mMap != null)
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                SEARCH_TYPE = Constant.SEARCH_SOURCE;
                getAddressData();
                break;
            case R.id.editDestLocation:
                viewDest.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_selected_bg));
                viewSrc.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_bg));
                ivLocationPin.setVisibility(View.GONE);
                if (mMap != null)
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                SEARCH_TYPE = Constant.SEARCH_DESTINATION;
                getAddressData();
                break;
            case R.id.favouriteSrcBtn:
                FAV_TYPE = Constant.FAV_SOURCE;
                if (isSrcFav) {
                    deleteAddressData(favSrcId);
                } else {
                    if (!editSrcLocation.getText().toString().isEmpty())
                        showSelectFavDialog();
                }

                break;
            case R.id.favouriteDestBtn:
                FAV_TYPE = Constant.FAV_DESTINATION;

                if (isDestFav) {
                    deleteAddressData(favDestId);
                } else {
                    if (!editDestLocation.getText().toString().isEmpty())
                        showSelectFavDialog();
                }

                break;
            case R.id.sosBtn:
                if (tempDetails != null)
                    showSosConfirmation();
                break;
            case R.id.cancelRideLt:
                startActivity(new Intent(this, CancellationRideActivity.class).putExtra
                        (Constant.CANCEL_CHARGE, CancelCharge).putExtra("CancelText", CancelText));
                break;
            case R.id.cancelRequestBtn:
                cancelRideRequest("CancelBtn");
                break;
            case R.id.endRideLt:
                ConfirmationEndRide(getString(R.string.end_ride_confirmation));
                break;
            case R.id.paymentLt:
                startActivity(new Intent(this, PaymentOptionActivity.class));
                break;
            case R.id.couponLt:
                startActivityForResult(new Intent(this, OffersActivity.class)
                        .putExtra("SourceLat", startPoint.latitude + "").putExtra("SourceLng", startPoint.longitude + "")
                        .putExtra("DestLat", endPoint.latitude + "").putExtra("DestLng", endPoint.longitude + "")
                        .putExtra("VehicleType", selectedVehicleId), Constant.COUPON_CODE);
                break;
            case R.id.callDriverLt:
                callDriver();
                break;
            case R.id.shareDetails:
                shareRideDetails();
                break;
            case R.id.totalFare:
//                Toast.makeText(activity, "hahahah", Toast.LENGTH_SHORT).show();
                break;
            default:
                selectedVehicleId = String.valueOf(view.getId());

                for (int i = 0; i < imagesList.size(); i++) {
                    if (vehicles.getVehiclesTypes() != null) {
                        if (view.getId() == Integer.parseInt(vehicles.getVehiclesTypes().get(i).getId())) {
                            imagesList.get(i).setColorFilter(null);
//                        selectedCarIndex = i;
                            carTypes.get(i).setTypeface(Typeface.DEFAULT_BOLD);
                            fares.get(i).setTypeface(Typeface.DEFAULT_BOLD);


                            Animation anim = AnimationUtils.loadAnimation(MapFrontActivity.this, R.anim.scale_in);
                            imagesList.get(i).startAnimation(anim);
                            anim.setFillAfter(true);


                            carType = vehicles.getVehiclesTypes().get(i).getName();
                            totalFare.setText(fares.get(i).getText());
                            setBookBtn(true);
                            confirmBookBtn.setText(getString(R.string.confirm_booking) + " " + vehicles.getVehiclesTypes().get(i).getName());
                        } else {
                            ColorMatrix matrix = new ColorMatrix();
                            matrix.setSaturation(0);
                            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                            imagesList.get(i).setColorFilter(filter);
                            carTypes.get(i).setTypeface(Typeface.DEFAULT);
                            fares.get(i).setTypeface(Typeface.DEFAULT);
                            if (lastCarSelectedCarId == Integer.parseInt(vehicles.getVehiclesTypes().get(i).getId())) {
                                Animation anim = AnimationUtils.loadAnimation(MapFrontActivity.this, R.anim.scale_out);
                                imagesList.get(i).startAnimation(anim);
                                anim.setFillAfter(true);
                            }
                        }
                    }
                }

                lastCarSelectedCarId = Integer.parseInt(selectedVehicleId);
                getNearByDrivers(latitude + "", longitude + "");
                break;
        }


    }
    */
/**
     * method used to add address dada from API
     *//*

    public void addAddressData(String lat, String lng, String locName) {
        if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this);
            return;
        }
//        System.out.println("lat = [" + lat + "], lng = [" + lng + "], locName = [" + locName + "]");
        DataHolder.getInstance().addRiderFavoriteLocations(Constant.ADD_FAV_LOCATION, DataHolder.getInstance().getToken(), locationTypeFav
                , lat + "", lng + "", locName
                        .toString(), listener);
    }

    */
/**
     * method used to show select fav dialog
     *//*

    public void showSelectFavDialog() {
        locationTypeFav = "";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, R.style
                .MaterialThemeDialog);
        View view = this.getLayoutInflater().inflate(R.layout.selected_address_view, null, false);
        final EditText edtLocation = view.findViewById(R.id.edtLocation);
        final EditText edtOtherType = view.findViewById(R.id.edtOtherType);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton rb = radioGroup.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    locationTypeFav = rb.getText().toString();
                    if (locationTypeFav.equalsIgnoreCase(getString(R.string.Other))) {
                        {
                            edtOtherType.setVisibility(View.VISIBLE);
                            edtOtherType.requestFocus();
                            utility.showSoftKeyboard(edtOtherType);
                        }
                    } else {
                        edtOtherType.setVisibility(View.GONE);
                        edtOtherType.setText("");
                        utility.hideSoftKeyboard(edtOtherType);
                    }
                }
            }
        });
        if (FAV_TYPE == Constant.FAV_SOURCE)
            edtLocation.setText(startLocAddress);
        else if (FAV_TYPE == Constant.FAV_DESTINATION)
            edtLocation.setText(endLocAddress);
//        edtLocation.setOnClickListener(this);
        alertDialogBuilder.setTitle(getString(R.string.set_adddress));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton(R.string.ok, null);
        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationTypeFav.isEmpty()) {
                    Utility.showToast(MapFrontActivity.this, getString(R.string.please_select_location_type));
                    return;
                } else if (locationTypeFav.equalsIgnoreCase(getString(R.string.Other)) && edtOtherType.getText().toString().isEmpty()) {
                    Utility.showToast(MapFrontActivity.this, getString(R.string.please_enter_location_type));
                    return;
                } else {
                    utility.hideSoftKeyboard(edtOtherType);
                    if (!edtOtherType.getText().toString().isEmpty()) {
                        locationTypeFav = edtOtherType.getText().toString();
                    }
                    System.out.println("editDestLocation.getText().toString() = locationTypeFav "
                            + locationTypeFav + " iiiii" + editDestLocation.getText().toString());
                    dialog.dismiss();
                    if (FAV_TYPE == Constant.FAV_SOURCE)
                        addAddressData(startPoint.latitude + "", startPoint.longitude + "", startLocAddress);
                    else if (FAV_TYPE == Constant.FAV_DESTINATION)
                        addAddressData(endPoint.latitude + "", endPoint.longitude + "", endLocAddress);
                }
            }
        });

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    */
/**
     * method used to call driver API
     *//*

    public void callDriver() {
        int callPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (callPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 2);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + driverMobile));
            startActivity(callIntent);
        }
    }

  */
/*  @Override
    protected void onDestroy() {
        Toast.makeText(activity, "stop", Toast.LENGTH_SHORT).show();
        System.out.println("MapFrontActivity.onDestroy");
        super.onDestroy();
    }*//*


    */
/**
     * method used to check permission
     * @return
     *//*

    public boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion <= Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        List<String> listPermissionsNeeded = new ArrayList<>();
        int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (listPermissionsNeeded.size() > 0) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return false;
        } else {
            initialView();
        }
        return true;
    }

    */
/**
     * method used to set my location
     *//*

    public void setMylocationBtn() {

        */
/*View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1"))
                .getParent()).findViewById(Integer.parseInt(("2")));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);*//*

        View mapView = mapFragment.getView();
//        System.out.println("mapView = " + mapView);
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {

            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()
            ).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
   */
/*   View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent())
        .findViewById(Integer.parseInt(("2")));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);*//*

        }
    }

    */
/**
     * method used to initilize firebase
     *//*

    public void initalizeFireDb() {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabaseRef = mFirebaseInstance.getReference("users");
        mFirebaseInstance.getReference("Tracking").setValue("Move Marker");
//      mFirebaseDatabaseRef.child("Driver").addValueEventListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.CANCEL_RIDE_CODE) {
            if (resultCode == RESULT_OK) {
                if (mMap != null)
                    mMap.clear();
                confirmLayout.setVisibility(View.GONE);
                hsvLayout.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);
                driverDataLayout.setVisibility(View.GONE);
                findingRideLt.setVisibility(View.GONE);
                editDestLocation.setText("");
                if (MyFirebaseMessagingService.notificationManager != null)
                    MyFirebaseMessagingService.notificationManager.cancelAll();
            }
        } */
/*else if (requestCode == Constant.PAYMENT_CODE) {
            if (resultCode == RESULT_OK) {
                wallet = DataHolder.getInstance().getStore(this).getString(Constant.WALLET_STATUS);
                System.out.println("wallet = " + wallet);
                if (wallet.contentEquals("0"))
                    setup_txt_payment.setText(getString(R.string.cash));
                else
                    setup_txt_payment.setText(getString(R.string.wallet));
            }
        }*//*
 else if (requestCode == Constant.COUPON_CODE) {
            if (resultCode == RESULT_OK) {
                coupon = data.getStringExtra(Constant.COUPON_CODE_VALUE);
                txt_applyCoupon.setText(data.getStringExtra(Constant.COUPON_MSG));
            }
        }
        */
/*if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "onActivityResult Place:" + place.toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (requestCode == RESULT_CANCELED) {

            }
        }*//*

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
                Log.d(TAG, "Location update resumed .....................");
            }
        }
    }

    @Override
    protected void onResume() {
        registerReceiver(receiver, filter);
        getNearByVehicles();
        System.out.println("onResume***************************************************");
        wallet = DataHolder.getInstance().getStore(this).getString(Constant.WALLET_STATUS);
        if (!wallet.isEmpty()) {
            if (wallet.contentEquals("1") && (!DataHolder.getInstance().getWalletBalance()
                    .isEmpty() && (Double.parseDouble(DataHolder.getInstance().getWalletBalance()))
                    > 1)) {
                wallet = "1";
                setup_txt_payment.setText(getString(R.string.wallet));
            } else {
                wallet = "0";
                setup_txt_payment.setText(getString(R.string.cash));
            }
        }

        payment_mode = DataHolder.getInstance().getStore(this).getString(Constant
                .PAYMENT_METHOD);
        if (payment_mode.equalsIgnoreCase("CARD"))
            payment_card = DataHolder.getInstance().getStore(this).getString(Constant.PAYMENT_CARD);
        else
            payment_card = "";

        System.out.println("onResume***************************************************");
        setup_txt_payment.setText(payment_mode);

        if(payment_mode.equalsIgnoreCase("WALLET")){
            wallet = "1";
        } else {
            wallet = "0";
        }
        super.onResume();
    }
    */
/**
     * method used to get nearby vehicles from API
     *//*

    public void getNearByVehicles() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!Utility.isNetworkConnected(MapFrontActivity.this)) {
                    if (timer != null) timer.cancel();
                    return;
                }
                System.out.println("DataHolder.getInstance().getRideId() = " + DataHolder.getInstance().getRideId());
                if (DataHolder.getInstance().getRideId() == null || DataHolder.getInstance().getRideId().isEmpty()) {
                    getNearByDrivers(latitude + "", longitude + "");
                }
            }
        }, 0, Constant.CallTime * 1000);
    }

    //Starting the location updates
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            */
/*mLocationRequest.setInterval(3000);
            mLocationRequest.setSmallestDisplacement(5.0f);*//*

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Toast.makeText(activity, "stop", Toast.LENGTH_SHORT).show();
        System.out.println("MapFrontActivity" +
                ".onStop************************************************");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        if (timer != null) timer.cancel();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    protected void onPause() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }

        if (timer != null) timer.cancel();
        System.out.println("MapFrontActivity.onPause*******************************************");
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);

        if (checkPermission()) {
            if (!rideAccepted) {
                mMap.setMyLocationEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
            }
        }
        if (mMap != null)
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    // TODO Auto-generated method stub
                    Log.e("methoddd", "locationchaNGE");
                    if (isFirstTime) {
                        if (startPoint == null) {
                            System.out.println(" = " +
                                    "**********************************************************");
                            startPoint = new LatLng(location.getLatitude(), location.getLongitude());
                        }
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                        if (!isSrcFilled) {
                            setSourceLoc();

                        }
                        if (DataHolder.getInstance().getStore(activity).getBoolean(Constant.oneTimeCall, true)) {
                            System.out.println("location = [" + location + "]");
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 15.5f));

                            DataHolder.getInstance().getStore(activity).saveBoolean
                                    (Constant.oneTimeCall, false);
                        }
                    }
                }
            });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.e("methoddd", "cameraidle");
                //get latlng at the center by calling
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    LatLng midLatLng = mMap.getCameraPosition().target;
                    if (!SEARCH_TYPE.equals(Constant.SEARCH_EMPTY)) {
                        if (SEARCH_TYPE == Constant.SEARCH_SOURCE) {
                            latitude = midLatLng.latitude;
                            longitude = midLatLng.longitude;
                            startPoint = new LatLng(midLatLng.latitude, midLatLng.longitude);
                            if (!isFavClick) {
                                favouriteSrcBtn.setImageResource(R.drawable.ic_favorite_border_grey_700_24dp);
                            }
                            isFavClick = false;
                        } else if (SEARCH_TYPE == Constant.SEARCH_DESTINATION) {
                            endPoint = new LatLng(midLatLng.latitude, midLatLng.longitude);

                            if (!isFavClick) {
                                favouriteDestBtn.setImageResource(R.drawable.ic_favorite_border_grey_700_24dp);
                            }


                            getVehicleType(startPoint.latitude + "", startPoint.longitude
                                    + "", endPoint.latitude + "", endPoint.longitude + "");

                            isFavClick = false;
                        }


                        if(placesResult) {
                            setLocation();
                        }
                        placesResult = true;
                        if (DataHolder.getInstance().getStore(activity).getBoolean(Constant.oneTimeCall, true)) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 15.5f));
                            DataHolder.getInstance().getStore(activity).saveBoolean
                                    (Constant.oneTimeCall, false);
                        }
                        Log.e("center", "latloing " + midLatLng.latitude);
                    }
                } else {

                    if (!isFirstTime) {
                        LatLng midLatLng = mMap.getCameraPosition().target;
                        if (!SEARCH_TYPE.equals(Constant.SEARCH_EMPTY)) {
                            if (SEARCH_TYPE == Constant.SEARCH_SOURCE) {
                                latitude = midLatLng.latitude;
                                longitude = midLatLng.longitude;
                                startPoint = new LatLng(midLatLng.latitude, midLatLng.longitude);
                                if (!isFavClick) {
                                    favouriteSrcBtn.setImageResource(R.drawable.ic_favorite_border_grey_700_24dp);
                                }
                                isFavClick = false;
                            } else if (SEARCH_TYPE == Constant.SEARCH_DESTINATION) {
                                endPoint = new LatLng(midLatLng.latitude, midLatLng.longitude);

                                if (!isFavClick) {
                                    favouriteDestBtn.setImageResource(R.drawable.ic_favorite_border_grey_700_24dp);
                                }


                                getVehicleType(startPoint.latitude + "", startPoint.longitude
                                        + "", endPoint.latitude + "", endPoint.longitude + "");

                                isFavClick = false;
                            }


                            if (placesResult) {
                                setLocation();
                            }
                            placesResult = true;
                            if (DataHolder.getInstance().getStore(activity).getBoolean(Constant.oneTimeCall, true)) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 15.5f));
                                DataHolder.getInstance().getStore(activity).saveBoolean
                                        (Constant.oneTimeCall, false);
                            }
                            Log.e("center", "latloing " + midLatLng.latitude);
                        }

                    }
                }
            }
        });

    }

    public void setMyLocation() {
        Bitmap bitmap = genric.getBitmapFromVectorDrawable
                (MapFrontActivity.this, R.drawable.pin);
        int height = 60;
        int width = 40;
        pinBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        locationMarker = mMap.addMarker(new MarkerOptions().position(startPoint)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromBitmap(pinBitmap)));
        bounceMarker();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    initialView();
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callDriver();
                    break;

                }
        }
    }
    */
/**
     * method used to get vehicle type from API
     *//*

    public void getVehicleType(String srcLat, String srcLng, String destLat, String destLng) {
        if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this);
            return;
        }
        if (isFavClick)
            utility.startProgressRotate(rotateLoading, this);
        DataHolder.getInstance().getVehicleType(Constant.GET_VEHICLES, srcLat, srcLng, destLat, destLng, listener);
    }

    public void onLocationChanged(Location location) {
        System.out.println("MapFrontActivity.onLocationChanged");
        mLastLocation = location;

        if (startPoint == null) {
            System.out.println(" = onLocationChanged " +
                    "**********************************************************");
            startPoint = new LatLng(location.getLatitude(), location.getLongitude());
            System.out.println("startPoint = " + startPoint.toString());
        }
        */
/*if (!rideAccepted) {
            if (!isSelectedLocManually)
                setLocationOnMap(new LatLng(location.getLatitude(), location.getLongitude()));
        }*//*


    }

    @Override
    public void onConnected(Bundle arg0) {
        displayLocation();
        if (mRequestingLocationUpdates)
            startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("TAG", "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onMapClick(LatLng point) {
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
//        Toast.makeText(activity, "ondatachange", Toast.LENGTH_SHORT).show();
        User user = dataSnapshot.getValue(User.class);
        if (user != null) {
            LatLng currentLtLng = new LatLng(user.lattitude, user.longitude);
            if (rideAccepted) {
                if (!firstTimeSameLoc) {
                    startPosition = new LatLng(user.lattitude, user.longitude);
                    driverlastPosition = new LatLng(user.lattitude, user.longitude);
                    endPosition = new LatLng(user.lattitude, user.longitude);
                    firstTimeSameLoc = true;
                } else {
                    System.out.println("else differnce " + getDistance(startPosition, currentLtLng));
                    */
/*if (polyLineList!=null && !PolyUtil.isLocationOnPath(currentLtLng,polyLineList,
                            true)) {
                        driverlastPosition = currentLtLng;
                        if (statusValue.equals("BOOKED")) {
                            if (mMap != null) mMap.clear();
                            drawPathPolyline(driverlastPosition, tempDetails.getRide_start_location
                                    (), new LatLng(Float.parseFloat(tempDetails.getSrc_lat()),
                                    Float.parseFloat(tempDetails.getSrc_lng())));
                        } else if (statusValue.equals("PICKUP_READY") || statusValue.equals("STARTED")) {
                            if (mMap != null) mMap.clear();
                            drawPathPolyline(driverlastPosition, tempDetails.getRide_end_location(), new LatLng(Float
                                    .parseFloat(tempDetails.getDest_lat()), Float.parseFloat(tempDetails.getDest_lng())));
                        }
                        firstTimeSameLoc = false;
                    }*//*

                    if (getDistance(driverlastPosition, currentLtLng) > 200) {
                        driverlastPosition = currentLtLng;
                        if (statusValue.equals("BOOKED")) {
                            if (mMap != null) mMap.clear();
                            drawPathPolyline(driverlastPosition, tempDetails.getRide_start_location
                                    (), new LatLng(Float.parseFloat(tempDetails.getSrc_lat()),
                                    Float.parseFloat(tempDetails.getSrc_lng())));
                        } else if (statusValue.equals("PICKUP_READY") || statusValue.equals("STARTED")) {
                            if (mMap != null) mMap.clear();
                            drawPathPolyline(driverlastPosition, tempDetails.getRide_end_location(), new LatLng(Float
                                    .parseFloat(tempDetails.getDest_lat()), Float.parseFloat(tempDetails.getDest_lng())));
                        }
                        firstTimeSameLoc = false;
                    }

                    if (!startPosition.equals(currentLtLng) && getDistance(startPosition,
                            currentLtLng) > 5) {
                        startPosition = endPosition;
                        endPosition = currentLtLng;
                        onLocationUpdate();
                        System.out.println(" entered ");
                    } else
                        System.out.println(" Not entered ");
                }

               */
/* utility.generateNoteOnSD(this, "Riderlogs.txt", "Start pos lat " + startPosition
                        .latitude + "\n" + "Start pos long " + startPosition.longitude + "\n"
                        + "end pos lat " + endPosition.latitude + "\n" + "end pos long " +
                        "" + endPosition.longitude + " " +
                        "Distance -> " + getDistance(startPosition, currentLtLng) + " \n*************\n");*//*

            }
        }

    }

    */
/**
     * method used to get distance
     *//*


    public Double getDistance(LatLng latLng1, LatLng latLng2) {
        Location mylocation = new Location("");
        Location dest_location = new Location("");
        dest_location.setLatitude(latLng2.latitude);
        dest_location.setLongitude(latLng2.longitude);
        mylocation.setLatitude(latLng1.latitude);
        mylocation.setLongitude(latLng1.longitude);
        float distance = mylocation.distanceTo(dest_location);//in meters
        return Double.parseDouble(distance + "");
    }

    private void animateCar(final LatLng destination) {
        try {
            final LatLng startPosition = carMarker.getPosition();
            System.out.println("startPosition " + startPosition);
            System.out.println("destination " + destination);
            final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);
            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(5000); // duration 5 seconds
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        carMarker.setPosition(newPosition);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            valueAnimator.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
/**
     * method used to get bearing
     *//*

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        System.out.println("databaseError = " + databaseError);
    }
    */
/**
     * method used to display location
     *//*

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Couldn't get the location. Make sure location is enabled on the device",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    */
/**
     * method used to get address
     *//*

    public String getAddress(double lat, double lng) {
        System.out.println("lat = [" + lat + "], lng = [" + lng + "]");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String address = "";
        try {
            if (lat != 0.0 || lng != 0.0) {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                System.out.println("addresses.size() = " + addresses.size());
                if (addresses != null && addresses.size() > 0) {
                    Address obj = addresses.get(0);
                    String add = obj.getAddressLine(0);
                    add = add + "\n" + obj.getCountryName();
                    add = add + "\n" + obj.getCountryCode();
                    add = add + "\n" + obj.getAdminArea();
                    add = add + "\n" + obj.getPostalCode();
                    add = add + "\n" + obj.getSubAdminArea();
                    add = add + "\n" + obj.getLocality();
                    add = add + "\n" + obj.getSubThoroughfare();

                    startLocAddress = obj.getAddressLine(0);
                    startLocName = obj.getAddressLine(0);
                    address = obj.getAddressLine(0);
                    System.out.println("startLocAddress address = " + startLocAddress);
                    System.out.println("startLocName address = " + startLocName);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return address;

    }


    public String getAddressNew(double latitude, double longitude) {

        System.out.println("lat = [" + latitude + "], lng = [" + longitude + "]");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String address = "";
        try {
            if (latitude != 0 || longitude != 0) {
                Log.e("idleee", "here " + latitude + "," + longitude);
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Log.e("idleee", "here " + addresses.size());
                System.out.println("addresses.size() = " + addresses.size());
                if (addresses != null && addresses.size() > 0) {
                    Address obj = addresses.get(0);
                    String add = obj.getAddressLine(0);
                    add = add + "\n" + obj.getCountryName();
                    add = add + "\n" + obj.getCountryCode();
                    add = add + "\n" + obj.getAdminArea();
                    add = add + "\n" + obj.getPostalCode();
                    add = add + "\n" + obj.getSubAdminArea();
                    add = add + "\n" + obj.getLocality();
                    add = add + "\n" + obj.getSubThoroughfare();

                    if (SEARCH_TYPE == Constant.SEARCH_SOURCE) {

                        startLocAddress = obj.getAddressLine(0);
                        startLocName = obj.getAddressLine(0);

                    } else {
                        endLocAddress = obj.getAddressLine(0);
                        endLocName = obj.getAddressLine(0);
                    }

                    address = obj.getAddressLine(0);
                    System.out.println("startLocAddress address = " + startLocAddress);
                    System.out.println("startLocName address = " + startLocName);
                }
            }
        } catch (IOException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return address;

    }

    */
/**
     * method used to get nearby drivers from API
     *//*

    public void getNearByDrivers(String lat, String lng) {
        if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this);
            return;
        }
        MarkerPoints.add(startPoint);
        if (isSelectedLocManually) {
            DataHolder.getInstance().getNearByDrivers(Constant.GET_NEARBY_DRIVERS, startPoint.latitude + "", startPoint.longitude + "",
                    selectedVehicleId, listener);
        } else {
            DataHolder.getInstance().getNearByDrivers(Constant.GET_NEARBY_DRIVERS, lat, lng,
                    selectedVehicleId, listener);
        }
    }
    PlacesClient placesClient;

    */
/**
     * method used to initlize view
     *//*

    public void initView() {
        viewDest = findViewById(R.id.viewDest);
        viewSrc = findViewById(R.id.viewSource);

        bookBtn = findViewById(R.id.bookBtn);
        ivLocationPin = findViewById(R.id.ivLocationPin);
        cancelRequestBtn = findViewById(R.id.cancelRequestBtn);
        cancelChargeMsg = findViewById(R.id.cancelChargeMsg);
        txt_applyCoupon = findViewById(R.id.txt_applyCoupon);
        confirmBookBtn = findViewById(R.id.confirmBookBtn);
        estimatedTimeArrival = findViewById(R.id.estimatedTimeArrival);
        otpLayout = findViewById(R.id.otpLayout);
        etaLayout = findViewById(R.id.etaLayout);
        etaLabel = findViewById(R.id.etaLabel);
        sosBtn = findViewById(R.id.sosBtn);
        confirmBookBtn.setOnClickListener(this);
        bookBtn.setOnClickListener(this);
        hsvLayout = findViewById(R.id.hsvLayout);
        bookRideLt = findViewById(R.id.bookRideLt);
        findingRideLt = findViewById(R.id.findingRideLt);
        searchLayout = findViewById(R.id.searchLayout);
        shareDetails = findViewById(R.id.shareDetails);
        confirmLayout = findViewById(R.id.confirmLayout);
        driverDataLayout = findViewById(R.id.driverDataLayout);
        callDriverLt = findViewById(R.id.callDriverLt);
        cancelRideLt = findViewById(R.id.cancelRideLt);
        endRideLt = findViewById(R.id.endRideLt);
        paymentLt = findViewById(R.id.paymentLt);
        setup_txt_payment = findViewById(R.id.setup_txt_payment);
        couponLt = findViewById(R.id.couponLt);
        rotateLoading = findViewById(R.id.rotateloading);
        callDriverLt.setOnClickListener(this);
        paymentLt.setOnClickListener(this);
        couponLt.setOnClickListener(this);
        cancelRideLt.setOnClickListener(this);
        shareDetails.setOnClickListener(this);
        endRideLt.setOnClickListener(this);
        sosBtn.setOnClickListener(this);
        MarkerPoints = new ArrayList();
        editSrcLocation = findViewById(R.id.editSrcLocation);
        editDestLocation = findViewById(R.id.editDestLocation);
        favouriteSrcBtn = findViewById(R.id.favouriteSrcBtn);
        favouriteDestBtn = findViewById(R.id.favouriteDestBtn);
        totalFare = findViewById(R.id.totalFare);
        editSrcLocation.setOnClickListener(this);
        editDestLocation.setOnClickListener(this);
        favouriteSrcBtn.setOnClickListener(this);
        favouriteDestBtn.setOnClickListener(this);
        totalFare.setOnClickListener(this);
        cancelRequestBtn.setOnClickListener(this);

        setDriverInitView();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        genric = new Genric(this, mMap);
        if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this);
            return;
        }
        if (genric.getServicesAvailable()) {
            //   buildGoogleApiClient();
            // Setup Places Client
            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), "AIzaSyDhYfxwjaCMMdSA50tsHBBfCsLoo5et9i0");
            }

            placesClient = Places.createClient(this);
            genric.createLocationRequest(mLocationRequest);
        }
        initalizeFireDb();
        listener = this;
        checkPermission();

    }

    */
/**
     * method used to cancel ride request
     *//*

    public void cancelRideRequest(String requestFrom) {
        System.out.println("requestFrom = [" + requestFrom + "]");
        requestCancelFrom = requestFrom;
        if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this);
            return;
        }
        utility.startProgressRotate(rotateLoading, this);
        DataHolder.getInstance().cancelRideRequest(Constant.CANCEL_RIDE_REQUEST, DataHolder.getInstance().getRideRequestId(), "0", DataHolder.getInstance().getToken(), listener);
    }

    */
/**
     * method used to get ride detail from API
     *//*

    public void getRideDetail(String rideId) {
        if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this);
            return;
        }
        utility.startProgressRotate(rotateLoading, this);
        DataHolder.getInstance().rideDetail(Constant.RIDE_DETAIL, rideId, DataHolder.getInstance().getToken(), listener);
    }

    */
/**
     * method used to add ride request
     *//*

    public void addRideRequest() {
        if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this);
            return;
        }

        utility.startProgressRotate(rotateLoading, this);



        if (isSelectedLocManually) {
            DataHolder.getInstance().addRideRequest(Constant.ADD_RIDE_REQUEST, startPoint
                            .latitude + "", startPoint.longitude + "", endPoint.latitude + "", endPoint.longitude + "", selectedVehicleId, startLocAddress,
                    endLocAddress,payment_card, payment_mode, wallet, coupon, DataHolder.getInstance().getToken(), listener);
        } else {
            DataHolder.getInstance().addRideRequest(Constant.ADD_RIDE_REQUEST, latitude + "", longitude + "", endPoint.latitude + "", endPoint.longitude + "", selectedVehicleId, startLocAddress,
                    endLocAddress, payment_card, payment_mode, wallet, coupon, DataHolder.getInstance().getToken(), listener);
        }

    }

    */
/**
     * method used to initlize view
     *//*

    public void initialView() {
        // if (utility.isGpsEnabled()) {
        mapFragment.getMapAsync(this);
        //  }
    }

    */
/**
     * method used to set driver initilization view
     *//*

    public void setDriverInitView() {
        carName = findViewById(R.id.carName);

        driverCarImage = findViewById(R.id.driverCarImage);
        carNumber = findViewById(R.id.carNumber);
        driverName = findViewById(R.id.driverName);
        driverRating = findViewById(R.id.driverRating);
        ratingLt = findViewById(R.id.ratingLt);
        starImageView = findViewById(R.id.starImageView);
        driverImage = findViewById(R.id.driverImage);
        rideOtp = findViewById(R.id.rideOtp);
    }

    */
/**
     * method used to set ride respond time
     * @param startTimer
     *//*

    public void setRideRespondTime(int startTimer) {
        rideCancelTimer = new CountDownTimer(startTimer * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int i = (int) millisUntilFinished / 1000;
            }

            @Override
            public void onFinish() {
                cancelRideRequest("Timer");
            }
        }.start();
    }

    public void onLocationUpdate() {
        System.out.println(startPosition);
        System.out.println(endPosition);
        if (carMarker != null) {
            carMarker.setPosition(endPosition);
            carMarker.setAnchor(0.5f, 0.5f);
            if (!startPosition.equals(endPosition))
                carMarker.setRotation(getBearing(startPosition, endPosition));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(endPosition).zoom(15.5f).build()));
        }

       */
/* System.out.println("MapFrontActivity.onLocationUpdate");
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float v = valueAnimator.getAnimatedFraction();
                lng = v * endPosition.longitude + (1 - v)
                        * startPosition.longitude;
                lat = v * endPosition.latitude + (1 - v)
                        * startPosition.latitude;
                LatLng newPos = new LatLng(lat, lng);


                if (carMarker != null) {
                    carMarker.setPosition(newPos);
                    carMarker.setAnchor(0.5f, 0.5f);
                    carMarker.setRotation(getBearing(startPosition, newPos));
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(newPos).zoom(15.5f).build()));
                }
            }
            });valueAnimator.start();*//*


    }
    */
/**
     * method used to draw ploy line path
     *//*

    public void drawPathPolyline(final LatLng startLoc, String endLocation, final LatLng endLclatlng) {

        if (timer != null)
            timer.cancel();
        System.out.println("startLoc = [" + startLoc + "], endLocation = [" + endLocation + "]");
        try {

            requestUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&"
                    + "transit_routing_preference=less_driving&"
                    + "origin=" + startLoc.latitude + "," + startLoc.longitude + "&"
                    + "destination=" + endLocation +
                    "&key=" + getString(R.string.google_maps_key);
            Log.d(TAG, requestUrl);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    requestUrl, null,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response + "");
                            try {
                                JSONObject jDuration;
                                JSONArray jsonArray = response.getJSONArray("routes");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONArray jLegs = ((JSONObject) jsonArray.get(i)).getJSONArray("legs");
                                    for (int j = 0; j < jLegs.length(); j++) {
                                        jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                                        estimatedTime = jDuration.getString("text");
                                    }

                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    System.out.println("polyline = " + polyline);
                                    polyLineList = decodePoly(polyline);

                                }
                                estimatedTimeArrival.setText(estimatedTime);
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng latLng : polyLineList) {
                                    builder.include(latLng);
                                }
                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                mMap.animateCamera(mCameraUpdate);
                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(8);
                                polylineOptions.startCap(new com.google.android.gms.maps.model.SquareCap());
                                polylineOptions.endCap(new com.google.android.gms.maps.model.SquareCap());
                                polylineOptions.jointType(ROUND);
                                polylineOptions.addAll(polyLineList);
                                greyPolyLine = mMap.addPolyline(polylineOptions);

                                blackPolylineOptions = new PolylineOptions();
                                blackPolylineOptions.width(8);
                                blackPolylineOptions.color(Color.BLACK);
                                blackPolylineOptions.startCap(new com.google.android.gms.maps.model.SquareCap());
                                blackPolylineOptions.endCap(new com.google.android.gms.maps.model.SquareCap());
                                blackPolylineOptions.jointType(ROUND);
                                blackPolyline = mMap.addPolyline(blackPolylineOptions);
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().zoom(15.5f).build()));
*/
/*
                                mMap.addMarker(new MarkerOptions()
                                        .position(polyLineList.get(polyLineList.size() - 1)));
*//*

                                ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
                                polylineAnimator.setDuration(2000);
                                polylineAnimator.setInterpolator(new LinearInterpolator());
                                polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        List<LatLng> points = greyPolyLine.getPoints();
                                        int percentValue = (int) valueAnimator.getAnimatedValue();
                                        int size = points.size();
                                        int newPoints = (int) (size * (percentValue / 100.0f));
                                        List<LatLng> p = points.subList(0, newPoints);
                                        blackPolyline.setPoints(p);
//                                        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                                    }
                                });

                                polylineAnimator.start();


//                               startTIMER();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                int height = 100;
                                int width = 60;
                                Bitmap bitmap = genric.getBitmapFromVectorDrawable
                                        (MapFrontActivity.this, R.mipmap.icon_car);
                                Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
                                carMarker = mMap.addMarker(new MarkerOptions().position(startLoc)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                                destinationMarker = mMap.addMarker(new MarkerOptions().position
                                        (endLclatlng));
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error + "It is ");
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error if = " + e.getMessage());
        }
    }

    public void startTIMER() {
        timerss = new Timer();
        initializeTimerTask();
        timerss.schedule(timerTask, 0, 1000);
    }

    */
/**
     * method used to initilize timer task
     *//*

    public void initializeTimerTask() {
        System.out.println(" initializeTimerTask ");
        timerTask = new TimerTask() {

            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            Location location = new Location("");
                            location.setLatitude(polyLineList.get(count).latitude);
                            location.setLongitude(polyLineList.get(count).longitude);

                            if (count == 0) {
                                startPosition = new LatLng(polyLineList.get(count).latitude, polyLineList.get(count).longitude);
                                endPosition = new LatLng(polyLineList.get(count).latitude, polyLineList.get(count).longitude);
                            } else {
                                startPosition = endPosition;
                                endPosition = new LatLng(polyLineList.get(count).latitude, polyLineList.get(count).longitude);
                            }
                            onLocationUpdate();
                            count++;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
    }

    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    */
/**
     * method used to load tool bar search
     *
     *//*

    private void loadToolBarSearch() {
        View view = getLayoutInflater().inflate(R.layout.view_toolbar_search, null);
        LinearLayout parentToolbarSearch = view.findViewById(R.id.parent_toolbar_search);
        ImageView imgToolBack = view.findViewById(R.id.img_tool_back);
        ListView list_search = view.findViewById(R.id.list_search);
        TextView tvChooseMap = view.findViewById(R.id.tvChooseMap);
        list_favourites = view.findViewById(R.id.list_favourites);
        list_favourites.setVisibility(View.VISIBLE);
        list_search.setVisibility(View.VISIBLE);

        final FavLocationSearchAdapter searchAdapter = new FavLocationSearchAdapter(this, locationData, this, this);
        list_favourites.setAdapter(searchAdapter);
//        setDynamicHeight(list_favourites);
//        list_favourites.setTextFilterEnabled(true);

        final GooglePlacesAutocompleteAdapter dataAdapter = new GooglePlacesAutocompleteAdapter(this, R.layout
                .adapter_google_places_autocomplete, this, placesClient);
        // Assign adapter to ListView
        list_search.setAdapter(dataAdapter);
        //enables filtering for the contents of the given ListView
        list_search.setTextFilterEnabled(true);
//        setDynamicHeight(list_search);
        edtToolSearch = view.findViewById(R.id.edt_tool_search);
        toolbarSearchDialog = new Dialog(this, R.style.MaterialSearch);
        toolbarSearchDialog.setContentView(view);
        toolbarSearchDialog.setCancelable(true);
        toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
        toolbarSearchDialog.show();

        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        edtToolSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (locationData != null) {
                    if (s.toString().isEmpty()) {
                        onFind(true, 1);
                    }
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (locationData != null) {
                    searchAdapter.getFilter().filter(s.toString());
                }

                dataAdapter.getFilter().filter(s.toString());
            }
        });

        tvChooseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utility.hideSoftKeyboard(edtToolSearch);
                toolbarSearchDialog.dismiss();
            }
        });

        parentToolbarSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utility.hideSoftKeyboard(edtToolSearch);
                toolbarSearchDialog.dismiss();
            }
        });

        imgToolBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utility.hideSoftKeyboard(edtToolSearch);
                toolbarSearchDialog.dismiss();
            }
        });
        toolbarSearchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                ivLocationPin.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = activity.getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(activity);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });
        toolbarSearchDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    toolbarSearchDialog.dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onFind(boolean find, int results) {
        System.out.println("find = " + find);
        System.out.println("results = " + results);
        if (find && results > 0) {
            list_favourites.setVisibility(View.VISIBLE);
        } else if (!find && results < 0) {
            list_favourites.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClickPlace(String placeId, boolean isFavourite, int position) {
        System.out.println("SEARCH_TYPE = " + SEARCH_TYPE + " isFavourite " + isFavourite);
        utility.hideSoftKeyboard(edtToolSearch);
        if (toolbarSearchDialog != null && toolbarSearchDialog.isShowing())
            toolbarSearchDialog.dismiss();
        txt_applyCoupon.setText(getString(R.string.apply_coupon));
        if (confirmLayout.getVisibility() == View.VISIBLE) {
            confirmLayout.setVisibility(View.GONE);
            hsvLayout.setVisibility(View.VISIBLE);
        }
        isFavClick = true;
        if (!isFavourite) {
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS
                    , Place.Field.LAT_LNG);

            FetchPlaceRequest request = null;
            if (placeId != null) {
                request = FetchPlaceRequest.builder(placeId, placeFields)
                        .build();
            }

            if (request != null) {
                placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(FetchPlaceResponse task) {

                        if (SEARCH_TYPE.equals(Constant.SEARCH_SOURCE)) {
                            isSrcFav = false;
                            placesResult = false;
                            favSrcId = "";
                            favouriteSrcBtn.setImageResource(R.drawable.ic_favorite_border_grey_700_24dp);
                            isSelectedLocManually = true;
                            editSrcLocation.setText(task.getPlace().getName());
                            isSrcFilled = true;
                            startLocAddress = task.getPlace().getAddress();
                            startLocName = task.getPlace().getName();
                            startPoint = task.getPlace().getLatLng();
                            if (endPoint == null)
                                getNearByDrivers(startPoint.latitude + "", startPoint.latitude + "");
                            else {
                                getVehicleType(startPoint.latitude + "", startPoint.longitude
                                        + "", endPoint.latitude + "", endPoint.longitude + "");
                            }
                            if (!rideAccepted) {
                                setLocationOnMap(startPoint);
                            }

                        } else if (SEARCH_TYPE.equals(Constant.SEARCH_DESTINATION)) {
                            placesResult = false;
                            isDestFav = false;
                            favDestId = "";
                            favouriteDestBtn.setImageResource(R.drawable.ic_favorite_border_grey_700_24dp);

                            editDestLocation.setText(task.getPlace().getName());
                            endLocName = task.getPlace().getName().toString();
                            endLocAddress = task.getPlace().getAddress().toString();
                            System.out.println("end loc address " + endLocAddress);
                            endPoint = task.getPlace().getLatLng();

                           */
/* if (startPoint != null && endPoint != null)
                                getVehicleType(startPoint.latitude + "", startPoint.longitude
                                        + "", endPoint.latitude + "", endPoint.longitude + "");*//*

                            if (!rideAccepted) {
                                setLocationOnMap(endPoint);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
           */
/* Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess()) {
                                final Place myPlace = places.get(0);
                                if (SEARCH_TYPE.equals(Constant.SEARCH_SOURCE)) {
                                    isSrcFav = false;
                                    favSrcId = "";
                                    favouriteSrcBtn.setImageResource(R.drawable.ic_favorite_border_grey_700_24dp);
                                    isSelectedLocManually = true;
                                    editSrcLocation.setText(myPlace.getName());
                                    isSrcFilled = true;
                                    startLocAddress = myPlace.getAddress().toString();
                                    startLocName = myPlace.getName().toString();
                                    startPoint = myPlace.getLatLng();
                                    if (endPoint == null)
                                        getNearByDrivers(startPoint.latitude + "", startPoint.latitude + "");
                                    else {
                                        getVehicleType(startPoint.latitude + "", startPoint.longitude
                                                + "", endPoint.latitude + "", endPoint.longitude + "");
                                    }
                                    if (!rideAccepted) {
                                        setLocationOnMap(startPoint);
                                    }

                                } else if (SEARCH_TYPE.equals(Constant.SEARCH_DESTINATION)) {
                                    isDestFav = false;
                                    favDestId = "";
                                    favouriteDestBtn.setImageResource(R.drawable.ic_favorite_border_grey_700_24dp);

                                    editDestLocation.setText(myPlace.getName());
                                    endLocName = myPlace.getName().toString();
                                    endLocAddress = myPlace.getAddress().toString();
                                    System.out.println("end loc address " + endLocAddress);
                                    endPoint = myPlace.getLatLng();
                                    *//*
*/
/*getVehicleType(startPoint.latitude + "", startPoint.longitude
                                            + "", endPoint.latitude + "", endPoint.longitude + "");*//*
*/
/*


                                    if (!rideAccepted) {
                                        setLocationOnMap(endPoint);
                                    }
                                }

                            }
                            places.release();
                        }
                    });*//*

        } else if (SEARCH_TYPE.equals(Constant.SEARCH_SOURCE)) {
            isFavClick = true;
            isSrcFav = true;
            favSrcId = locationData.get(position).getId();

            favouriteSrcBtn.setImageResource(R.drawable.ic_favorite_red_700_24dp);
            isSelectedLocManually = true;
            isSrcFilled = true;
            startLocAddress = locationData.get(position).getGoogle_loc_name();
            startLocName = locationData.get(position).getGoogle_loc_name();
            startPoint = new LatLng(Double.parseDouble(locationData.get(position).getLatitude
                    ()), Double.parseDouble(locationData.get(position).getLongitude()));
            editSrcLocation.setText(startLocAddress);
            if (endPoint == null)
                getNearByDrivers(startPoint.latitude + "", startPoint.latitude + "");
            else {
                if (startPoint != null && endPoint != null)
                    getVehicleType(startPoint.latitude + "", startPoint.longitude
                            + "", endPoint.latitude + "", endPoint.longitude + "");
            }
            if (!rideAccepted) {
                setLocationOnMap(startPoint);
            }
        } else if (SEARCH_TYPE.equals(Constant.SEARCH_DESTINATION)) {
            isFavClick = true;
            isDestFav = true;
            favDestId = locationData.get(position).getId();
            favouriteDestBtn.setImageResource(R.drawable.ic_favorite_red_700_24dp);
            endLocName = locationData.get(position).getGoogle_loc_name();
            endLocAddress = locationData.get(position).getGoogle_loc_name();
            endPoint = new LatLng(Double.parseDouble(locationData.get(position).getLatitude()), Double.parseDouble(locationData.get(position).getLongitude()));
            editDestLocation.setText(endLocAddress);

            if (!rideAccepted) {
                setLocationOnMap(endPoint);
            }
            if (startPoint != null && endPoint != null)
                getVehicleType(startPoint.latitude + "", startPoint.longitude
                        + "", endPoint.latitude + "", endPoint.longitude + "");
        }
    }

    */
/**
     * method used to set location on map
     * @param latLng
     *//*

    public void setLocationOnMap(LatLng latLng) {
        System.out.println("latLng setLocationOnMap = " + latLng);


        if (mMap != null) {

            if (SEARCH_TYPE == Constant.SEARCH_SOURCE) {
                startPoint = latLng;
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(startPoint).zoom(15.5f).build()));
            } else {
                endPoint = latLng;
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(endPoint).zoom(15.5f).build()));
            }
            */
/*CameraUpdate center = CameraUpdateFactory.newLatLng(startPoint);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15.5f);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
            setSourceLoc();*//*

        }
    }


    */
/**
     * A method to download json data from url
     *//*

    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

    public final class SquareCap extends Cap {
        public SquareCap() {
            super(1);
        }

        public final String toString() {
            return "[SquareCap]";
        }
    }

    */
/**
     * method used to delete fav location from API
     *//*

    public void deleteAddressData(final String id) {
        if (!Utility.isNetworkConnected(activity)) {
            Utility.showErrorDialog(activity);
            return;
        }
        DataHolder.getInstance().deleteFavoriteLocation(Constant.DELETE_FAV_LOCATION, DataHolder.getInstance().getToken(), id,
                new ResponseHandler() {
                    @Override
                    public void onSuccess(int tag, Response response) {
                        if (tag == Constant.DELETE_FAV_LOCATION) {
                            checkFavSrcDestAddress(id);
                            ArrayList<FavLocation.Locations> arrayList = new ArrayList<>();
                            Common common = (Common) response.body();
                            if (common != null && common.getStatus().contentEquals("1")) {
                                Type type = new TypeToken<List<FavLocation.Locations>>() {
                                }.getType();
                                Gson gson = new Gson();

                                String data = DataHolder.getInstance().getStore(activity).getString(Constant.FAV_LOCATION);
                                if (!data.equals("")) {
                                    arrayList = gson.fromJson(data, type);
                                    for (int i = 0; i < arrayList.size(); i++) {
                                        if (arrayList.get(i).getId().equals(id)) {
                                            arrayList.remove(i);
                                            break;
                                        }
                                    }
                                }

                                Utility.showToast(activity, common.getMsg());

                                String list = gson.toJson(arrayList);
                                DataHolder.getInstance().getStore(activity).saveString(Constant.FAV_LOCATION, list);


                            } else if (common != null && common.getStatus().contentEquals(Constant.SESSION_EXPIRED)) {
                                new Utility(activity).sessionExpire(activity);
                            } else if (common != null) {
                                Utility.showToast(activity, common.getMsg());
                            } else
                                Utility.showToast(activity, activity.getString(R.string.error_something_wrong));
                        }

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    */
/**
     * method used to check favorite source and destination address.
     * @param id
     *//*

    public void checkFavSrcDestAddress(String id) {
        if (id.equals(favSrcId)) {
            isSrcFav = false;
            favouriteSrcBtn.setImageResource(R.drawable.ic_favorite_border_grey_700_24dp);
        }

        if (id.equals(favDestId)) {
            isDestFav = false;
            favouriteDestBtn.setImageResource(R.drawable.ic_favorite_border_grey_700_24dp);
        }
    }
}

*/
/*OLDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD*/
