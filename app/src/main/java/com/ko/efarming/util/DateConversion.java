package com.ko.efarming.util;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateConversion {

    public static String getDateFromString(String dateInString, String actualformat, String exceptedFormat) {
        SimpleDateFormat form = new SimpleDateFormat(actualformat, new Locale("en", "EN"));
        String formatedDate = null;
        Date date;
        try {
            date = form.parse(dateInString);
            SimpleDateFormat postFormater = new SimpleDateFormat(exceptedFormat, Locale.getDefault());
            formatedDate = postFormater.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatedDate;
    }

    public static String converToTimeForRecentActivity(long timeInMillis) {
        String lastSeen = "";
        long diff = System.currentTimeMillis() - timeInMillis;
        long secs = diff / 1000;
        long mins = secs / 60;
        long diffInSecond = diff / 1000;
        long diffInMinute = diff / (60 * 1000);
        long diffInHour = diff / (60 * 60 * 1000);
        long diffInDays = diff / (24 * 60 * 60 * 1000);
        long diffInWeeks = diff / (7 * 24 * 60 * 60 * 1000);
        if (diffInDays != 0 && diffInDays >= 2) {
            lastSeen = "last seen yesterday " + getDate(timeInMillis, "yyyy-MM-dd HH:mm aa");
        } else if (diffInDays != 0 && diffInDays == 1) {
            lastSeen = "last seen yesterday " + getDate(timeInMillis, "HH:mm aa");
        } else {
            lastSeen = "last seen today " + getDate(timeInMillis, "HH:mm aa");
        }
        return lastSeen;
//        return DateConversion.formatDateString(date);
    }

    public static boolean isToday(long millis) {
        return isDayEqual(0, millis);
    }

    public static boolean isYesterday(long millis) {
        return isDayEqual(-1, millis);
    }

    public static boolean isTomorrow(long millis) {
        return isDayEqual(1, millis);
    }

    private static boolean isDayEqual(int dayToAdd, long millis) {
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.DAY_OF_YEAR, dayToAdd);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(millis);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    public static Date stringToDate(String strDate, String parseFormat) {
        DateFormat formatter;
        Date date = null;
        formatter = new SimpleDateFormat(parseFormat, Locale.getDefault());
        try {
            date = (Date) formatter.parse(strDate);
        } catch (ParseException e) {
            date = new Date(strDate);
        }
        return date;
    }

    public static String calendarToStringwithslash(Calendar cal) {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
        return df.format(cal.getTime());
    }

    public static Calendar getPreviousMonth(Calendar cal) {
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        return cal;
    }

    public static Calendar getNextMonth(Calendar cal) {
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        return cal;
    }

    public static String getTimeFromLong(long time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date(time));
    }

    /**
     * Will display full date eg. Friday, 30 October 2015
     */
    public static String getFullDate(long time) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
        return dateFormat.format(new Date(time));
    }

    public static long getTimeFromString(String dateTime, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        try {
            Date date = sdf.parse(dateTime);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getTimeFromDate(Date date, String format) {
        return getTimeFromDate(date, format, TimeZone.getDefault());
    }

    public static String getTimeFromDate(Date date, String format, TimeZone timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        if (timeZone != null) {
            sdf.setTimeZone(timeZone);
        }
        return sdf.format(date);
    }

    public static long getCurrentTime(String timeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        return sdf.getCalendar().getTimeInMillis();
    }

    /**
     * The time(long) value is seconds not millis
     *
     * @param timeZone String representation of time format
     * @param time     time as long value in seconds
     * @return time time as long in seconds
     */
    // GMT0
    public static long getLocalizedTime(String timeZone, String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        long millist = 0;
        try {
            Date date = sdf.parse(time);
            millist = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millist;
    }

    /**
     * This is for GLOBAL
     */
    public static long getTimeForGMT0(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT0"));
        long millist = 0;
        try {
            Date date = sdf.parse(time);
            millist = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millist;
    }

    /**
     * This is for ony Mingl App 2014-05-19 11:38:03
     */
    /*
     * public static String getTimeForApp(String time, String requiredFormat) {
     * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
     * Locale.US); sdf.setTimeZone(TimeZone.getTimeZone("GMT0")); Date date =
     * null; try { date = sdf.parse(time); } catch (ParseException e) {
     * e.printStackTrace(); } return new SimpleDateFormat(requiredFormat,
     * Locale.getDefault()).format(date); }
     */
    public static long getDateDiff(long time, long current) {
        long diff = Math.max(time, current) - Math.min(time, current);
        long days = diff / (1000 * 60 * 60 * 24);
        return days;
    }

    /**
     * This is for ony Mingl App 2014-05-19 11:38:03
     */
    public static long convertToMillis(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        long millis = 0l;
        try {
            Date date = sdf.parse(time);
            millis = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis;
    }

    /**
     * It converts to GMT-0
     */
    public static long getTimeMillisForApp(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT0"));
        long millis = 0l;
        try {
            Date date = sdf.parse(time);
            millis = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis;
    }

    /**
     * @param timeZone
     * @param time
     * @return
     */
    public static long getLocalizedTime(String timeZone, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aaa", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        String dateS = sdf.format(new Date(time)); // /1351330745
        return DateConversion.stringToDate(dateS, "dd-MM-yyyy hh:mm:ss aaa").getTime();
    }

    public static long getLocalizedTime(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        long millist = 0;
        try {
            Date date = sdf.parse(time);
            millist = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millist;
    }

    public static String getDateWithTFromMilliSeconds(long timeMilliSecs, long dateMilliSecs) {
        String dateString = "";
        try {
            if (timeMilliSecs == -1) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                dateString = format.format(new Date(dateMilliSecs));
                Date parsedDate = format.parse(dateString);
                dateString = formatter.format(parsedDate);
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                dateString = formatter.format(new Date(timeMilliSecs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }

    public static String getDateAndTimeWithoutGMT(String date, String finalFormat) {
        String formattedDateString = null;
        if (!TextUtils.isEmpty(date)) {
            if (date.contains("T00:00:00")) {
                finalFormat = "MMMM dd, yyyy";
            }
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat format2 = new SimpleDateFormat(finalFormat);
            if (!TextUtils.isEmpty(date) && !date.equalsIgnoreCase("-1")) {
                try {
                    Date parsedDate = format1.parse(date);
                    formattedDateString = format2.format(parsedDate);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return formattedDateString;
    }

    public static String setDateAndTime(String date, String finalFormat) {
        String formattedDateString = null;
        if (!TextUtils.isEmpty(date)) {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat format2 = new SimpleDateFormat(finalFormat);
            if (!TextUtils.isEmpty(date) && !date.equalsIgnoreCase("-1")) {
                try {
                    Date parsedDate = format1.parse(date);
                    formattedDateString = format2.format(parsedDate);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return formattedDateString;
    }

    public static String getDateAndTime(String date, String finalFormat) {
        String formattedDateString = null;
        if (!TextUtils.isEmpty(date)) {
            if (date.contains("T00:00:00")) {
                finalFormat = "MMMM dd, yyyy";
            }
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat format2 = new SimpleDateFormat(finalFormat);
            if (!TextUtils.isEmpty(date) && !date.equalsIgnoreCase("-1")) {

                try {
                    Date parsedDate = format1.parse(date);
                    formattedDateString = format2.format(parsedDate);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return formattedDateString;
    }

    public static String getDateAndTimeForMeasure(String date, String finalFormat) {
        String formattedDateString = null;
        if (!TextUtils.isEmpty(date)) {
            if (date.contains("T00:00:00")) {
                finalFormat = "dd/MM/yyyy";
            }
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat format2 = new SimpleDateFormat(finalFormat);
            if (!TextUtils.isEmpty(date) && !date.equalsIgnoreCase("-1")) {

                try {
                    Date parsedDate = format1.parse(date);
                    formattedDateString = format2.format(parsedDate);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return formattedDateString;
    }

    public static long getMilliSecondFromString(String date) {
        long timeInMilliseconds = -1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        if (!TextUtils.isEmpty(date) && !date.equalsIgnoreCase("-1")) {
            try {
                Date mDate = sdf.parse(date);
                timeInMilliseconds = mDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return timeInMilliseconds;
    }

    public static long getMilliSecondFromStringForDate(String date) {
        long timeInMilliseconds = -1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (!TextUtils.isEmpty(date) && !date.equalsIgnoreCase("-1")) {
            try {
                Date mDate = sdf.parse(date);
                timeInMilliseconds = mDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return timeInMilliseconds;
    }

    public static long getMilliSecondFromStringForHrs(String date, String format) {
        long timeInMilliseconds = -1;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (!TextUtils.isEmpty(date) && !date.equalsIgnoreCase("-1")) {
            try {
                sdf.setTimeZone(TimeZone.getTimeZone("GMT0"));
                Date mDate = sdf.parse(date);
                timeInMilliseconds = mDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return timeInMilliseconds;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static long getMilliSeconds(String date, String format) {
        long timeInMilliseconds = -1;
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        try {
            Date mDate = sdf.parse(date);
            timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public static long timeStringtoMilis(String time, String format) {
        long milis = 0;

        try {
            SimpleDateFormat sd = new SimpleDateFormat(format, Locale.ENGLISH);
            Date date = sd.parse(time);
            milis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return milis;
    }

    public static String getDateFromMilliSeconds(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static long getMilliSecondFromString(String date, String format) {
        long timeInMilliseconds = -1;
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        if (!TextUtils.isEmpty(date) && !date.equalsIgnoreCase("-1")) {
            try {
                Date mDate = sdf.parse(date);
                timeInMilliseconds = mDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return timeInMilliseconds;
    }

    public static String getSystemDate(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateAsFormat(String date, boolean numericDates) {
        try {
/*        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date convertedDate = new Date();
        try {
            convertedDate = sdf.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String outputText = outputFormat.format(convertedDate);
        Date finalDate = new Date();
        try {
            finalDate = outputFormat.parse(outputText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("DateFormated",""+outputText);*/
/*        Calendar calendar = Calendar.getInstance();
        calendar.setTime(finalDate);
        if (calendar.get(Calendar.WEEK_OF_YEAR) >= 4) {
            return date;
        } else if (calendar.get(Calendar.WEEK_OF_YEAR) >= 1){
            if (numericDates){
                return "1 week ago";
            } else {
                return "Last week";
            }
        } else if (calendar.get(Calendar.DAY_OF_WEEK) >= 2) {
            return " days ago";
        } else if (calendar.get(Calendar.DAY_OF_WEEK) >= 1){
            if (numericDates){
                return "1 day ago";
            } else {
                return "Yesterday";
            }
        } else if (calendar.get(Calendar.HOUR)>= 2) {
            return +calendar.get(Calendar.HOUR)+" hours ago";
        } else if (calendar.get(Calendar.HOUR) >= 1){
            if (numericDates){
                return "1 hour ago";
            } else {
                return "An hour ago";
            }
        } else if (calendar.get(Calendar.MINUTE) >= 2) {
            return +calendar.get(Calendar.SECOND)+" minutes ago";
        } else if (calendar.get(Calendar.MINUTE) >= 1){
            if (numericDates){
                return "1 minute ago";
            } else {
                return "A minute ago";
            }
        } else if (calendar.get(Calendar.SECOND) >= 3) {
            return +calendar.get(Calendar.SECOND)+" seconds ago";
        } else {
            return "Just now";
        }*/
            Log.e("date", "" + date);
            final DateFormat sdf;
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date convertedDate = new Date();
            try {
                convertedDate = sdf.parse(date);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Date currentDate = null;
            long timeDiff;
            currentDate = new Date(System.currentTimeMillis());
            long localisedLongTime = convertedDate.getTime();
            timeDiff = Math.abs(currentDate.getTime() - localisedLongTime);

            long diffInSecond = timeDiff / 1000;
            long diffInMinute = timeDiff / (60 * 1000);
            long diffInHour = timeDiff / (60 * 60 * 1000);
            long diffInDays = timeDiff / (24 * 60 * 60 * 1000);
            long diffInWeeks = timeDiff / (7 * 24 * 60 * 60 * 1000);
            long diffmon = diffInDays / 30;
            long diffYear = diffmon / 12;

            if (!numericDates) {
                if (diffInSecond < 60) {
                    return "Just now";
                } else if (diffInMinute < 60) {
                    return String.valueOf(diffInMinute) + " " + "m";
                } else if (diffInHour < 24) {
                    return String.valueOf(diffInHour) + " " + "h";
                } else if (diffInDays == 1) {
                    return "Yesterday";
                }
            } else {
                if (diffInWeeks >= 4) {
//                String formatDate = DateConversion.formatDateString(date);
//                return formatDate;
                } else if (diffInWeeks != 0 && diffInWeeks >= 1) {
                    return String.valueOf(diffInWeeks) + " " + "week ago";
                } else if (diffInDays != 0 && diffInDays == 1) {
                    return String.valueOf(diffInDays) + " " + "day ago";
                } else if (diffInDays != 0 && diffInDays < 30) {
                    return String.valueOf(diffInDays) + " " + "days ago";
                } else if (diffInHour != 0 && diffInHour == 1) {
                    return String.valueOf(diffInHour) + " " + "hour ago";
                } else if (diffInHour != 0 && diffInHour < 24) {
                    return String.valueOf(diffInHour) + " " + "hours ago";
                } else if (diffInMinute != 0 && diffInMinute == 1) {
                    return "1 minute ago";
                } else if (diffInMinute != 0 && diffInMinute < 60) {
                    return String.valueOf(diffInMinute) + " " + "minutes ago";
                } else if (diffInSecond >= 3 && diffInSecond <= 60) {
                    return String.valueOf(diffInSecond) + " " + "seconds ago";
                } else if (diffInSecond < 3) {
                    return "Just now";
                } /*else if (diffmon >= 1 && diffmon < 12) {
                if (diffmon > 1) {
                    return String.valueOf(diffmon) + " " + "months" + " ago";
                } else {
                    return String.valueOf(diffmon) + " " + "month" + " ago";
                }
            } else if (diffYear >= 1) {
                if (diffYear > 1) {
                    return String.valueOf(diffYear) + " " + "years" + " ago";
                } else {
                    return String.valueOf(diffYear) + " " + "year" + " ago";
                }
            }*/
            }
        } catch (Exception e) {
            return "";
        }

        return null;
    }

//    public static String formatDateString(String dateCreated) {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
//        Date myDate = null;
//        try {
//            myDate = dateFormat.parse(dateCreated);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(myDate);
//        DateFormat df = new SimpleDateFormat("yy", Locale.ENGLISH);
//        String formattedDate = df.format(Calendar.getInstance().getTime());
//        com.ciceroneme.android.logger.Log.e("Date", "" + myDate);
//        SimpleDateFormat month_date = new SimpleDateFormat("MMM", Locale.ENGLISH);
//        String month = month_date.format(cal.getTime());
//        String date = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
//        String year = String.valueOf(cal.get(Calendar.YEAR));
//        return date + "-" + month + "-" + formattedDate;
//    }

}
