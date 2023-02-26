package com.laodev.dwbrouter.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.model.RegionModel;

public class DeliveryRegionUI extends LinearLayout {

    private TextView lbl_region;
    private EditText txt_value;

    private RegionModel regionModel = new RegionModel();

    public DeliveryRegionUI(Context _context) {
        super(_context);

        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(_context).inflate(R.layout.ui_delivery_region, this, true);

        initUIView();
    }

    private void initUIView() {
        lbl_region = findViewById(R.id.lbl_region_name);
        txt_value = findViewById(R.id.txt_region_value);
    }

    public void initData(RegionModel model) {
        regionModel = model;

        lbl_region.setText(regionModel.title);
        lbl_region.setBackgroundColor(Color.parseColor(regionModel.color));
    }

    public String getValue() {
        return txt_value.getText().toString();
    }

    public RegionModel getRegionModel() {
        return regionModel;
    }

}
