package com.venomtech.bellatez.gnytransax.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.sip.SipSession;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class formatDate {

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    public static String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat(" E, d MMM yyyy");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

    public static String formatDate2(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat(" E, d MMM yyyy");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

    public static String formatDate3(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat(" E, d MMM yyyy");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }


}
