package com.laodev.dwbrouter.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.model.CarServiceModel;
import com.laodev.dwbrouter.model.OrderModel;
import com.laodev.dwbrouter.util.AppConst;

import java.util.List;

public class CarAdapter extends BaseAdapter {

    private Context context;
    private List<CarServiceModel> mCars;
    private CarAdapterListener carAdapterListener;

    public CarAdapter(Context context, List<CarServiceModel> cars) {
        this.context = context;
        mCars = cars;
    }

    @Override
    public int getCount() {
        return mCars.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CarServiceModel car = mCars.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_car, null);

        TextView lbl_name = view.findViewById(R.id.txt_car_name);
        TextView lbl_regdate = view.findViewById(R.id.txt_car_regdate);
        ImageView img_edit = view.findViewById(R.id.img_car_edit);
        ImageView img_remove = view.findViewById(R.id.img_car_remove);

        lbl_name.setText(car.name);
        lbl_regdate.setText(car.regdate);
        img_edit.setOnClickListener(view1 -> carAdapterListener.editCarInfoEvent(car));
        img_remove.setOnClickListener(view12 -> carAdapterListener.removeCarInfoEvent(car));

        return view;
    }

    public void setCarAdapterListener(CarAdapterListener carAdapterListener) {
        this.carAdapterListener = carAdapterListener;
    }

    public interface CarAdapterListener {
        void editCarInfoEvent(CarServiceModel car);
        void removeCarInfoEvent(CarServiceModel car);
    }

}
