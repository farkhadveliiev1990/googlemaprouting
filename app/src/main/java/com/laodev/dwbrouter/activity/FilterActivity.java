package com.laodev.dwbrouter.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.model.OrderModel;
import com.laodev.dwbrouter.model.RegionModel;
import com.laodev.dwbrouter.ui.CheckBoxUI;
import com.laodev.dwbrouter.util.APIManager;
import com.laodev.dwbrouter.util.AppConst;
import com.laodev.dwbrouter.util.AppUtils;
import com.laodev.dwbrouter.util.TimeManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class FilterActivity extends AppCompatActivity {

    private final Calendar mCalendar = Calendar.getInstance();
    private EditText txt_date;

    private List<OrderModel> allOrders = new ArrayList<>();
    private List<CheckBoxUI> checkBoxUIS = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        AppUtils.initUIActivity(this);

        setToolbar();
        initWithView();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }
    }

    private void initWithView() {
        txt_date = findViewById(R.id.txt_filter_date);
        txt_date.setInputType(InputType.TYPE_NULL);

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel();
        };

        txt_date.setOnClickListener(view -> new DatePickerDialog(FilterActivity.this, date, mCalendar
                .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show()
        );

        LinearLayout llt_check = findViewById(R.id.llt_filter_check);

        for (RegionModel regionModel: AppConst.gAllRegions) {
            CheckBoxUI chkUI = new CheckBoxUI(this, regionModel);
            checkBoxUIS.add(chkUI);
            llt_check.addView(chkUI);
        }

        LinearLayout llt_set = findViewById(R.id.llt_filter_set);
        llt_set.setOnClickListener(view -> {
            String date1 = txt_date.getText().toString();
            if (date1.length() == 0) {
                Toast.makeText(FilterActivity.this, getString(R.string.alert_set_date), Toast.LENGTH_SHORT).show();
                return;
            }

            final List<String> regions = new ArrayList<>();
            for (CheckBoxUI model: checkBoxUIS) {
                if (model.isChecked())
                    regions.add(model.getRegion().title);
            }

            ProgressDialog dialog;
            dialog = ProgressDialog.show(this, "", getString(R.string.alert_connect));
            dialog.show();

            Map<String, String> params = new HashMap<>();
            params.put("token", APIManager.token);

            APIManager.onAPIConnectionResponse(APIManager.API_GET_ORDER, params, APIManager.APIMethod.GET, new APIManager.APIManagerCallback() {
                @Override
                public void onEventCallBack(JSONObject obj) {
                    dialog.dismiss();
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = obj.getJSONArray("items");
                    } catch (Exception e) {
                        Toast.makeText(FilterActivity.this, e.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                    allOrders.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = null;
                        try {
                            object = jsonArray.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        OrderModel model = new OrderModel(object);
                        allOrders.add(model);
                    }
                    onGetFilterOrders(regions, date1);
                }

                @Override
                public void onEventInternetError(Exception e) {
                    dialog.dismiss();
                    Toast.makeText(FilterActivity.this, e.getMessage() , Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onEventServerError(Exception e) {
                    dialog.dismiss();
                    Toast.makeText(FilterActivity.this, getString(R.string.alert_server_error) , Toast.LENGTH_SHORT).show();
                }
            });
        });

        updateLabel();
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        txt_date.setText(sdf.format(mCalendar.getTime()));
    }

    private void onGetFilterOrders(List<String> region, String date) {
        AppUtils.gSelOrders.clear();

        for (OrderModel model: allOrders) {
            if (region.size() > 0) {
                for (String reg: region) {
                    if (TimeManager.isSameDate(model.deliveryDate, date) && model.shippingPerson.region.equals(reg)) {
                        if (!AppUtils.gSelOrders.contains(model)) {
                            AppUtils.gSelOrders.add(model);
                        }
                    }
                }
            } else {
                if (TimeManager.isSameDate(model.deliveryDate, date)) {
                    model.deliveryDate = date;
                    AppUtils.gSelOrders.add(model);
                }
            }
        }
        AppUtils.gSelDate = mCalendar.getTime();
        AppUtils.showOtherActivity(this, OrdersActivity.class, 0);
    }

}
