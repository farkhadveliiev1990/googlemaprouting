package com.laodev.dwbrouter.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.model.RegionModel;
import com.laodev.dwbrouter.util.AppConst;
import com.laodev.dwbrouter.util.AppUtils;
import com.laodev.dwbrouter.util.FireManager;
import com.laodev.dwbrouter.util.PermissionsUtil;

import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 451;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppUtils.initUIActivity(this);

        new Handler().postDelayed(this::onNextActivity, 1500);
    }

    private void onNextActivity() {
        FireManager.getAllRegions(new FireManager.FBRegionCallback() {
            @Override
            public void onSuccess(List<RegionModel> regionModels) {
                AppConst.gAllRegions.clear();
                AppConst.gAllRegions.addAll(regionModels);

                if (PermissionsUtil.hasPermissions(SplashActivity.this)) {
                    AppUtils.showOtherActivity(SplashActivity.this, LoginActivity.class, -1);
                } else {
                    ActivityCompat.requestPermissions(SplashActivity.this, PermissionsUtil.permissions, PERMISSION_REQUEST_CODE);
                }
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(SplashActivity.this, error, Toast.LENGTH_SHORT).show();

                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionsUtil.permissionsGranted(grantResults)) {
            AppUtils.showOtherActivity(this, LoginActivity.class, -1);
            finish();
        } else {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

}
