package com.laodev.dwbrouter.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseError;
import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.model.HistoryModel;
import com.laodev.dwbrouter.util.AppUtils;
import com.laodev.dwbrouter.util.FireManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NavigationActivity extends AppCompatActivity implements LocationListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setToolbar();
        initUIView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initUIView() {
        WebView wvw_navigation = findViewById(R.id.wvw_navigation);
        String url = "";
        if (AppUtils.gSelIndex == 0) {
            url = "https://www.google.com/maps/dir/?api=1&destination="
                    + AppUtils.gSelOrder.shippingPerson.lat
                    + "," + AppUtils.gSelOrder.shippingPerson.lng
                    + "&origin=Van Zeggelenlaan 112&travelmode=driving&dir_action=navigate";
        } else {
            url = "https://www.google.com/maps/dir/?api=1&destination="
                    + AppUtils.gSelOrder.shippingPerson.lat
                    + "," + AppUtils.gSelOrder.shippingPerson.lng
                    + "&origin="
                    + AppUtils.gSelRouterOrder.shippingPerson.lat
                    + "," + AppUtils.gSelRouterOrder.shippingPerson.lng
                    +  "&travelmode=driving&dir_action=navigate";

        }
        WebSettings webSettings = wvw_navigation.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wvw_navigation.loadUrl(url);

    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
            toolbar.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_done) {
                    AppUtils.allProgress.set(AppUtils.gSelIndex, "Done");
//                    onEventRouterDone();
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_router, menu);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
