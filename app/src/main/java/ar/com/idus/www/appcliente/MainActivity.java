package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    SharedPreferences sharedPreferences;
    boolean firstEntry = false;


    public boolean getFirstEntry() {
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
        sharedPreferences = getPreferences(MODE_PRIVATE);

        response = findPhone2(token, id);

        System.out.println("llego");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int result;
//       , back = false;
        String token = null, idPhone, idCustomer;
        int msg = 0;

//        testingAPI();

        btnEnter = findViewById(R.id.btnEnter);
        txtIdCustomer = findViewById(R.id.txtIdCustomer);
        editIdCustomer = findViewById(R.id.editIdCustomer);
        editPassCustomer = findViewById(R.id.editPassCustomer);
        sharedPreferences = getPreferences(MODE_PRIVATE);

        if (!Utilities.checkConnection(getApplicationContext())) {
//            showMsg((R.string.msgErrInternet));
            showExit(getString(R.string.msgErrInternet));

            return;
        }

        System.out.println("pasando internet off");
//        token = getToken();

        ResponseObject responseToken = Utilities.getNewToken(getApplicationContext(), sharedPreferences);

        if (responseToken == null) {
            showExit(getString(R.string.msgErrToken));
            return;
        }

        System.out.println("pasando response token null");

        if (responseToken.getResponseCode() == Constants.SHOW_EXIT) {
            showExit(responseToken.getResponseData());
            return;
        }

        System.out.println("pasando exit");

//                else
        token = responseToken.getResponseData();

//                switch (responseToken.getResponseCode()) {
//                    case Constants.OK:
//                        token = responseToken.getResponseData();
//                        Utilities.saveData(sharedPreferences, "token", token);
//                        System.out.println("token guardado " + token);
//                        break;
//
//                    case Constants.NO_DATA:
//                        showExit(getString(msgErrToken));
//                        break;
//
//                    case Constants.EXCEPTION:
//                        showExit(getString(R.string.msgErrException) + " (" + responseToken.getResponseData() + ")");
//
//                    case Constants.SERVER_ERROR:
//                        showExit(getString(R.string.msgErrEServer));
//                }


        idPhone = getIdPhone();
        idCustomer = Utilities.getData(sharedPreferences,"idCustomer");

        if (idCustomer.equals(Constants.NO_RESULT))
            idCustomer = "";

        ResponseObject responsePhone = findPhone2(token, idPhone);

        if (responsePhone == null) {
            showExit(getString(R.string.msgErrFind));
            return;
        }

        if (responsePhone.getResponseCode() == Constants.SHOW_EXIT) {
            showExit(responsePhone.getResponseData());
            return;
        }

        // TODO testing....
        responsePhone.setResponseCode(Constants.CREATED);

        setFirstEntry(responsePhone.getResponseCode() == Constants.CREATED );



        editIdCustomer.setText(idCustomer);

        if (!getFirstEntry())
            txtIdCustomer.setVisibility(View.GONE);
        else
            editPassCustomer.setVisibility(View.GONE);



        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id;
                String pass;
                boolean firstTime =  getFirstEntry();


//                firstEntry

                id = editIdCustomer.getText().toString();
                pass = editPassCustomer.getText().toString();

                if (firstTime && id.isEmpty()) {
                    showMsg(R.string.msgErrorEmptyId);
                } else if (!firstTime && (id.isEmpty() || pass.isEmpty())){
                    showMsg(R.string.msgErrorEmptyIdPass);
                } else {
                    Utilities.saveData(sharedPreferences,"idCustomer", id);
                    client = getClient(id);

                    if (client != null)
                        callActivity();
                    else
                        showMsg(R.string.msgErrClientData);

                }

            }
        });




//        // primer intento de busqueda
//        result = findPhone(token, idPhone);
//
//            if (result == 0 || result == Constants.INVALID_TOKEN) {
//                System.out.println("A - segundo intento de busqueda " + result);
//                token = getToken();
//                result = findPhone(token, idPhone);
//            }
//
//            if (result == 0 || result == Constants.INVALID_TOKEN) { // segundo intento
//                System.out.println("B - segundo intento de busqueda " + result);
//                token = getToken();
//                result = findPhone(token, idPhone);
//            }
//
//            switch (result) {
//                case 0:
//                    System.out.println("segundo intento fallido");
//                    msg = R.string.msgErrFind;
//                    back = true;
//                    break;
//
//                case Constants.INVALID_TOKEN:
//                    System.out.println("segundo token invalido");
//                    msg = R.string.msgErrFind;
//                    back = true;
//                    break;
//
//                case Constants.DISABLED:
//                    msg = R.string.msgDisabledPhone;
//                    back = true;
//                    break;
//
//                case Constants.SERVER_ERROR:
//                    msg = R.string.msgErrFind;
//                    back = true;
//                    break;
//
//                case Constants.CREATED: // primer ingreso
//                    Utilities.saveData(sharedPreferences, "idPhone", idPhone);
//                    System.out.println("primer ingreso");
//                    firstEntry = true;
//                    break;
//
//                case Constants.OK:// ingresos posteriores
//                    System.out.println("ingresos posteriores");
//                    break;
//
//                default:
//                    System.out.println("error" + result);
//                    break;
//
//            }

