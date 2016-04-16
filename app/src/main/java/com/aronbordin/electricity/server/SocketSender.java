package com.aronbordin.electricity.server;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * This AsyncTask sends the chat message through the output stream.
 * Created by aron on 3/5/16.
 */
public class SocketSender extends AsyncTask<Void, Void, Void> {

    private String message;
    private PrintWriter out;
    private SocketOperator socketOperator;

    public SocketSender(String message, OutputStream os, SocketOperator socketOperator){
        this.message = message;
        out = new PrintWriter(os);
        this.socketOperator = socketOperator;
    }

    @Override
    protected Void doInBackground(Void... params) {
        out.write(message);
        out.flush();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        socketOperator.onMessageSent();
    }
}