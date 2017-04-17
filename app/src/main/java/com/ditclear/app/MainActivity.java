package com.ditclear.app;

import com.ditclear.app.base.BaseBindActivity;
import com.ditclear.app.databinding.ActivityMainBinding;

public class MainActivity extends BaseBindActivity<ActivityMainBinding> {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

        setShowBackNavigation(false);
        mBinding.toolbarLayout.titleTv.setText("main");
        mBinding.contentTv.setText("this is bindText");
    }

    @Override
    protected void initEventHandler() {

    }

    @Override
    protected void loadData(boolean isRefresh) {

    }
}
