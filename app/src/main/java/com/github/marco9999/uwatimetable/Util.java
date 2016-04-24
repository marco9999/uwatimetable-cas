package com.github.marco9999.uwatimetable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Marco on 24/04/2016.
 */
class Util {

    String getDayOfWeek() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        return dayFormat.format(Calendar.getInstance().getTime());
    }

}