//            if (msg == 0) {
//                Utilities.saveData(sharedPreferences, "token", token);
//                System.out.println("token guardado in " + token);
//
//                if (firstEntry) {
//                    editPassCustomer.setVisibility(View.GONE);
//
//                    btnEnter.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            String id;
//
//                            id = editIdCustomer.getText().toString();
//
//                            if (id.isEmpty())
//                                showMsg(R.string.msgErrorEmptyId);
//
//                            else {
//                                Utilities.saveData(sharedPreferences,"idCustomer", id);
//                                client = getClient(id);
//
//                                if (client != null)
//                                    callActivity();
//                                else
//                                    showMsg(R.string.msgErrClientData);
//
//                            }
//
//                        }
//                    });
//                }
//                else {
//                    txtIdCustomer.setVisibility(View.GONE);
//                    editIdCustomer.setText(idCustomer);
//
//                    btnEnter.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            String id;
//                            String pass;
//
//                            id = editIdCustomer.getText().toString();
//                            pass = editPassCustomer.getText().toString();
//
//                            if (id.isEmpty() || pass.isEmpty())
//                                showMsg(R.string.msgErrorEmptyIdPass);
//
//                            else {
//                                Utilities.saveData(sharedPreferences,"idCustomer", id);
//                                client = getClient(id);
//
//                                if (client != null)
//                                    callActivity();
//                                else
//                                    showMsg(R.string.msgErrClientData);
//
//                            }
//
//                        }
//                    });
//                }
//
//            } else {
//                showMsg(msg);
//                editPassCustomer.setVisibility(View.GONE);
//                editIdCustomer.setVisibility(View.GONE);
//                txtIdCustomer.setText(msg);
//                btnEnter.setText(R.string.btnExit);
//
//                btnEnter.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        System.out.println("salio");
//                        System.exit(0);
//                    }
//                });
//            }
//        }


    }

    private void showExit(String msg) {
        editPassCustomer.setVisibility(View.GONE);
        editIdCustomer.setVisibility(View.GONE);
        txtIdCustomer.setText(msg);
        btnEnter.setText(R.string.btnExit);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("salio");
                System.exit(0);
            }
        });
    }

    private void showMsg(int msg) {
        Toast.makeText(getApplicationContext(), getString(msg), Toast.LENGTH_LONG).show();
        System.out.println(getString(msg));
    }

    private ResponseObject findPhone2(String token, String idPhone) {
        int code;
        String url = "/findtelephone.php?token=" + token + "&idTelephone=" + idPhone;
        ResponseObject responseToken;

        ResponseObject responseObject = Utilities.getResponse(getApplicationContext(), url, 1000);
        code = responseObject.getResponseCode();


        if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
            responseObject = Utilities.getResponse(getApplicationContext(), url, 2000);

//        if (responsePhone.getResponseCode() == Constants.INVALID_TOKEN)



        if (responseObject.getResponseCode() == Constants.INVALID_TOKEN) {
            responseToken = Utilities.getNewToken(getApplicationContext(), sharedPreferences);

            if (responseToken == null) {
                responseObject.setResponseCode(Constants.SHOW_EXIT);
                responseObject.setResponseData(getString(R.string.msgErrToken));

//                showExit();
//                return;
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

    private String getToken() {
        String token = Utilities.getData(sharedPreferences, "token");
        ResponseObject responseObject;

        if (token.equals(Constants.NO_RESULT)) {
            System.out.println("token no encontrado");

            token = Utilities.getToken(getApplicationContext()); // primer intento de obtencion

            responseObject = Utilities.getResponse(getApplicationContext(), "/getToken.php?idApp=BuyIdus", 1000);

            if (token == null || token.equals(Constants.NO_RESULT)) {
                token = Utilities.getToken(getApplicationContext()); // segundo intento
                System.out.println("segundo intento token " + token);
            }

            System.out.println("token " + token);

        } else //TODO borrar, solo para test
            System.out.println("token encontrado " + token);

        return token;
    }

    private void callActivity() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);

//        intent.putExtra("clientId", client.getId());
//        intent.putExtra("clientName", client.getName());
//        intent.putExtra("clientAddress", client.getAddress());
//        intent.putExtra("clientEmail", client.getEmail());
//        intent.putExtra("clientPhone", client.getPhone());
        intent.putExtra("client", client);
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

    private ResponseObject getCustomer(final String id, final String token){
        String url = "/getCustomer.php?token=" + token + "&idCustomer=" + id;
        ResponseObject responseObject = Utilities.getResponse(getApplicationContext(), url, 1000);


        return responseObject;
    }
}
