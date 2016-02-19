package com.{{company_name}}.android.{{app_package_name_prefix}}.util.manager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.{{company_name}}.android.{{app_package_name_prefix}}.{{app_class_prefix}}App;
import com.{{company_name}}.android.{{app_package_name_prefix}}.R;
import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView;
import com.{{company_name}}.android.{{app_package_name_prefix}}.util.IntentUtils;

import java.lang.ref.WeakReference;

/**
 * For all Dialog Management, ensures styling and that there is only one dialog shown at a time
 */
public class DialogManager {

    public static final int NO_FIELD = 0;

    protected WeakReference<AlertDialog> mCurrentAlertDialogRef;

    /**
     * Runs default behaviour for the provided error type, returns true if handled
     */
    public boolean runDefaultErrorHandling(@Nullable Context context, @NonNull MvpView.ErrorType errorType, @Nullable OnClickListener onRetryListener) {
        if (errorType == null) {
            throw new IllegalArgumentException("errorType cannot be null");
        }

        switch (errorType) {
            case NETWORK:
                showNetworkErrorDialog(context, onRetryListener);
                break;

            case NO_NETWORK:
                showNoNetworkErrorDialog(context, onRetryListener);
                break;

            case SERVER:
                showGeneralErrorDialog(context);
                break;

            case CERT_EXPIRED:
                showForceUpdateDialog(context);
                break;

            case BAD_CERT:
                showBadCertDialog(context);
                break;

            case VIEW_SPECIFIC:
                return false;

            default:
                // Fall through
        }
        return true;
    }

    public AlertDialog showNetworkErrorDialog(@Nullable Context context, @Nullable OnClickListener onRetryListener) {
        return showAlertDialog(
                context,
                R.string.dialog_title_error_network,
                R.string.dialog_body_error_network,
                R.string.dialog_action_retry,
                R.string.dialog_action_cancel,
                onRetryListener,
                null,
                NO_FIELD
        );
    }

    public AlertDialog showNoNetworkErrorDialog(@Nullable Context context, @Nullable OnClickListener onRetryListener) {
        return showAlertDialog(
                context,
                R.string.dialog_title_error_no_network,
                R.string.dialog_body_error_no_network,
                R.string.dialog_action_retry,
                R.string.dialog_action_cancel,
                onRetryListener,
                null,
                NO_FIELD
        );
    }

    public AlertDialog showGeneralErrorDialog(@Nullable Context context) {
        return showAlertDialog(
                context,
                R.string.dialog_title_error_general,
                R.string.dialog_body_error_general,
                NO_FIELD,
                R.string.dialog_action_close,
                null,
                null,
                NO_FIELD
        );
    }

    public AlertDialog showGeneralErrorDialog(@Nullable Context context, @Nullable OnClickListener onCloseListener) {
        return showAlertDialog(
                context,
                R.string.dialog_title_error_general,
                R.string.dialog_body_error_general,
                NO_FIELD,
                R.string.dialog_action_close,
                null,
                onCloseListener,
                NO_FIELD
        );
    }

    public AlertDialog showBadCertDialog(@Nullable final Context context) {
        return showAlertDialog(
                context,
                R.string.dialog_title_error_bad_cert,
                R.string.dialog_body_error_bad_cert,
                NO_FIELD,
                R.string.dialog_action_close,
                null,
                null,
                NO_FIELD);
    }

    public AlertDialog showForceUpdateDialog(@Nullable final Context context) {
        if (context == null) {
            return null;
        }
        return showAlertDialog(
                context,
                R.string.dialog_title_error_out_of_date,
                R.string.dialog_body_error_out_of_date,
                R.string.dialog_action_update,
                R.string.dialog_action_close,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(IntentUtils.getPlayStoreIntent());
                    }
                },
                null,
                NO_FIELD
        );
    }

    public AlertDialog showAlertDialog(@Nullable Context context, @StringRes int title, @StringRes int message, @StringRes int positive, @StringRes int negative, @Nullable OnClickListener positiveListener, @Nullable OnClickListener negativeListener, @LayoutRes int viewResId) {
        if (context == null) {
            return null;
        }
        return showAlertDialog(
                context,
                title == NO_FIELD ? null : context.getString(title),
                message == NO_FIELD ? null : context.getString(message),
                positive == NO_FIELD ? null : context.getString(positive),
                negative == NO_FIELD ? null : context.getString(negative),
                positiveListener,
                negativeListener,
                viewResId
        );
    }

    public AlertDialog showAlertDialog(@Nullable Context context, @Nullable String title, @Nullable String message, @Nullable String positive, @Nullable String negative, @Nullable OnClickListener positiveListener, @Nullable OnClickListener negativeListener, @LayoutRes int viewResId) {
        AlertDialog currentAlertDialog = mCurrentAlertDialogRef == null ? null : mCurrentAlertDialogRef.get();

        if (context != null && (currentAlertDialog == null || !currentAlertDialog.isShowing())) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.General_AlertDialog);

            if (!TextUtils.isEmpty(title)) {
                builder.setTitle(title);
            }

            if (!TextUtils.isEmpty(message)) {
                builder.setMessage(message);
            }

            if (!TextUtils.isEmpty(positive)) {
                builder.setPositiveButton(positive, positiveListener);
            }

            if (!TextUtils.isEmpty(negative)) {
                builder.setNegativeButton(negative, negativeListener);
            }

            if (viewResId != NO_FIELD) {
                builder.setView(viewResId);
            }

            currentAlertDialog = builder.create();
            currentAlertDialog.show();
            mCurrentAlertDialogRef = new WeakReference<>(currentAlertDialog);
            return currentAlertDialog;
        }
        return null;
    }

}
