package com.laodev.dwbrouter.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.model.RegionModel;

public class CheckBoxUI extends LinearLayout {

    private TextView txtTitle;
    private ImageView imgCheck;

    private RegionModel region;
    private boolean isChecked;

    private OnClickListener clicker = v -> {
        isChecked = !isChecked;
        initCheckImg();
    };

    private void initEvents() {
        txtTitle.setOnClickListener(clicker);
        imgCheck.setOnClickListener(clicker);
    }

    public CheckBoxUI(Context context, RegionModel model) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.ui_checkbox, this, true);

        region = model;
        isChecked = false;

        initUIView();
        initEvents();
    }

    private void initUIView() {
        txtTitle = findViewById(R.id.txt_checkui_title);
        txtTitle.setText(region.title);
        imgCheck = findViewById(R.id.img_checkui_check);

        LinearLayout llt_back = findViewById(R.id.llt_region_back);
        llt_back.setBackgroundColor(Color.parseColor(region.color));

        initCheckImg();
    }

    private void initCheckImg() {
        if (isChecked) {
            imgCheck.setImageResource(R.drawable.ic_check_circle_green);
        } else {
            imgCheck.setImageResource(R.drawable.ic_circle_green);
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public RegionModel getRegion() {
        return region;
    }

}
