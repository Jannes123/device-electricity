package com.aronbordin.electricity.server;

import android.os.AsyncTask;

import com.aronbordin.electricity.MainActivity;
import com.aronbordin.electricity.R;
import com.aronbordin.electricity.utils.Utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.EventListener;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This AsyncTask create the connection with the server and initialize the
 * chat senders and receivers.
 *
 * Created by aron on 3/5/16.
 */
public class SocketOperator extends AsyncTask<Void, Void, Void> {

    private MainActivity mainActivity;
    private InputStream is;
    private OutputStream os;
    private SSLSocket sslSocket;
    private SocketOperatorInterface listener;
    private boolean connected = false;

    public interface SocketOperatorInterface extends EventListener {
        void onConnect();
        void onError(Exception e);
    }

    public SocketOperator(MainActivity mainActivity){
        super();
        this.mainActivity = mainActivity;
    }

    public void setOnSocketOperatorListener(SocketOperatorInterface listener){
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            SSLContext context;
            KeyManagerFactory kmf;
            KeyStore ks;

            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            context = SSLContext.getInstance("TLS");
            kmf = KeyManagerFactory.getInstance("X509");
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(mainActivity.getResources().openRawResource(R.raw.clienttruststore),
                    Utils.CERT.toCharArray());
            kmf.init(ks, Utils.CERT.toCharArray());
            context.init(kmf.getKeyManagers(), trustAllCerts, null);
            SSLSocketFactory sslsocketfactory = context.getSocketFactory();

            sslSocket = (SSLSocket) sslsocketfactory.createSocket(Utils.SERVER_HOST, Utils.SERVER_PORT);
            sslSocket.setSoTimeout(60000);
            sslSocket.startHandshake();

            os = sslSocket.getOutputStream();
            is = sslSocket.getInputStream();

            connected = true;
        } catch (Exception e) {
            System.err.println("Faild to connect server: " + Utils.SERVER_HOST);
            e.printStackTrace();
            if(listener != null){
                listener.onError(e);
            }
        }
        return null;
    }

    /**
     * Following method is executed at the end of doInBackground method.
     */
    @Override
    protected void onPostExecute(Void result) {
        if (connected) {
            // Initialize receiver AsyncTask.
            SocketReceiver receiver = new SocketReceiver(is);
            receiver.execute();
            if (listener != null)
                listener.onConnect();
        }
    }
}

