package com.rnbiometrics;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.rnbiometrics.R;

/**
 * Created by francescodrag on 3/09/2020.
 */

@TargetApi(Build.VERSION_CODES.M)
public class ReactNativeBiometricsDialog extends DialogFragment implements ReactNativeBiometricsCallback {

    protected String title;
    protected String cancel;
    protected String description;
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

    public void init(String title, String description, String cancel, FingerprintManager.CryptoObject cryptoObject,
            ReactNativeBiometricsCallback callback) {
        this.title = title;
        this.description = description;
        this.cancel = cancel;
        this.cryptoObject = cryptoObject;
        this.biometricAuthCallback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BiometricsDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
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

        // promptDescription = (TextView)
        // view.findViewById(R.id.fingerprint_description);
        // promptDescription.setText(description);

        biometricAuthenticationHelper = new ReactNativeBiometricsHelper(
                activity.getSystemService(FingerprintManager.class),
                (ImageView) view.findViewById(R.id.fingerprint_icon),
                (TextView) view.findViewById(R.id.fingerprint_status), this);

        return view;
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

    @Override
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
