package com.aronbordin.electricity.server;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

/**
 * This AsyncTask continuously reads the input buffer and show the chat
 * message if a message is available.
 * Created by aron on 3/5/16.
 */
public class SocketReceiver extends AsyncTask<Void, Void, Void> {

    private String message = "";
    private BufferedReader in;
    private SocketOperator socketOperator;

    public SocketReceiver(InputStream is, SocketOperator socketOperator){
        super();
        in = new BufferedReader(new InputStreamReader(is));
        this.socketOperator = socketOperator;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (true) {
            StringBuilder sb = new StringBuilder();
            try {
                String line;
                while((line = in.readLine()) != null) {
                    sb.append(line);
                }
                message = sb.toString();
                if (!message.isEmpty())
                    publishProgress();
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {

        socketOperator.onReceiveMessage(message);
        message = "";
    }

}
