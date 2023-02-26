package com.laodev.dwbrouter.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.internal.FlowLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.model.CheckListModel;
import com.laodev.dwbrouter.model.RegionModel;
import com.laodev.dwbrouter.model.RegionShowModel;
import com.laodev.dwbrouter.model.UserModel;
import com.laodev.dwbrouter.ui.RegionItemUI;
import com.laodev.dwbrouter.ui.RoosterDetailUI;
import com.laodev.dwbrouter.util.AppUtils;
import com.laodev.dwbrouter.util.BannerUtil;
import com.laodev.dwbrouter.util.FireManager;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class RoosterDetailActivity extends AppCompatActivity {

    private EditText txt_from;
    private Calendar mCalendar = Calendar.getInstance();
    private PieChartView pcv_delivery;
    private LinearLayout llt_data;
    private TextView lbl_name;
    private View content;
    private FlowLayout llt_postal;

    private List<RegionShowModel> regionShowModels = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooster_detail);

        setToolbar();
        initView();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }
    }

    private void initView() {
        content = findViewById(R.id.content);

        llt_postal = findViewById(R.id.llt_postal);
        lbl_name = findViewById(R.id.lbl_rooster_user);
        txt_from = findViewById(R.id.txt_rooster_from);
        txt_from.setInputType(InputType.TYPE_NULL);
        txt_from.setOnClickListener(view -> createDialogWithoutDateField());

        pcv_delivery = findViewById(R.id.pcv_delivery);

        llt_data = findViewById(R.id.llt_rooster_data);

        String myFormat = "yyyy-MM"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        try {
            mCalendar.setTime(sdf.parse(AppUtils.gCheckListModel.year + "-" + AppUtils.gCheckListModel.numMonth));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        updateLabel();

        initData();
    }

    private void initData() {
        String myFormat = "yyyy-MM"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String year_month = sdf.format(mCalendar.getTime());

        ProgressDialog dialog = ProgressDialog.show(this, "", "Refreshing Datas ...");
        FireManager.mHistoryRef.child(AppUtils.gCheckListModel.userid)
                .child(year_month)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<CheckListModel> listModels = new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    for (DataSnapshot shot: snapshot.getChildren()) {
                        CheckListModel model = shot.getValue(CheckListModel.class);
                        listModels.add(model);

                        FireManager.getAllRegions(new FireManager.FBRegionCallback() {
                            @Override
                            public void onSuccess(List<RegionModel> regionModels) {
                                dialog.dismiss();
                                regionShowModels.clear();
                                for (RegionModel regionModel: regionModels) {
                                    RegionShowModel regionShowModel = new RegionShowModel();
                                    regionShowModel.regionModel = regionModel;
                                    regionShowModels.add(regionShowModel);
                                }
                                refreshView(listModels);
                            }

                            @Override
                            public void onFailed(String error) {
                                dialog.dismiss();
                                BannerUtil.onShowErrorAlertEvent(content, error, 2000);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                BannerUtil.onShowErrorAlertEvent(content, databaseError.getMessage(), 2000);
            }
        });

        FireManager.getUserFromUserID(AppUtils.gCheckListModel.userid, new FireManager.FBUserCallback() {
            @Override
            public void onSuccess(UserModel user) {
                lbl_name.setText(user.name);
            }

            @Override
            public void onFailed(String error) {
                BannerUtil.onShowErrorAlertEvent(content, error, 2000);
            }
        });
    }

    private void refreshView(List<CheckListModel> listModels) {
        llt_data.removeAllViews();
        for (CheckListModel model: listModels) {
            RoosterDetailUI detailUI = new RoosterDetailUI(this);
            detailUI.initWithDatas(model);
            detailUI.isEditable(false);
            llt_data.addView(detailUI);

            String[] regions = model.region.split(",");
            String[] amounts = model.amount.split(",");
            for (int i = 0; i < regions.length; i++) {
                String region = regions[i];
                for (RegionShowModel regionShowModel: regionShowModels) {
                    if (regionShowModel.regionModel.title.equals(region)) {
                        regionShowModel.region++;
                        regionShowModel.delivery = regionShowModel.delivery + Integer.valueOf(amounts[i]);
                    }
                }
            }
        }

        llt_postal.removeAllViews();
        for (RegionShowModel regionShowModel: regionShowModels) {
            RegionItemUI regionItemUI = new RegionItemUI(this);
            regionItemUI.setPostalCode(regionShowModel.regionModel.title, regionShowModel.regionModel.color);
            regionItemUI.isEditable(false);
            llt_postal.addView(regionItemUI);
        }

        List pieDeliveryData = new ArrayList<>();
        int total = 0;
        for (RegionShowModel regionShowModel: regionShowModels) {
            pieDeliveryData.add(new SliceValue(regionShowModel.delivery, Color.parseColor(regionShowModel.regionModel.color)).setLabel(String.valueOf(regionShowModel.delivery)));
            total = total + regionShowModel.delivery;
        }
        PieChartData pieChartDeliveryData = new PieChartData(pieDeliveryData);
        pieChartDeliveryData.setHasLabels(true).setValueLabelTextSize(12);
        pieChartDeliveryData.setHasCenterCircle(true).setCenterText1(listModels.size() + " Dagen").setCenterText1FontSize(22).setCenterText1Color(Color.parseColor("#0097A7"));
        pieChartDeliveryData.setHasCenterCircle(true).setCenterText2(total + " Bestellingen").setCenterText2FontSize(14).setCenterText2Color(Color.parseColor("#0097A7"));
        pcv_delivery.setPieChartData(pieChartDeliveryData);
    }

    private void createDialogWithoutDateField() {
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(this,
                (selectedMonth, selectedYear) -> {
                    mCalendar.set(Calendar.YEAR, selectedYear);
                    mCalendar.set(Calendar.MONTH, selectedMonth);
                    mCalendar.set(Calendar.DAY_OF_MONTH, 1);
                    updateLabel();
                }
                , mCalendar.get(Calendar.YEAR)
                , mCalendar.get(Calendar.MONTH)
        );

        builder.setTitle("Select month")
                .setActivatedYear(mCalendar.get(Calendar.YEAR))
                .setYearRange(2010, 2030)
                .setActivatedMonth(mCalendar.get(Calendar.MONTH))
                .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                .build()
                .show();
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        txt_from.setText(sdf.format(mCalendar.getTime()));
        initData();
    }

}
