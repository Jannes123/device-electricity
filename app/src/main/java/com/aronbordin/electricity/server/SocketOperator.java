package com.aronbordin.electricity.server;

import android.os.AsyncTask;
import android.os.Build;

import com.aronbordin.electricity.MainActivity;
import com.aronbordin.electricity.R;
import com.aronbordin.electricity.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.EventListener;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
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
    private boolean connected;
    private SocketReceiver socketReceiver;

    public interface SocketOperatorInterface extends EventListener {
        void onConnect();
        void onError(Exception e);
        void onReceiveMessage(String message);
        void onMessageSent();
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
            sslSocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
                @Override
                public void handshakeCompleted(HandshakeCompletedEvent event) {
                    connected = true;
                    try {
                        os = sslSocket.getOutputStream();
                        is = sslSocket.getInputStream();

                        runReceiver();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (listener != null)
                        listener.onConnect();
                }
            });

            sslSocket.startHandshake();
        } catch (Exception e) {
            System.err.println("Failed to connect server: " + Utils.SERVER_HOST);
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

    }

    public void runReceiver(){
        // Initialize receiver AsyncTask.
        socketReceiver = new SocketReceiver(is, SocketOperator.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            socketReceiver.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            socketReceiver.execute();
        }
    }

    public void onReceiveMessage(String message){
        if(listener != null){
            listener.onReceiveMessage(message);
        }
    }

    public void onMessageSent(){
        if(listener != null){
            listener.onMessageSent();
        }
    }

    public void sendMessage(String message){
        String content = message.trim();
        String msg = String.format("%06d%s", content.length(), content);
        SocketSender sender = new SocketSender(msg, os, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            sender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            sender.execute();
        }
    }

    public void close(){
        try {
            sslSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

