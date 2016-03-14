package com.aronbordin.electricity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.aronbordin.electricity.model.XMLWriter;
import com.aronbordin.electricity.server.SocketOperator;


public class MainActivity extends AppCompatActivity implements
        SocketOperator.SocketOperatorInterface,
        View.OnClickListener {

    private SocketOperator socketOperator;
    private XMLWriter xmlWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        socketOperator = new SocketOperator(this);
        socketOperator.setOnSocketOperatorListener(this);
        socketOperator.execute();
        View content_main = findViewById(R.id.content_main);
        content_main.setVisibility(View.GONE);

        Button btn = (Button) findViewById(R.id.btnTest);
        btn.setOnClickListener(this);

        xmlWriter = new XMLWriter(this);
    }

    @Override
    protected void onDestroy() {
        socketOperator.close();
        super.onDestroy();

    }

    @Override
    public void onConnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View content_loading = findViewById(R.id.content_loading);
                content_loading.setVisibility(View.GONE);

                View content_main = findViewById(R.id.content_main);
                content_main.setVisibility(View.VISIBLE);
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

    @Override
    public void onReceiveMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void onMessageSent() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnTest:
                String message = xmlWriter.getICEEVRequest(100);
                socketOperator.sendMessage(message);
        }
    }

}
