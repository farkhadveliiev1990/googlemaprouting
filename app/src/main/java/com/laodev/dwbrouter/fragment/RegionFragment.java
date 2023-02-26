package com.laodev.dwbrouter.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.annotations.NotNull;
import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.activity.CheckListActivity;
import com.laodev.dwbrouter.model.RegionModel;
import com.laodev.dwbrouter.ui.RegionModelUI;
import com.laodev.dwbrouter.util.AppConst;
import com.laodev.dwbrouter.util.FireManager;

public class RegionFragment extends Fragment {

    private CheckListActivity mActivity;
    private LinearLayout llt_region;

    public RegionFragment(CheckListActivity activity) {
        mActivity = activity;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_region, container, false);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        llt_region = view.findViewById(R.id.llt_region);
        initData();
    }

    private void initData() {
        AppConst.gAllRegions.clear();
        FireManager.mRegionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                RegionModel regionModel = dataSnapshot.getValue(RegionModel.class);
                if (AppConst.gAllRegions.size() == 0) {
                    AppConst.gAllRegions.add(regionModel);
                } else {
                    boolean flag = true;
                    for (RegionModel model: AppConst.gAllRegions) {
                        if (model.title.equals(regionModel.title)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        AppConst.gAllRegions.add(regionModel);
                    }
                }
                refreshRegionView();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                RegionModel regionModel = dataSnapshot.getValue(RegionModel.class);
                for (int i = 0; i < AppConst.gAllRegions.size(); i++) {
                    RegionModel model = AppConst.gAllRegions.get(i);
                    if (model.title.equals(regionModel.title)) {
                        AppConst.gAllRegions.set(i, regionModel);
                        break;
                    }
                }
                refreshRegionView();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                RegionModel regionModel = dataSnapshot.getValue(RegionModel.class);
                for (int i = 0; i < AppConst.gAllRegions.size(); i++) {
                    RegionModel model = AppConst.gAllRegions.get(i);
                    if (model.title.equals(regionModel.title)) {
                        AppConst.gAllRegions.remove(i);
                        break;
                    }
                }
                refreshRegionView();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void refreshRegionView() {
        llt_region.removeAllViews();
        for (RegionModel regionModel: AppConst.gAllRegions) {
            RegionModelUI regionModelUI = new RegionModelUI(getContext());
            regionModelUI.setRegionModel(mActivity.content, regionModel);
            llt_region.addView(regionModelUI);
        }
    }

    private void initEvent() {

    }

}
