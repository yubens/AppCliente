package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import ar.com.idus.www.appcliente.models.BodyOrder;
import ar.com.idus.www.appcliente.models.Distributor;
import ar.com.idus.www.appcliente.models.HeadOrder;
import ar.com.idus.www.appcliente.models.Product;
import ar.com.idus.www.appcliente.utilities.BasketAdapter;
import ar.com.idus.www.appcliente.utilities.Constants;
import ar.com.idus.www.appcliente.utilities.ResponseObject;
import ar.com.idus.www.appcliente.utilities.Utilities;

public class BasketActivity extends AppCompatActivity {
    ArrayList<Product> productList;
    HeadOrder headOrder;
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

        btnCancel = findViewById(R.id.btnCancel);
        btnSendOrder = findViewById(R.id.btnSendOrder);
        editObs = findViewById(R.id.editObservations);
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
                                                    Utilities.showMsg(getString(R.string.msgSuccSendOrder), getApplicationContext());
                                                    //TODO
                                                    // crear pantalla con datos de distribuidor
                                                    // mandarlo a esa pantalla

                                                    break;

                                                case Constants.SHOW_ERROR:
                                                    Utilities.showMsg(responseConfirm.getResponseData(), getApplicationContext());
                                                    break;

                                                case Constants.SHOW_EXIT:
                                                    showExit(responseConfirm.getResponseData());
                                                    break;
                                            }
                                        }

                                        break;

                                    case Constants.SHOW_ERROR:
                                        Utilities.showMsg(responseSendBody.getResponseData(), getApplicationContext());
                                        break;

                                    case Constants.SHOW_EXIT:
                                        showExit(responseSendBody.getResponseData());
                                        break;
                                }
                            }


                            break;

                        case Constants.SHOW_ERROR:
                            Utilities.showMsg(responseSendOrder.getResponseData(), getApplicationContext());
                            break;

                        case Constants.SHOW_EXIT:
                            showExit(responseSendOrder.getResponseData());
                            break;
                    }
                }
            }
        });

        //TODO
        // responden token invalidos los 3 endpoints
    }

    private ResponseObject sendOrder() {
        ResponseObject responseObject, responseToken;
        idOrder = UUID.randomUUID().toString();
        String geo = "-64.477876;-32.407801";
        String observations = editObs.getText().toString();

        date = new Date();
        headOrder.setDateEnd(formatter.format(date));
        headOrder.setDateOrder(formatter.format(date));

        // TODO falta
        // obtener coordenas del dispositivo
        // sumar 24 horas al date end
        headOrder.setDateDelivery(formatter.format(date));
        headOrder.setGeo("-64.477876;-32.407801");

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
//        txtMultiple.setVisibility(View.GONE);
//        txtSalePrice.setVisibility(View.GONE);
//        txtTotal.setVisibility(View.GONE);
//        txtStock.setVisibility(View.GONE);
//        editQuantity.setVisibility(View.GONE);
//        editDescription.setVisibility(View.GONE);
//        editCode.setVisibility(View.GONE);
//        txtOfferPrice.setVisibility(View.GONE);
//        imgButFindCode.setVisibility(View.GONE);
//        imgButFindDesc.setVisibility(View.GONE);
//        btnAdd.setVisibility(View.GONE);
//        btnWatch.setVisibility(View.GONE);
//
//        btnExit.setVisibility(View.VISIBLE);
//
//        btnExit.setText(R.string.btnExit);
//
//        txtError.setText(msg);
//
//        btnExit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println("salio");
//                System.exit(0);
//            }
//        });
    }
}
