package com.laodev.dwbrouter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.model.CarServiceModel;
import com.laodev.dwbrouter.model.UserModel;
import com.laodev.dwbrouter.util.AppConst;
import com.laodev.dwbrouter.util.FireManager;

import java.util.List;

public class UserAdapter extends BaseAdapter {

    private Context context;
    private List<UserModel> mUsers;
    private UserAdapterListener userAdapterListener;

    public UserAdapter(Context context, List<UserModel> users) {
        this.context = context;
        mUsers = users;
    }

    @Override
    public int getCount() {
        return mUsers.size();
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
        UserModel userModel = mUsers.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_user, null);

        TextView txt_name = view.findViewById(R.id.txt_user_name);
        TextView txt_phone = view.findViewById(R.id.txt_user_phone);
        TextView txt_regdate = view.findViewById(R.id.txt_user_regdate);
        TextView txt_type = view.findViewById(R.id.txt_user_type);
        ImageView img_edit = view.findViewById(R.id.img_car_edit);
        ImageView img_remove = view.findViewById(R.id.img_car_remove);

        if (userModel.name != null && userModel.name.length() > 0) {
            txt_name.setText(userModel.name);
        } else {
            txt_name.setText("Nieuwe gebruiker registreren");
        }
        txt_phone.setText(userModel.phone);
        txt_regdate.setText(userModel.regdate);
        if (userModel.type == null || userModel.type.length() == 0) {
            userModel.type = AppConst.USER_DRIVER;
            FireManager.updateUserInfo(userModel, new FireManager.FBUserCallback() {
                @Override
                public void onSuccess() { }

                @Override
                public void onFailed(String error) { }
            });
        }
        txt_type.setText(userModel.type.toUpperCase());

        img_edit.setOnClickListener(view1 -> userAdapterListener.editUserInfoEvent(userModel));
        img_remove.setOnClickListener(view12 -> userAdapterListener.removeUserInfoEvent(userModel));

        return view;
    }

    public void setUserAdapterListener(UserAdapterListener userAdapterListener) {
        this.userAdapterListener = userAdapterListener;
    }

    public interface UserAdapterListener {
        void editUserInfoEvent(UserModel user);
        void removeUserInfoEvent(UserModel user);
    }


}
