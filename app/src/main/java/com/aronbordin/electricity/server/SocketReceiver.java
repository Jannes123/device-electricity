package com.aronbordin.electricity.server;

import android.os.AsyncTask;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

/**
 * This AsyncTask continuously reads the input buffer and show the chat
 * message if a message is available.
 * Created by aron on 3/5/16.
 */
public class SocketReceiver extends AsyncTask<Void, Void, Void> {

    private String message = "";
    private String content = "";
    private InputStream is;
    private SocketOperator socketOperator;

    public SocketReceiver(InputStream is, SocketOperator socketOperator){
        super();
        this.is = is;
        this.socketOperator = socketOperator;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (true) {
            try {
                byte buffer[] = new byte[2048];

                int ok = is.read(buffer);
                message = new String(buffer);
                if (message.trim().isEmpty() && !content.isEmpty()) {
                    publishProgress();
                    Thread.sleep(50);
                    continue;
                }
                content += message.trim();

                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        socketOperator.onReceiveMessage(content);
        content = "";
    }

}
