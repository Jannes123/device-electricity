package com.aronbordin.electricity.server;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This AsyncTask sends the chat message through the output stream.
 * Created by aron on 3/5/16.
 */
public class SocketSender extends AsyncTask<Void, Void, Void> {

    private String message;
    private OutputStream os;
    private SocketOperator socketOperator;

    public SocketSender(String message, OutputStream os, SocketOperator socketOperator){
        this.message = message;
        this.os = os;
        this.socketOperator = socketOperator;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            os.write(message.getBytes("US-ASCII"));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        socketOperator.onMessageSent();
    }
}