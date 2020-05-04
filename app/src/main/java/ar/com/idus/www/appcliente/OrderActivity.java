package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import ar.com.idus.www.appcliente.models.Company;
import ar.com.idus.www.appcliente.models.Customer;
import ar.com.idus.www.appcliente.models.Distributor;
import ar.com.idus.www.appcliente.utilities.Constants;
import ar.com.idus.www.appcliente.utilities.ResponseObject;
import ar.com.idus.www.appcliente.utilities.Utilities;

public class OrderActivity extends AppCompatActivity {
    ArrayList<Distributor> distributors;
    Distributor distributor;
    Customer customer;
    TextView txtMultiple, txtSalePrice, txtOfferPrice, txtTotal, txtStock, txtError;
    EditText editQuantity, editDescription, editCode;
    ImageButton imgButFindCode, imgButFindDesc;
    Button btnAdd, btnWatch;
    Company company;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        txtMultiple = findViewById(R.id.txtMultiple);
        txtOfferPrice = findViewById(R.id.txtOfferPrice);
        txtSalePrice = findViewById(R.id.txtSalePrice);
        txtTotal = findViewById(R.id.txtTotal);
        txtStock = findViewById(R.id.txtStock);
        txtError = findViewById(R.id.txtError);
        editCode = findViewById(R.id.editCode);
        editDescription = findViewById(R.id.editDescription);
        editQuantity = findViewById(R.id.editQuantity);
        imgButFindCode = findViewById(R.id.imgButFindCode);
        imgButFindDesc = findViewById(R.id.imgButFindDesc);
        btnAdd = findViewById(R.id.btnAdd);
        btnWatch = findViewById(R.id.btnWatch);

        btnAdd.setActivated(false);
        btnAdd.setBackgroundResource(R.drawable.btn_disabled);
        btnWatch.setActivated(false);
        btnWatch.setBackgroundResource(R.drawable.btn_disabled);

        ResponseObject responseToken, responseCompany;
        Bundle bundle;

        bundle = getIntent().getExtras();

        bundle = null;

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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (customer == null) {
            showExit(getString(R.string.msgErrClientData));
            return;
        }

        responseCompany = getCompany(customer.getEmpresaId());

        if (responseCompany != null) {
            switch (responseCompany.getResponseCode()) {
                case Constants.OK:
                    checkCompany(responseCompany.getResponseData());
                    break;

                case Constants.SHOW_ERROR:
                    Utilities.showMsg(responseCompany.getResponseData(), getApplicationContext());
                    break;

                case Constants.SHOW_EXIT:
                    showExit(responseCompany.getResponseData());
                    break;
            }
        }

        imgButFindDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = editDescription.getText().toString();

                if (desc.length() < 3) {
                    Utilities.showMsg(getString(R.string.msgErrMinLength), getApplicationContext());
                }
            }
        });

        imgButFindCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


//        if (bundle != null) {
//            distributors = (ArrayList<Distributor>) bundle.getSerializable("distributors");
//            distributor = distributors.get(0);
//
//            textView.setText("ORDER " + distributor.getName()) ;
//        }
    }

    private void checkCompany(String data) {
        Gson gson = new Gson();

        Company[] companies;

        companies = gson.fromJson(data, Company[].class);
        company = companies[0];

    }

    private ResponseObject getCompany (String id) {
        String url = "/getCompany.php?token=" + Utilities.getData(sharedPreferences, "token") + "&idCompany=" + id;
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

    private ResponseObject getProduct(String data, boolean isCode) {
        String prodDesc = "", prodCode = "";

        if (isCode)
            prodCode = data;
        else
            prodDesc = data;

        String url = "/getProduct.php?token=" + Utilities.getData(sharedPreferences, "token") +
                        "&idCompany=" + company.getCodigo() + "&codePriceList=" + customer.getCodigoLista() +
                        "&findDesc=" + prodDesc + "&findCode=" + prodCode;

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
                url = "/getProduct.php?token=" +  responseToken.getResponseData() +
                        "&idCompany=" + company.getCodigo() + "&codePriceList=" + customer.getCodigoLista() +
                        "&findDesc=" + prodDesc + "&findCode=" + prodCode;
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
        txtMultiple.setVisibility(View.GONE);
        txtSalePrice.setVisibility(View.GONE);
        txtTotal.setVisibility(View.GONE);
        txtStock.setVisibility(View.GONE);
        editQuantity.setVisibility(View.GONE);
        editDescription.setVisibility(View.GONE);
        editCode.setVisibility(View.GONE);
        txtOfferPrice.setVisibility(View.GONE);
        imgButFindCode.setVisibility(View.GONE);
        imgButFindDesc.setVisibility(View.GONE);
        btnAdd.setVisibility(View.GONE);

        btnWatch.setText(R.string.btnExit);
        btnWatch.setBackgroundResource(R.drawable.btn_rounded);
        btnWatch.setActivated(true);

        txtError.setText(msg);

        btnWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("salio");
                System.exit(0);
            }
        });
    }
}
