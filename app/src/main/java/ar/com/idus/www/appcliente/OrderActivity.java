package ar.com.idus.www.appcliente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import ar.com.idus.www.appcliente.models.BodyOrder;
import ar.com.idus.www.appcliente.models.Company;
import ar.com.idus.www.appcliente.models.Customer;
import ar.com.idus.www.appcliente.models.HeadOrder;
import ar.com.idus.www.appcliente.models.OrderState;
import ar.com.idus.www.appcliente.models.Product;
import ar.com.idus.www.appcliente.utilities.Constants;
import ar.com.idus.www.appcliente.utilities.OrderAdapter;
import ar.com.idus.www.appcliente.utilities.ResponseObject;
import ar.com.idus.www.appcliente.utilities.SoftInputAssist;
import ar.com.idus.www.appcliente.utilities.Utilities;

public class OrderActivity extends AppCompatActivity {
    ArrayList<OrderState> listOrderState;
    Customer customer;
    EditText editDescription;
    ImageButton imgButFindDesc;
    OrderAdapter orderAdapter;
    Button btnWatch;
    Company company;
    ArrayList<Product> productList;
    ArrayList<Product> chosenProductsList;
    ListView listView;
    DecimalFormat format;
    HeadOrder headOrder;
    ArrayList<BodyOrder> listOrder;
    SimpleDateFormat formatter;
    Date date;
    SharedPreferences sharedPreferences;
    SoftInputAssist softInputAssist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Bundle bundle;

        editDescription = findViewById(R.id.editDescription);
        imgButFindDesc = findViewById(R.id.imgButFindDesc);
        btnWatch = findViewById(R.id.btnWatch);
        listView = findViewById(R.id.listProd);
        productList = new ArrayList<>();
        chosenProductsList = new ArrayList<>();
        headOrder = new HeadOrder();
        listOrder = new ArrayList<>();

        format = new DecimalFormat("#.00");
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        bundle = getIntent().getExtras();

        if (bundle == null) {
            showExit(getString(R.string.msgErrClientData));
            return;
        }

        date = new Date();
        headOrder.setDateStart(formatter.format(date));

        customer = (Customer) bundle.getSerializable("customer");
        company = (Company) bundle.getSerializable("company");

