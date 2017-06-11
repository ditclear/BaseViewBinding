package com.ditclear.app;

import android.content.Intent;
import android.view.View;

import com.ditclear.app.base.BaseBindActivity;
import com.ditclear.app.databinding.ActivityMainBinding;
import com.ditclear.app.validate.ValidateSampleActivity;

public class MainActivity extends BaseBindActivity<ActivityMainBinding> {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

        setShowBackNavigation(false);
        mBinding.toolbarLayout.titleTv.setText("MAIN");
        mBinding.contentTv.setText("this is bindText");
    }

    @Override
    protected void initEventHandler() {

        mBinding.validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ValidateSampleActivity.class));
            }
        });
    }

    @Override
    protected void loadData(boolean isRefresh) {

    }
}
