package com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.presenter;

import android.support.annotation.NonNull;

import com.{{company_name}}.android.{{app_package_name_prefix}}.module.AppServicesComponent;
import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.presenter.HomePresenter.HomeMvpView;
import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView;

public class HomePresenter extends Presenter<HomeMvpView> {

    public final static int SUBMIT_MESSAGE_REQUEST = nextId();

    public HomePresenter(@NonNull HomeMvpView view, AppServicesComponent component) {
        super(view, component);
    }

    @Override
    protected void injectInto(@NonNull AppServicesComponent component) {
        component.inject(this);
    }

    public interface HomeMvpView extends MvpView<String> {

    }

}
