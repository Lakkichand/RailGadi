package com.railgadi.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utilities {


    public static int[] getTimeDiffBtwStation(String srcDepTime, String srcDay, String destArrTime, String destDay){

        int[] timeDiff=new int[2];
        try{
            String str_date = "01/01/2015 00:00";
            SimpleDateFormat formatter = Utilities.getDateFormat("dd/MM/yyyy hh:mm");
            Date date = formatter.parse(str_date);
            Date date1,date2;
            String[] hhmm = srcDepTime.split(":");
            int sDay = Integer.parseInt(srcDay.trim()) ;
            int dDay = Integer.parseInt(destDay.trim()) ;
            int diff = Math.abs(sDay - dDay);

            Calendar cal = Calendar.getInstance(Configuration.IND_TIMEZONE);
            cal.setTime(date);
            cal.set(Calendar.HOUR, Integer.parseInt(hhmm[0].trim()));
            cal.set(Calendar.MINUTE, Integer.parseInt(hhmm[1].trim()));
            date1 = cal.getTime();

            hhmm = destArrTime.split(":");
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, diff);
            cal.set(Calendar.HOUR, Integer.parseInt(hhmm[0].trim()));
            cal.set(Calendar.MINUTE, Integer.parseInt(hhmm[1].trim()));
            date2=cal.getTime();
            timeDiff = getTimeDifference(date1, date2);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return timeDiff;
    }


    public static int[] getTimeDifference(Date date1, Date date2) {
        int[] result = new int[2];

        long diff = Math.abs(date1.getTime() - date2.getTime());
        final int ONE_DAY = 1000 * 60 * 60 * 24;
        final int ONE_HOUR = ONE_DAY / 24;
        final int ONE_MINUTE = ONE_HOUR / 60;
        final int ONE_SECOND = ONE_MINUTE / 60;

        long d = diff / ONE_DAY;
        diff %= ONE_DAY;

        long h = diff / ONE_HOUR;
        diff %= ONE_HOUR;

        long m = diff / ONE_MINUTE;
        diff %= ONE_MINUTE;

        //result[0] = (int)d;
        result[0] = (int)(h+24*d);
        result[1] = (int)m ;

        return result;
    }

    public static boolean checkAvailable() throws Exception{
        try {

            String string1 = Configuration.START_TIME;
            Date time1 = new SimpleDateFormat("HH:mm:ss").parse(string1);
            Calendar calendar1 =Calendar.getInstance(Configuration.IND_TIMEZONE);
            calendar1.setTime(time1);
            Date d1 = calendar1.getTime();
            String string2 = Configuration.END_TIME;
            Date time2 = new SimpleDateFormat("HH:mm:ss").parse(string2);
            Calendar calendar2 = Calendar.getInstance(Configuration.IND_TIMEZONE);
            calendar2.setTime(time2);
            //calendar2.add(Calendar.DATE, 0);
            Date d2 = calendar2.getTime();


            Calendar calendar3 = Calendar.getInstance(Configuration.IND_TIMEZONE);
            SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
            String someRandomTime = sdf.format(calendar3.getTime());
            Date d = new SimpleDateFormat("HH:mm:ss").parse(someRandomTime);
            calendar3.setTime(d);
            Date d3 = calendar3.getTime();


            Date x = calendar3.getTime();

            if (x.after(calendar1.getTime()) || x.before(calendar2.getTime())) {
                return false;
            }
        } catch (ParseException e) {
            throw e;
        }
        return true;
    }


    public static Date addDay(Date date,int day) {

        int[] duration=new int[2];
        duration[0]=day*24;
        duration[1]=0;
        return addDuration(date, duration);
    }

    // Add day, hour and time to the date
    public static Date addDuration(Date date, int[] duration){
        Calendar cal = Calendar.getInstance(Configuration.IND_TIMEZONE);
        cal.setTime(date);
        int day=duration[0]/24;
        int hours=duration[0]%24;
        cal.add(Calendar.DATE, day);
        cal.add(Calendar.HOUR, hours);
        cal.add(Calendar.MINUTE, duration[1]);
        return cal.getTime();
    }



    //convert time string to int array e.g. time="13:33" to int[] = 13,33
    public static int[] dateStringToInt(String time){
        int[] times = new int[2];
        try{
            String[] tmp = time.split(":");
            times[0] = Integer.parseInt(tmp[0]);
            times[1] = Integer.parseInt(tmp[1]);
        }catch(Exception e){

        }
        return times;
    }

    public static Date setTime(Date date, int[] time) {

        Calendar cal = Calendar.getInstance(Configuration.IND_TIMEZONE);
        cal.setTime(date);
        cal.set(Calendar.HOUR, time[0]);
        cal.set(Calendar.MINUTE, time[1]);
        return cal.getTime();
    }

    // Find the date of next day from today. e.g today is 18 Apr 2015(Saturday) , dow = 5(Friday), then output is 24 Apr 2015 (Friday)
    public static Date nextDayOfWeek(int dow) { //dow --> 1 to 7 mean Sunday to Saturday
        Calendar cal = Calendar.getInstance(Configuration.IND_TIMEZONE);
        int diff = dow - cal.get(Calendar.DAY_OF_WEEK);
        if (!(diff > 0)) {
            diff += 7;
        }
        cal.add(Calendar.DAY_OF_MONTH, diff);
        return cal.getTime();
    }

    public static  void runVacuum() {
        Thread thrdVacuum = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] dbList = Configuration.DB_LIST;
                    String connString="jdbc:sqlite:";
                    Class.forName("org.sqlite.JDBC");
                    for (int i = 0; i < dbList.length; i++) {
                        Connection conn = DriverManager.getConnection(connString + dbList[i]);
                        Statement stmt = conn.createStatement();
                        //conn.setAutoCommit(false);
                        StringBuilder sql = new StringBuilder() ;
                        sql.append("VACUUM");
                        boolean rs = stmt.execute(sql.toString()) ;
                        if(stmt!=null){
                            stmt.close();
                        }
                        conn.close();
                        System.out.println("Executed vacuum " + rs ) ;
                    }
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        thrdVacuum.start();
    }

    public static SimpleDateFormat getDateFormat(String s)
    {
        SimpleDateFormat simpledateformat = new SimpleDateFormat(s, Locale.US);
        simpledateformat.setTimeZone(Configuration.IND_TIMEZONE);
        return simpledateformat;
    }


    public static final String trainType[] = {
            "ALL", "All", "ALL_EXP", "All Express", "ALL_PAS", "All Passenger", "DMU", "DMU", "DRNT", "Duronto",
            "EMU", "EMU", "GBR", "Garib Rath", "HSP", "Holiday Special", "JSH", "Jan Shatabdi", "LUX", "Luxury",
            "MEMU", "MEMU", "MEX", "Mail Express", "MMTS", "MMTS", "PAS", "Passenger", "PRUM", "Premium",
            "RAJ", "Rajdhani", "SHT", "Shatabdi", "SUB", "Suburban", "SUF", "Superfast"
    };

    public static String getTrainType(String s1)
    {
        for (int i1 = 0; i1 < trainType.length; i1 += 2)
        {
            if (trainType[i1].equals(s1))
            {
                return trainType[i1 + 1];
            }
        }

        return "";
    }

}
