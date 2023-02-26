package com.laodev.dwbrouter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.model.CarServiceModel;
import com.laodev.dwbrouter.model.RegionModel;
import com.laodev.dwbrouter.model.UserModel;
import com.laodev.dwbrouter.ui.DeliveryRegionUI;
import com.laodev.dwbrouter.util.BannerUtil;
import com.laodev.dwbrouter.util.FireManager;

import java.util.ArrayList;
import java.util.List;

public class AddDeliveryDialog extends Dialog {

    private Spinner spn_user, spn_car;
    private LinearLayout llt_regions;
    private Button btn_cancel, btn_add;
    private View content;

    private List<UserModel> userModels;
    private List<CarServiceModel> carModels;
    private List<RegionModel> regionModels = new ArrayList<>();
    private List<DeliveryRegionUI> regionUIS = new ArrayList<>();

    private String userid = "";
    private String carid = "";
    private AddDeliveryDialogListener addDeliveryDialogListener;


    public AddDeliveryDialog(Context context, View content, List<UserModel> userModels, List<CarServiceModel> carModels) {
        super(context);
        setContentView(R.layout.dig_add_delivery);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setTitle(null);
        setCanceledOnTouchOutside(true);

        this.userModels = userModels;
        this.carModels = carModels;
        this.content = content;

        initView();
        initEvent();
    }

    private void initEvent() {
        spn_user.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userid = userModels.get(i).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spn_car.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                carid = carModels.get(i).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_cancel.setOnClickListener(view -> dismiss());
        btn_add.setOnClickListener(view -> {
            int total = 0;
            for (DeliveryRegionUI regionUI: regionUIS) {
                if (regionUI.getValue().isEmpty() || regionUI.getValue().equals("0")) {
                    continue;
                }
                total = total + Integer.valueOf(regionUI.getValue());
            }
            if (total > 0) {
                String regions = "";
                String colors = "";
                String values = "";
                for (DeliveryRegionUI regionUI: regionUIS) {
                    if (regionUI.getValue().isEmpty() || regionUI.getValue().equals("0")) {
                        continue;
                    }
                    regions = regions + "," + regionUI.getRegionModel().title;
                    colors = colors + "," + regionUI.getRegionModel().color;
                    values = values + "," + regionUI.getValue();
                }
                regions = regions.substring(1);
                colors = colors.substring(1);
                values = values.substring(1);
                addDeliveryDialogListener.onClickAddEvent(userid, carid, regions, values, colors);

                dismiss();
            } else {
                BannerUtil.onShowWaringAlertEvent(content, "Voer een waarde van de regio in.", 2000);
            }
        });
    }

    private void initView() {
        spn_user = findViewById(R.id.spn_user_name);
        spn_car = findViewById(R.id.spn_car_name);
        llt_regions = findViewById(R.id.llt_regions);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_add = findViewById(R.id.btn_add);

        initData();
    }

    private void initData() {
        userid = userModels.get(0).id;
        carid = carModels.get(0).id;
        List<String> usernames = new ArrayList<>();
        for (UserModel user: userModels) {
            usernames.add(user.name);
        }
        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, usernames);
        spn_user.setAdapter(userAdapter);

        List<String> carnames = new ArrayList<>();
        for (CarServiceModel car: carModels) {
            carnames.add(car.name);
        }
        ArrayAdapter<String> carAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, carnames);
        spn_car.setAdapter(carAdapter);

        FireManager.getAllRegions(new FireManager.FBRegionCallback() {
            @Override
            public void onSuccess(List<RegionModel> regions) {
                regionModels.addAll(regions);
                llt_regions.removeAllViews();
                regionUIS.clear();
                for (RegionModel model: regionModels) {
                    DeliveryRegionUI regionUI = new DeliveryRegionUI(getContext());
                    regionUI.initData(model);
                    regionUIS.add(regionUI);
                    llt_regions.addView(regionUI);
                }
            }

            @Override
            public void onFailed(String error) {
                BannerUtil.onShowErrorAlertEvent(content, error, 2000);
            }
        });
    }

    public void setAddDeliveryDialogListener(AddDeliveryDialogListener addDeliveryDialogListener) {
        this.addDeliveryDialogListener = addDeliveryDialogListener;
    }

    public interface AddDeliveryDialogListener {
        void onClickAddEvent(String userid, String carid, String regions, String values, String colors);
    }

}
