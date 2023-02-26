package com.laodev.dwbrouter.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.adapter.RouteAdapter;
import com.laodev.dwbrouter.model.CarServiceModel;
import com.laodev.dwbrouter.model.CheckListModel;
import com.laodev.dwbrouter.model.HistoryModel;
import com.laodev.dwbrouter.model.OrderModel;
import com.laodev.dwbrouter.model.RegionModel;
import com.laodev.dwbrouter.model.RouterInfoModel;
import com.laodev.dwbrouter.ui.DeliveryRegionUI;
import com.laodev.dwbrouter.util.APIManager;
import com.laodev.dwbrouter.util.AppConst;
import com.laodev.dwbrouter.util.AppUtils;
import com.laodev.dwbrouter.util.BannerUtil;
import com.laodev.dwbrouter.util.DirectionsJSONParser;
import com.laodev.dwbrouter.util.FireManager;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String SENDGRID_APIKEY = "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";

    private GoogleMap mMap;
    private ProgressDialog dialog;
    private TextView lbl_distance;
    private TextView lbl_delay;

    private RouteAdapter routeAdapter;
    private List<RouterModel> showOrders = new ArrayList<>();
    private List<RouterInfoModel> routerInfoModels = new ArrayList<>();
    private List<RegionModel> regionModels = new ArrayList<>();
    private boolean isCheckable = false;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            //Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(AppUtils.gUser.name);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_pink));
            markerOptions.anchor((float) 0.5, (float) 0.5);
            mCurrLocationMarker = mMap.addMarker(markerOptions);
        }
    };
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient.ConnectionCallbacks mConnectionListener = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            if (isLocationPermissionGranted()) {
                if (ActivityCompat.checkSelfPermission(OrderDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(OrderDetailActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener = connectionResult -> {

    };
    private Marker mCurrLocationMarker;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        setToolbar();
        initMap();
        initWithUIView();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
            toolbar.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_select:
                        if (isCheckable) {
                            List<RouterModel> routers = new ArrayList<>(AppUtils.gSelDOrders);
                            if (routers.size() > 0) {
                                CheckListModel historyModel = new CheckListModel();
                                historyModel.amount = "";
                                historyModel.color = "";
                                historyModel.region = "";
                                for (RegionModel region: regionModels) {
                                    int count = 0;
                                    for (RouterModel router: routers) {
                                        if (router.orderModel.shippingPerson.region.equals(region.title)) {
                                            count++;
                                        }
                                    }
                                    if (count > 0) {
                                        historyModel.amount = historyModel.amount + "," + count;
                                        historyModel.color = historyModel.color + "," + region.color.toLowerCase().replace("#ff", "#");
                                        historyModel.region = historyModel.region + "," + region.title;
                                    }
                                }
                                historyModel.amount = historyModel.amount.substring(1);
                                historyModel.color = historyModel.color.substring(1);
                                historyModel.region = historyModel.region.substring(1);
                                historyModel.userid = AppUtils.gUser.id;
                                historyModel.carid = AppUtils.gUser.id;

                                SimpleDateFormat formatterDay = new SimpleDateFormat("dd", Locale.US);
                                SimpleDateFormat formatterMonth = new SimpleDateFormat("MMM", Locale.US);
                                SimpleDateFormat formatterNumMonth = new SimpleDateFormat("MM", Locale.US);
                                SimpleDateFormat formatterWeek = new SimpleDateFormat("EEE", Locale.US);
                                SimpleDateFormat formatterYear = new SimpleDateFormat("yyyy", Locale.US);
                                historyModel.day = formatterDay.format(AppUtils.gSelDate);
                                historyModel.month = formatterMonth.format(AppUtils.gSelDate);
                                historyModel.numMonth = formatterNumMonth.format(AppUtils.gSelDate);
                                historyModel.week = formatterWeek.format(AppUtils.gSelDate);
                                historyModel.year = formatterYear.format(AppUtils.gSelDate);

                                FireManager.getDeliveryByDelivery(historyModel, new FireManager.FBDeliveryCallback() {
                                    @Override
                                    public void onSuccess(List<CheckListModel> models) {
                                        if (models.size() > 0) {
                                            Toast.makeText(OrderDetailActivity.this, "Already added data.", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            FireManager.addDelivery(historyModel, new FireManager.FBDeliveryCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    Toast.makeText(OrderDetailActivity.this, "Success History", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }

                                                @Override
                                                public void onFailed(String error) {
                                                    Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        isCheckable = !isCheckable;
                        routeAdapter.setCheckable(isCheckable);
                        break;
                }
                return true;
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void initWithUIView() {
        ListView lst_detail = findViewById(R.id.lst_order_detail);
        routeAdapter = new RouteAdapter(this, AppUtils.gSelDOrders, AppUtils.allProgress);
        lst_detail.setAdapter(routeAdapter);
        lst_detail.setOnItemClickListener((adapterView, view, i, l) -> {
            if (AppUtils.allProgress.get(i).equals("Processing")) {
                AppUtils.gSelIndex = i;
                AppUtils.gSelOrder = showOrders.get(i).orderModel;
                if (i > 0) {
                    AppUtils.gSelRouterOrder = showOrders.get(i - 1).orderModel;
                }
                String latitude = AppUtils.gSelOrder.shippingPerson.lat;
                String longitude = AppUtils.gSelOrder.shippingPerson.lng;
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                try{
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }catch (NullPointerException e){
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
//                AppUtils.showOtherActivity(OrderDetailActivity.this, NavigationActivity.class, 0);
            }
        });

        lbl_distance = findViewById(R.id.lbl_order_distance);
        lbl_delay = findViewById(R.id.lbl_order_delay);

        TextView lbl_missing = findViewById(R.id.lbl_miss_order);
        lbl_missing.setText("Missing Orders:");
        lbl_missing.setVisibility(View.GONE);
        for (RouterModel order: AppUtils.gSelDOrders) {
            String postalCode = order.orderModel.shippingPerson.postalCode.replace(" ", "").toLowerCase();
            postalCode = postalCode.substring(0, 4) + " " + postalCode.substring(4, 6);

            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addresses = geocoder.getFromLocationName(postalCode, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    order.orderModel.shippingPerson.lat = String.format(Locale.US, "%.6f", address.getLatitude());
                    order.orderModel.shippingPerson.lng = String.format(Locale.US, "%.6f", address.getLongitude());
                } else {
                    String str_address = order.orderModel.shippingPerson.street + " " + order.orderModel.shippingPerson.city;
                    try {
                        addresses = geocoder.getFromLocationName(str_address, 1);
                        if (addresses == null) {
                            order.orderModel.shippingPerson.lat = "Unknown";
                            order.orderModel.shippingPerson.lng = "Unknown";
                            if (lbl_missing.getVisibility() == View.GONE) {
                                lbl_missing.setVisibility(View.VISIBLE);
                            }
                            lbl_missing.setText(lbl_missing.getText() + " " + order.orderModel.orderNumber);
                        } else {
                            Address address = addresses.get(0);
                            order.orderModel.shippingPerson.lat = String.format(Locale.US, "%.6f", address.getLatitude());
                            order.orderModel.shippingPerson.lng = String.format(Locale.US, "%.6f", address.getLongitude());
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        order.orderModel.shippingPerson.lat = "Unknown";
                        order.orderModel.shippingPerson.lng = "Unknown";
                        if (lbl_missing.getVisibility() == View.GONE) {
                            lbl_missing.setVisibility(View.VISIBLE);
                        }
                        lbl_missing.setText(lbl_missing.getText() + " " + order.orderModel.orderNumber);
                    }
                }
            } catch (IOException e) {
                order.orderModel.shippingPerson.lat = "Unknown";
                order.orderModel.shippingPerson.lng = "Unknown";
                if (lbl_missing.getVisibility() == View.GONE) {
                    lbl_missing.setVisibility(View.VISIBLE);
                }
                lbl_missing.setText(lbl_missing.getText() + " " + order.orderModel.orderNumber);
            }
        }

        FireManager.getAllRegions(new FireManager.FBRegionCallback() {
            @Override
            public void onSuccess(List<RegionModel> regions) {
                regionModels.clear();
                regionModels.addAll(regions);
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        drawPolylines();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frg_order_detail_map);
        mapFragment.getMapAsync(this);
    }

    private void sendEmailMessage() {
        for (int i = 0; i < showOrders.size(); i++) {
            OrderModel orderModel = showOrders.get(i).orderModel;
            long currentTime = new Date().getTime();
            long startTime = currentTime + routerInfoModels.get(i).during * 1000 + i * 300 * 1000;
            long endTime = currentTime + routerInfoModels.get(i).during * 1000 + 3600 * 1000 + i * 300 * 1000;

            String startString = new SimpleDateFormat("HH:mm", Locale.US).format(new Date(startTime));
            String endString = new SimpleDateFormat("HH:mm", Locale.US).format(new Date(endTime));

            String send_msg = "Uw bestelling wordt tussen " + startString +
                    " en " + endString + " bezorgd. \n" +
                    " met vriendelijke groet, \n" +
                    " De Westlandse Bezorgslager";

            SendEmailASyncTask task = new SendEmailASyncTask(
                    orderModel.email,
                    "info@dewestlandsebezorgslager.nl",
                    "DWB Shipping",
                    send_msg,
                    null,
                    null);
            task.execute();
        }
    }

    private void sendSmsMessage() {
        for (int i = 0; i < showOrders.size(); i++) {
            OrderModel orderModel = showOrders.get(i).orderModel;
            String str_phone = orderModel.shippingPerson.phone;
            str_phone.replace("+", "");
            if (!str_phone.substring(0, 2).equals("31")) {
                str_phone = "31" + str_phone;
            }

            long currentTime = new Date().getTime();
            long startTime = currentTime + routerInfoModels.get(i).during * 1000 + i * 300 * 1000;
            long endTime = currentTime + routerInfoModels.get(i).during * 1000 + 3600 * 1000 + i * 300 * 1000;

            String startString = new SimpleDateFormat("HH:mm", Locale.US).format(new Date(startTime));
            String endString = new SimpleDateFormat("HH:mm", Locale.US).format(new Date(endTime));

            String send_msg = "Uw bestelling wordt tussen " + startString +
                    " en " + endString + " bezorgd. \n" +
                    " met vriendelijke groet, \n" +
                    " De Westlandse Bezorgslager";
            try {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(str_phone, null, send_msg, null, null);
            } catch (Exception e) {
                Toast.makeText(this, "Sms not Send", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void drawPolylines() {
        dialog = ProgressDialog.show(this, "", getString(R.string.alert_connect));
        dialog.show();
        String url = getDirectionsUrl();
        APIManager.onAPIConnectionResponse(url, null, APIManager.APIMethod.GET, new APIManager.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                onUpdateGoogleMap(obj);

                ParserTask parserTask = new ParserTask();
                parserTask.execute(obj.toString());

                dialog.dismiss();
            }

            @Override
            public void onEventInternetError(Exception e) {
                dialog.dismiss();
            }

            @Override
            public void onEventServerError(Exception e) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnectionListener)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void onUpdateGoogleMap(JSONObject json) {
        try {
            JSONArray routes = json.getJSONArray("routes");
            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");

            int dis = 0;
            int dur = 0;

            routerInfoModels.clear();
            showOrders.clear();
            for (int i = 0; i < legs.length(); i++) {
                JSONObject leg = legs.getJSONObject(i);
                JSONObject distance = leg.getJSONObject("distance");
                int dis_one = distance.getInt("value");
                dis = dis + dis_one;

                JSONObject duration = leg.getJSONObject("duration");
                int dur_one = duration.getInt("value");
                dur = dur + dur_one;

                RouterInfoModel infoModel = new RouterInfoModel();
                infoModel.during = dur;
                infoModel.distance = dis_one;
                routerInfoModels.add(infoModel);

                JSONObject loc_start = leg.getJSONObject("start_location");
                LatLng latLng_start = new LatLng(loc_start.getDouble("lat"), loc_start.getDouble("lng"));

                JSONObject loc_end = leg.getJSONObject("end_location");
                LatLng latLng_end = new LatLng(loc_end.getDouble("lat"), loc_end.getDouble("lng"));

                String address_start = leg.getString("start_address");
                String adr_start = address_start.split(",")[0];

                String address_end = leg.getString("end_address");
                String adr_end = address_end.split(",")[0];

                if (i == 0) {
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng_start)
                            .title(adr_start)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                    String str_maker = getMarkerFromLatlng(latLng_end);
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng_end)
                            .title(str_maker)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                } else if (i == legs.length() - 1) {
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng_end)
                            .title(adr_end)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                } else {
                    String str_maker = getMarkerFromLatlng(latLng_end);
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng_end)
                            .title(str_maker)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
            }
            AppUtils.gSelDOrders.clear();
            AppUtils.gSelDOrders.addAll(showOrders);
            routeAdapter.notifyDataSetChanged();

            lbl_distance.setText(String.format(Locale.US, "%.1f Km", dis / 1000.0));

            int hour = dur / 3600;
            int min = (dur - hour * 3600) / 60;
            String duringTime = "";
            if (hour == 0) {
                duringTime = min + " min";
            } else {
                duringTime = hour + "h " + min + " m";
            }
            lbl_delay.setText(duringTime);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.not_best_way), Toast.LENGTH_SHORT).show();
        }
    }

    private String getMarkerFromLatlng(LatLng adr_end) {
        for (RouterModel model: AppUtils.gSelDOrders) {
            double lat = Double.valueOf(model.orderModel.shippingPerson.lat);
            double lng = Double.valueOf(model.orderModel.shippingPerson.lng);
            double delta = Math.sqrt(Math.abs(lat - adr_end.latitude) * Math.abs(lat - adr_end.latitude)
                    + Math.abs(lng - adr_end.longitude) * Math.abs(lng - adr_end.longitude));
            if (delta < 5e-4) {
                showOrders.add(model);
                return "#" + model.orderModel.orderNumber + ", " + model.orderModel.shippingPerson.name;
            }
        }
        return "No Found";
    }

    public void onClickSendMessage(View view) {
//        sendSmsMessage();
        sendEmailMessage();
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            if (result == null || result.size() == 0) {
                Toast.makeText(OrderDetailActivity.this, getString(R.string.not_best_way), Toast.LENGTH_SHORT).show();
                return;
            }
            List<LatLng> points = new ArrayList<>();

            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.width(8);
            lineOptions.color(Color.RED);
            lineOptions.geodesic(true);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (int i = 0; i < result.size(); i++) {
                points.clear();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                    builder.include(new LatLng(lat, lng));
                }
                lineOptions.addAll(points);
            }

            LatLngBounds bounds = builder.build();
            int padding = 50; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);

            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl() {
        String origin = "Van Zeggelenlaan 112";
        String str_origin = "origin=" + origin;

        // Destination of route
        String str_dest = "destination=" + AppUtils.gUser.address;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String waypoints = "waypoints=optimize:true";
        List<String> streets = new ArrayList<>();

        for (RouterModel model: AppUtils.gSelDOrders) {
            if (model.orderModel.shippingPerson.lat.equals("Unknown")) {
                continue;
            }
            String street = model.orderModel.shippingPerson.lat + " " + model.orderModel.shippingPerson.lng;
            if (streets.size() == 0) {
                streets.add(street);
            } else {
                if (!streets.contains(street)) {
                    streets.add(street);
                }
            }
        }

        for (String street: streets) {
            waypoints = waypoints + "|" + street;
        }

        String apiKey = "key=" + getString(R.string.google_map_key);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + waypoints + "&" + apiKey;

        // Output format
        String output = "json";

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    private boolean isLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                return false;
            }
        }
        else {
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order, menu);
//        menuItem = menu.getItem(R.id.action_select);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //resume tasks needing this permission
                    if (mGoogleApiClient == null) {
                        buildGoogleApiClient();
                    }
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        routeAdapter.notifyDataSetChanged();
    }

    private class SendEmailASyncTask extends AsyncTask<Void, Void, Void> {

        private String mMsgResponse;

        private String mTo;
        private String mFrom;
        private String mSubject;
        private String mText;
        private Uri mUri;
        private String mAttachmentName;

        public SendEmailASyncTask(String mTo, String mFrom, String mSubject,
                                  String mText, Uri mUri, String mAttachmentName) {
            this.mTo = mTo;
            this.mFrom = mFrom;
            this.mSubject = mSubject;
            this.mText = mText;
            this.mUri = mUri;
            this.mAttachmentName = mAttachmentName;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SendGrid sendgrid = new SendGrid(SENDGRID_APIKEY);
                SendGrid.Email email = new SendGrid.Email();

                // Get values from edit text to compose email
                // TODO: Validate edit texts
                email.addTo(mTo);
                email.setFrom(mFrom);
                email.setSubject(mSubject);
                email.setText(mText);

                // Attach image
                if (mUri != null) {
                    email.addAttachment(mAttachmentName, OrderDetailActivity.this.getContentResolver().openInputStream(mUri));
                }

                // Send email, execute http request
                SendGrid.Response response = sendgrid.send(email);
                mMsgResponse = response.getMessage();

            } catch (SendGridException | IOException e) {
                //
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                JSONObject obj = new JSONObject(mMsgResponse);
                String message = obj.getString("message");
                if (message.equals("success")) {
                    Toast.makeText(OrderDetailActivity.this, "De bezorger is onderweg", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OrderDetailActivity.this, mMsgResponse, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class RouterModel {
        public OrderModel orderModel;
        public boolean isCheck = false;

        public RouterModel(OrderModel orderModel) {
            this.orderModel = orderModel;
        }

        public boolean isCheck() {
            return isCheck;
        }
    }


}
