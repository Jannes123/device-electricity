package com.aronbordin.electricity.server;

import android.os.AsyncTask;

import com.aronbordin.electricity.MainActivity;

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
    private InputStream is;

    public SocketReceiver(InputStream is){
        super();
        this.is = is;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (true) {
            try {
                byte buffer[] = new byte[1024];
                if(is == null)
                    continue;
                is.read(buffer);
                message = new String(buffer);
                publishProgress();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
            }
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        //TODO
    }

}
