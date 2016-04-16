package com.aronbordin.electricity.model;


import android.text.Html;
import android.text.Spanned;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

public class XMLReader {

    public Resp parse(String message){
        XStream xStream = new XStream();
        xStream.processAnnotations(Resp.class);
        xStream.processAnnotations(Result.class);
        xStream.processAnnotations(EventDetails.class);
        xStream.processAnnotations(TranDetails.class);
        xStream.processAnnotations(VoucherDetails.class);
        xStream.processAnnotations(Amount.class);
        xStream.processAnnotations(Token.class);

        return (Resp) xStream.fromXML(message);
    }


    @XStreamAlias("iceevdresp")
    public static class Resp {
        public String date;
        public String time;
        public Result result;
        public EventDetails evddetails;

        public Spanned getVoucherHTML() {
            VoucherDetails v = evddetails.voucherdetails;
            String html = ""
                    + "<br><b>VAT Reg. No</b>: " + v.vatno
                    + "<br><b>Ref#</b>: " + result.refno
                    + "<br><b>Retailer</b>: " +  "ADD HERE"
                    + "<br><b>Date</b>: " + date
                    + "<br><b>Time</b>: " + time
                    + "<br><b>Name</b>: " + v.name
                    + "<br><b>Address</b>: " + "ADDR HERE"
                    + "<br><b>TAX Ref</b>: " + "TAX REF HER"
                    + "<br><b>Meter nr</b>: " + v.meterno
                    + "<br><b>Tariff</b>: " + "TARIFF HERE"
                    + "<br><b>Units</b>: " + v.units
                    + "<br><b>Amount Vat Excl</b>: " + "AMT EXC HERE"
                    + "<br><b>VAT</b>: " + v.vatno
                    + "<br><br><b><h3>VOUCHER HERE</h3></b>"
                    + "<b>Total</b>: " + evddetails.trandetails.amt
                    + "<br><b>Footer like airtime receipt</b> ";
            return Html.fromHtml(html);
        }

        public Spanned getError() {
            return Html.fromHtml("<b>Error</b>: " + result.desc);
        }
    }

    @XStreamAlias("result")
    public static class Result {
        public String code;
        public String desc;
        public String refno;
    }

    @XStreamAlias("evddetails")
    public static class EventDetails {
        public TranDetails trandetails;
        public VoucherDetails voucherdetails;
        public String message;
        public double balance;
    }

    @XStreamAlias("trandetails")
    public static class TranDetails {
        public String date;
        public String time;
        public float amt;
        public String vat;
        public String no;
        public String status;
    }

    @XStreamAlias("voucherdetails")
    public static class VoucherDetails {

        public String meterno;
        public String sgc;
        public String ti;
        public String krn;
        public String tt;
        public String alg;
        @XStreamImplicit(itemFieldName = "amounts")
        public List list_amounts = new ArrayList();
        public float units;
        public float elecamt;
        public String vatno;
        @XStreamImplicit(itemFieldName = "tokens")
        public List list_tokens = new ArrayList();
        public String message;
        public String name;
        public String receiver;
        public String receiptno;
        public String receiptdesc;
    }

    @XStreamAlias("amounts")
    public static class Amount {
        public String desc;
        public float amt;
    }

    @XStreamAlias("tokens")
    public static class Token {
        public String token;
        public String info;
    }
}
