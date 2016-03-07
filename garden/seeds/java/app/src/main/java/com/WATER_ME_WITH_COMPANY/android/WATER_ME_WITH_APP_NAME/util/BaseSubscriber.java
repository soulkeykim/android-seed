package com.{{company_name}}.android.{{app_package_name_prefix}}.util;

import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView;
import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType;

import java.net.SocketTimeoutException;
import java.security.cert.CertificateExpiredException;

import javax.net.ssl.SSLException;

import rx.Subscriber;
import java.io.IOException;

import static com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType.BAD_CERT;
import static com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType.CERT_EXPIRED;
import static com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType.NETWORK;
import static com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType.NO_NETWORK;
import static com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType.SERVER;


/**
 * Simple pattern for RX {@link Subscriber}
 * <p/>
 * With default error handling capabilities
 */
public class BaseSubscriber<T> extends Subscriber<T> {

    protected MvpView mMvpView;
    protected int mRequestCode;

    public BaseSubscriber(MvpView mvpView, int requestCode) {
        mMvpView = mvpView;
        mRequestCode = requestCode;
    }

    @Override
    public void onCompleted() {
        // Override for implementation
    }

    /**
     * Note not final just in case additional functionality is ever required in a
     * specific case
     */
    @Override
    public void onError(Throwable e) {
        final ErrorType type;
        if (e instanceof SocketTimeoutException) {
            type = NETWORK;
        } else if (e instanceof CertificateExpiredException) {
            type = CERT_EXPIRED;
        } else if (e instanceof SSLException) {
            type = BAD_CERT;
        } else if (e instanceof IOException) {
            type = NO_NETWORK;
        } else {
            type = SERVER;
        }
        mMvpView.showError(type, mRequestCode);
    }

    @Override
    public void onNext(T t) {
        // Override for implementation
    }

    private String getRequestId() {
        return mMvpView.getClass().getName() + "-" + mRequestCode;
    }
}