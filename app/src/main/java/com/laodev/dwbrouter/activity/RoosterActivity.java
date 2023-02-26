package com.laodev.dwbrouter.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.dialog.AddDeliveryDialog;
import com.laodev.dwbrouter.model.CarServiceModel;
import com.laodev.dwbrouter.model.CheckListModel;
import com.laodev.dwbrouter.model.UserModel;
import com.laodev.dwbrouter.ui.CheckListCellUI;
import com.laodev.dwbrouter.util.BannerUtil;
import com.laodev.dwbrouter.util.FireManager;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RoosterActivity extends AppCompatActivity {

    private LinearLayout lltTable;
    private List<CheckListModel> checkModels = new ArrayList<>();
    private EditText txt_from;
    private Calendar mCalendar = Calendar.getInstance();
    public View content;

    private List<UserModel> allUsers = new ArrayList<>();
    private List<CarServiceModel> allCars = new ArrayList<>();


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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooster);

        setToolbar();
        initUIView();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }
    }

    private void initUIView() {
        content = findViewById(R.id.content);
        lltTable = findViewById(R.id.llt_checklist_table);
        txt_from = findViewById(R.id.txt_rooster_from);
        txt_from.setInputType(InputType.TYPE_NULL);
        txt_from.setOnClickListener(view -> createDialogWithoutDateField());

        initTableData();
    }

    private void initTableData() {
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        updateLabel();

        FireManager.getAllUsers(new FireManager.FBUserCallback() {
            @Override
            public void onSuccess(List<UserModel> users) {
                allUsers.clear();
                allUsers.addAll(users);
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(RoosterActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        FireManager.getAllCars(new FireManager.FBCarCallback() {
            @Override
            public void onSuccessAllCar(List<CarServiceModel> carModels) {
                allCars.clear();
                allCars.addAll(carModels);
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(RoosterActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "yyyy-MM"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        txt_from.setText(sdf.format(mCalendar.getTime()));
        showTableView();
    }

    private void showTableView() {
        lltTable.removeAllViews();
        Date shownDate = mCalendar.getTime();

        checkModels.clear();
        int index = 0;
        while (true) {
            CheckListModel model = new CheckListModel();
            model.id = index + "";
            index++;

            long timeStamp = shownDate.getTime();

            SimpleDateFormat formatterMonthYear = new SimpleDateFormat("yyyy-MM", Locale.US);
            String monthYear = formatterMonthYear.format(shownDate);
            if (!monthYear.equals(txt_from.getText().toString())) {
                break;
            }

            SimpleDateFormat formatterDay = new SimpleDateFormat("dd", Locale.US);
            SimpleDateFormat formatterMonth = new SimpleDateFormat("MMM", Locale.US);
            SimpleDateFormat formatterNumMonth = new SimpleDateFormat("MM", Locale.US);
            SimpleDateFormat formatterWeek = new SimpleDateFormat("EEE", Locale.US);
            SimpleDateFormat formatterYear = new SimpleDateFormat("yyyy", Locale.US);
            model.day = formatterDay.format(new Date(timeStamp));
            model.month = formatterMonth.format(new Date(timeStamp));
            model.numMonth = formatterNumMonth.format(new Date(timeStamp));
            model.week = formatterWeek.format(new Date(timeStamp));
            model.year = formatterYear.format(new Date(timeStamp));
            checkModels.add(model);

            timeStamp = timeStamp + 86400000;
            shownDate = new Date(timeStamp);
        }

        for(int i = 0; i < checkModels.size(); i++) {
            CheckListModel checkModel = checkModels.get(i);
            CheckListCellUI cell = new CheckListCellUI(this);
            cell.initWithCheckListModel(checkModel, content);
            cell.setCheckListCellUIListener(() -> {
                AddDeliveryDialog deliveryDialog = new AddDeliveryDialog(RoosterActivity.this, content, allUsers, allCars);
                deliveryDialog.setAddDeliveryDialogListener((userid, carid, regions, values, colors) -> {
                    checkModel.userid = userid;
                    checkModel.carid = carid;
                    checkModel.region = regions;
                    checkModel.amount = values;
                    checkModel.color = colors;
                    FireManager.addDelivery(checkModel, new FireManager.FBDeliveryCallback() {
                        @Override
                        public void onSuccess() {
                            cell.initWithDatas();
                            BannerUtil.onShowSuccessAlertEvent(content, "Succes voor het toevoegen van geschiedenis.", 2000);
                        }

                        @Override
                        public void onFailed(String error) {
                            BannerUtil.onShowErrorAlertEvent(content, error, 2000);
                        }
                    });
                });
                deliveryDialog.show();
            });
            lltTable.addView(cell);
        }
    }

}