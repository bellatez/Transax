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
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d, yyyy");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

    public static String dateOnly(java.util.Date d) {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return Long.toString(cal.getTimeInMillis());
    }

    public static Date convertDate(String dateStr) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy");
        Date date = formatter.parse(dateStr);
        return date;
    }
}
