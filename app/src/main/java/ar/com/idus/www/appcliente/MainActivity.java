package ar.com.idus.www.appcliente;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ar.com.idus.www.appcliente.models.BodyOrder;
import ar.com.idus.www.appcliente.models.Customer;
import ar.com.idus.www.appcliente.models.HeadOrder;
import ar.com.idus.www.appcliente.utilities.Constants;
import ar.com.idus.www.appcliente.utilities.ResponseObject;
import ar.com.idus.www.appcliente.utilities.Utilities;

public class MainActivity extends AppCompatActivity {
    Button btnEnter;
    TextView txtIdCustomer;
    EditText editIdCustomer, editPassCustomer;
    Customer customer;
    SharedPreferences sharedPreferences;
    boolean firstEntry = false;
    ProgressBar progressBar;

    public boolean isFirstEntry() {
        return this.firstEntry;
    }

    public void setFirstEntry(boolean firstEntry) {
        this.firstEntry = firstEntry;
    }

    private void testingAPI() {


//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//        LocalDateTime now = LocalDateTime.now();
//        System.out.println(dtf.format(now));

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(formatter.format(date));

        List<ResponseObject> lista = new ArrayList<>();


        String url, token, id;
        ResponseObject response;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        url = "http://widus-app-bygvs.dyndns.info:8086/WebServiceIdusApp/findtelephone.php?token=c89cabc3a17a0132e9c22878c402da6f&idTelephone=11110000";


        for (int j = 1; j < 100; j++) {
            response = new ResponseObject();
            url = url + j;

            response = Utilities.getResponse(MainActivity.this, url, 10000);
            System.out.println(j + " " + response.getResponseCode());
            lista.add(response);
        }


        System.out.println();

            url = "http://idus-app-bygvs.dyndns.info:8086/WebServiceIdusApp/getToken.php?idApp=BuyIdus";
//            url = "http://widus-app-bygvs.dyndns.info:8086/WebServiceIdusApp/findtelephone.php?token=4cba21f9171fade0755cdbe72834821a&idTelephone=78521";
//        url = "http://widus-app-bygvs.dyndns.info:8086/WebServiceIdusApp/getCustomer.php?token=14cba21f9171fade0755cdbe72834821a&idCustomer=526CLIENTE0091";
//        response =  Utilities.getResponse(getApplicationContext(), url, 2000);

        token = "1";
        id = "1";



        token = Utilities.getData(sharedPreferences, "token");

        Utilities.saveData(sharedPreferences, "token", "12132154");

        token = Utilities.getData(sharedPreferences, "token");

        Utilities.deleteData(sharedPreferences, "token");

        token = Utilities.getData(sharedPreferences, "token");

        response = getCustomer(id);

        System.out.println("llego");
    }

    private void testScreen() {
//        try
//        {
//            progressBar.setVisibility(View.VISIBLE);
//            Thread.sleep(5000);
//        }
//        catch(InterruptedException ex)
//        {
//            Thread.currentThread().interrupt();
//        }
//
//        progressBar.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);

        Thread thread2 = new Thread() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
//                            sleep(10000);
                            progressBar.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
            }
        };

        thread2.start();


//        HeadOrder headOrder = new HeadOrder();
//        BodyOrder bodyOrder = new BodyOrder();
//        BodyOrder bodyOrder2 = new BodyOrder();
//        ArrayList<BodyOrder> bodyOrders = new ArrayList<>();
//
//        bodyOrder.setName("Comrpobante 22225F-087156");
//        bodyOrder.setIdItem("10");
//        bodyOrder.setQuantity("5");
//        bodyOrder.setPrice(20.50f);
//        bodyOrder.setIdProduct("526ARTICULO550");
//        bodyOrder.setTotal(205040.50f);
//        bodyOrders.add(bodyOrder);
//        bodyOrders.add(bodyOrder);
//        bodyOrders.add(bodyOrder);
//        bodyOrder2.setName("OREO CHOCOLATE 36X117GR");
//        bodyOrder2.setIdItem("10");
//        bodyOrder2.setQuantity("Cantidad: 125");
//        bodyOrder2.setPrice(2330.50f);
//        bodyOrder2.setIdProduct("526ARTICULO332");
//        bodyOrder2.setTotal(11205040.50f);
//        bodyOrders.add(bodyOrder2);
//        bodyOrders.add(bodyOrder);
//        bodyOrders.add(bodyOrder);
//        bodyOrders.add(bodyOrder);
//        bodyOrders.add(bodyOrder);
//
//        headOrder.setBodyOrders(bodyOrders);
//        Intent intent = new Intent(getApplicationContext(), BasketActivity.class);
//        intent.putExtra("order", headOrder);
//        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ResponseObject responseToken;
        String idPhone, idCustomer;

