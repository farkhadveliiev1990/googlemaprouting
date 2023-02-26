package com.laodev.dwbrouter.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.model.OrderModel;
import com.laodev.dwbrouter.model.RegionModel;
import com.laodev.dwbrouter.util.AppConst;

import java.util.List;
import java.util.Locale;

public class OrderAdapter extends BaseAdapter {

    private Context context;
    private List<OrderModel> mOrders;

    private boolean isCheckVisible;

    public OrderAdapter(Context context, List<OrderModel> orders) {
        this.context = context;
        mOrders = orders;
        isCheckVisible = false;
    }

    @Override
    public int getCount() {
        return mOrders.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderModel model = mOrders.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = layoutInflater.inflate(R.layout.item_order, null);

        LinearLayout llt_back = view.findViewById(R.id.llt_order_back);
        for (RegionModel regionModel: AppConst.gAllRegions) {
            if (model.shippingPerson.region.equals(regionModel.title)) {
                llt_back.setBackgroundColor(Color.parseColor(regionModel.color));
            }
        }

        TextView lbl_id = view.findViewById(R.id.lbl_order_id);
        lbl_id.setText("#" + model.orderNumber);

        TextView lbl_date = view.findViewById(R.id.lbl_order_time);
        lbl_date.setText(model.deliveryDate);

        TextView lbl_price = view.findViewById(R.id.lbl_order_price);
        lbl_price.setText(String.format(Locale.getDefault(), "€ %.2f", model.total));

        TextView lbl_payment_status = view.findViewById(R.id.lbl_order_pay_status);
        lbl_payment_status.setText(model.paymentStatus);

        TextView lbl_order_status = view.findViewById(R.id.lbl_order_processing_status);
        lbl_order_status.setText(model.fulfillmentStatus);

        TextView lbl_name = view.findViewById(R.id.lbl_order_name);
        lbl_name.setText(model.shippingPerson.name);

        TextView lbl_city = view.findViewById(R.id.lbl_order_city);
        lbl_city.setText(model.shippingPerson.city);

        TextView lbl_address = view.findViewById(R.id.lbl_order_address);
        lbl_address.setText(model.shippingPerson.street);

        view.setOnClickListener(view1 -> {
            model.isCheck = !model.isCheck;
            notifyDataSetChanged();
        });

        LinearLayout llt_check = view.findViewById(R.id.llt_order_check);
        ImageView img_check = view.findViewById(R.id.img_order_check);
        if (isCheckVisible) {
            llt_check.setVisibility(View.VISIBLE);
            if (model.isCheck) {
                img_check.setImageResource(R.drawable.ic_check_circle_green);
            } else {
                img_check.setImageResource(R.drawable.ic_circle_green);
            }
        } else {
            llt_check.setVisibility(View.GONE);
        }

        return view;
    }

    public void setCheckVisible(boolean visible) {
        isCheckVisible = visible;
        notifyDataSetChanged();
    }

}
