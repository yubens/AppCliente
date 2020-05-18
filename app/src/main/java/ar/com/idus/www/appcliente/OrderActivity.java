package ar.com.idus.www.appcliente;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
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
import ar.com.idus.www.appcliente.utilities.Utilities;

public class OrderActivity extends AppCompatActivity {
    ArrayList<OrderState> listOrderState;
    Customer customer;
    EditText editDescription;
    ImageButton imgButFindDesc;
    ArrayAdapter<String> adapter;
    OrderAdapter orderAdapter;
    Button btnWatch;
    Company company;
    ArrayList<Product> productList;
    ArrayList<Product> chosenProductsList;
    ArrayList <String> stringProds;
    Product chosenProduct;
    ListView listView;
    DecimalFormat format;
    HeadOrder headOrder;
    BodyOrder bodyOrder;
    ArrayList<BodyOrder> listOrder;
    SimpleDateFormat formatter;
    Date date;
    int itemOrder;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Bundle bundle;

        editDescription = findViewById(R.id.editDescription);
        imgButFindDesc = findViewById(R.id.imgButFindDesc);
        btnWatch = findViewById(R.id.btnWatch);
//        editDescription.addTextChangedListener(watcherTxt);
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

        ResponseObject auxResponseProducts = getProducts(getString(R.string.defaultProduct), false);

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
        }

        imgButFindDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = editDescription.getText().toString().trim();

                if (desc.length() < 4) {
                    showMsg(getString(R.string.msgErrMinLength));
                    return;
                }

                ResponseObject auxResponseProducts = getProducts(editDescription.getText().toString(), false);

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
                }
            }
        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                listView.setVisibility(View.GONE);
//                chosenProduct = productList.get(position);
//                setProduct();
//            }
//        });


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