//        testingAPI();


//
        btnEnter = findViewById(R.id.btnEnter);
        txtIdCustomer = findViewById(R.id.txtIdCustomer);
        editIdCustomer = findViewById(R.id.editIdCustomer);
        editPassCustomer = findViewById(R.id.editPassCustomer);
        progressBar = findViewById(R.id.progressBar);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                   1);
        }
      //
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.INTERNET},
//                    1);
//        }


//        if (!Utilities.checkConnection(getApplicationContext())) {
//            showExit(getString(R.string.msgErrInternet));
//            return;
//        }

        if(Utilities.getData(sharedPreferences, "token").equals(Constants.NO_RESULT_STR)) {
            responseToken = Utilities.getNewToken(getApplicationContext(), sharedPreferences);

            if (responseToken == null) {
                showExit(getString(R.string.msgErrToken));
                return;
            }

            if (responseToken.getResponseCode() == Constants.SHOW_EXIT) {
                showExit(responseToken.getResponseData());
                return;
            }
        }

        idPhone = getIdPhone();
        idCustomer = Utilities.getData(sharedPreferences,"idCustomer");

        if (idCustomer.equals(Constants.NO_RESULT_STR))
            idCustomer = "";

        ResponseObject responsePhone = findPhone(idPhone);

        if (responsePhone == null) {
            showExit(getString(R.string.msgErrFind));
            return;
        }

        if (responsePhone.getResponseCode() == Constants.SHOW_EXIT) {
            showExit(responsePhone.getResponseData());
            return;
        }

        // TODO testing....
//        responsePhone.setResponseCode(Constants.CREATED);

        setFirstEntry(responsePhone.getResponseCode() == Constants.CREATED  || idCustomer.isEmpty());
        editIdCustomer.setText(idCustomer);

        if (!isFirstEntry())
            txtIdCustomer.setVisibility(View.GONE);
        else {
            editPassCustomer.setVisibility(View.GONE);
            System.out.println("primer response code findphone " + responsePhone.getResponseCode());
        }

        progressBar.setVisibility(View.GONE);

//        try
//        {
//            progressBar.setVisibility(View.GONE);
//            Thread.sleep(5000);
//        }
//        catch(InterruptedException ex)
//        {
//            Thread.currentThread().interrupt();
//        }


