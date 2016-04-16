package com.aronbordin.electricity.utils;


import cn.wintec.wtandroidjar.ComIO;
import cn.wintec.wtandroidjar.Printer;

public class DevicePrinter {
    private static Printer p = null;
    public enum DEVICE_MODEL {
        WTA
    }

    public static Printer getPrinter() {
        DEVICE_MODEL model = getModel();
        if (model == DEVICE_MODEL.WTA) {
            if (p == null) {
                p = new Printer("/dev/ttySAC1", ComIO.Baudrate.BAUD_38400);
                p.PRN_Init();
            }
            return p;
        } else {
            return null;
        }
    }

    public static DEVICE_MODEL getModel() {
        return DEVICE_MODEL.WTA;
    }
}
