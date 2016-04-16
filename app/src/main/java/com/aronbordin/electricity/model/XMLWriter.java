package com.aronbordin.electricity.model;

import android.content.Context;
import android.content.res.AssetManager;

import com.aronbordin.electricity.utils.Utils;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by aron on 3/7/16.
 */
public class XMLWriter {
    private Context context;
    private AssetManager manager;

    public XMLWriter(Context context){

        this.context = context;
        manager = context.getAssets();
    }

    public String getICEEVRequest(String meterNR, String amount){
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(Utils.FILE_ICEEV);

            byte[] bytes = new byte[1000];

            StringBuilder x = new StringBuilder();

            int numRead = 0;
            while ((numRead = is.read(bytes)) >= 0) {
                x.append(new String(bytes, 0, numRead));
            }
            String msg = x.toString();


            //prepare message variables

            //date
            SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy/MM/dd");
            sdf_date.setTimeZone(TimeZone.getTimeZone("GMT+2"));
            String date = sdf_date.format(new Date());

            SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm:ss");
            sdf_time.setTimeZone(TimeZone.getTimeZone("GMT+2"));
            String time = sdf_time.format(new Date());

            // refno
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+2"));
            long utc = cal.getTimeInMillis();

            return String.format(msg, date, time, utc, meterNR, amount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
