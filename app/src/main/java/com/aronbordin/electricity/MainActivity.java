package com.aronbordin.electricity;

import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.aronbordin.electricity.server.SocketOperator;

public class MainActivity extends AppCompatActivity implements SocketOperator.SocketOperatorInterface{

    private SocketOperator socketOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        socketOperator = new SocketOperator(this);
        socketOperator.setOnSocketOperatorListener(this);
        socketOperator.execute();
    }

    @Override
    public void onConnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View content_loading = findViewById(R.id.content_loading);
                content_loading.setVisibility(View.GONE);
                if(BuildConfig.DEBUG) {
                    Toast.makeText(MainActivity.this, "Connected with the server", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onError(Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, R.string.internal_error, Toast.LENGTH_SHORT).show();
            }
        });
        e.printStackTrace();
    }
}
