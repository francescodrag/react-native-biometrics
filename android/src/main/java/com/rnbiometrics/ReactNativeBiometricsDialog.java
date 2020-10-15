package com.rnbiometrics;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by francescodrag on 3/09/2020.
 */

@TargetApi(Build.VERSION_CODES.M)
public class ReactNativeBiometricsDialog extends DialogFragment implements ReactNativeBiometricsCallback {

    protected String title;
    protected String cancel;
    protected String description;
    protected boolean promptVisible;
    protected FingerprintManager.CryptoObject cryptoObject;
    protected ReactNativeBiometricsCallback biometricAuthCallback;

    protected ReactNativeBiometricsHelper biometricAuthenticationHelper;
    protected Activity activity;
    protected Button cancelButton;
    protected TextView promptDescription;

    public void init(String title, FingerprintManager.CryptoObject cryptoObject,
            ReactNativeBiometricsCallback callback) {
        this.title = title;
        this.cryptoObject = cryptoObject;
        this.biometricAuthCallback = callback;
    }

    public void init(String title, String description, String cancel, boolean promptVisible,
            FingerprintManager.CryptoObject cryptoObject, ReactNativeBiometricsCallback callback) {
        Log.e("DEBUG:: init", Boolean.toString(promptVisible));
        this.title = title;
        this.description = description;
        this.cancel = cancel;
        this.promptVisible = promptVisible;
        this.cryptoObject = cryptoObject;
        this.biometricAuthCallback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (promptVisible) {
            Log.e("DEBUG:: style", "visible");
            setStyle(DialogFragment.STYLE_NORMAL, R.style.BiometricsDialog);
        } else {
            Log.e("DEBUG:: style", "invisible");
            setStyle(DialogFragment.STYLE_NO_INPUT, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (promptVisible) {
            getDialog().setTitle(title);
            View view = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
            cancelButton = (Button) view.findViewById(R.id.cancel_button);
            cancelButton.setText(cancel);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismissAllowingStateLoss();
                    onCancel();
                }
            });

            promptDescription = (TextView) view.findViewById(R.id.fingerprint_description);
            promptDescription.setText(description);

            biometricAuthenticationHelper = new ReactNativeBiometricsHelper(
                    activity.getSystemService(FingerprintManager.class),
                    (ImageView) view.findViewById(R.id.fingerprint_icon),
                    (TextView) view.findViewById(R.id.fingerprint_status), this);

            return view;
        } else {
            View view = inflater.inflate(R.layout.transparent_dialog, container, false);
            biometricAuthenticationHelper = new ReactNativeBiometricsHelper(
                    activity.getSystemService(FingerprintManager.class), this);
            return view;
        }
    }

    // DialogFragment lifecycle methods
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        biometricAuthenticationHelper.stopListening();
        dismissAllowingStateLoss();
        onCancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        biometricAuthenticationHelper.startListening(cryptoObject);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        onCancel();
    }

    // ReactNativeBiometricsCallback methods
    @Override
    public void onAuthenticated(FingerprintManager.CryptoObject cryptoObject) {
        dismissAllowingStateLoss();
        if (biometricAuthCallback != null) {
            biometricAuthCallback.onAuthenticated(cryptoObject);
        }
    }

    public void onCancel() {
        if (biometricAuthCallback != null) {
            biometricAuthCallback.onCancel();
        }
    }

    @Override
    public void onError() {
        dismissAllowingStateLoss();
        if (biometricAuthCallback != null) {
            biometricAuthCallback.onError();
        }
    }
}
