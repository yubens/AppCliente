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
import ar.com.idus.www.appcliente.utilities.Utilities;

import static ar.com.idus.www.appcliente.R.string.msgErrToken;

public class MainActivity extends AppCompatActivity {
    Button btnEnter;
    TextView txtIdCustomer;
    EditText editIdCustomer, editPassCustomer;
    Client client;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int result;
        boolean firstEntry = false, back = false;
        String token, idPhone, idCustomer;
        int msg = 0;

        btnEnter = findViewById(R.id.btnEnter);
        txtIdCustomer = findViewById(R.id.txtIdCustomer);
        editIdCustomer = findViewById(R.id.editIdCustomer);
        editPassCustomer = findViewById(R.id.editPassCustomer);
        sharedPreferences = getPreferences(MODE_PRIVATE);

        token = getToken();

        if (token != null && !token.equals(Constants.NO_RESULT)) {
            Utilities.saveData(sharedPreferences, "token", token);
            System.out.println("token guardado " + token);
        }


        idPhone = getIdPhone();
        idCustomer = Utilities.getData(sharedPreferences,"idCustomer");

        // primer intento de busqueda
        result = findPhone(token, idPhone);

        if (result == 0 || result == Constants.INVALID_TOKEN) {
            System.out.println("A - segundo intento de busqueda " + result);
            token = getToken();
            result = findPhone(token, idPhone);
        }

        if (result == 0 || result == Constants.INVALID_TOKEN) { // segundo intento
            System.out.println("B - segundo intento de busqueda " + result);
            token = getToken();
            result = findPhone(token, idPhone);
        }

        switch (result) {
            case 0:
                System.out.println("segundo intento fallido");
                msg = R.string.msgErrFind;
                back = true;
                break;

            case Constants.INVALID_TOKEN:
                System.out.println("segundo token invalido");
                msg = R.string.msgErrFind;
                back = true;
                break;

            case Constants.DISABLED:
                msg = R.string.msgDisabledPhone;
                back = true;
                break;

            case Constants.SERVER_ERROR:
                msg = R.string.msgErrFind;
                back = true;
                break;

            case Constants.CREATED: // primer ingreso
                Utilities.saveData(sharedPreferences, "idPhone", idPhone);
                System.out.println("primer ingreso");
                firstEntry = true;
                break;

            case Constants.OK:// ingresos posteriores
                System.out.println("ingresos posteriores");
                break;

            default:
                System.out.println("error" + result);
                break;

        }

        if (msg == 0) {
            Utilities.saveData(sharedPreferences, "token", token);
            System.out.println("token guardado in " + token);

            if (firstEntry) {
                editPassCustomer.setVisibility(View.GONE);

                btnEnter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id;

                        id = editIdCustomer.getText().toString();

                        if (id.isEmpty())
                            showMsg(R.string.msgErrorEmptyId);

                        else {
                            Utilities.saveData(sharedPreferences,"idCustomer", id);
                            client = getClient(id);

                            if (client != null)
                                callActivity();
                            else
                                showMsg(R.string.msgErrClientData);

                        }

                    }
                });
            }
            else {
                txtIdCustomer.setVisibility(View.GONE);
                editIdCustomer.setText(idCustomer);

                btnEnter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id;
                        String pass;

                        id = editIdCustomer.getText().toString();
                        pass = editPassCustomer.getText().toString();

                        if (id.isEmpty() || pass.isEmpty())
                            showMsg(R.string.msgErrorEmptyIdPass);

                        else {
                            Utilities.saveData(sharedPreferences,"idCustomer", id);
                            client = getClient(id);

                            if (client != null)
                                callActivity();
                            else
                                showMsg(R.string.msgErrClientData);

                        }

                    }
                });
            }

        } else {
            showMsg(msg);
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


    }

    private void showMsg(int msg) {
        Toast.makeText(getApplicationContext(), getString(msg), Toast.LENGTH_LONG).show();
        System.out.println(getString(msg));
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

        if (token.equals(Constants.NO_RESULT)) {
            System.out.println("token no encontrado");

            token = Utilities.getToken(getApplicationContext()); // primer intento de obtencion

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

    private String [] getCustomer(final String id, final String token){
        String response [];

        final AtomicReference<String> resultString = new AtomicReference<>();
        final AtomicReference<String> resultCode = new AtomicReference<>();

        if (Utilities.checkConnection(getApplicationContext())) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    URL url;
                    int response;
                    String linea;
                    StringBuilder result = new StringBuilder();
                    HttpURLConnection cnx = null;
                    super.run();

                    try {
                        url = new URL(Constants.URL + "getCustomer.php?token=" + token + "&idCustomer=" + id);
                        cnx = (HttpURLConnection) url.openConnection();

                        response = cnx.getResponseCode();

                        if (response == Constants.OK) {
                            InputStream in = new BufferedInputStream(cnx.getInputStream());
                            BufferedReader leer = new BufferedReader(new InputStreamReader(in));

                            while ((linea = leer.readLine()) != null) {
                                result.append(linea);
                            }

                            System.out.println("cliente obtenido " + linea);
                            resultString.set(result.toString());

                        } else
                            resultString.set(Constants.NO_RESULT);


                    } catch (Exception e) {
                        e.printStackTrace();
                        resultString.set(Constants.NO_RESULT);

                    } finally {
                        if (cnx != null) {
                            resultString.set(result.toString());
                            cnx.disconnect();
                        }

                    }
                }
            };

            try {
                thread.start();
                thread.join(1000);
//                response = resultString.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;

        } else
            return null;
    }
}
