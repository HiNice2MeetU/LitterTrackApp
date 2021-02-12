package dev.hiworld.littertrackingapp.Network;

import android.content.Context;
import android.util.Log;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.Certificate;

import dev.hiworld.littertrackingapp.R;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class MQSSL {
    public static SSLSocketFactory getSocketFactory(Context con) {
        try {
            // Get the BKS Keystore type required by Android
            KeyStore trustStore = KeyStore.getInstance("BKS");
            InputStream in = con.getResources().openRawResource(R.raw.mystore);
            trustStore.load(in, null);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            SSLContext sslCtx = SSLContext.getInstance("TLS");
            sslCtx.init(null, tmf.getTrustManagers(), null);
            return sslCtx.getSocketFactory();
        } catch (Exception e) {
            Log.e("MQSSL", e.toString());
            return null;
        }
    }


}
