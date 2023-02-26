package com.laodev.dwbrouter.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.laodev.dwbrouter.R;

public class RegionItemUI extends LinearLayout {

    private TextView lbl_name;
    private CardView cdv_back;
    private ImageView img_remove;

    private String postalCode = "";
    private RegionItemUIListener regionItemUIListener;

    public RegionItemUI(Context _context) {
        super(_context);

        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(_context).inflate(R.layout.ui_region_item, this, true);

        initUIView();
    }

    private void initUIView() {
        cdv_back = findViewById(R.id.cdv_back);
        lbl_name = findViewById(R.id.lbl_postal);
        img_remove = findViewById(R.id.img_remove);
        img_remove.setOnClickListener(view -> regionItemUIListener.onRemoveRegionEvent(postalCode));
    }

    public void setPostalCode(String postalCode, String color) {
        this.postalCode = postalCode;
        lbl_name.setText(postalCode);
        cdv_back.setCardBackgroundColor(Color.parseColor(color));
    }

    public void setRegionItemUIListener(RegionItemUIListener regionItemUIListener) {
        this.regionItemUIListener = regionItemUIListener;
    }

    public void isEditable(boolean flag) {
        if (flag) {
            img_remove.setVisibility(VISIBLE);
        } else {
            img_remove.setVisibility(GONE);
        }
    }

    public interface RegionItemUIListener {
        void onRemoveRegionEvent(String code);
    }

}
