package com.laodev.dwbrouter.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.util.AppConst;
import com.laodev.dwbrouter.util.AppUtils;
import com.laodev.dwbrouter.util.BannerUtil;
import com.laodev.dwbrouter.util.FireManager;

public class EditUserActivity extends AppCompatActivity {

    private View content;

    private EditText edt_name;
    private EditText edt_address;
    private Spinner spn_type;
    private FloatingActionButton fab_check;

    private String user_type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        setToolbar();

        initView();
        initEvent();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }
    }

    private void initView() {
        content = findViewById(R.id.content);

        edt_name = findViewById(R.id.edt_user_name);
        edt_address = findViewById(R.id.edt_user_address);
        EditText edt_phone = findViewById(R.id.edt_user_phone);
        EditText edt_regdate = findViewById(R.id.edt_user_regdate);

        spn_type = findViewById(R.id.spn_user_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{AppConst.USER_OWNER, AppConst.USER_DRIVER});
        spn_type.setAdapter(adapter);

        fab_check = findViewById(R.id.fab_check);
        fab_check.show();

        if (AppUtils.gSelUser.name != null && AppUtils.gSelUser.name.length() > 0) {
            edt_name.setText(AppUtils.gSelUser.name);
        } else {
            edt_name.setHint("AppUtils.gSelUser.name");
        }
        if (AppUtils.gSelUser.address != null && AppUtils.gSelUser.name.length() > 0) {
            edt_address.setText(AppUtils.gSelUser.address);
        } else {
            edt_address.setHint("AppUtils.gSelUser.name");
        }
        edt_phone.setText(AppUtils.gSelUser.phone);
        edt_regdate.setText(AppUtils.gSelUser.regdate);
        if (AppUtils.gSelUser.type.equals(AppConst.USER_OWNER)) {
            spn_type.setSelection(0);
            user_type = AppConst.USER_OWNER;
        } else {
            spn_type.setSelection(1);
            user_type = AppConst.USER_DRIVER;
        }
    }

    private void initEvent() {
        fab_check.setOnClickListener(view -> {
            String name = edt_name.getText().toString();
            if (name.isEmpty()) {
                BannerUtil.onShowWaringAlertEvent(content,getString(R.string.banner_no_name),2000);
                return;
            }
            String address = edt_address.getText().toString();
            if (address.isEmpty()) {
                BannerUtil.onShowWaringAlertEvent(content,getString(R.string.banner_no_address),2000);
                return;
            }
            AppUtils.gSelUser.name = name;
            AppUtils.gSelUser.address = address;
            AppUtils.gSelUser.type = user_type;
            FireManager.updateUserInfo(AppUtils.gSelUser, new FireManager.FBUserCallback() {
                @Override
                public void onSuccess() {
                    BannerUtil.onShowSuccessAlertEvent(content,getString(R.string.banner_success_update),2000);
                    new Handler().postDelayed(() -> onBackPressed(), 2500);
                }

                @Override
                public void onFailed(String error) {
                    BannerUtil.onShowErrorAlertEvent(content, error,2000);
                }
            });

        });
        spn_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    user_type = AppConst.USER_OWNER;
                } else {
                    user_type = AppConst.USER_DRIVER;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}
