package com.laodev.dwbrouter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.model.RegionModel;
import com.laodev.dwbrouter.util.BannerUtil;
import com.laodev.dwbrouter.util.FireManager;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

public class RegionDialog extends Dialog {

    private View content;
    private EditText txt_name;
    private ColorPickerView cpv_region;
    private Button btn_cancel, btn_add;

    private String regionColor = "#ffffff";
    private RegionModel regionModel = new RegionModel();

    public RegionDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dig_add_region);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setTitle(null);
        setCanceledOnTouchOutside(true);

        initView();
        initEvent();
    }

    private void initEvent() {
        btn_cancel.setOnClickListener(view -> dismiss());
        btn_add.setOnClickListener(view -> {
            String name = txt_name.getText().toString();
            if (name.isEmpty()) {
                BannerUtil.onShowWaringAlertEvent(content, getContext().getString(R.string.banner_no_name), 2000);
                return;
            }
            if (regionModel.title.isEmpty()) {
                regionModel.title = name;
                regionModel.color = regionColor;
                BannerUtil.onShowProcessingAlertEvent(content, "Verwerking voor het toevoegen van gebied.", 2000);
                FireManager.addRegionModel(regionModel, new FireManager.FBRegionCallback() {
                    @Override
                    public void onSuccess() {
                        dismiss();
                        BannerUtil.onShowSuccessAlertEvent(content, "Succes toevoegen regio.", 2000);
                    }

                    @Override
                    public void onFailed(String error) {
                        dismiss();
                        BannerUtil.onShowErrorAlertEvent(content, error, 2000);
                    }
                });
            } else {
                BannerUtil.onShowProcessingAlertEvent(content, "verwerking voor het bijwerken van gebied.", 2000);
                FireManager.removeRegionModel(regionModel, new FireManager.FBRegionCallback() {
                    @Override
                    public void onSuccess() {
                        RegionModel newRegion = new RegionModel();
                        newRegion.title = name;
                        newRegion.color = regionColor;
                        newRegion.postal.addAll(regionModel.postal);
                        FireManager.addRegionModel(newRegion, new FireManager.FBRegionCallback() {
                            @Override
                            public void onSuccess() {
                                dismiss();
                                BannerUtil.onShowSuccessAlertEvent(content, "Regio voor geslaagde update.", 2000);
                            }

                            @Override
                            public void onFailed(String error) {
                                dismiss();
                                BannerUtil.onShowErrorAlertEvent(content, error, 2000);
                            }
                        });
                    }

                    @Override
                    public void onFailed(String error) {
                        dismiss();
                        BannerUtil.onShowErrorAlertEvent(content, error, 2000);
                    }
                });
            }
        });
        cpv_region.setColorListener((ColorEnvelopeListener) (envelope, fromUser) -> regionColor = "#" + envelope.getHexCode());
    }

    private void initView() {
        txt_name = findViewById(R.id.txt_region_name);
        cpv_region = findViewById(R.id.cpv_region);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_add = findViewById(R.id.btn_add);
    }

    public void initData(View content, RegionModel model) {
        this.content = content;
        if (model != null) {
            regionModel = model;
            txt_name.setText(regionModel.title);
            regionColor = regionModel.color;
        }
    }

}
