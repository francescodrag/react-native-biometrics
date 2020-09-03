package com.rnbiometrics;

import android.hardware.fingerprint.FingerprintManager;

/**
 * Created by francescodrag on 3/09/2020.
 */

public interface ReactNativeBiometricsCallback {

    void onAuthenticated(FingerprintManager.CryptoObject cryptoObject);

    void onCancel();

    void onError();
}
