package com.{{company_name}}.android.{{app_package_name_prefix}}.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.{{company_name}}.android.{{app_package_name_prefix}}.module.AppServicesComponent;
import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.presenter.Presenter;

/**
 * Base class for all activities which have a corresponding {@link Presenter} object
 *
 * @param <T> The type of presenter the activity uses
 */
abstract class PresentableActivity<T extends Presenter> extends BaseActivity {

    protected T mPresenter;

    /**
     * Return a presenter to use for this activity. This will only be called once per activity,
     * so there is no need to cache the results
     *
     * @param component Used for injecting dependencies
     * @return A {@link Presenter} instance to use with this activity
     */
    @NonNull
    protected abstract T createPresenter(AppServicesComponent component);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter(app.getAppServicesComponent());
        if (mPresenter == null) {
            throw new IllegalStateException("presenter == null");
        }

        mPresenter.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        mPresenter.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        mPresenter = null;
        super.onDestroy();
    }
}