//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int multiple, quantity, stock;
//                float total;
//
//                if (chosenProduct == null) {
//                    showMsg(getString(R.string.msgErrChosenProd));
//                    return;
//                }
//
//                if (editQuantity.getText().toString().isEmpty()) {
//                    showMsg(getString(R.string.msgErrEmptyQuantity));
//                    return;
//                }
//
//                multiple = Integer.valueOf(chosenProduct.getMultiple());
//
//                if (multiple == 0)
//                    multiple = 1;
//
//                quantity = Integer.valueOf(editQuantity.getText().toString());
//                stock = Integer.valueOf(chosenProduct.getStock());
//
//                if ((quantity % multiple) != 0) {
//                    showMsg(getString(R.string.msgErrMultiple));
//                    return;
//                }
//
//                if (quantity > stock) {
//                    showMsg(getString(R.string.msgErrStock));
//                    return;
//                }
//
//                bodyOrder = new BodyOrder();
//                bodyOrder.setIdProduct(chosenProduct.getIdProduct());
//                bodyOrder.setPrice(chosenProduct.getRealPrice());
//                bodyOrder.setQuantityString(String.valueOf(quantity));
//                bodyOrder.setIdItem(String.valueOf(itemOrder++));
//                bodyOrder.setName(chosenProduct.getName());
//                total = chosenProduct.getRealPrice() * quantity;
//                bodyOrder.setTotal(total);
//                listOrder.add(bodyOrder);
//                showMsg(getString(R.string.msgSuccAddProd));
//                cleanUp();
//
//            }
//        });
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
            ResponseObject responseListOrders = getListOrders();

            if (responseListOrders != null) {
                switch (responseListOrders.getResponseCode()) {
                    case Constants.OK:
                        checkListOrders(responseListOrders.getResponseData());
                        callOrderInquiry();
                        System.out.println();
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

        return super.onContextItemSelected(item);
    }

    private void fillProducts () {
        stringProds = new ArrayList<>();
        for (Product product : productList) {
            stringProds.add(product.getName());
        }
    }

    private void setProduct() {
//        float price;
//        String priceString = "", multiple = "", stock, aux;
//
//        loadImage();
//        editDescription.setText(chosenProduct.getName());
//        editCode.setText(getString(R.string.editCode) + " " + chosenProduct.getCode());
//        editQuantity.setKeyListener(new DigitsKeyListener());
//        editQuantity.setText("");
//        multiple = getString(R.string.txtMultiple) +  " " + (chosenProduct.getMultiple().equals("0") ?  "1" : chosenProduct.getMultiple());
//        txtMultiple.setText(multiple);
//        stock = Integer.valueOf(chosenProduct.getStock()) > 0 ? getString(R.string.avilableProd) : getString(R.string.notAvailableProd) ;
//        txtStock.setText(stock);
//        txtStock.setVisibility(View.VISIBLE);
//        txtTotal.setText(getString(R.string.txtTotal));
//
//        if(chosenProduct.getOfferPrice() != null && !chosenProduct.getOfferPrice().isEmpty() && !chosenProduct.getOfferPrice().equals("0")) {
//            aux = chosenProduct.getOfferPrice();
//
//        } else if (chosenProduct.getListPrice02() != null && !chosenProduct.getListPrice02().isEmpty() && !chosenProduct.getListPrice02().equals("0"))
//            aux = chosenProduct.getListPrice02();
//        else if (chosenProduct.getListPrice01() != null && !chosenProduct.getListPrice01().isEmpty() && !chosenProduct.getListPrice01().equals("0"))
//            aux = chosenProduct.getListPrice01();
//        else if (chosenProduct.getListPrice00() != null && !chosenProduct.getListPrice00().isEmpty() && !chosenProduct.getListPrice00().equals("0"))
//            aux = chosenProduct.getListPrice00();
//        else
//            aux = chosenProduct.getSalePrice00();
//
//        price = Utilities.roundNumber(aux);
//        priceString = String.format("%.2f", price);
//        priceString = getString(R.string.txtPrice) + " " + priceString;
//
//        chosenProduct.setRealPrice(price);
//
//        editQuantity.addTextChangedListener(watcher);
//        txtPrice.setText(priceString);
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            float amount, result;
            String total = "";
//            if (s.length() > 0) {
//                amount = Float.parseFloat(s.toString());
//                result = chosenProduct.getRealPrice() * amount;
//                total = getString(R.string.txtTotal ) + " " + String.format("%.2f", result);
//                txtTotal.setText(total);
//            }
//
//            if(editQuantity.getText().toString().isEmpty()){
//                txtTotal.setText(R.string.txtTotal);
//            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher watcherTxt = new TextWatcher() {
        String previous;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            previous = charSequence.toString();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (charSequence.toString().isEmpty())
                listView.setVisibility(View.GONE);

            if (charSequence.toString().isEmpty() && editDescription.getText().toString().isEmpty() && !previous.isEmpty())
                cleanUp();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher watcherAuto = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            System.out.println();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (charSequence.length() < 3)
                return;

            System.out.println("start " + start);
            System.out.println("before " + before);
            System.out.println("count " + count);

            ResponseObject auxResponseProducts = getProducts(charSequence.toString(), false);

            if (auxResponseProducts != null) {
                switch (auxResponseProducts.getResponseCode()) {
                    case Constants.OK:
                        //TODO
                        checkProducts(auxResponseProducts.getResponseData());
                        //mostar productos para que se elija uno

                        fillProducts();

                        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, stringProds);
                        listView.setAdapter(adapter);
                        listView.setVisibility(View.VISIBLE);

                        break;

                    case Constants.SHOW_ERROR:
                        showMsg(auxResponseProducts.getResponseData());
                        break;

                    case Constants.SHOW_EXIT:
                        showExit(auxResponseProducts.getResponseData());
                        break;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

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

        productList = aux;
    }

    private ResponseObject getCompany (String id) {
        String url = "/getCompany.php?token=" + Utilities.getData(sharedPreferences, "token") + "&idCompany=" + id;
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
                url = "/getCompany.php?token=" + responseToken.getResponseData() + "&idCustomer=" + id;
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

    private ResponseObject getProducts(String data, boolean isCode) {
        String prodDesc = "", prodCode = "";

        if (isCode)
            prodCode = data;
        else
            prodDesc = data;

        String url = "/getProduct.php?token=" + Utilities.getData(sharedPreferences, "token") +
                        "&idCompany=" + customer.getEmpresaId() + "&codePriceList=" + customer.getCodigoLista() +
                        "&findDesc=" + prodDesc + "&findCode=" + prodCode;

        ResponseObject responseObject = Utilities.getResponse(getApplicationContext(), url, 0);
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
                        "&findDesc=" + prodDesc + "&findCode=" + prodCode;
                responseObject = Utilities.getResponse(getApplicationContext(), url, 0);

                code = responseObject.getResponseCode();

                if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
                    responseObject = Utilities.getResponse(getApplicationContext(), url, 0);
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
                cleanUp();
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

    private void cleanUp() {
//        imgProduct.setVisibility(View.GONE);
//        txtStock.setVisibility(View.GONE);
//        chosenProduct = null;
//        editQuantity.setText("");
//        editQuantity.setKeyListener(null);
//        editDescription.setText("");
//        editCode.setText("");
//        txtPrice.setText(R.string.txtPrice);
//        txtTotal.setText(R.string.txtTotal);
//        txtMultiple.setText(R.string.txtMultiple);
//        txtStock.setText(R.string.txtStock);
//        listView.setVisibility(View.GONE);
    }

    private void callBasket() {
        Intent intent = new Intent(getApplicationContext(), BasketActivity.class);
        intent.putExtra("customer", customer);
        intent.putExtra("order", headOrder);
//        intent.putExtra("order", testing(headOrder));
        intent.putExtra("company", company);
        startActivity(intent);
    }

    private HeadOrder testing (HeadOrder headOrder) {
        BodyOrder order;
        ArrayList<BodyOrder> list = new ArrayList<>();

        for (int i = 1 ; i < 31; i++) {
            order = new BodyOrder();
            order.setUpdatedStock(i);
            order.setName("Producto " + i);

            if (i%2 == 0)
                order.setPrice(10);
            else
                order.setPrice(20);

            order.setQuantity(i);
            order.setTotal(i * order.getPrice());
            order.setIdProduct("ID " + i);
            list.add(order);
        }

        headOrder.setBodyOrders(list);
        return  headOrder;

    }

    private void callOrderInquiry () {
        Intent intent = new Intent(getApplicationContext(), OrderInquiryActivity.class);
        intent.putExtra("orders", listOrderState);
        intent.putExtra("customer", customer);
        intent.putExtra("company", company);
        startActivity(intent);
    }
}
