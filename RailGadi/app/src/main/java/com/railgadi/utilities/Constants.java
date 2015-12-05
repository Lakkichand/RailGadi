package com.railgadi.utilities;

import com.railgadi.beans.SeatMapStaticBean;

public class Constants {

    public static final String RAILGADI_COMMENTS_SUBJECT = "feedback" ;
    public static final String FEEDBACK_RECEIVER_MAIL = "railgadiapp@gmail.com" ;

    public static final String FACEBOOK_TAIL = "@facebook.com" ;

    public static final String DEFAULT_GENDER = "m" ;

    public static final String DEVICE_TYPE_ANDROID = "android" ;
    public static final String DEVICE_TYPE_IOS = "ios" ;
    public static final String DEVICE_TYPE_WINDOWS = "windows" ;

    public static final int STATION_ANIM_DURATION = 500 ;

    //public static final String APP_PLUS_ONE_URL = "https://play.google.com/store/apps/details?id=com.kiloo.subwaysurf" ;
    public static final String APP_PLUS_ONE_URL = "https://plus.google.com/111044638337771294705" ;

    public static final String NEXT = "next";
    public static final String PREV = "prev";
    public static final String NO_THING = "nothing";

    public final static String FROM_LIST = "FROM";
    public final static String TO_LIST = "TO";
    public final static String VIA_LIST = "VIA";

    public static final String UPCOMING = "upcoming" ;
    public static final String COMPLETED = "completed" ;
    public static final String OTHERS = "others" ;

    public static final int HOME_TICKET_ALPHA = 75 ;

    public static final int AMENITIES_DISTANCE = 5000 ;

    public static final int AUTO_SUGGESTION_LIMIT = 10 ;

    public static final int MAP_DEFAULT_ZOOM = 15 ;

    public static final String ATM = "atm" ;
    public static final String HOSPITAL = "hospital" ;
    public static final String HOTEL = "hotel" ;
    public static final String RESTAURANT = "restaurant" ;

    public static final String SPECIAL = "SPECIAL_TRAINS" ;
    public static final String RESCHEDULED = "RESCHEDULED_TRAINS" ;
    public static final String NEAR_BY_TRAINS = "NEAR_BY_TRAINS" ;


    public static final String RES = "RESCHEDULED" ;
    public static final String CAN = "CANCELLED" ;
    public static final String DIV = "DIVERTED" ;

    public static final String FULL = "FULL" ;
    public static final String PARTIAL = "PARTIAL" ;

    public static final String TODAY = "Today" ;
    public static final String TOMORROW = "Tomorrow" ;
    public static final String YESTERDAY = "Yesterday" ;
    public static final String DAY_BEFORE_YESTERDAY = "Before Yesterday";
    public static final String DAY_AFTER_TOMORROW = "After Tomorrow" ;

    public static final String GRP_PACKAGE = "com.info.railwayapp" ;

    public static final double NEAR_BY_STATION_MIN = 25 ;
    public static final double NEAR_BY_STATION_MAX = 200 ;

    public static final int MIN_LOCATION_UPDATE_TIME = 30000 ;

    public static final String POPUP_TRIGGER = "showPopupMenu";
    public static final String VIEW_TRIGGER = "expandLayout";

    public static final String DEP_TYPE = "DEPARTURE";
    public static final String ARR_TYPE = "ARRIVAL";
    public static final String DUR_TYPE = "DURATION";

    // font sizes in px
    public static final int TAB_HEADER_SIZE = 17 ;

    public static final String THIS_IS_WONDERFUL = "This is wonderful app download from" ;

    public static final String GOOGLE_PLAY_STATIC_URL = "http://play.google.com/store/apps/details?id=";

    public static final String GOOGLE_DOCS_URL = "http://docs.google.com/gview?embedded=true&url=" ;

    public static final String RAILWAY_MENU_URL = "http://www.indianrailways.gov.in/railwayboard/uploads/directorate/coaching/pdf/MenuMeals.pdf" ;

    public static final String TUTORIAL_FIVE = "Auto scan & track PNR from" +
                                                "\nSMS. Auto tracking and" +
                                                "\nNotification for PNR and train" +
                                                "\nstatus changes" ;

    public static final String TUTORIAL_FOUR = "Identify Nearest station," +
                                                "\nSet location based alarm, Find" +
                                                "\nrunning Train Coach Position," +
                                                "\nCheck train speed, track last and" +
                                                "\nupcoming station while" +
                                                "\ntravelling on the train." ;

    public static final String TUTORIAL_THREE = "Organised , Minimum click &" +
                                                "\n Intuitive UI design using latest" +
                                                "\nmaterial design" ;

    public static final String TUTORIAL_TWO = "Single touch tracking for" +
                                                "\nPNR, Train and Stations." +
                                                "\nSeat confirmation prediction for" +
                                                "\nWL ticket" ;

