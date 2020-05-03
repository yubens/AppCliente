package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.idus.www.appcliente.models.Customer;
import ar.com.idus.www.appcliente.models.Distributor;
import ar.com.idus.www.appcliente.utilities.Constants;
import ar.com.idus.www.appcliente.utilities.ResponseObject;
import ar.com.idus.www.appcliente.utilities.Utilities;

public class OrderActivity extends AppCompatActivity {
    ArrayList<Distributor> distributors;
    Distributor distributor;
    Customer customer;
    TextView textView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        String token;
        ResponseObject responseToken, responseCompany;
        Bundle bundle;

        textView = findViewById(R.id.textViewTest);

        bundle = getIntent().getExtras();

        if (bundle == null) {
//            Toast.makeText(getApplicationContext(), R.string.msgErrCustomerData, Toast.LENGTH_LONG).show();
            showExit(getString(R.string.msgErrClientData));
            return;
        }

        customer = (Customer) bundle.getSerializable("customer");

        if (customer == null) {
            showExit(getString(R.string.msgErrClientData));
            return;
        }

        sharedPreferences = getPreferences(MODE_PRIVATE);
        responseToken = Utilities.getNewToken(getApplicationContext(), sharedPreferences);

        if (responseToken == null) {
            showExit(getString(R.string.msgErrToken));
            return;
        }

        if (responseToken.getResponseCode() == Constants.SHOW_EXIT) {
            showExit(responseToken.getResponseData());
            return;
        }

        token = responseToken.getResponseData();

        if (customer == null) {
            showExit(getString(R.string.msgErrClientData));
            return;
        }

        responseCompany = getCompany(token, customer.getEmpresaId());

        if (responseCompany != null) {
            switch (responseCompany.getResponseCode()) {
                case Constants.OK:
                    break;

                case Constants.SHOW_ERROR:
                    Utilities.showMsg(responseCompany.getResponseData(), getApplicationContext());
                    break;

                case Constants.SHOW_EXIT:
                    showExit(responseCompany.getResponseData());
                    break;
            }
        }

//        if (bundle != null) {
//            distributors = (ArrayList<Distributor>) bundle.getSerializable("distributors");
//            distributor = distributors.get(0);
//
//            textView.setText("ORDER " + distributor.getName()) ;
//        }
    }

    private ResponseObject getCompany (String token, String id) {
        String url = "/getCompany.php?token=" + token + "&idCompany=" + id;
        ResponseObject responseObject = Utilities.getResponse(getApplicationContext(), url, 1000);
        ResponseObject responseToken;

        int code = responseObject.getResponseCode();

        if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
            responseObject = Utilities.getResponse(getApplicationContext(), url, 2000);

        if (responseObject.getResponseCode() == Constants.INVALID_TOKEN) {
            responseToken = Utilities.getNewToken(getApplicationContext(), sharedPreferences);

            if (responseToken == null) {
                responseObject.setResponseCode(Constants.SHOW_EXIT);
                responseObject.setResponseData(getString(R.string.msgErrToken));

            } else if (responseToken.getResponseCode() == Constants.SHOW_EXIT) {
                responseObject.setResponseCode(Constants.SHOW_EXIT);
                responseObject.setResponseData(responseToken.getResponseData());
            } else {
                url = "/getCompany.php?token=" + responseToken.getResponseData() + "&idCustomer=" + id;
                responseObject = Utilities.getResponse(getApplicationContext(), url, 1000);

                code = responseObject.getResponseCode();

                if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
                    responseObject = Utilities.getResponse(getApplicationContext(), url, 2000);
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
                responseObject.setResponseData(getString(R.string.msgErrCustomerData));
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

    }
}
