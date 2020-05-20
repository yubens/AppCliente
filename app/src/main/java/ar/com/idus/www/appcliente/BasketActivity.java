package ar.com.idus.www.appcliente;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import ar.com.idus.www.appcliente.models.BodyOrder;
import ar.com.idus.www.appcliente.models.Company;
import ar.com.idus.www.appcliente.models.Customer;
import ar.com.idus.www.appcliente.models.HeadOrder;
import ar.com.idus.www.appcliente.utilities.BasketAdapter;
import ar.com.idus.www.appcliente.utilities.Constants;
import ar.com.idus.www.appcliente.utilities.ResponseObject;
import ar.com.idus.www.appcliente.utilities.Utilities;

public class BasketActivity extends AppCompatActivity {
    HeadOrder headOrder;
    Customer customer;
    Company company;
    BasketAdapter adapter;
    ListView listView;
    Button btnSendOrder, btnCancel;
    SharedPreferences sharedPreferences;
    EditText editObs;
    TextView txtTotal;
    SimpleDateFormat formatter;
    Date date;
    String idOrder;
    String geo;
    LocationManager locManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        btnCancel = findViewById(R.id.btnCancel);
        btnSendOrder = findViewById(R.id.btnSendOrder);
        editObs = findViewById(R.id.editObservations);
        txtTotal = findViewById(R.id.txtTotal);

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            showExit(getString(R.string.msgErrBasket));
            return;
        }

        headOrder = (HeadOrder) bundle.getSerializable("order");

        if (headOrder == null) {
            showExit(getString(R.string.msgErrBasket));
            return;
        }

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        txtTotal.setTypeface(null, Typeface.BOLD);

        customer = (Customer) bundle.getSerializable("customer");
        company = (Company) bundle.getSerializable("company");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        listView = findViewById(R.id.listBody);
        adapter = new BasketAdapter(BasketActivity.this, R.layout.basket_item, headOrder.getBodyOrders(), txtTotal);
        listView.setAdapter(adapter);

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        calculateTotal();

        btnSendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertBuilder;
                alertBuilder = new AlertDialog.Builder(BasketActivity.this);
                alertBuilder.setMessage(R.string.msgConfirmOrder)
                        .setCancelable(false)
                        .setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(R.string.btnAccept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ResponseObject responseSendOrder = sendOrder();

                                if (responseSendOrder != null) {
                                    switch (responseSendOrder.getResponseCode()) {
                                        case Constants.OK:
                                            ResponseObject responseSendBody = sendBody();

                                            if (responseSendBody != null) {
                                                switch (responseSendBody.getResponseCode()) {
                                                    case Constants.OK:
                                                        ResponseObject responseConfirm = confirmOrder();

                                                        if (responseConfirm != null) {
                                                            switch (responseSendBody.getResponseCode()) {
                                                                case Constants.OK:
                                                                    showMsg(getString(R.string.msgSuccSendOrder));
                                                                    callDistributor();
                                                                    break;

                                                                case Constants.SHOW_ERROR:
                                                                    showMsg(responseConfirm.getResponseData());
                                                                    break;

                                                                case Constants.SHOW_EXIT:
                                                                    showExit(responseConfirm.getResponseData());
                                                                    break;
                                                            }
                                                        }

                                                        break;

                                                    case Constants.SHOW_ERROR:
                                                        showMsg(responseSendBody.getResponseData());
                                                        break;

                                                    case Constants.SHOW_EXIT:
                                                        showExit(responseSendBody.getResponseData());
                                                        break;
                                                }
                                            }

                                            break;

                                        case Constants.SHOW_ERROR:
                                            showMsg(responseSendOrder.getResponseData());
                                            break;

                                        case Constants.SHOW_EXIT:
                                            showExit(responseSendOrder.getResponseData());
                                            break;
                                    }
                                }
                            }
                        });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertBuilder;
                alertBuilder = new AlertDialog.Builder(BasketActivity.this);
                alertBuilder.setMessage(R.string.msgCancelOrder)
                        .setCancelable(false)
                        .setNegativeButton(R.string.btnNo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(R.string.btnYes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                callDistributor();
                            }
                        });
                AlertDialog alert = alertBuilder.create();
                alert.show();


            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.REQUEST_CODE_LOCATION);
        } else {
            getCoordinates();
        }
    }

    private void getCoordinates() {
        Location locationGPS = null, locationNet;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showMessageOK(getString(R.string.msgErrorGPS),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO --> ABRIR PARA QUE ENCIENDA GPS
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                                showExit(getString(R.string.msgErrorGPS));
                            }
                        });
            } else {
                locationGPS = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (locationGPS != null) {
                    geo = locationGPS.getLatitude() + ";" + locationGPS.getLongitude();
                    return;
                }

                locationNet = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (locationNet != null)
                    geo = locationNet.getLatitude() + ";" + locationNet.getLongitude();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showMessageOK(getString(R.string.msgErrorGPS),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO --> ABRIR PARA QUE ENCIENDA GPS
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                                showExit(getString(R.string.msgErrorGPS));
                        }
                    });
            return;
        }

        if (geo == null)
            getCoordinates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final List<String> permsDenied = new ArrayList<>();

        switch (requestCode) {
            case Constants.REQUEST_CODE_LOCATION:
                if (grantResults.length == 0)
                    return;

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permiso Concedido", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permiso Denegado", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {

                            showMessageOK(getString(R.string.msgErrorManualConfig),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //TODO >>>>>> LLEVARLO A LA PANTALLA DE PERMISOS
                                            finishAndRemoveTask();
                                            moveTaskToBack(true);
                                            android.os.Process.killProcess(android.os.Process.myPid());
                                            System.exit(0);
                                        }
                                    });

                            return;

                        }
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Permission is not granted

                            showMessageOK(getString(R.string.msgErrorLocation),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                ActivityCompat.requestPermissions(BasketActivity.this,
                                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                        Constants.REQUEST_CODE_LOCATION);
                                            }
                                        }
                                    });
                        }
                    }
                }

                break;
        }
    }

    private void showMessageOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(BasketActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.btnAccept), okListener)
                .setCancelable(false)
                .create()
                .show();
    }

    private void showMsg(String msg) {
        if (!BasketActivity.this.isFinishing())
            Toast.makeText(BasketActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private void calculateTotal() {
        float aux = 0.0f;
        String totalS;

        for (BodyOrder body: headOrder.getBodyOrders()) {
            aux = aux + body.getTotal();
        }

        totalS = String.format("%.2f", aux);

        txtTotal.setText(totalS);
    }

    private void callDistributor() {
        Intent intent = new Intent(getApplicationContext(), DistributorActivity.class);
        intent.putExtra("customer", customer);
        intent.putExtra("company", company);
        startActivity(intent);
    }

    private ResponseObject sendOrder() {
        ResponseObject responseObject, responseToken;
        idOrder = UUID.randomUUID().toString();

//         geo = "-1;-1";
        String observations = editObs.getText().toString();

        if (geo == null)
            geo = "0.0;0.0";

        date = new Date();
        headOrder.setDateEnd(formatter.format(date));
        headOrder.setDateOrder(formatter.format(date));

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        date = c.getTime();

        headOrder.setDateDelivery(formatter.format(date));
        headOrder.setGeo(geo);

        String url = "/putB2BOrderHead.php?token=" + Utilities.getData(sharedPreferences, "token") +
                        "&idOrder=" + idOrder + "&idCustomer=" + headOrder.getIdCustomer() + "&cantItems=" + headOrder.getBodyOrders().size() +
                        "&dateOrder=" + headOrder.getDateOrder() + "&dateStar=" + headOrder.getDateStart() + "&dateEnd=" + headOrder.getDateEnd() +
                        "&geoPos=" + geo + "&obsOrder=" + observations + "&dateDelivery=" + headOrder.getDateDelivery();

        responseObject = Utilities.putResponse(getApplicationContext(), url, 5000);

        int code = responseObject.getResponseCode();

        if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
            responseObject = Utilities.getResponse(getApplicationContext(), url, 5000);

        if (responseObject.getResponseCode() == Constants.INVALID_TOKEN) {
            responseToken = Utilities.getNewToken(getApplicationContext(), sharedPreferences);

            if (responseToken == null) {
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString(R.string.msgErrToken));

            } else if (responseToken.getResponseCode() == Constants.SHOW_EXIT) {
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(responseToken.getResponseData());
            } else {
                url = "/putB2BOrderHead.php?token=" + responseToken.getResponseData() +
                        "&idOrder=" + idOrder + "&idCustomer=" + headOrder.getIdCustomer() + "&cantItems=" + headOrder.getBodyOrders().size() +
                        "&dateOrder=" + headOrder.getDateOrder() + "&dateStar=" + headOrder.getDateStart() + "&dateEnd=" + headOrder.getDateEnd() +
                        "&geoPos=" + geo + "&obsOrder=" + observations + "&dateDelivery=" + headOrder.getDateDelivery();


                responseObject = Utilities.putResponse(getApplicationContext(), url, 5000);

                code = responseObject.getResponseCode();

                if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
                    responseObject = Utilities.getResponse(getApplicationContext(), url, 5000);
            }
        }

        switch (responseObject.getResponseCode()) {
            case Constants.NO_INTERNET:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString(R.string.msgErrInternet));
                break;

            case Constants.SHOW_EXIT:
                break;

            case Constants.NO_DATA:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString(R.string.msgErrOrder));
                break;

            case Constants.EXCEPTION:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString(R.string.msgErrException) + " (" + responseObject.getResponseData() + ")");
                break;

            case Constants.SERVER_ERROR:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString((R.string.msgErrServer)));
                break;

            case Constants.INVALID_TOKEN:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString((R.string.msgErrToken)));
                break;
        }

        return responseObject;
    }

    private ResponseObject sendBody() {
        ResponseObject responseObject = null;
        String url;

        int i = 0;

        for (BodyOrder bodyOrder : headOrder.getBodyOrders()) {
            url = "/putB2BOrderBody.php?token=" + Utilities.getData(sharedPreferences, "token") +
                    "&idOrder=" + idOrder + "&idOrderItems=" + i++ + "&idProduct=" + bodyOrder.getIdProduct() + "&cantOrderProduct=" +  bodyOrder.getQuantity() +
                    "&priceUnitarProductOrder=" + bodyOrder.getPrice();

            responseObject = Utilities.putResponse(getApplicationContext(), url, 5000);

            if (responseObject.getResponseCode() != Constants.OK) {
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString(R.string.msgErrOrder));
                break;
            }
        }

        return responseObject;
    }

    private ResponseObject confirmOrder() {
        ResponseObject responseObject, responseToken;
        String url = "/putB2BOrderConfirmed.php?token=" + Utilities.getData(sharedPreferences, "token") +
                    "&idOrder=" + idOrder;

        responseObject = Utilities.putResponse(getApplicationContext(), url, 5000);

        int code = responseObject.getResponseCode();

        if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
            responseObject = Utilities.getResponse(getApplicationContext(), url, 5000);

        if (responseObject.getResponseCode() == Constants.INVALID_TOKEN) {
            responseToken = Utilities.getNewToken(getApplicationContext(), sharedPreferences);

            if (responseToken == null) {
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString(R.string.msgErrToken));

            } else if (responseToken.getResponseCode() == Constants.SHOW_EXIT) {
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(responseToken.getResponseData());
            } else {
                url = "/putB2BOrderConfirmed.php?token=" + responseToken.getResponseData() +
                        "&idOrder=" + idOrder;

                responseObject = Utilities.putResponse(getApplicationContext(), url, 5000);

                code = responseObject.getResponseCode();

                if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
                    responseObject = Utilities.getResponse(getApplicationContext(), url, 5000);
            }
        }

        switch (responseObject.getResponseCode()) {
            case Constants.NO_INTERNET:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString(R.string.msgErrInternet));
                break;

            case Constants.SHOW_EXIT:
                break;

            case Constants.NO_DATA:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString(R.string.msgErrOrder));
                break;

            case Constants.EXCEPTION:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString(R.string.msgErrException) + " (" + responseObject.getResponseData() + ")");
                break;

            case Constants.SERVER_ERROR:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString((R.string.msgErrServer)));
                break;

            case Constants.INVALID_TOKEN:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString((R.string.msgErrToken)));
                break;
        }

        return responseObject;
    }

    private void showExit(String msg) {
        Intent intent = new Intent(this, ErrorActivity.class);
        intent.putExtra("error", msg);
        startActivity(intent);
    }
}
