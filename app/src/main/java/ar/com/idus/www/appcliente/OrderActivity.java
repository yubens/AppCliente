package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import ar.com.idus.www.appcliente.models.Company;
import ar.com.idus.www.appcliente.models.Customer;
import ar.com.idus.www.appcliente.models.Distributor;
import ar.com.idus.www.appcliente.models.Product;
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
    Button btnAdd, btnWatch, btnExit;
    Company company;
    ArrayList<Product> productList;
    ArrayList<Product> chosenProductsList;
    Product chosenProduct;
    ListView listView;
    ImageView imgProduct;
    DecimalFormat format = new DecimalFormat("#.00");

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
        btnExit = findViewById(R.id.btnExit);
        btnExit.setVisibility(View.GONE);
        imgProduct = findViewById(R.id.imgProduct);

        listView = new ListView(getApplicationContext());
        imgProduct.setVisibility(View.GONE);
        editQuantity.setKeyListener(null);

//        listView.setVisibility(View.GONE);

        productList = new ArrayList<>();
        chosenProductsList = new ArrayList<>();


        ResponseObject responseCompany;
        final ResponseObject responseProducts;
        Bundle bundle;

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

                ResponseObject auxResponseProducts = getProducts(editCode.getText().toString(), false);

                if (auxResponseProducts != null) {
                    switch (auxResponseProducts.getResponseCode()) {
                        case Constants.OK:
                            //TODO
                            //mostar productos para que se elija uno

                            break;

                        case Constants.SHOW_ERROR:
                            Utilities.showMsg(auxResponseProducts.getResponseData(), getApplicationContext());
                            break;

                        case Constants.SHOW_EXIT:
                            showExit(auxResponseProducts.getResponseData());
                            break;
                    }
                }


            }
        });

        imgButFindCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = editCode.getText().toString();

                if (code. isEmpty())
                    Utilities.showMsg(getString(R.string.msgErrFindCode), getApplicationContext());

                ResponseObject auxResponseProducts = getProducts(code, true);

                if (auxResponseProducts != null) {
                    switch (auxResponseProducts.getResponseCode()) {
                        case Constants.OK:
                            checkProducts(auxResponseProducts.getResponseData());
                            chosenProduct = productList.get(0);
                            setProduct();
                            break;

                        case Constants.SHOW_ERROR:
                            Utilities.showMsg(auxResponseProducts.getResponseData(), getApplicationContext());
                            break;

                        case Constants.SHOW_EXIT:
                            showExit(auxResponseProducts.getResponseData());
                            break;
                    }
                }
            }
        });

        btnWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productList.isEmpty()) {
                    Utilities.showMsg(getString(R.string.msgErrEmptyBasket), getApplicationContext());
                    return;
                }

                callBasket();

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int multiple, quantity;
                if (chosenProduct == null) {
                    Utilities.showMsg(getString(R.string.msgErrChosenProd), getApplicationContext());
                    return;
                }

                if (editQuantity.getText().toString().isEmpty()) {
                    Utilities.showMsg(getString(R.string.msgErrEmptyQuantity), getApplicationContext());
                    return;
                }

                multiple = Integer.valueOf(txtMultiple.getText().toString());
                quantity = Integer.valueOf(editQuantity.getText().toString());

                if ((quantity % multiple) != 0) {
                    Utilities.showMsg(getString(R.string.msgErrMultiple), getApplicationContext());
                    return;
                }

                productList.add(chosenProduct);
                chosenProduct = null;


            }
        });


//        if (bundle != null) {
//            distributors = (ArrayList<Distributor>) bundle.getSerializable("distributors");
//            distributor = distributors.get(0);
//
//            textView.setText("ORDER " + distributor.getName()) ;
//        }
    }

    private void setProduct() {
        //TODO
        // crear producto con los datos necesarios para enviar al carrito. cantidad, precio total, etc, segun web service

        //TODO
        // preguntar si se muestra precio de venta y de oferta a la vez
        // stock no viene

        float salePrice,offerPrice, total;
        String salePriceS = "", offerPriceS = "", totalS = "", multiple = "", stock;

        loadImage();
        editCode.setText("");
        editDescription.setText("");
        editQuantity.setKeyListener(new DigitsKeyListener());
        multiple = getString(R.string.txtMultiple) +  " " + chosenProduct.getMultiple();
        txtMultiple.setText(multiple);

        if(chosenProduct.getOfferPrice() != null && !chosenProduct.getOfferPrice().isEmpty() && !chosenProduct.getOfferPrice().equals("0")) {
            offerPrice = Utilities.roundNumber(chosenProduct.getOfferPrice());
            offerPriceS = String.format("%.2f", offerPrice);
            offerPriceS = getString(R.string.txtOfferPrice) + " " + offerPriceS;
        }

        if (chosenProduct.getListPrice02() != null && !chosenProduct.getListPrice02().isEmpty() && !chosenProduct.getListPrice02().equals("0"))
            salePriceS = chosenProduct.getListPrice02();
        else if (chosenProduct.getListPrice01() != null && !chosenProduct.getListPrice01().isEmpty() && !chosenProduct.getListPrice01().equals("0"))
            salePriceS = chosenProduct.getListPrice01();
        else if (chosenProduct.getListPrice00() != null && !chosenProduct.getListPrice00().isEmpty() && !chosenProduct.getListPrice00().equals("0"))
            salePriceS = chosenProduct.getListPrice00();
        else
            salePriceS = chosenProduct.getSalePrice00();

        salePrice = Utilities.roundNumber(salePriceS);
        salePriceS = String.format("%.2f", salePrice);

        salePriceS = getString(R.string.txtSalePrice) + " " + salePriceS;

        txtOfferPrice.setText(salePriceS);
        txtSalePrice.setText(salePriceS);

        totalS = getString(R.string.txtTotal) + salePriceS;

        txtTotal.setText(salePriceS);

        System.out.println();

        // TODO
        // alinear cantidad, mulitplo y stock
        // mostar un solo precio (venta u oferta) y total


    }

    private void loadImage() {}

    private void checkCompany(String data) {
        Gson gson = new Gson();

        Company[] companies;

        companies = gson.fromJson(data, Company[].class);
        company = companies[0];
    }

    private void checkProducts (String data) {
        Gson gson = new Gson();

        Product[] products = gson.fromJson(data, Product[].class);

        productList = new ArrayList<>(Arrays.asList(products));

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
                url = "/getProduct.php?token=" +  responseToken.getResponseData() +
                        "&idCompany=" + customer.getEmpresaId() + "&codePriceList=" + customer.getCodigoLista() +
                        "&findDesc=" + prodDesc + "&findCode=" + prodCode;
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
        btnWatch.setVisibility(View.GONE);

        btnExit.setVisibility(View.VISIBLE);

        btnExit.setText(R.string.btnExit);

        txtError.setText(msg);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("salio");
                System.exit(0);
            }
        });
    }

    private void callBasket() {
        Intent intent = new Intent(getApplicationContext(), BasketActivity.class);
        intent.putExtra("productList", productList);
        startActivity(intent);
    }
}