//        testScreen();


        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id;
                String pass;
                boolean firstTime =  isFirstEntry();
                ResponseObject responseCustomer;

                id = editIdCustomer.getText().toString();
                pass = editPassCustomer.getText().toString();

                if (firstTime && id.isEmpty()) {
                    showMsg(getString(R.string.msgErrorEmptyId));
                } else if (!firstTime && (id.isEmpty() || pass.isEmpty())){
                    showMsg(getString(R.string.msgErrorEmptyIdPass));
                } else {

                    responseCustomer = getCustomer(id);

                    if (responseCustomer != null) {
                        switch (responseCustomer.getResponseCode()) {
                            case Constants.OK:
                                checkCustomer(responseCustomer.getResponseData(), id, pass, firstTime);
                                System.out.println(responseCustomer.getResponseData());
                                break;

                            case Constants.SHOW_ERROR:
                                showMsg(responseCustomer.getResponseData());
                                break;

                            case Constants.SHOW_EXIT:
                                showExit(responseCustomer.getResponseData());
                                break;
                        }
                    }
                }
            }
        });
    }

    private void checkCustomer (String data, String id, String pass, boolean firstTime) {
        Gson gson = new Gson();
        Customer[] customers;

        customers = gson.fromJson(data, Customer[].class);
        customer = customers[0];

        if (!customer.getHabilidado().equals(Constants.ENABLED) ) {
            showExit(getString(R.string.msgDisabledCustomer));
            return;
        }

        customer.setIdCliente(id);

        if (!firstTime && !customer.getContrasena().equals(pass)) {
            showMsg(getString(R.string.msgErrWrongPass));
            return;
        }

        if (isFirstEntry() || customer.getEmailOtorgado().isEmpty() || customer.getContrasena().isEmpty() || customer.getDireccionOtorgada().isEmpty() || customer.getTelefonoOtorgado().isEmpty())
            callRegister();
        else
            callDistributor();
    }

    private void callDistributor() {
        Intent intent = new Intent(getApplicationContext(), DistributorActivity.class);
        intent.putExtra("customer", customer);
        startActivity(intent);
    }

    private void showExit(String msg) {
        Intent intent = new Intent(this, ErrorActivity.class);
        intent.putExtra("error", msg);
        startActivity(intent);
    }

    private void showMsg(String msg) {
        if (!MainActivity.this.isFinishing())
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private ResponseObject findPhone(String idPhone) {
        int code;
        String url = "/findtelephone.php?token=" + Utilities.getData(sharedPreferences, "token") + "&idTelephone=" + idPhone;
        ResponseObject responseToken;

        ResponseObject responseObject = Utilities.getResponse(getApplicationContext(), url, 0);
        code = responseObject.getResponseCode();


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
                url = "/findtelephone.php?token=" + responseToken.getResponseData() + "&idTelephone=" + idPhone;
                responseObject = Utilities.getResponse(getApplicationContext(), url, 0);

                code = responseObject.getResponseCode();

                if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
                    responseObject = Utilities.getResponse(getApplicationContext(), url, 0);
            }
        }

        switch (responseObject.getResponseCode()) {
            case Constants.OK:
                Utilities.saveData(sharedPreferences, "idPhone", idPhone);
                break;

            case Constants.NO_INTERNET:
                responseObject.setResponseCode(Constants.SHOW_EXIT);
                responseObject.setResponseData(getString(R.string.msgErrInternet));
                break;

            case Constants.SHOW_EXIT:
                break;

            case Constants.DISABLED:
                responseObject.setResponseCode(Constants.SHOW_EXIT);
                responseObject.setResponseData(getString(R.string.msgDisabledPhone));
                break;

            case Constants.CREATED: // primer ingreso
                Utilities.saveData(sharedPreferences, "idPhone", idPhone);
                System.out.println("primer ingreso");
                break;

            case Constants.NO_DATA:
                responseObject.setResponseCode(Constants.SHOW_EXIT);
                responseObject.setResponseData(getString(R.string.msgErrFind));
                break;

            case Constants.EXCEPTION:
                responseObject.setResponseCode(Constants.SHOW_EXIT);
                responseObject.setResponseData(getString(R.string.msgErrException) + " (" + responseObject.getResponseData() + ")");
                break;

            case Constants.SERVER_ERROR:
                responseObject.setResponseCode(Constants.SHOW_EXIT);
                responseObject.setResponseData(getString((R.string.msgErrServer)));
                break;

            case Constants.INVALID_TOKEN:
                responseObject.setResponseCode(Constants.SHOW_EXIT);
                responseObject.setResponseData(getString((R.string.msgErrToken)));
                break;
        }

        return responseObject;
    }

    private String getIdPhone() {
        String idPhone = Utilities.getData(sharedPreferences,"idPhone");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    1);
        }

        if (idPhone.equals(Constants.NO_RESULT_STR)) {
            idPhone = UUID.randomUUID().toString();

//            int min = 10000;
//            int max = 10100;
//
//            int random_int = (int)(Math.random() * (max - min + 1) + min);
//            idPhone = String.valueOf(random_int);

            Utilities.saveData(sharedPreferences, "idPhone", idPhone);
        }

        return idPhone;
    }

    private void callRegister() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        intent.putExtra("customer", customer);
        startActivity(intent);
    }

    private ResponseObject getCustomer(String id){
        String url = "/getCustomer.php?token=" + Utilities.getData(sharedPreferences, "token") + "&idCustomer=" + id;
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
                url = "/getCustomer.php?token=" + responseToken.getResponseData() + "&idCustomer=" + id;
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

            case Constants.DISABLED:
                responseObject.setResponseCode(Constants.SHOW_EXIT);
                responseObject.setResponseData(getString(R.string.msgDisabledPhone));
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
}
