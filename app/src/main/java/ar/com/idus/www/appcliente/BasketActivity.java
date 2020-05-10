package ar.com.idus.www.appcliente;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    SimpleDateFormat formatter;
    Date date;
    String idOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        btnCancel = findViewById(R.id.btnCancel);
        btnSendOrder = findViewById(R.id.btnSendOrder);
        editObs = findViewById(R.id.editObservations);

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

        customer = (Customer) bundle.getSerializable("customer");
        company = (Company) bundle.getSerializable("company");


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        listView = findViewById(R.id.listBody);
        adapter = new BasketAdapter(getApplicationContext(), R.layout.basket_item, headOrder.getBodyOrders());
        listView.setAdapter(adapter);

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        btnSendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDistributor();
            }
        });
    }

    private void showMsg(String msg) {
        if (!BasketActivity.this.isFinishing())
            Toast.makeText(BasketActivity.this, msg, Toast.LENGTH_LONG).show();
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
        String geo = "-1;-1";
        String observations = editObs.getText().toString();

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = null;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            geo = location.getLatitude() + ";" + location.getLongitude();
        }

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
        btnSendOrder.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
    }
}
