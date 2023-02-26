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

public class RoosterDataUI extends LinearLayout {

    private TextView lbl_name, lbl_car;
    private LinearLayout llt_region;
    private ImageView img_remove;

    private CheckListModel model;
    private RoosterDataUIListener roosterDataUIListener;


    public RoosterDataUI(Context _context) {
        super(_context);

        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(_context).inflate(R.layout.ui_rooster_data, this, true);

        initUIView();
        initEvent();
    }

    private void initEvent() {
        img_remove.setOnClickListener(view -> roosterDataUIListener.onRemoveClickEvent(model));
        this.setOnClickListener(view -> {
            AppUtils.gCheckListModel = model;
            AppUtils.showOtherActivity(getContext(), RoosterDetailActivity.class, 0);
        });
    }

    private void initUIView() {
        lbl_name = findViewById(R.id.lbl_rooster_name);
        lbl_car = findViewById(R.id.lbl_rooster_car);
        llt_region = findViewById(R.id.llt_rooster_data);
        img_remove = findViewById(R.id.img_rooster_remove);
        if (AppUtils.gUser.type.equals(AppConst.USER_DRIVER)) {
            img_remove.setVisibility(GONE);
        }
    }

    public void initWithDatas(CheckListModel model) {
        this.model = model;
        FireManager.getUserFromUserID(model.userid, new FireManager.FBUserCallback() {
            @Override
            public void onSuccess(UserModel user) {
                lbl_name.setText(user.name);
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        FireManager.getCarByID(model.carid, new FireManager.FBCarCallback() {
            @Override
            public void onSuccess(CarServiceModel car) {
                if (car == null) {
                    lbl_car.setText("Unknown");
                } else {
                    lbl_car.setText(car.name);
                }
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

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

    public void setRoosterDataUIListener(RoosterDataUIListener roosterDataUIListener) {
        this.roosterDataUIListener = roosterDataUIListener;
    }

    public interface RoosterDataUIListener {
        void onRemoveClickEvent(CheckListModel model);
    }

}
