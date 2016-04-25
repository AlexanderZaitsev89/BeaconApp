package alex.beaconapp;

/**
 * Created by Alex on 03.04.2016.
 */
public class AppConfig {

    // Shared preferences file name
    public static  String PREF_NAME = "ReservationSystem";

    //parameters for the user info
    public static String USER_ID ="userId";
    public static String USER_TOKEN ="token";
    public static String USER_NAME ="firstName";
    public static String USER_LAST_NAME ="lastName";
    public static String USER_EMAIL ="username";
    public static String USER_PASSWORD ="password";
    public static String USER_TYPE ="type";

    //Other parameters
    public static String CLASS_ID ="classId";

    // Server user login url
    public static String URL_LOGIN = "http://5.101.107.114:8080/api/login";

    // Server user register url
    public static String URL_REGISTER = "http://5.101.107.114:8080/api/register";

    // Server user=student attend class url
    public static String URL_ATTEND_CLASS = "http://5.101.107.114:8080/api/attendClass";

    // Server user=student leave class url
    public static String URL_LEAVE_CLASS = "http://5.101.107.114:8080/api/leaveClass";

    //Parameters that are in json object, whick we receive from server
    public static String JSON_OBJECT_SUCCESS ="success";
    public static String JSON_OBJECT_DATA ="data";

    // Server user=student leave class url
    public static String URL_GET_CURRENT_CLASS_BY_BEACON = "http://5.101.107.114:8080/api/getClassByBeacon";

    // Server user=student leave class url
    public static String URL_SHOW_MY_ENROLLED_COURSES = "http://5.101.107.114:8080/api/getMyClass ";
}
