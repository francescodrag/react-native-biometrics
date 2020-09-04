package com.rnbiometrics;

import android.annotation.TargetApi;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;
import com.rnbiometrics.R;

/**
 * Created by francescodrag on 3/09/2020.
 */

@TargetApi(Build.VERSION_CODES.M)
public class ReactNativeBiometricsHelper extends FingerprintManager.AuthenticationCallback {

    private static final long ERROR_TIMEOUT_MILLIS = 1600;
    private static final long SUCCESS_DELAY_MILLIS = 1300;

    private final FingerprintManager fingerprintManager;
    private final ImageView icon;
    private final TextView errorTextView;
    private final ReactNativeBiometricsCallback callback;
    private CancellationSignal cancellationSignal;

    private boolean selfCancelled;

    ReactNativeBiometricsHelper(FingerprintManager fingerprintManager, ImageView icon, TextView errorTextView,
            ReactNativeBiometricsCallback callback) {
        this.fingerprintManager = fingerprintManager;
        this.icon = icon;
        this.errorTextView = errorTextView;
        this.callback = callback;
    }

    ReactNativeBiometricsHelper(FingerprintManager fingerprintManager, ReactNativeBiometricsCallback callback) {
        this.fingerprintManager = fingerprintManager;
        this.callback = callback;
        icon = null;
        errorTextView = null;
    }

    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        selfCancelled = false;

        cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0 /* flags */, this, null);
        if (icon != null) {
            icon.setImageResource(R.drawable.ic_fp_40px);
        }
    }

    public void stopListening() {
        if (cancellationSignal != null) {
            selfCancelled = true;
            cancellationSignal.cancel();
            cancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!selfCancelled) {
            showError(errString);
            if (icon != null) {
                icon.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError();
                    }
                }, ERROR_TIMEOUT_MILLIS);
            } else {
                callback.onError();
            }
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        showError(helpString);
    }

    @Override
    public void onAuthenticationFailed() {
        showError(errorTextView.getResources().getString(R.string.fingerprint_not_recognized));
    }

    @Override
    public void onAuthenticationSucceeded(final FingerprintManager.AuthenticationResult result) {
        if (null != errorTextView) {
            errorTextView.removeCallbacks(resetErrorTextRunnable);
        }
        if (icon != null) {
            icon.setImageResource(R.drawable.ic_fingerprint_success);
        }
        if (null != errorTextView) {
            errorTextView.setTextColor(errorTextView.getResources().getColor(R.color.success_color, null));
        }
        if (null != errorTextView) {
            errorTextView.setText(errorTextView.getResources().getString(R.string.fingerprint_recognized));
        }
        if (icon != null) {
            final boolean b = icon.postDelayed(new Runnable() {
                @Override
                public void run() {
                    callback.onAuthenticated(result.getCryptoObject());
                }
            }, SUCCESS_DELAY_MILLIS);
        } else {
            callback.onAuthenticated(result.getCryptoObject());
        }
    }

    private void showError(CharSequence error) {
        if (icon != null) {
            icon.setImageResource(R.drawable.ic_fingerprint_error);
        }
        if (errorTextView != null) {
            errorTextView.setText(error);
            errorTextView.setTextColor(errorTextView.getResources().getColor(R.color.warning_color, null));
            errorTextView.removeCallbacks(resetErrorTextRunnable);
            errorTextView.postDelayed(resetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
        }
    }

    private Runnable resetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            if (errorTextView != null) {
                errorTextView.setTextColor(errorTextView.getResources().getColor(R.color.hint_color, null));
                errorTextView.setText(errorTextView.getResources().getString(R.string.fingerprint_hint));
            }
            if (icon != null) {
                icon.setImageResource(R.drawable.ic_fp_40px);
            }
        }
    };
}
