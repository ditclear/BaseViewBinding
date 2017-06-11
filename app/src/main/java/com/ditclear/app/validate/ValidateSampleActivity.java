package com.ditclear.app.validate;

import android.support.design.widget.Snackbar;

import com.ditclear.app.R;
import com.ditclear.app.base.BaseBindActivity;
import com.ditclear.app.databinding.ValidateSampleActivityBinding;

/**
 * 页面描述：表单验证
 *
 * Created by ditclear on 2017/6/11.
 */

public class ValidateSampleActivity extends
        BaseBindActivity<ValidateSampleActivityBinding> implements
        ValidateViewModel.CallBack {

    ValidateViewModel mViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.validate_sample_activity;
    }

    @Override
    protected void initView() {
        mViewModel = new ValidateViewModel();
        mViewModel.setCallBack(this);
        mBinding.setVm(mViewModel);
    }

    @Override
    protected void initEventHandler() {
    }

    @Override
    protected void loadData(boolean isRefresh) {

    }

    @Override
    public void signInSuccess() {
        Snackbar.make(mBinding.getRoot(), "Sign In Success", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void signInFailure() {
        Snackbar.make(mBinding.getRoot(), "Sign In Failure", Snackbar.LENGTH_SHORT).show();

    }
}
