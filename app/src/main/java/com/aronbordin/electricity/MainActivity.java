package com.aronbordin.electricity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.alertdialogpro.AlertDialogPro;
import com.aronbordin.electricity.model.XMLReader;
import com.aronbordin.electricity.model.XMLWriter;
import com.aronbordin.electricity.server.SocketOperator;
import com.dd.processbutton.ProcessButton;

public class MainActivity extends AppCompatActivity implements
        SocketOperator.SocketOperatorInterface,
        View.OnClickListener {

    private SocketOperator socketOperator;
    private XMLWriter xmlWriter;
    private ProcessButton btnSubmit;
    private EditText edtMeterNR;
    private EditText edtAmount;

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

        xmlWriter = new XMLWriter(this);

        btnSubmit = (ProcessButton) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        edtMeterNR = (EditText) findViewById(R.id.edtMeterNR);
        edtAmount = (EditText) findViewById(R.id.edtAmount);
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
                if (BuildConfig.DEBUG) {
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
        message = message.substring(6);
        XMLReader.Resp resp = new XMLReader().parse(message);

        if (resp.result.code.equals("0000")) { //OK
            btnSubmit.setProgress(100);
            new AlertDialogPro.Builder(this)
                    .setTitle(R.string.voucher)
                    .setMessage(resp.getVoucherHTML())
                    .setNeutralButton(R.string.close, null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (btnSubmit.getProgress() == 100)
                                        btnSubmit.setProgress(0);
                                }
                            }, 500);
                        }
                    })
                    .show();
        } else {
            btnSubmit.setProgress(-1);
            new AlertDialogPro.Builder(this)
                    .setTitle(R.string.voucher)
                    .setMessage(resp.getError())
                    .setNeutralButton(R.string.close, null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(btnSubmit.getProgress() == -1)
                                        btnSubmit.setProgress(0);
                                }
                            }, 1000);
                        }
                    })
                    .show();
        }
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
        switch(v.getId()) {
            case R.id.btnSubmit:
                if(btnSubmit.getProgress() == 1) {
                    return;
                }
                btnSubmit.setProgress(1);
                String meter_nr = edtMeterNR.getText().toString();
                String amount = edtAmount.getText().toString();

                String message = xmlWriter.getICEEVRequest(meter_nr, amount);
                socketOperator.sendMessage(message);
//                    onReceiveMessage("000000<iceevdresp version=\"1.0\" agent=\"\"> <date>2016/04/16</date> <time>15:54:12</time> <result> <code>0000</code> <desc>OK</desc> <refno>1460814850462</refno> </result> <evddetails> <trandetails> <date>2016/04/16</date> <time>15:54:10</time> <amt>1.00</amt> <vat></vat> <no>8020</no> <status>OK</status> </trandetails> <voucherdetails> <meterno>30000000007</meterno> <sgc>000394</sgc> <ti>01</ti> <krn>1</krn> <tt>2</tt> <alg>-</alg> <amounts> <desc>Arrears</desc> <amt>10.00</amt> </amounts> <amounts> <desc>VAT</desc> <amt>64.04</amt> </amounts> <units>587.3</units> <elecamt>457.43</elecamt> <vatno>4860193491</vatno> <tokens> <token>5379 1717 4345 1320 4971</token> <info>FBE</info> </tokens> <tokens> <token>1234 5678 9123 1230 4143</token> <info>Electricity Credit</info> </tokens> <message>NEW SUMMER BlockTariff kWh^cost: 1-50@69.56c 51-350@78.41c^351-600@104.90c 600-@125.89c</message> <name>D.C. KUPUTSA</name> <receiver>Emfuleni Local Municip</receiver> <receiptno>1120/209803</receiptno> <receiptdesc></receiptdesc> </voucherdetails> <message></message> <balance>-8790.40</balance> </evddetails> </iceevdresp>");
                break;
        }

    }

}
