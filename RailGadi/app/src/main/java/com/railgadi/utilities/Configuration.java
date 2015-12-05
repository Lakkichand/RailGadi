package com.railgadi.utilities;

import java.util.TimeZone;

public class Configuration {

    public static final TimeZone IND_TIMEZONE = TimeZone.getTimeZone("Asia/Kolkata");

    //FOR CONNECTION
    public static final String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.2) Gecko/2008070208 Firefox/13.0.1";
    public static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    public static final String ACCEPT_LANG = "en-us,en;q=0.5";
    public static final String ACCEPT_ENCODING = "gzip, deflate";
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String CONNECTION = "keep-alive";
    public static final int TIMEOUT = 60000;
    public static final String CACHE_CONTROL = "private, max-age=864000";

    //For NTES
    public static final String NTES_HOST = "enquiry.indianrail.gov.in";
    public static final String NTES_LIVE_STATION= "http://enquiry.indianrail.gov.in/ntes/NTES?action=getTrainsViaStn";
    public static final String NTES_REFER = "http://enquiry.indianrail.gov.in/ntes/";
    public static final String NTES_VALIDATION_CAPTCHA_URL = "http://enquiry.indianrail.gov.in/ntes/CaptchaServlet?action=validateCaptchaText";
    public static final String NTES_GETNEWCAPTCHA_ID_URL = "http://enquiry.indianrail.gov.in/ntes/CaptchaServlet?action=getNewCaptchaId";
    public static final String NTES_GETNEWCAPTCHAImage_URL = "http://enquiry.indianrail.gov.in/ntes/CaptchaServlet?action=getNewCaptchaImg";
    public static final String NTES_TRAIN_DATA = "http://enquiry.indianrail.gov.in/ntes/NTES?action=getTrainData";
    public static final String NTES_TRAIN_START_DATE = "http://enquiry.indianrail.gov.in/ntes/NTES?action=getTrainForDate";
    public static final String NTES_FIND_TRAIN_BTW_STNS = "http://enquiry.indianrail.gov.in/ntes/NTES?action=getTrnBwStns";
    public static final String NTES_TRAIN_SCHEDULE = "http://enquiry.indianrail.gov.in/ntes/FutureTrain?action=getTrainData";
    public static final String NTES_CANCELLED_TRAIN = "http://enquiry.indianrail.gov.in/ntes/NTES?action=showAllCancelledTrains";
    public static final String NTES_RESCHEDULED_TRAIN = "http://enquiry.indianrail.gov.in/ntes/NTES?action=showAllRescheduledTrains";
    public static final String NTES_DIVERTED_TRAIN = "http://enquiry.indianrail.gov.in/ntes/NTES?action=showAllDivertedTrains";
    public static  final String NTES_SEARCH_TRAIN = "http://enquiry.indianrail.gov.in/ntes/SearchTrain?";
    public static final String NTES_SPECIAL_TRAIN = "http://enquiry.indianrail.gov.in/ntes/NTES?action=getSpecialEventsTrains";

    //FOR HOST
    public static final String HOST = "www.indianrail.gov.in";

    public static final String VALIDATION_URL = "http://www.indianrail.gov.in/valid.php";

    public static final String PNR_REFERRER = "http://www.indianrail.gov.in/pnr_Enq.html";
    public static final String PNR_SERVICE = "http://www.indianrail.gov.in/cgi_bin/inet_pnstat_cgi_10521.cgi";

    public static final String TRAIN_SCHEDULE_REFERRER = "http://www.indianrail.gov.in/train_Schedule.html";
    public static final String TRAIN_SCHEDULE_SERVICE = "http://www.indianrail.gov.in/cgi_bin/inet_trnnum_cgi.cgi";

    public static final String CUR_BKNG_AVL_SERVICE = "http://www.indianrail.gov.in/cgi_bin/inet_curbkg.cgi";
    public static final String CUR_BKNG_AVL_REFER = "http://www.indianrail.gov.in/inet_curbkg_Enq.html";

    public static final String FIND_TRAINS_REFERRER = "http://www.indianrail.gov.in/dont_Know_Station_Code.html";
    public static final String FIND_TRAINS_SERVICE = "http://www.indianrail.gov.in/cgi_bin/inet_srcdest_cgi_time.cgi";
    public static final String FIND_TRAINS_ANYDATE_REFERRER = "http://www.indianrail.gov.in/inet_Srcdest.html";
    public static final String FIND_TRAINS_ANYDATE = "http://www.indianrail.gov.in/cgi_bin/inet_srcdest_cgi.cgi";

    public static final String SEAT_AVAILABILITY_REFERRER = "http://www.indianrail.gov.in/seat_Avail.html";
    public static final String SEAT_AVAILABILITY_SERVICE = "http://www.indianrail.gov.in/cgi_bin/inet_accavl_cgi.cgi";

    public static final String FARE_ENQ_REFERRER = "http://www.indianrail.gov.in/fare_Enq.html";
    public static final String FARE_ENQ_SERVICE = "http://www.indianrail.gov.in/cgi_bin/inet_frenq_cgi.cgi";

    public static final String CNFTKT_URL = "http://api.confirmtkt.com/api/pnr/status/%s";
    public static final String CNFTKT_HOST = "http://api.confirmtkt.com";
    public static final String CNFTKT_REFERRER = "http://api.confirmtkt.com/api/pnr/status/";

    //Time duration for non available of pnr service and ticket booking
    //public static final String START_TIME="11:30:00";
    public static final String START_TIME = "23:30:00";
    public static final String END_TIME = "00:30:00";

    //public static  final String[] DB_LIST = {"G:\\Practice\\TrainApi\\StationsDB","G:\\Practice\\TrainApi\\TrainTimeTable"};
    public static final String[] DB_LIST = {"StationsDB", "RAIL_GADI_DB"};

}
