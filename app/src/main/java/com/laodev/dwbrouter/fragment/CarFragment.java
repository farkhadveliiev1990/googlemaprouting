package com.laodev.dwbrouter.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.annotations.NotNull;
import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.activity.CheckListActivity;
import com.laodev.dwbrouter.adapter.CarAdapter;
import com.laodev.dwbrouter.model.CarServiceModel;
import com.laodev.dwbrouter.util.AppUtils;
import com.laodev.dwbrouter.util.BannerUtil;
import com.laodev.dwbrouter.util.FireManager;

import java.util.ArrayList;
import java.util.List;

public class CarFragment extends Fragment {

    private CheckListActivity mActivity;
    private List<CarServiceModel> ary_car = new ArrayList<>();
    private CarAdapter carAdapter;

    public CarFragment(CheckListActivity activity) {
        mActivity = activity;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car, container, false);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        ListView lst_car = view.findViewById(R.id.lst_car);
        carAdapter = new CarAdapter(getContext(), ary_car);
        lst_car.setAdapter(carAdapter);

        initData();
    }

    private void initData() {
        FireManager.mCarRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CarServiceModel carServiceModel = dataSnapshot.getValue(CarServiceModel.class);
                if (ary_car.size() == 0) {
                    ary_car.add(carServiceModel);
                    carAdapter.notifyDataSetChanged();
                    return;
                }
                boolean flg = true;
                for (CarServiceModel car: ary_car) {
                    if (carServiceModel.id.equals(car.id)) {
                        flg = false;
                        break;
                    }
                }
                if (flg) {
                    ary_car.add(carServiceModel);
                    carAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CarServiceModel carServiceModel = dataSnapshot.getValue(CarServiceModel.class);
                for (int i = 0; i < ary_car.size(); i++) {
                    CarServiceModel car = ary_car.get(i);
                    if (carServiceModel.id.equals(car.id)) {
                        ary_car.set(i, carServiceModel);
                        carAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                CarServiceModel carServiceModel = dataSnapshot.getValue(CarServiceModel.class);
                for (int i = 0; i < ary_car.size(); i++) {
                    CarServiceModel car = ary_car.get(i);
                    if (carServiceModel.id.equals(car.id)) {
                        ary_car.remove(i);
                        carAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                BannerUtil.onShowErrorAlertEvent(mActivity.content, databaseError.getMessage(), 2000);
            }
        });
    }

    private void initEvent() {
        carAdapter.setCarAdapterListener(new CarAdapter.CarAdapterListener() {
            @Override
            public void editCarInfoEvent(CarServiceModel car) {
                AppUtils.showEditTextDialog(mActivity, "Auto-informatie bewerken", text -> {
                    car.name = text;
                    FireManager.editCar(car, new FireManager.FBCarCallback() {
                        @Override
                        public void onSuccessAddCar() {
                            BannerUtil.onShowSuccessAlertEvent(mActivity.content, getString(R.string.banner_edit_car),2000);
                        }

                        @Override
                        public void onFailed(String error) {
                            BannerUtil.onShowErrorAlertEvent(mActivity.content, error,2000);
                        }
                    });
                });
            }

            @Override
            public void removeCarInfoEvent(CarServiceModel car) {
                FireManager.removeCar(car, new FireManager.FBCarCallback() {
                    @Override
                    public void onSuccessAddCar() {
                        BannerUtil.onShowSuccessAlertEvent(mActivity.content, getString(R.string.banner_edit_car),2000);
                    }

                    @Override
                    public void onFailed(String error) {
                        BannerUtil.onShowErrorAlertEvent(mActivity.content, error,2000);
                    }
                });
            }
        });
    }

}