    public static final String TUTORIAL_ONE = "Welcome to RAILGADI app" +
                                                "\ncomplete rail information in" +
                                                "\nyour pocket" ;


    //SEAT MAP

    public static final SeatMapStaticBean FIRST_AC_ONE_O = new SeatMapStaticBean("COACH DIAGRAM FOR FULL FIRST AC SLEEPER COACH (ICF)", "To Seat/Sleep 24", "Coach Numbering: H1, H2 etc", null) ; // footer is null for now
    public static final SeatMapStaticBean FIRST_AC_TWO_O = new SeatMapStaticBean("COACH DIAGRAM FOR FULL FIRST AC SLEEPER COACH (LHB)", "To Seat/Sleep 24", "Coach Numbering: H1, H2 etc", null) ;
    public static final SeatMapStaticBean FIRST_AC_THREE_O = new SeatMapStaticBean("COACH DIAGRAM FOR COMPOSITE FIRST AC + SECOND AC SLEEPER COACH (ICF)", "To Seat/Sleep 10 (First AC) + 20 (Second AC)", "Coach Numbering: HA1, HA2 etc", null) ;
    public static final SeatMapStaticBean FIRST_AC_FOUR_O = new SeatMapStaticBean("COACH DIAGRAM FOR COMPOSITE FIRST AC + THIRD AC SLEEPER COACH (ICF)", "To Seat/Sleep 10 (First AC) + 30 (Third AC)", "Coach Numbering: HB1, HB2 etc", null) ;

    public static final SeatMapStaticBean SECOND_AC_ONE_O = new SeatMapStaticBean("COACH DIAGRAM FOR FULL SECOND AC SLEEPER COACH (ICF)", "To Seat/Sleep 48*", "Coach Numbering: A1, A2 etc", "Note:  Only newer Second AC Sleeper coaches made after 2007 (Layout 3) have 48 berths. Older coaches (Layouts 1 and 2) have only 46 berths.") ;
    public static final SeatMapStaticBean SECOND_AC_TWO_O = new SeatMapStaticBean("COACH DIAGRAM FOR FULL SECOND AC SLEEPER COACH (LHB)", "To Seat/Sleep 52*", "Coach Numbering: A1, A2 etc", "Note: Only newer Second AC Sleeper made after 2007 (Layout 2) have 52 berths. Older coaches (Layout 1) have 54") ;
    public static final SeatMapStaticBean SECOND_AC_THREE_O = new SeatMapStaticBean("COACH DIAGRAM FOR COMPOSITE FIRST AC + SECOND AC SLEEPER COACH (ICF)", "To Seat/Sleep 10 (First AC) + 20 (Second AC)", "Coach Numbering: HA1, HA2 etc", null) ;
    public static final SeatMapStaticBean SECOND_AC_FOUR_O = new SeatMapStaticBean("COACH DIAGRAM FOR COMPOSITE SECOND/THIRD AC SLEEPER COACH (ICF)", "To Seat/Sleep 24 (Second AC) + 32 (Third AC)", "Coach Numbering: AB1, AB2 etc", null) ;

    public static final SeatMapStaticBean THIRD_AC_ONE_O = new SeatMapStaticBean("COACH DIAGRAM FOR FULL THIRD AC SLEEPER COACH (ICF)", "To Seat/Sleep 64", "Coach Numbering: B1, B2 etc", null) ;
    public static final SeatMapStaticBean THIRD_AC_TWO_O = new SeatMapStaticBean("COACH DIAGRAM FOR FULL THIRD AC SLEEPER COACH (LHB)", "To Seat/Sleep 72", "Coach Numbering: B1, B2 etc", null) ;
    public static final SeatMapStaticBean THIRD_AC_THREE_O = new SeatMapStaticBean("COACH DIAGRAM FOR COMPOSITE FIRST AC + THIRD AC SLEEPER COACH (ICF)", "To Seat/Sleep 10 (First AC) + 30 (Third AC)", "Coach Numbering: HB1, HB2 etc", null) ;
    public static final SeatMapStaticBean THIRD_AC_FOUR_O = new SeatMapStaticBean("COACH DIAGRAM FOR COMPOSITE SECOND/THIRD AC SLEEPER COACH (ICF)", "To Seat/Sleep 24 (Second AC) + 32 (Third AC)", "Coach Numbering: AB1, AB2 etc", null) ;

    public static final SeatMapStaticBean SLEEP_CLASS_ONE_O = new SeatMapStaticBean("COACH DIAGRAM FOR SLEEPER CLASS NON-AC COACH (ICF)", "To Seat/Sleep 72", "Coach Numbering: S1, S2 etc", null) ;
    public static final SeatMapStaticBean SLEEP_CLASS_TWO_O = new SeatMapStaticBean("COACH DIAGRAM FOR SLEEPER CLASS NON-AC COACH (LHB)", "To Seat/Sleep 80", "Coach Numbering: S1, S2 etc", null) ;

