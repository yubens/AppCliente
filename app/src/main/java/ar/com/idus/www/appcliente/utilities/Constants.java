package ar.com.idus.www.appcliente.utilities;

public  abstract class  Constants {
    public static final String URL = "http://widus-app-bygvs.dyndns.info:8086/WebServiceIdusApp";
    public static final String ENABLED = "1";
    public static final String NO_RESULT_STR = "400";

    public static final int REQUEST_CODE_STATE = 100;
    public static final int REQUEST_CODE_LOCATION = 101;

    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int SERVER_ERROR = 500;
    public static final int INVALID_TOKEN = 501;
    public static final int NO_DATA = 502;
    public static final int NO_RESULT = 504;

    public static final int EXCEPTION = 300;
    public static final int NO_INTERNET = 301;

    public static final int DISABLED = 1000;
    public static final int SHOW_EXIT = 2000;
    public static final int SHOW_ERROR = 3000;

    //TODO ver codigos de disabled y no data
}
