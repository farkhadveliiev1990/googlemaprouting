package com.laodev.dwbrouter.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.internal.FlowLayout;
import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.dialog.RegionDialog;
import com.laodev.dwbrouter.model.RegionModel;
import com.laodev.dwbrouter.util.AppUtils;
import com.laodev.dwbrouter.util.BannerUtil;
import com.laodev.dwbrouter.util.FireManager;

public class RegionModelUI extends LinearLayout {

    private TextView lbl_name;
    private TextView lbl_count;
    private LinearLayout llt_title;
    private FlowLayout llt_postal;
    private View content;

    private RegionModel regionModel;

    public RegionModelUI(Context _context) {
        super(_context);

        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(_context).inflate(R.layout.ui_region_model, this, true);

        initUIView();
    }

    private void initUIView() {
        lbl_name = findViewById(R.id.lbl_region_name);
        lbl_count = findViewById(R.id.lbl_region_value);

        ImageView img_add_postal = findViewById(R.id.img_add_postal);
        img_add_postal.setOnClickListener(view -> AppUtils.showEditTextDialog(getContext(), "Voer de postcode in.", text -> {
            if (text.isEmpty()) {
                BannerUtil.onShowWaringAlertEvent(content, "Voer de postcode in", 2000);
                return;
            }
            if (regionModel.postal.contains(text)) {
                BannerUtil.onShowWaringAlertEvent(content, "Deze postcode bestaat al.", 2000);
                return;
            }
            regionModel.postal.add(text);
            FireManager.addRegionModel(regionModel, new FireManager.FBRegionCallback() {
                @Override
                public void onSuccess() {
                    BannerUtil.onShowSuccessAlertEvent(content, "Succes voor het toevoegen van postcode.", 2000);
                }

                @Override
                public void onFailed(String error) {
                    BannerUtil.onShowErrorAlertEvent(content, error, 2000);
                }
            });
        }));

        ImageView img_edit_region = findViewById(R.id.img_edit_postal);
        img_edit_region.setOnClickListener(view -> {
            RegionDialog regionDialog = new RegionDialog(getContext());
            regionDialog.initData(content, regionModel);
            regionDialog.show();
        });

        ImageView img_remove_region = findViewById(R.id.img_remove_postal);
        img_remove_region.setOnClickListener(view -> FireManager.removeRegionModel(regionModel, new FireManager.FBRegionCallback() {
            @Override
            public void onSuccess() {
                BannerUtil.onShowSuccessAlertEvent(content, "Succes voor verwijder regio", 2000);
            }

            @Override
            public void onFailed(String error) {
                BannerUtil.onShowErrorAlertEvent(content, error, 2000);
            }
        }));

        llt_postal = findViewById(R.id.llt_postal);
        llt_title = findViewById(R.id.llt_region_title);
    }

    @SuppressLint("SetTextI18n")
    public void setRegionModel(View content, RegionModel regionModel) {
        this.content = content;
        this.regionModel = regionModel;

        llt_title.setBackgroundColor(Color.parseColor(regionModel.color));
        lbl_name.setText(regionModel.title);
        lbl_count.setText("(" + regionModel.postal.size() + ")");
        llt_postal.removeAllViews();
        for (String postal: regionModel.postal) {
            RegionItemUI regionItemUI = new RegionItemUI(getContext());
            regionItemUI.setPostalCode(postal, regionModel.color);
            regionItemUI.setRegionItemUIListener(code -> {
                regionModel.postal.remove(code);
                FireManager.addRegionModel(regionModel, new FireManager.FBRegionCallback() {
                    @Override
                    public void onSuccess() {
                        BannerUtil.onShowSuccessAlertEvent(content, getContext().getString(R.string.banner_remove_postal), 2000);
                    }

                    @Override
                    public void onFailed(String error) {
                        BannerUtil.onShowErrorAlertEvent(content, error, 2000);
                    }
                });
            });
            llt_postal.addView(regionItemUI);
        }
    }

}
