package com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import java.util.concurrent.atomic.AtomicInteger;

import com.{{company_name}}.android.{{app_package_name_prefix}}.activity.BaseActivity;
import com.{{company_name}}.android.{{app_package_name_prefix}}.fragment.BaseFragment;
import com.{{company_name}}.android.{{app_package_name_prefix}}.util.RxUtils;
import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView;
import com.{{company_name}}.android.{{app_package_name_prefix}}.module.AppServicesComponent;
import com.{{company_name}}.android.{{app_package_name_prefix}}.util.BaseSubscriber;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Base class for all presenters (In the Model-View-Presenter architecture) within the application
 */
public abstract class Presenter<V extends MvpView> {

    private static final AtomicInteger NEXT_ID = new AtomicInteger();

    private final V mView;

    public Presenter(@NonNull V view, AppServicesComponent component) {
        mView = view;
        if (mView == null) {
            throw new IllegalArgumentException("view != null");
        } else if (component == null) {
            throw new IllegalArgumentException("component cannot be null");
        }
        injectInto(component);
    }

    /**
     * Because of code generation Dagger 2 requires injection at the class level,
     * super classes will not work. We could get around this using reflection but that
     * defeats the purpose of Dagger 2 over Dagger 1. Instead use an abstract method
     * to guarantee subclass injection.
     */
    protected abstract void injectInto(@NonNull AppServicesComponent component);

    protected V getView() {
        return mView;
    }

    protected Context getContext() {
        return mView.getContext();
    }

    public static int nextId() {
        return NEXT_ID.incrementAndGet();
    }

    public void onCreate(Bundle savedInstanceState) {

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {

    }

    public void onDestroy() {

    }

    public void onStart() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    protected <R> Subscription bind(Observable<R> observable, BaseSubscriber<? super R> observer) {
        final Observable<R> sourceObservable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        final Observable<R> boundObservable;
        if (mView instanceof BaseFragment) {
            boundObservable = RxUtils.bindFragment((BaseFragment) mView, sourceObservable);
        } else if (getContext() instanceof BaseActivity) {
            boundObservable = RxUtils.bindActivity((BaseActivity) getContext(), sourceObservable);
        } else {
            boundObservable = sourceObservable;
        }

        return boundObservable.subscribe(observer);
    }
}
