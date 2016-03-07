package com.{{company_name}}.android.{{app_package_name_prefix}}.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.{{company_name}}.android.{{app_package_name_prefix}}.R;
import com.{{company_name}}.android.{{app_package_name_prefix}}.module.AppServicesComponent;
import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.presenter.HomePresenter;
import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.presenter.HomePresenter.HomeMvpView;
import com.{{company_name}}.android.{{app_package_name_prefix}}.util.ViewUtils;

import butterknife.Bind;

public class HomeActivity extends PresentableActivity<HomePresenter> implements HomeMvpView {

    @Bind(R.id.content_wv) WebView mContentWv;
    @Bind(R.id.loading_fl) FrameLayout mLoadingFl;

    @Override
    protected HomePresenter createPresenter(AppServicesComponent component) {
        return new HomePresenter(this, component);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.act_home;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showData(String data) {
        // Display your data for the view. (Change HomeMvpView's implementation of MvpView to change data to another class)
        ViewUtils.show(mContentWv);
        ViewUtils.hide(mLoadingFl);
    }

    @Override
    public void showLoading() {
        // Display a loading state for network requests or leave empty if not applicable
        ViewUtils.show(mLoadingFl);
        ViewUtils.hide(mContentWv);
    }

    @Override
    public void showError(ErrorType errorType, int requestCode) {
        ViewUtils.hide(mLoadingFl, mContentWv);
        mDialogManager.runDefaultErrorHandling(this, errorType, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDialogManager.showGeneralErrorDialog(HomeActivity.this);
            }
        });
    }

}
