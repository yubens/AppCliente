package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import ar.com.idus.www.appcliente.models.Client;
import ar.com.idus.www.appcliente.models.Customer;
import ar.com.idus.www.appcliente.utilities.Constants;
import ar.com.idus.www.appcliente.utilities.ResponseObject;
import ar.com.idus.www.appcliente.utilities.Utilities;


public class MainActivity extends AppCompatActivity {
    Button btnEnter;
    TextView txtIdCustomer;
    EditText editIdCustomer, editPassCustomer;
    Client client;
    Customer customer;
    String token;
    SharedPreferences sharedPreferences;
    boolean firstEntry = false;


    public boolean isFirstEntry() {
        return this.firstEntry;
    }

    public void setFirstEntry(boolean firstEntry) {
        this.firstEntry = firstEntry;
    }

    private void testingAPI() {
        String url, token, id;
        ResponseObject response;


            url = "http://idus-app-bygvs.dyndns.info:8086/WebServiceIdusApp/getToken.php?idApp=BuyIdus";
//            url = "http://widus-app-bygvs.dyndns.info:8086/WebServiceIdusApp/findtelephone.php?token=4cba21f9171fade0755cdbe72834821a&idTelephone=78521";
//        url = "http://widus-app-bygvs.dyndns.info:8086/WebServiceIdusApp/getCustomer.php?token=14cba21f9171fade0755cdbe72834821a&idCustomer=526CLIENTE0091";
//        response =  Utilities.getResponse(getApplicationContext(), url, 2000);

        token = "1";
        id = "1";

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        token = Utilities.getData(sharedPreferences, "token");

        Utilities.saveData(sharedPreferences, "token", "12132154");

        token = Utilities.getData(sharedPreferences, "token");

        Utilities.deleteData(sharedPreferences, "token");

        token = Utilities.getData(sharedPreferences, "token");

        response = getCustomer(id);

        System.out.println("llego");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int result;
        ResponseObject responseToken;
        String token = null, idPhone, idCustomer;
        int msg = 0;

//        testingAPI();

        btnEnter = findViewById(R.id.btnEnter);
        txtIdCustomer = findViewById(R.id.txtIdCustomer);
        editIdCustomer = findViewById(R.id.editIdCustomer);
        editPassCustomer = findViewById(R.id.editPassCustomer);
//        sharedPreferences = getPreferences(MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (!Utilities.checkConnection(getApplicationContext())) {
//            showMsg((R.string.msgErrInternet));
            showExit(getString(R.string.msgErrInternet));

            return;
        }

        System.out.println("pasando internet off");


        if(Utilities.getData(sharedPreferences, "token").equals(Constants.NO_RESULT)) {
            responseToken = Utilities.getNewToken(getApplicationContext(), sharedPreferences);

            if (responseToken == null) {
                showExit(getString(R.string.msgErrToken));
                return;
            }

            System.out.println("pasando response token null");

            if (responseToken.getResponseCode() == Constants.SHOW_EXIT) {
                showExit(responseToken.getResponseData());
                return;
            }
        }

        idPhone = getIdPhone();
        idCustomer = Utilities.getData(sharedPreferences,"idCustomer");

        if (idCustomer.equals(Constants.NO_RESULT))
            idCustomer = "";

        final ResponseObject responsePhone = findPhone2(idPhone);

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

        setFirstEntry(responsePhone.getResponseCode() == Constants.CREATED );

        editIdCustomer.setText(idCustomer);

        if (!isFirstEntry())
            txtIdCustomer.setVisibility(View.GONE);
        else
            editPassCustomer.setVisibility(View.GONE);

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
//                    client = getClient(id);
                    responseCustomer = getCustomer(id);

