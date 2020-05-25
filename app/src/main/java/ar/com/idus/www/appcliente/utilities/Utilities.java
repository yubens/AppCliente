package ar.com.idus.www.appcliente.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import ar.com.idus.www.appcliente.R;

import static ar.com.idus.www.appcliente.R.string.msgErrToken;

public abstract class Utilities {

    public static float roundNumber(String number) {
        float f = Float.valueOf(number);
        BigDecimal bd = new BigDecimal(f);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        f = bd.floatValue();

        return f;
    }

    public static boolean checkConnection(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // No sólo wifi, también GPRS
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();

            for (NetworkInfo net: netInfo) {
                if (net .getState() == NetworkInfo.State.CONNECTED)
                    return true;
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(context, R.string.msgErrInternet, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public static boolean setEditColor (Context context, EditText editText, boolean error) {
        int color = error ? android.R.color.holo_red_light : R.color.colorPrimaryLight ;
        editText.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(color)));
        return error;
    }

    public static void saveData(SharedPreferences sharedPreferences, String key, String data) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, data);
        editor.commit();
        System.out.println("guardado " + key + " " + data);
    }

    public static String getData(SharedPreferences sharedPreferences, String key) {
        return sharedPreferences.getString(key, Constants.NO_RESULT_STR);
    }

    public static void deleteData(SharedPreferences sharedPreferences, String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public static ResponseObject getNewToken(Context context, SharedPreferences sharedPreferences) {
        ResponseObject responseObject;
        int code;

        deleteData(sharedPreferences, "token");

        responseObject = getResponse(context, "/getToken.php?idApp=BuyIdus", 5000);

        code = responseObject.getResponseCode();

        if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
            responseObject = getResponse(context, "/getToken.php?idApp=BuyIdus", 0);

        if (responseObject != null) {
            switch (responseObject.getResponseCode()) {
                case Constants.OK:
                    saveData(sharedPreferences, "token", responseObject.getResponseData());
                    System.out.println("token guardado " + responseObject.getResponseData());
                    break;

                case Constants.NO_DATA:
                    responseObject.setResponseCode(Constants.SHOW_EXIT);
                    responseObject.setResponseData(context.getString(msgErrToken));
                    break;

                case Constants.EXCEPTION:
                    responseObject.setResponseCode(Constants.SHOW_EXIT);
                    responseObject.setResponseData(context.getString(R.string.msgErrException) + " (" + responseObject.getResponseData() + ")");
                    break;

                case Constants.SERVER_ERROR:
                    responseObject.setResponseCode(Constants.SHOW_EXIT);
                    responseObject.setResponseData(context.getString((R.string.msgErrServer)));
                    break;

                case Constants.NO_INTERNET:
                    responseObject.setResponseCode(Constants.SHOW_EXIT);
                    responseObject.setResponseData(context.getString((R.string.msgErrInternet)));
                    break;
            }
        }

        return responseObject;
    }

    public static ResponseObject getResponse(Context context, final String url, long waitTime) {
        ResponseObject responseObject = new ResponseObject();
        final AtomicInteger resultCode = new AtomicInteger(Constants.NO_DATA);
        final AtomicReference <String> resultString = new AtomicReference<>();

        if (checkConnection(context)) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    int status;
                    String line;
                    URL urlOpen;
                    StringBuilder result = new StringBuilder();
                    HttpURLConnection cnx = null;
                    super.run();

                    try {
                        urlOpen = new URL(Constants.URL + url);
                        cnx = (HttpURLConnection) urlOpen.openConnection();
                        status = cnx.getResponseCode();
                        resultCode.set(status);

                        if (status == Constants.OK) {
                            InputStream in = new BufferedInputStream(cnx.getInputStream());
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                            }

                            if (result.toString().equals("[]"))
                                resultCode.set(Constants.NO_DATA);

                            resultString.set(result.toString());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        resultString.set(e.getMessage());
                        resultCode.set(Constants.EXCEPTION);

                    } finally {
                        if (cnx != null) {
                            cnx.disconnect();
                        }
                    }
                }
            };

            try {
                thread.start();
                thread.join(waitTime);
                responseObject.setResponseCode(resultCode.get());
                responseObject.setResponseData(resultString.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
                responseObject.setResponseCode(Constants.EXCEPTION);
                responseObject.setResponseData(e.getMessage());
            }

            return responseObject;

        } else
            responseObject.setResponseCode(Constants.NO_INTERNET);

        return responseObject;
    }

    public static ResponseObject getResponse2(Context context, final String url) {
        ResponseObject responseObject = new ResponseObject();
        final AtomicInteger resultCode = new AtomicInteger(Constants.NO_DATA);
        final AtomicReference <String> resultString = new AtomicReference<>();

        if (checkConnection(context)) {
                int status;
                String line;
                URL urlOpen;
                StringBuilder result = new StringBuilder();
                HttpURLConnection cnx = null;

                try {
                    urlOpen = new URL(Constants.URL + url);
                    cnx = (HttpURLConnection) urlOpen.openConnection();
                    status = cnx.getResponseCode();
                    resultCode.set(status);

                    if (status == Constants.OK) {
                        InputStream in = new BufferedInputStream(cnx.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        if (result.toString().equals("[]"))
                            resultCode.set(Constants.NO_DATA);

                        resultString.set(result.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    resultString.set(e.getMessage());
                    resultCode.set(Constants.EXCEPTION);

                } finally {
                    if (cnx != null) {
                        cnx.disconnect();
                        responseObject.setResponseCode(resultCode.get());
                        responseObject.setResponseData(resultString.get());
                    }
                }

                responseObject.setResponseCode(resultCode.get());
                responseObject.setResponseData(resultString.get());

            return responseObject;

        } else
            responseObject.setResponseCode(Constants.NO_INTERNET);

        return responseObject;
    }

    public static ResponseObject putResponse(Context context, final String url, long waitTime) {
        ResponseObject responseObject = new ResponseObject();
        final AtomicInteger resultCode = new AtomicInteger(Constants.NO_DATA);
        final AtomicReference <String> resultString = new AtomicReference<>();

        if (checkConnection(context)) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    int status;
                    String line;
                    URL urlOpen;
                    StringBuilder result = new StringBuilder();
                    HttpURLConnection cnx = null;
                    super.run();

                    try {
                        urlOpen = new URL(Constants.URL + url);
                        cnx = (HttpURLConnection) urlOpen.openConnection();
                        cnx.setRequestMethod("PUT");
                        status = cnx.getResponseCode();
                        resultCode.set(status);

                    } catch (Exception e) {
                        e.printStackTrace();
                        resultString.set(e.getMessage());
                        resultCode.set(Constants.EXCEPTION);

                    } finally {
                        if (cnx != null) {
//                            resultString.set(result.toString());
                            cnx.disconnect();
                        }
                    }
                }
            };

            try {
                thread.start();
                thread.join(waitTime);
                responseObject = new ResponseObject();
                responseObject.setResponseCode(resultCode.get());
                responseObject.setResponseData(resultString.get());

            } catch (InterruptedException e) {
                e.printStackTrace();
                responseObject.setResponseCode(Constants.EXCEPTION);
                responseObject.setResponseData(e.getMessage());
            }

            return responseObject;

        } else
            responseObject.setResponseCode(Constants.NO_INTERNET);

        return responseObject;
    }
}
