package com.{{company_name}}.android.{{app_package_name_prefix}}.util;

import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView;
import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType;

import java.net.SocketTimeoutException;
import java.security.cert.CertificateExpiredException;

import javax.net.ssl.SSLException;

import retrofit.RetrofitError;
import retrofit.RetrofitError.Kind;
import rx.Subscriber;

import static com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType.BAD_CERT;
import static com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType.CERT_EXPIRED;
import static com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType.NETWORK;
import static com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType.NO_NETWORK;
import static com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType.SERVER;
import static com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType.UNAUTHORIZED;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static retrofit.RetrofitError.unexpectedError;

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
        RetrofitError re = e instanceof RetrofitError ? (RetrofitError) e : unexpectedError(getRequestId(), e);
        onError(re);
    }

    /**
     * Note not final just in case additional functionality is ever required in a
     * specific case
     */
    public void onError(RetrofitError re) {
        String msg = re.getUrl() + " failed";
        Exception exception = new NonCriticalException(msg, re);

        if (re.getKind() == Kind.NETWORK) {
            final ErrorType type;

            if (re.getCause() instanceof SocketTimeoutException) {
                type = NETWORK;
            } else if (re.getCause() instanceof CertificateExpiredException) {
                type = CERT_EXPIRED;
            } else if (re.getCause() instanceof SSLException) {
                type = BAD_CERT;
            } else {
                type = NO_NETWORK;
            }

            onError(type, mRequestCode);
        } else if (re.getKind() == Kind.HTTP) {
            switch (re.getResponse().getStatus()) {
                case HTTP_UNAUTHORIZED:
                    onError(UNAUTHORIZED, mRequestCode);
                    break;

                case HTTP_BAD_REQUEST:
                case HTTP_NOT_FOUND:
                case HTTP_INTERNAL_ERROR:
                default:
                    onError(SERVER, mRequestCode);
                    break;
            }
        } else {
            onError(UNEXPECTED, mRequestCode);
        }
    }

    public void onError(ErrorType errorType, int requestCode) {
        mMvpView.showError(errorType, requestCode, GENERIC_ERROR_CODE);
    }

    @Override
    public void onNext(T t) {
        // Override for implementation
    }

    private String getRequestId() {
        return mMvpView.getClass().getName() + "-" + mRequestCode;
    }
}