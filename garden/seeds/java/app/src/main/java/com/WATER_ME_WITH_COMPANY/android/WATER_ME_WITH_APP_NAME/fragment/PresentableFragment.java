package com.{{company_name}}.android.{{app_package_name_prefix}}.fragment;

import android.os.Bundle;

import com.{{company_name}}.android.{{app_package_name_prefix}}.module.AppServicesComponent;
import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.presenter.Presenter;

/**
 * Base class for all fragments which have a corresponding {@link Presenter} object
 *
 * @param <T> The type of presenter the fragment uses
 */
public abstract class PresentableFragment<T extends Presenter> extends BaseFragment {

    protected T mPresenter;

    protected abstract T createPresenter(AppServicesComponent servicesComponent, Bundle savedInstanceState);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter(mApp.getAppServicesComponent(), savedInstanceState);
        if (mPresenter == null) {
            throw new IllegalStateException("presenter == null");
        }
        mPresenter.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onPause() {
        mPresenter.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void onDestroy() {
        mPresenter.onDestroy();
        mPresenter = null;
        super.onDestroy();
    }

}