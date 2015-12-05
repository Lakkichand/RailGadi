package com.railgadi.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateAndMore {

    public static final String SUPER_DATE_FORMAT = "E, MMM dd, yyyy HH:mm:ss a" ;
    public static final String DATE_WITHOUT_TIME = "E, MMM dd, yyyy" ;
    public static final String DAY_DATE = "E, MMM dd" ;
    public static final String DATE_MONTH = "dd MMM" ;
    public static final String TIME_DATE = "dd MMM, HH:mm" ;
    public static final String DAY_DATE_MONTH = "EEEE, MMM dd" ;
    public static final String DATE_WITH_DASH_ONE = "dd-MMM-yyyy" ;
    public static final String DATE_WITH_DASH_TWO = "dd-MM-yyyy" ;
    public static final String DATA_HH_MM= "dd-MM-yyyy HH:mm" ;
    public static final String DATE_WITH_SLASH= "dd/MM/yyyy" ;
    public static final String MMM_DD_YYYY= "MMM dd, yyyy" ;
    public static final String FULL_DAY_WITHOUT_TIME = "EEEE, MMM dd, yyyy" ;


    public static String getPnrFromMsg(String msg) {

        int sPnr = msg.indexOf("PNR:");

        return msg.substring(sPnr + 4, sPnr + 14);
    }


    public static Date formatStringToDate(String dateInString , String date_format) throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat(date_format);

        Date date = formatter.parse(dateInString);

        return date ;
    }

    public static String formatDateToString(Date date, String date_format) {
        SimpleDateFormat format = new SimpleDateFormat(date_format);
        return format.format(date);
    }

    public static Date get120DaysAfterDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 120);
        return cal.getTime() ;
    }

}
