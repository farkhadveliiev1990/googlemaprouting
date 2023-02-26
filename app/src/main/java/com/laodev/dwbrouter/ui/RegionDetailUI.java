package com.laodev.dwbrouter.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laodev.dwbrouter.R;


public class RegionDetailUI extends LinearLayout {

    private TextView lbl_region, lbl_value;

    public RegionDetailUI(Context _context) {
        super(_context);

        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(_context).inflate(R.layout.ui_region_detail, this, true);

        initUIView();
    }

    private void initUIView() {
        lbl_region = findViewById(R.id.lbl_region_name);
        lbl_value = findViewById(R.id.lbl_region_value);
    }

    public void initWithDatas(String region, String value, String color) {
        lbl_region.setText(region);
        lbl_region.setBackgroundColor(Color.parseColor(color));
        lbl_value.setText(value);
    }

}
