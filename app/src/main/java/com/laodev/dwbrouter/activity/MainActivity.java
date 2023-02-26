package com.laodev.dwbrouter.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.util.AppConst;
import com.laodev.dwbrouter.util.AppUtils;

public class MainActivity extends AppCompatActivity {

    public void onClickRouteLlt(View view) {
        AppUtils.showOtherActivity(this, FilterActivity.class, 0);
    }

    public void onClickRoosterLlt(View view) {
        AppUtils.showOtherActivity(this, RoosterActivity.class, 0);
    }

    public void onClickCheckList(View view) {
        AppUtils.showOtherActivity(this, CheckListActivity.class, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout llt_manager = findViewById(R.id.llt_main_manager);
        if (AppUtils.gUser.type.equals(AppConst.USER_OWNER)) {
            llt_manager.setVisibility(View.VISIBLE);
        } else {
            llt_manager.setVisibility(View.GONE);
        }
    }
}
