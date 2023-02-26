package com.laodev.dwbrouter.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.model.UserModel;
import com.laodev.dwbrouter.util.AppUtils;
import com.laodev.dwbrouter.util.FireManager;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private int counter = 60;
    private String verificationId = "";
    private FirebaseAuth mAuth;

    private TextView lbl_counter;
    private EditText txt_phone, txt_code;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NotNull PhoneAuthCredential phoneAuthCredential) {
            mAuth.signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            getUserInfo(user);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            verificationId = s;

            if (lbl_counter.getVisibility() == View.GONE) {
                counter = 60;
                lbl_counter.setVisibility(View.VISIBLE);
                onCalcDownCounter();
            }
        }
    };


    public void onClickLogin(View view) {
        String str_code = txt_code.getText().toString();
        if (str_code.length() == 0) {
            Toast.makeText(this, getString(R.string.alert_verify), Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, str_code);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information

                        FirebaseUser user = task.getResult().getUser();
                        getUserInfo(user);
                    } else {
                        // Sign in failed, display a message and update the UI
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                });
    }

    public void onClickVerifyUB(View view) {
        String str_phone = txt_phone.getText().toString();
        if (str_phone.length() == 0) {
            Toast.makeText(this, getString(R.string.alert_phone), Toast.LENGTH_SHORT).show();
            return;
        }

        if (str_phone.equals("2096227257")) {
            str_phone = "+8562096227257";
        } else {
            str_phone = "+31" + str_phone.substring(1);
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                str_phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppUtils.initUIActivity(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();
        if (fbUser != null) {
            getUserInfo(fbUser);
            return;
        }
        initWithView();
    }

    private void initWithView() {
        lbl_counter = findViewById(R.id.lbl_login_counter);
        lbl_counter.setVisibility(View.GONE);

        txt_phone = findViewById(R.id.txt_login_phone);
        txt_code = findViewById(R.id.txt_login_code);
    }

    private void onCalcDownCounter() {
        new Handler().postDelayed(this::onShowCounter, 1000);
    }

    private void onShowCounter() {
        counter--;
        if (counter == 0) {
            lbl_counter.setVisibility(View.GONE);
            return;
        }
        lbl_counter.setText(counter + " s");
        onCalcDownCounter();
    }

    private void getUserInfo(FirebaseUser fbUser) {
        ProgressDialog dialog = ProgressDialog.show(this, "", getString(R.string.alert_connect));
        dialog.show();

        FireManager.getUserFromUserID(fbUser.getUid(), new FireManager.FBUserCallback() {
            @Override
            public void onSuccess(UserModel user) {
                dialog.dismiss();
                if (AppUtils.gUser == null) {
                    FireManager.addUserInfo(fbUser.getPhoneNumber(), fbUser.getUid(), new FireManager.FBUserCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(LoginActivity.this, "Wachten op managerovereenkomst.", Toast.LENGTH_SHORT).show();
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }

                        @Override
                        public void onFailed(String error) {
                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    AppUtils.gUser = user;
                    if (AppUtils.gUser.name == null && AppUtils.gUser.name.length() == 0) {
                        Toast.makeText(LoginActivity.this, "Wachten op managerovereenkomst.", Toast.LENGTH_SHORT).show();
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                        return;
                    }
                    AppUtils.showOtherActivity(LoginActivity.this, MainActivity.class, 0);
                }
            }

            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
