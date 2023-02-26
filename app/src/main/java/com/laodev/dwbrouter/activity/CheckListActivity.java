package com.laodev.dwbrouter.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.dialog.RegionDialog;
import com.laodev.dwbrouter.fragment.CarFragment;
import com.laodev.dwbrouter.fragment.RegionFragment;
import com.laodev.dwbrouter.fragment.UserFragment;
import com.laodev.dwbrouter.util.AppUtils;
import com.laodev.dwbrouter.util.BannerUtil;
import com.laodev.dwbrouter.util.FireManager;

public class CheckListActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab_add;
    public View content;

    private int index = 0;

    class ManagerAdapter extends FragmentPagerAdapter {

        private String[] titles = {"Car", "User", "Region"};

        public ManagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CarFragment(CheckListActivity.this);
                case 1:
                    return new UserFragment(CheckListActivity.this);
                case 2:
                    return new RegionFragment(CheckListActivity.this);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        setToolbar();

        initView();
        initEvent();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }
    }

    private void initView() {
        content = findViewById(R.id.content);

        tabLayout = findViewById(R.id.tab_manager);
        viewPager = findViewById(R.id.vpr_manager);
        viewPager.setAdapter(new ManagerAdapter(getSupportFragmentManager()));
        tabLayout.post(() -> tabLayout.setupWithViewPager(viewPager));
        fab_add = findViewById(R.id.fab_add);
        fab_add.show();
    }

    private void initEvent() {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
                switch (position) {
                    case 0:
                        fab_add.show();
                        break;
                    case 1:
                        fab_add.hide();
                        break;
                    case 2:
                        fab_add.show();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        fab_add.setOnClickListener(view -> {
            if (index == 0) {
                AppUtils.showEditTextDialog(CheckListActivity.this, getString(R.string.input_car_number), text -> FireManager.addNewCar(text, new FireManager.FBCarCallback() {
                    @Override
                    public void onSuccessAddCar() {
                        BannerUtil.onShowSuccessAlertEvent(content, getString(R.string.banner_add_car),2000);
                    }

                    @Override
                    public void onFailed(String error) {
                        BannerUtil.onShowErrorAlertEvent(content, error,2000);
                    }
                }));
            } else {
                RegionDialog regionDialog = new RegionDialog(this);
                regionDialog.initData(content, null);
                regionDialog.show();
            }
        });
    }

}