                    if (responseCustomer != null) {
                        switch (responseCustomer.getResponseCode()) {
                            case Constants.OK:
                                Utilities.saveData(sharedPreferences,"idCustomer", id);
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
            callOrder();
    }

    private void showExit(String msg) {
        editPassCustomer.setVisibility(View.GONE);
        editIdCustomer.setVisibility(View.GONE);
        txtIdCustomer.setVisibility(View.VISIBLE);
        txtIdCustomer.setText(msg);
        txtIdCustomer.setTextSize(18);
        btnEnter.setText(R.string.btnExit);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("salio");
                System.exit(0);
            }
        });
    }

    private void showMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        System.out.println(msg);
    }

    private ResponseObject findPhone2(String idPhone) {
        int code;
        String url = "/findtelephone.php?token=" + Utilities.getData(sharedPreferences, "token") + "&idTelephone=" + idPhone;
        ResponseObject responseToken;

        ResponseObject responseObject = Utilities.getResponse(getApplicationContext(), url, 1000);
        code = responseObject.getResponseCode();


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
                url = "/findtelephone.php?token=" + responseToken.getResponseData() + "&idTelephone=" + idPhone;
                responseObject = Utilities.getResponse(getApplicationContext(), url, 1000);

                code = responseObject.getResponseCode();

                if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
                    responseObject = Utilities.getResponse(getApplicationContext(), url, 2000);
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
//                firstEntry = true;
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

    private int findPhone(final String token, final String idPhone) {
        int result = Constants.NO_DATA;
        final AtomicInteger response = new AtomicInteger();

        if (Utilities.checkConnection(getApplicationContext())) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    URL url;
                    HttpURLConnection cnx = null;
                    super.run();

                    try {
                        url = new URL(Constants.URL + "/findtelephone.php?token=" + token + "&idTelephone=" + idPhone);
                        cnx = (HttpURLConnection) url.openConnection();

                        System.out.println("response code findphone " + cnx.getResponseCode());

                        response.set(cnx.getResponseCode());

                    } catch (Exception e) {
                        response.set(Constants.EXCEPTION);
                        e.printStackTrace();


                    } finally {
                        if (cnx != null) {
                            cnx.disconnect();
                        }

                    }
                }
            };

            try {
                thread.start();
                thread.join(1500);
                result = response.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return  result;

        } else
            return result;
    }

    private String getIdPhone() {
        String idPhone = Utilities.getData(sharedPreferences,"idPhone");

        if (idPhone.equals(Constants.NO_RESULT)) {
            idPhone = UUID.randomUUID().toString();
            Utilities.saveData(sharedPreferences, "idPhone", idPhone);
        }

        System.out.println("id phone " + idPhone);

        return idPhone;
    }

//    private String getToken() {
//        String token = Utilities.getData(sharedPreferences, "token");
//        ResponseObject responseObject;
//
//        if (token.equals(Constants.NO_RESULT)) {
//            System.out.println("token no encontrado");
//
//            token = Utilities.getToken(getApplicationContext()); // primer intento de obtencion
//
//            responseObject = Utilities.getResponse(getApplicationContext(), "/getToken.php?idApp=BuyIdus", 1000);
//
//            if (token == null || token.equals(Constants.NO_RESULT)) {
//                token = Utilities.getToken(getApplicationContext()); // segundo intento
//                System.out.println("segundo intento token " + token);
//            }
//
//            System.out.println("token " + token);
//
//        } else //TODO borrar, solo para test
//            System.out.println("token encontrado " + token);
//
//        return token;
//    }

    private void callRegister() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        intent.putExtra("customer", customer);
        startActivity(intent);
    }

    private void callOrder() {
        Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
        intent.putExtra("customer", customer);
        startActivity(intent);
    }

    private Client getClient(String id) {
        Client client = null;
        if(Utilities.checkConnection(getApplicationContext())) {
            // TODO
            // buscar al cliente por idPhone, recuperar los datos y devolver objeto los datos recibidos

            client = new Client();

            if (id.equals("1")) {
                client.setId("1111");
                client.setAddress("Almafuerte 2020, Mendoza");
                client.setName("Pepe Honguito");
                client.setPhone("2612223333");
                client.setEmail("pepe@honguito.com");
            } else {
                client.setId("4444");
                client.setAddress("Adolfo Calle 450, Mendoza");
                client.setName("Fulanito de TAl");
                client.setPhone("1155554444");
                client.setEmail("fulatino@tal.com");
            }

        }

        return client;
    }

    private ResponseObject getCustomer(String id){
        String url = "/getCustomer.php?token=" + Utilities.getData(sharedPreferences, "token") + "&idCustomer=" + id;
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
                url = "/getCustomer.php?token=" + responseToken.getResponseData() + "&idCustomer=" + id;
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
