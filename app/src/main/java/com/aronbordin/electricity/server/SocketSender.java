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
    public SocketSender(String message, OutputStream os){
        this.message = message;
        this.os = os;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            os.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        //TODO
    }
}