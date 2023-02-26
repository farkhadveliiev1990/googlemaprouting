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
import com.laodev.dwbrouter.activity.OrderDetailActivity;
import com.laodev.dwbrouter.model.RegionModel;
import com.laodev.dwbrouter.util.AppConst;

import java.util.List;

public class RouteAdapter extends BaseAdapter {

    private Context context;
    private List<OrderDetailActivity.RouterModel> mOrders;
    private List<String> mProcesses;
    private boolean isCheckable;


    public RouteAdapter(Context context, List<OrderDetailActivity.RouterModel> orders, List<String> processes) {
        this.context = context;
        mOrders = orders;
        mProcesses = processes;
        isCheckable = false;
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
        OrderDetailActivity.RouterModel model = mOrders.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = layoutInflater.inflate(R.layout.item_route, null);

        LinearLayout llt_back = view.findViewById(R.id.llt_route_back);
        for (RegionModel regionModel: AppConst.gAllRegions) {
            if (model.orderModel.shippingPerson.region.equals(regionModel.title)) {
                llt_back.setBackgroundColor(Color.parseColor(regionModel.color));
            }
        }

        TextView lbl_id = view.findViewById(R.id.lbl_route_id);
        lbl_id.setText("#" + model.orderModel.orderNumber);

        TextView lbl_name = view.findViewById(R.id.lbl_route_name);
        lbl_name.setText(model.orderModel.shippingPerson.name);

        TextView lbl_city = view.findViewById(R.id.lbl_route_city);
        lbl_city.setText(model.orderModel.shippingPerson.city);

        TextView lbl_address = view.findViewById(R.id.lbl_route_address);
        lbl_address.setText(model.orderModel.shippingPerson.street);

        TextView lbl_time = view.findViewById(R.id.lbl_route_time);
        lbl_time.setText(model.orderModel.datetime.split(" ")[0]);

        TextView lbl_phone = view.findViewById(R.id.lbl_route_phone);
        lbl_phone.setText(model.orderModel.shippingPerson.phone);

        ImageView img_check = view.findViewById(R.id.img_check);
        if (isCheckable)
            img_check.setVisibility(View.VISIBLE);
        else img_check.setVisibility(View.GONE);
        img_check.setOnClickListener(view1 -> {
            if (isCheckable) {
                model.isCheck = ! model.isCheck;
                notifyDataSetChanged();
            }
        });
        img_check.setImageResource(model.isCheck? R.drawable.ic_check_circle_green : R.drawable.ic_circle_green);

        TextView lbl_router = view.findViewById(R.id.lbl_router_done);
        ImageView img_router = view.findViewById(R.id.img_router);
        if (mProcesses.get(position).equals("Processing")) {
            lbl_router.setVisibility(View.GONE);
            img_router.setVisibility(View.VISIBLE);
        } else {
            lbl_router.setVisibility(View.VISIBLE);
            img_router.setVisibility(View.GONE);
        }

        return view;
    }

    public void setCheckable(boolean checkable) {
        isCheckable = checkable;
        notifyDataSetChanged();
    }

}
