package com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view;

import android.content.Context;

/**
 *
 */
public interface MvpView<T> {

    enum ErrorType {
        SERVER,
        NETWORK,
        NO_NETWORK,
        BAD_CERT,
        CERT_EXPIRED,
        UNAUTHORIZED,
        UNEXPECTED,
        VIEW_SPECIFIC;

        public boolean isCriticalError() {
            return this == UNAUTHORIZED;
        }
    }

    Context getContext();

    void showLoading();

    void showData(T data);

    void showError(ErrorType errorType, int requestCode);
}
