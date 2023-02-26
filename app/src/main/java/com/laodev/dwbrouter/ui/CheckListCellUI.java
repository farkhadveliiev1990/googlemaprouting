package com.laodev.dwbrouter.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.model.CheckListModel;
import com.laodev.dwbrouter.util.AppConst;
import com.laodev.dwbrouter.util.AppUtils;
import com.laodev.dwbrouter.util.BannerUtil;
import com.laodev.dwbrouter.util.FireManager;

import java.util.ArrayList;
import java.util.List;

public class CheckListCellUI extends LinearLayout {

    private TextView txtWeek, txtDay, txtMonth;
    private LinearLayout llt_data;
    private ImageView img_add;
    private View content;

    private CheckListModel checkListModel = new CheckListModel();
    private CheckListCellUIListener checkListCellUIListener;

    public CheckListCellUI(Context _context) {
        super(_context);

        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(_context).inflate(R.layout.ui_checklist_cell, this, true);

        initUIView();
        initEvent();
    }

    private void initEvent() {
        img_add.setOnClickListener(view -> checkListCellUIListener.onAddDeliveryEvent());
    }

    private void initUIView() {
        txtWeek = findViewById(R.id.txt_calander_week);
        txtDay = findViewById(R.id.txt_calander_day);
        txtMonth = findViewById(R.id.txt_calander_month);

        llt_data = findViewById(R.id.llt_rooster_data);
        LinearLayout llt_add = findViewById(R.id.llt_rooster_add);
        if (AppUtils.gUser.type.equals(AppConst.USER_DRIVER)) {
            llt_add.setVisibility(GONE);
        }
        img_add = findViewById(R.id.img_rooster_add);
    }

    public void initWithCheckListModel(CheckListModel model, View content) {
        checkListModel = model;
        this.content = content;

        txtWeek.setText(model.week);
        txtDay.setText(model.day);
        txtMonth.setText(model.month);

        initWithDatas();
    }

    public void initWithDatas() {
        FireManager.getDeliveryByDate(checkListModel.year, checkListModel.numMonth, checkListModel.day, new FireManager.FBDeliveryCallback() {
            @Override
            public void onSuccess(List<CheckListModel> models) {
                if (AppUtils.gUser.type.equals(AppConst.USER_DRIVER)) {
                    List<CheckListModel> modelList = new ArrayList<>();
                    for (CheckListModel listModel: models) {
                        if (listModel.userid.equals(AppUtils.gUser.id)) {
                            modelList.add(listModel);
                        }
                    }
                    refreshDataView(modelList);
                } else {
                    refreshDataView(models);
                }
            }

            @Override
            public void onFailed(String error) {

            }
        });
    }

    private void refreshDataView(List<CheckListModel> models) {
        llt_data.removeAllViews();
        for (CheckListModel model: models) {
            RoosterDataUI dataUI = new RoosterDataUI(getContext());
            dataUI.initWithDatas(model);
            dataUI.setRoosterDataUIListener(model1 -> FireManager.removeDelivery(model1, new FireManager.FBDeliveryCallback() {
                @Override
                public void onSuccess() {
                    BannerUtil.onShowErrorAlertEvent(content, "Succes voor verwijder bestelling", 2000);
                    initWithDatas();
                }

                @Override
                public void onFailed(String error) {
                    BannerUtil.onShowErrorAlertEvent(content, error, 2000);
                }
            }));
            llt_data.addView(dataUI);
        }
    }

    public void setCheckListCellUIListener(CheckListCellUIListener checkListCellUIListener) {
        this.checkListCellUIListener = checkListCellUIListener;
    }

    public interface CheckListCellUIListener {
        void onAddDeliveryEvent();
    }

}