        if (customer == null) {
            showExit(getString(R.string.msgErrClientData));
            return;
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (company == null) {
            showExit(getString(R.string.msgErrDistribData));
            return;
        }

        softInputAssist = new SoftInputAssist(this);

        searchProducts("menta");

        imgButFindDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = editDescription.getText().toString().trim();

                if (desc.length() < 4) {
                    showMsg(getString(R.string.msgErrMinLength));
                    return;
                }

                closeKeyboard();

                searchProducts(editDescription.getText().toString());
            }
        });

        btnWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listOrder.isEmpty()) {
                    showMsg(getString(R.string.msgErrEmptyBasket));
                    return;
                }

                setHeadOrder();
                callBasket();
            }
        });

    }

    @Override
    protected void onResume() {
        softInputAssist.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        softInputAssist.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        softInputAssist.onDestroy();
        super.onDestroy();
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();

        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_orders, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.optWatchOrders) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false); // if you want user to wait for some process to finish,
            builder.setView(R.layout.layout_loading_dialog);
            final AlertDialog dialog = builder.create();
            dialog.show();

            Thread thread = new Thread() {
                @Override
                public void run() {
                    final ResponseObject responseListOrders = getListOrders();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseListOrders != null) {
                                switch (responseListOrders.getResponseCode()) {
                                    case Constants.OK:
                                        checkListOrders(responseListOrders.getResponseData());
                                        callOrderInquiry();
                                        break;

                                    case Constants.SHOW_ERROR:
                                        showMsg(responseListOrders.getResponseData());
                                        break;

                                    case Constants.SHOW_EXIT:
                                        showExit(responseListOrders.getResponseData());
                                        break;
                                }
                            }
                        }
                    });
                }
            };

            thread.start();
        }

        return super.onContextItemSelected(item);
    }

    private void setHeadOrder() {
        headOrder.setBodyOrders(listOrder);
        headOrder.setIdOrder(UUID.randomUUID().toString());
        headOrder.setIdCustomer(customer.getIdCliente());
    }

    private void checkProducts (String data) {
        ArrayList<Product> aux;
        Gson gson = new Gson();
        Product[] products = gson.fromJson(data, Product[].class);
        aux = new ArrayList<>(Arrays.asList(products));

        for (Product product: aux) {
            if (product.getStock() == null)
                product.setStock("0");

            if (product.getMultiple() == null || product.getMultiple().equals("0"))
                product.setMultiple("1");
        }

        productList = aux;
    }

    private void searchProducts(final String data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Thread thread = new Thread() {
            @Override
            public void run() {
                final ResponseObject auxResponseProducts = getProducts(data);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (auxResponseProducts != null) {
                            switch (auxResponseProducts.getResponseCode()) {
                                case Constants.OK:
                                    checkProducts(auxResponseProducts.getResponseData());
                                    orderAdapter = new OrderAdapter(OrderActivity.this, R.layout.order_item, productList, listOrder);
                                    listView.setAdapter(orderAdapter);
                                    break;

                                case Constants.SHOW_ERROR:
                                    showMsg(auxResponseProducts.getResponseData());
                                    break;

                                case Constants.SHOW_EXIT:
                                    showExit(auxResponseProducts.getResponseData());
                                    break;
                            }

                            if (dialog.isShowing())
                                dialog.cancel();
                        }
                    }
                });
            }
        };

        thread.start();
    }

    private ResponseObject getProducts(final String data) {
        String url = "/getProduct.php?token=" + Utilities.getData(sharedPreferences, "token") +
                "&idCompany=" + customer.getEmpresaId() + "&codePriceList=" + customer.getCodigoLista() +
                "&findDesc=" + data + "&findCode=";

        ResponseObject responseObject = Utilities.getResponse2(getApplicationContext(), url);
        ResponseObject responseToken;

        int code = responseObject.getResponseCode();

        if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
            responseObject = Utilities.getResponse(getApplicationContext(), url, 0);

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
                        "&idCompany=" + customer.getEmpresaId() + "&codePriceList=" + customer.getCodigoLista() +
                        "&findDesc=" + data + "&findCode=";
                responseObject = Utilities.getResponse2(getApplicationContext(), url);

                code = responseObject.getResponseCode();

                if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
                    responseObject = Utilities.getResponse2(getApplicationContext(), url);
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
                responseObject.setResponseData(getString(R.string.msgErrFindProd));
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

    private void checkListOrders(String data) {
        Gson gson = new Gson();
        OrderState[] orders = gson.fromJson(data, OrderState[].class);
        listOrderState = new ArrayList<>(Arrays.asList(orders));
    }

    private ResponseObject getListOrders() {
        String url = "/getB2BOrdersState.php?token=" + Utilities.getData(sharedPreferences, "token") +
                "&idCustomer=" +  customer.getIdCliente();

        ResponseObject responseObject = Utilities.getResponse2(getApplicationContext(), url);
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

            case Constants.NO_RESULT:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString(R.string.msgErrEmptyInquiry));
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

    private void showMsg(String msg) {
        if (!OrderActivity.this.isFinishing())
            Toast.makeText(OrderActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private void callBasket() {
        Intent intent = new Intent(getApplicationContext(), BasketActivity.class);
        intent.putExtra("customer", customer);
        intent.putExtra("order", headOrder);
        intent.putExtra("company", company);
        startActivity(intent);
    }

    private void callOrderInquiry () {
        Intent intent = new Intent(getApplicationContext(), OrderInquiryActivity.class);
        intent.putExtra("orders", listOrderState);
        intent.putExtra("customer", customer);
        intent.putExtra("company", company);
        startActivity(intent);
    }
}
