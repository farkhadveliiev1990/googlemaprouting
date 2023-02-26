package com.laodev.dwbrouter.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.adapter.OrderAdapter;
import com.laodev.dwbrouter.adapter.PdfDocumentAdapter;
import com.laodev.dwbrouter.model.OrderModel;
import com.laodev.dwbrouter.util.AppUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OrdersActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 98;

    private OrderAdapter orderAdapter;
    public void onClickPrintUB(View view) {
        AppUtils.gSelDOrders.clear();

        for (OrderModel model: AppUtils.gSelOrders) {
            if (model.isCheck) {
                AppUtils.gSelDOrders.add(new OrderDetailActivity.RouterModel(model));
            }
        }
        if (AppUtils.gSelDOrders.size() == 0) {
            Toast.makeText(this, getString(R.string.order_select_error), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isStoragePermissionGranted()) {
            Toast.makeText(this, getString(R.string.print_permission_revoked), Toast.LENGTH_SHORT).show();
            return;
        }

        String path = AppUtils.createPdf(this, AppUtils.gSelDOrders);
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(this, path);
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        } catch (Exception ignored) {

        }

        Toast.makeText(this, getString(R.string.alert_save), Toast.LENGTH_LONG).show();
    }

    public void onClickRouteUB(View view) {
        AppUtils.gSelDOrders.clear();
        AppUtils.allProgress.clear();
        for (OrderModel model: AppUtils.gSelOrders) {
            if (model.isCheck) {
                AppUtils.gSelDOrders.add(new OrderDetailActivity.RouterModel(model));
                AppUtils.allProgress.add("Processing");
            }
        }
        if (AppUtils.gSelDOrders.size() > 0) {
            AppUtils.showOtherActivity(this, OrderDetailActivity.class, 0);
        } else {
            Toast.makeText(this, getString(R.string.order_select_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        setToolbar();
        initWithUIView();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(view -> onBackPressed());
            toolbar.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_select) {
                    for (OrderModel model: AppUtils.gSelOrders) {
                        model.isCheck = true;
                    }
                    orderAdapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            });
            toolbar.setTitle(getString(R.string.order_title) + " (" + AppUtils.gSelOrders.size() + ")");
        }
    }

    private void initWithUIView() {
        ListView lst_order = findViewById(R.id.lst_order);
        orderAdapter = new OrderAdapter(this, AppUtils.gSelOrders);
        orderAdapter.setCheckVisible(true);
        lst_order.setAdapter(orderAdapter);
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //resume tasks needing this permission
                    onClickPrintUB(new View(this));
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