    public static final SeatMapStaticBean JNSH_AC_CC_ONE_O = new SeatMapStaticBean("COACH DIAGRAMS FOR AC CHAIR CAR COACH (ICF)", "To Seat 73", "Coach Numbering: C1, C2 etc", "In this coach arrangement, seats 1 - 39 face one direction (towards the pantry in this diagram) and seats 40 - 73 face the other direction.  As a result, seats 35-39 and 40 - 44 face each other over the centre table.*Note 2: In layout 3, seat numbers 3 and 73 do not exist - this isn’t a typographical error") ;
    public static final SeatMapStaticBean JNSH_AC_CC_TWO_O = new SeatMapStaticBean("COACH DIAGRAMS FOR AC CHAIR CAR COACH (ICF)", "To Seat 73", "Coach Numbering: C1, C2 etc", "In this coach arrangement, seats 1 - 39 face one direction (towards the pantry in this diagram) and seats 40 - 73 face the other direction.  As a result, seats 35-39 and 40 - 44 face each other over the centre table.*Note 2: In layout 3, seat numbers 3 and 73 do not exist - this isn’t a typographical error") ;
    public static final SeatMapStaticBean JNSH_AC_CC_THREE_O = new SeatMapStaticBean("COACH DIAGRAMS FOR AC CHAIR CAR COACH (ICF)", "To Seat 73", "Coach Numbering: C1, C2 etc", "In this coach arrangement, seats 1 - 39 face one direction (towards the pantry in this diagram) and seats 40 - 73 face the other direction.  As a result, seats 35-39 and 40 - 44 face each other over the centre table.*Note 2: In layout 3, seat numbers 3 and 73 do not exist - this isn’t a typographical error") ;

    public static final SeatMapStaticBean JNSH_NON_AC_ONE_O = new SeatMapStaticBean("COACH DIAGRAMS FOR SECOND SITTING COACH (ICF)", "To Seat 108", "Coach Numbering: D1, D2 etc", null) ;
    public static final SeatMapStaticBean JNSH_NON_AC_TWO_O = new SeatMapStaticBean("COACH DIAGRAMS FOR SECOND SITTING COACH (ICF)", "To Seat 108", "Coach Numbering: D1, D2 etc", null) ;

    public static final SeatMapStaticBean SHTB_CC_ONE_O = new SeatMapStaticBean("COACH DIAGRAM FOR SHATABDI AC CHAIR CAR COACH (ICF)", "To Seat 67", "Coach Numbering: C1, C2 etc", null) ;
    public static final SeatMapStaticBean SHTB_CC_TWO_O = new SeatMapStaticBean("COACH DIAGRAM FOR SHATABDI AC CHAIR CAR COACH (LHB)", "To Seat 78", "Coach Numbering: C1, C2 etc", null) ;

    public static final SeatMapStaticBean SHTB_EX_O = new SeatMapStaticBean("COACH DIAGRAM FOR EXECUTIVE CHAIR CAR COACH (ICF)", "To Seat 56", "Coach Numbering: E1, E2 etc", "*Note:  In this coach arrangement, seats 1 - 28 face one direction (towards the pantry in this diagram) and seats 29 - 56 face the other direction.  As a result, seats 25 - 28 and 29 - 32 face each other over the centre table") ;

    public static final SeatMapStaticBean DOUBLE_DACKER_O = new SeatMapStaticBean("COACH DIAGRAM FOR AC DOUBLE DECKER CHAIR CAR COACH (LHB)", "To Seat 120", "Coach Numbering: C1L, C1U, C2L, C2U etc", "Note:  In this coach arrangement, seats in the mezzanines, lower and upper deck face each other over the centre table.\n*Note 2: This coach diagram is not valid for coaches of the Howrah - Dhanbad Double Decker Express") ;

    public static final SeatMapStaticBean FC_NON_AC_O = new SeatMapStaticBean("COACH DIAGRAM FOR FIRST CLASS NON-AC COACH (ICF)", "To Sleep 26*", "Coach Numbering: F1, F2 etc", "Note:  If the First Class coach is being used on an exclusively day train, three people sit on the lower berth.  The seating capacityof this coach is thus 39, higher than the sleeping capacity") ;

    public static final SeatMapStaticBean GARIB_RATH_3A_O = new SeatMapStaticBean("COACH DIAGRAM FOR FIRST CLASS NON-AC COACH (ICF)", "To Sleep 26*", "Coach Numbering: F1, F2 etc", "Note:  If the First Class coach is being used on an exclusively day train, three people sit on the lower berth.  The seating capacityof this coach is thus 39, higher than the sleeping capacity") ;

}
