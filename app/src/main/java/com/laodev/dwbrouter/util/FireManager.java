package com.laodev.dwbrouter.util;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.laodev.dwbrouter.activity.OrderDetailActivity;
import com.laodev.dwbrouter.model.CarServiceModel;
import com.laodev.dwbrouter.model.CheckListModel;
import com.laodev.dwbrouter.model.HistoryModel;
import com.laodev.dwbrouter.model.OrderModel;
import com.laodev.dwbrouter.model.RegionModel;
import com.laodev.dwbrouter.model.UserModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FireManager {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static DatabaseReference mUserRef = database.getReference().child("user");
    public static DatabaseReference mHistoryRef = database.getReference().child("delivery");
    public static DatabaseReference mCarRef = database.getReference().child("car");
    public static DatabaseReference mRegionRef = database.getReference().child("region");

    //User Managment
    public static void getUserFromUserID(String userID, FBUserCallback callback) {
        mUserRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                callback.onSuccess(userModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed(databaseError.getMessage());
            }
        });
    }

    public static void addUserInfo(String phone, String id, FBUserCallback callback) {
        String key = mUserRef.push().getKey();
        UserModel userModel = new UserModel();
        userModel.id = id;
        userModel.phone = phone;
        userModel.regdate = TimeManager.getCurrentDate();
        userModel.type = AppConst.USER_DRIVER;
        mUserRef.child(key).setValue(userModel)
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()))
                .addOnSuccessListener(aVoid -> callback.onSuccess());
    }

    public static void updateUserInfo(UserModel userModel, FBUserCallback fbUserCallback) {
        mUserRef.child(userModel.id).setValue(userModel)
                .addOnFailureListener(e -> fbUserCallback.onFailed(e.getMessage()))
                .addOnSuccessListener(aVoid -> fbUserCallback.onSuccess());
    }

    public static void removeUserInfo(UserModel userModel, FBUserCallback callback) {
        mUserRef.child(userModel.id).removeValue()
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()))
                .addOnSuccessListener(aVoid -> callback.onSuccess());
    }

    public static void getAllUsers(FBUserCallback callback) {
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserModel> users = new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    users.add(userModel);
                }
                callback.onSuccess(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface FBUserCallback {
        default void onSuccess(UserModel user) {}
        default void onSuccess(List<UserModel> users) {}
        default void onSuccess() {}
        default void onFailed(String error) {}
    }

    //Car Managment
    public static void addNewCar(String number, FBCarCallback callback) {
        String key = mCarRef.push().getKey();
        CarServiceModel carServiceModel = new CarServiceModel();
        carServiceModel.id = key;
        carServiceModel.name = number;
        carServiceModel.regdate = TimeManager.getCurrentDate();
        mCarRef.child(key).setValue(carServiceModel)
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()))
                .addOnSuccessListener(aVoid -> callback.onSuccessAddCar());
    }

    public static void getCarByID(String id, FBCarCallback callback) {
        mCarRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CarServiceModel model = dataSnapshot.getValue(CarServiceModel.class);
                callback.onSuccess(model);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed(databaseError.getMessage());
            }
        });
    }

    public static void editCar(CarServiceModel car, FBCarCallback callback) {
        mCarRef.child(car.id).setValue(car)
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()))
                .addOnSuccessListener(aVoid -> callback.onSuccessAddCar());
    }

    public static void removeCar(CarServiceModel car, FBCarCallback callback) {
        mCarRef.child(car.id).removeValue()
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()))
                .addOnSuccessListener(aVoid -> callback.onSuccessAddCar());
    }

    public static void getAllCars(FBCarCallback callback) {
        mCarRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<CarServiceModel> cars = new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    CarServiceModel carModel = snapshot.getValue(CarServiceModel.class);
                    cars.add(carModel);
                }
                callback.onSuccessAllCar(cars);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed(databaseError.getMessage());
            }
        });
    }

    public interface FBCarCallback {
        default void onSuccessAllCar(List<CarServiceModel> carModels) {}
        default void onSuccessAddCar() {}
        default void onSuccess(CarServiceModel car) {}
        default void onFailed(String error) {}
    }

    //Delivery Managment
    public static void getDeliveryByDate(String year, String month, String date, FBDeliveryCallback callback) {
        getAllUsers(new FBUserCallback() {
            @Override
            public void onSuccess(List<UserModel> users) {
                List<CheckListModel> checkListModels = new ArrayList<>();
                for (UserModel user: users) {
                    mHistoryRef.child(user.id).child(year + "-" + month).child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                CheckListModel listModel = snapshot.getValue(CheckListModel.class);
                                checkListModels.add(listModel);
                            }
                            callback.onSuccess(checkListModels);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            callback.onSuccess(checkListModels);
                        }
                    });
                }
            }

            @Override
            public void onFailed(String error) {
                callback.onFailed(error);
            }
        });
    }

    public static void getDeliveryByDelivery(CheckListModel model, FBDeliveryCallback callback) {
        mHistoryRef.child(AppUtils.gUser.id).child(model.year + "-" + model.numMonth).child(model.day)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<CheckListModel> checkListModels = new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    CheckListModel listModel = snapshot.getValue(CheckListModel.class);
                    checkListModels.add(listModel);
                }
                callback.onSuccess(checkListModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed(databaseError.getMessage());
            }
        });
    }

    public static void addDelivery(CheckListModel model, FBDeliveryCallback callback) {
        String key = mHistoryRef.child(model.userid).child(model.year + "-" + model.numMonth).child(model.day).push().getKey();
        model.id = key;
        mHistoryRef.child(model.userid).child(model.year + "-" + model.numMonth).child(model.day).child(model.id).setValue(model)
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()))
                .addOnSuccessListener(aVoid -> callback.onSuccess());
    }

    public static void removeDelivery(CheckListModel model, FBDeliveryCallback callback) {
        mHistoryRef.child(model.userid).child(model.year + "-" + model.numMonth).child(model.day).child(model.id).removeValue()
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()))
                .addOnSuccessListener(aVoid -> callback.onSuccess());
    }

    public interface FBDeliveryCallback {
        default void onSuccess(List<CheckListModel> models) {}
        default void onSuccess() {}
        default void onFailed(String error) {}
    }

    public static void addRegionModel(RegionModel model, FBRegionCallback callback) {
        mRegionRef.child(model.title).setValue(model)
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()))
                .addOnSuccessListener(aVoid -> callback.onSuccess());
    }

    public static void removeRegionModel(RegionModel model, FBRegionCallback callback) {
        mRegionRef.child(model.title).removeValue()
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()))
                .addOnSuccessListener(aVoid -> callback.onSuccess());
    }

    public static void getAllRegions(FBRegionCallback callback) {
        mRegionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<RegionModel> regionModels = new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    RegionModel regionModel = snapshot.getValue(RegionModel.class);
                    regionModels.add(regionModel);
                }
                callback.onSuccess(regionModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed(databaseError.getMessage());
            }
        });
    }

    public interface FBRegionCallback {
        default void onSuccess() {}
        default void onSuccess(List<RegionModel> regionModels) {}
        default void onFailed(String error) {}
    }

}
