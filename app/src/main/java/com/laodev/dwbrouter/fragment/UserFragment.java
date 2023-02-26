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
import com.laodev.dwbrouter.activity.EditUserActivity;
import com.laodev.dwbrouter.adapter.UserAdapter;
import com.laodev.dwbrouter.model.UserModel;
import com.laodev.dwbrouter.util.AppUtils;
import com.laodev.dwbrouter.util.BannerUtil;
import com.laodev.dwbrouter.util.FireManager;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {

    private CheckListActivity mActivity;
    private UserAdapter userAdapter;
    private List<UserModel> userModels = new ArrayList<>();

    public UserFragment(CheckListActivity activity) {
        mActivity = activity;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        initView(view);
        initEvent(view);
        return view;
    }

    private void initView(View view) {
        ListView lst_user = view.findViewById(R.id.lst_user);
        userAdapter = new UserAdapter(mActivity, userModels);
        lst_user.setAdapter(userAdapter);

        initData(view);
    }

    private void initData(View view) {
        FireManager.mUserRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if (userModels.size() == 0) {
                    userModels.add(userModel);
                } else {
                    boolean flag = true;
                    for (UserModel user: userModels) {
                        if (user.id.equals(userModel.id)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        userModels.add(userModel);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                for (int i = 0; i < userModels.size(); i++) {
                    UserModel user = userModels.get(i);
                    if (user.id.equals(userModel.id)) {
                        userModels.set(i, userModel);
                        userAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                for (UserModel user: userModels) {
                    if (user.id.equals(userModel.id)) {
                        userModels.remove(user);
                        userAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                BannerUtil.onShowErrorAlertEvent(view, databaseError.getMessage(), 2000);
            }
        });
    }

    private void initEvent(View view) {
        userAdapter.setUserAdapterListener(new UserAdapter.UserAdapterListener() {
            @Override
            public void editUserInfoEvent(UserModel user) {
                AppUtils.gSelUser = user;
                AppUtils.showOtherActivity(mActivity, EditUserActivity.class, 0);
            }

            @Override
            public void removeUserInfoEvent(UserModel user) {
                FireManager.removeUserInfo(user, new FireManager.FBUserCallback() {
                    @Override
                    public void onSuccess() {
                        BannerUtil.onShowSuccessAlertEvent(view, getString(R.string.banner_remove_user), 2000);
                    }

                    @Override
                    public void onFailed(String error) {
                        BannerUtil.onShowErrorAlertEvent(view, error, 2000);
                    }
                });
            }
        });
    }

}
