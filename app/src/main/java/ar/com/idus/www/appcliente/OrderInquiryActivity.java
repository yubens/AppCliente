package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import ar.com.idus.www.appcliente.models.Customer;
import ar.com.idus.www.appcliente.models.OrderState;
import ar.com.idus.www.appcliente.models.Product;
import ar.com.idus.www.appcliente.utilities.Constants;
import ar.com.idus.www.appcliente.utilities.ListOrderAdapter;
import ar.com.idus.www.appcliente.utilities.ResponseObject;
import ar.com.idus.www.appcliente.utilities.Utilities;

public class OrderInquiryActivity extends AppCompatActivity {
    Customer customer;
    SharedPreferences sharedPreferences;
    ArrayList<OrderState> listOrders;
    ListOrderAdapter adapter;
    Button btnExit, btnNewOrder;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_inquiry);

        Bundle bundle;

        bundle = getIntent().getExtras();

        if (bundle == null) {
//            Toast.makeText(getApplicationContext(), R.string.msgErrCustomerData, Toast.LENGTH_LONG).show();
            showExit(getString(R.string.msgErrClientData));
            return;
        }

        customer = (Customer) bundle.getSerializable("customer");

        if (customer == null) {
//            Toast.makeText(getApplicationContext(), R.string.msgErrCustomerData, Toast.LENGTH_LONG).show();
            showExit(getString(R.string.msgErrClientData));
            return;
        }

        btnExit = findViewById(R.id.btnExitApp);
        btnNewOrder = findViewById(R.id.btnNewOrder);
        listView = findViewById(R.id.listOrdersInquiry);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        ResponseObject responseListOrders = getListOrders();

        if (responseListOrders != null) {
            switch (responseListOrders.getResponseCode()) {
                case Constants.OK:
                    checkListOrders(responseListOrders.getResponseData());
                    adapter = new ListOrderAdapter(getApplicationContext(), R.layout.order_item, listOrders);
                    listView.setAdapter(adapter);
                    System.out.println();
                    break;

                case Constants.SHOW_ERROR:
                    Utilities.showMsg(responseListOrders.getResponseData(), getApplicationContext());
                    break;

                case Constants.SHOW_EXIT:
                    showExit(responseListOrders.getResponseData());
                    break;
            }
        }

    }

    private void checkListOrders(String data) {
        Gson gson = new Gson();
        OrderState[] orders = gson.fromJson(data, OrderState[].class);
        listOrders = new ArrayList<>(Arrays.asList(orders));
    }

    private ResponseObject getListOrders() {
        String url = "/getB2BOrdersState.php?token=" + Utilities.getData(sharedPreferences, "token") +
                "&idCustomer=" + customer.getIdCliente();

        ResponseObject responseObject = Utilities.getResponse(getApplicationContext(), url, 5000);
        ResponseObject responseToken;

        int code = responseObject.getResponseCode();

        if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
            responseObject = Utilities.getResponse(getApplicationContext(), url, 5000);

        if (responseObject.getResponseCode() == Constants.INVALID_TOKEN) {
            responseToken = Utilities.getNewToken(getApplicationContext(), sharedPreferences);

            if (responseToken == null) {
                responseObject.setResponseCode(Constants.SHOW_EXIT);
                responseObject.setResponseData(getString(R.string.msgErrToken));

            } else if (responseToken.getResponseCode() == Constants.SHOW_EXIT) {
                responseObject.setResponseCode(Constants.SHOW_EXIT);
                responseObject.setResponseData(responseToken.getResponseData());
            } else {
                url = "/getB2BOrdersState.php?token=" + responseToken.getResponseData() +
                        "&idCustomer=" + customer.getIdCliente();

                responseObject = Utilities.getResponse(getApplicationContext(), url, 5000);

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
                responseObject.setResponseData(getString(R.string.msgErrOrderInquiry));
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

    private void showExit(String msg) { }
}
