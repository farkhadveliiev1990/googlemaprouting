package com.laodev.dwbrouter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.activity.RoosterDetailActivity;
import com.laodev.dwbrouter.model.CarServiceModel;
import com.laodev.dwbrouter.model.CheckListModel;
import com.laodev.dwbrouter.model.UserModel;
import com.laodev.dwbrouter.util.AppConst;
import com.laodev.dwbrouter.util.AppUtils;
import com.laodev.dwbrouter.util.FireManager;

public class RoosterDetailUI extends LinearLayout {

    private TextView lbl_week, lbl_day, lbl_month;
    private LinearLayout llt_region;

    private CheckListModel model;
    private boolean flag = true;


    public RoosterDetailUI(Context _context) {
        super(_context);

        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(_context).inflate(R.layout.ui_rooster_detail, this, true);

        initUIView();
        initEvent();
    }

    private void initEvent() {
        this.setOnClickListener(view -> {
            if (!flag) {
                return;
            }
            AppUtils.gCheckListModel = model;
            AppUtils.showOtherActivity(getContext(), RoosterDetailActivity.class, 0);
        });
    }

    private void initUIView() {
        lbl_week = findViewById(R.id.lbl_rooster_week);
        lbl_day = findViewById(R.id.lbl_rooster_day);
        lbl_month = findViewById(R.id.lbl_rooster_month);
        llt_region = findViewById(R.id.llt_rooster_data);
    }

    public void initWithDatas(CheckListModel model) {
        this.model = model;

        lbl_week.setText(model.week);
        lbl_day.setText(model.day);
        lbl_month.setText(model.month);

        llt_region.removeAllViews();
        String[] regions = model.region.split(",");
        String[] values = model.amount.split(",");
        String[] colors = model.color.split(",");
        for (int i = 0; i < regions.length; i++) {
            String region = regions[i];
            String value = values[i];
            String color = colors[i];
            RegionDetailUI detailUI = new RegionDetailUI(getContext());
            detailUI.initWithDatas(region, value, color);
            llt_region.addView(detailUI);
        }
    }

    public void isEditable(boolean flag) {
        this.flag = flag;
    }

}
