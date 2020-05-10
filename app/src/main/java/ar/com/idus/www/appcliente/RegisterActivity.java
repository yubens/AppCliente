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
import java.util.ArrayList;
import java.util.regex.Pattern;
import ar.com.idus.www.appcliente.models.Client;
import ar.com.idus.www.appcliente.models.Customer;
import ar.com.idus.www.appcliente.models.Distributor;
import ar.com.idus.www.appcliente.utilities.Constants;
import ar.com.idus.www.appcliente.utilities.ResponseObject;
import ar.com.idus.www.appcliente.utilities.Utilities;

public class RegisterActivity extends AppCompatActivity {
    Button btnConfirm;
    EditText editName, editAddress, editPhone, editPass, editPassRep, editId, editEmail, editGivenAddress, editGivenEmail, editGivenPhone;
    TextView txtMsg;
    Customer customer;
    SharedPreferences sharedPreferences;
    ArrayList<Distributor> distributors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        btnConfirm  = findViewById(R.id.btnConfirm);
        editAddress = findViewById(R.id.editAddress);
        editId = findViewById(R.id.editCustomerId);
        editName = findViewById(R.id.editCustomerName);
        editPhone = findViewById(R.id.editPhone);
        editEmail = findViewById(R.id.editEmail);
        editPass = findViewById(R.id.editPass);
        editPassRep = findViewById(R.id.editPassRep);
        editGivenAddress = findViewById(R.id.editGivenAddress);
        editGivenEmail = findViewById(R.id.editGivenEmail);
        editGivenPhone = findViewById(R.id.editGivenPhone);
        txtMsg = findViewById(R.id.txtMsg);

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            showExit(getString(R.string.msgErrClientData));
            return;
        }

        customer = (Customer) bundle.getSerializable("customer");

        if (customer == null) {
            showExit(getString(R.string.msgErrClientData));
            return;
        }

        editId.setText(customer.getIdCliente());
        editId.setKeyListener(null);
        editName.setText(customer.getNombre());
        editName.setKeyListener(null);
        editGivenAddress.setText(customer.getDomicilio());
        editGivenAddress.setKeyListener(null);
        editGivenEmail.setText(customer.getEmailDistribuidora());
        editGivenEmail.setKeyListener(null);
        editGivenPhone.setText(customer.getTelefonoDistribuidora());
        editGivenPhone.setKeyListener(null);
        editEmail.setText(customer.getEmailOtorgado());
        editAddress.setText(customer.getDireccionOtorgada());
        editPass.setText(customer.getContrasena());
        editPassRep.setText(customer.getContrasena());
        editPhone.setText(customer.getTelefonoOtorgado());

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResponseObject responseUpdate;
                boolean errorEmail, errorPhone, errorPass;
                String email, phone, pass, passRep, address;
                email = editEmail.getText().toString();
                phone = editPhone.getText().toString();
                address = editAddress.getText().toString();
                pass = editPass.getText().toString();
                passRep = editPassRep.getText().toString();

                if (email.isEmpty() || phone.isEmpty() || address.isEmpty() || pass.isEmpty() || passRep.isEmpty())
                    Toast.makeText(getApplicationContext(), R.string.msgMandatoryData,
                            Toast.LENGTH_LONG).show();
                else {
                    errorEmail = Utilities.setEditColor(getApplicationContext(), editEmail, checkEmail(email));
                    errorPhone = Utilities.setEditColor(getApplicationContext(), editPhone, checkPhone(phone));
                    errorPass = Utilities.setEditColor(getApplicationContext(), editPass, checkPass(pass));

                    if (errorEmail || errorPhone || errorPass)
                        Toast.makeText(getApplicationContext(), R.string.msgErrDataIn, Toast.LENGTH_SHORT).show();

                    else if (!pass.equals(passRep))
                        Toast.makeText(getApplicationContext(), R.string.msgErrPass, Toast.LENGTH_SHORT).show();
                    else {
                        customer.setDireccionOtorgada(address);
                        customer.setEmailOtorgado(email);
                        customer.setTelefonoOtorgado(phone);
                        customer.setContrasena(pass);

                        responseUpdate = updateCustomer();

                        if (responseUpdate != null) {
                            switch (responseUpdate.getResponseCode()) {
                                case Constants.OK:
                                    showMsg(responseUpdate.getResponseData());
                                    Utilities.saveData(sharedPreferences,"idCustomer", customer.getIdCliente());
                                    callDistributor();
                                    System.out.println(responseUpdate.getResponseData());
                                    break;

                                case Constants.SHOW_ERROR:
                                    showMsg(responseUpdate.getResponseData());
                                    break;

                                case Constants.SHOW_EXIT:
                                    showExit(responseUpdate.getResponseData());
                                    break;
                            }
                        }
                    }
                }
            }
        });
    }

    private void callDistributor() {
        Intent intent = new Intent(getApplicationContext(), DistributorActivity.class);
        intent.putExtra("customer", customer);
        startActivity(intent);
    }

    private void showMsg(String msg) {
        if (!RegisterActivity.this.isFinishing())
            Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private void showExit(String msg) {
        editName.setVisibility(View.GONE);
        editAddress.setVisibility(View.GONE);
        editPhone.setVisibility(View.GONE);
        editPass.setVisibility(View.GONE);
        editPassRep.setVisibility(View.GONE);
        editPassRep.setVisibility(View.GONE);
        editId.setVisibility(View.GONE);
        editEmail.setVisibility(View.GONE);
        editGivenPhone.setVisibility(View.GONE);
        editGivenAddress.setVisibility(View.GONE);
        editGivenEmail.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.GONE);

        txtMsg.setTextSize(18);
        txtMsg.setText(msg);

//        btnConfirm.setText(R.string.btnExit);
//
//        btnConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println("salio");
//                System.exit(0);
//            }
//        });
    }

    private ResponseObject updateCustomer() {
        String url = "/putAditionalCustomerData.php?token=" + Utilities.getData(sharedPreferences, "token") + "&idCustomer=" + customer.getIdCliente() +
                    "&direccion=" + customer.getDireccionOtorgada() + "&telefono=" + customer.getTelefonoOtorgado() +
                    "&eMail=" + customer.getEmailOtorgado() + "&contraseña=" + customer.getContrasena();

        ResponseObject responseObject = Utilities.putResponse(getApplicationContext(), url, 5000);
        ResponseObject responseToken;

        int code = responseObject.getResponseCode();

        if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
            responseObject = Utilities.putResponse(getApplicationContext(), url, 5000);

        if (responseObject.getResponseCode() == Constants.INVALID_TOKEN) {
            responseToken = Utilities.getNewToken(getApplicationContext(), sharedPreferences);

            if (responseToken == null) {
                responseObject.setResponseCode(Constants.SHOW_EXIT);
                responseObject.setResponseData(getString(R.string.msgErrToken));

            } else if (responseToken.getResponseCode() == Constants.SHOW_EXIT) {
                responseObject.setResponseCode(Constants.SHOW_EXIT);
                responseObject.setResponseData(responseToken.getResponseData());
            } else {
                url = "/putAditionalCustomerData.php?token=" + responseToken.getResponseData() + "&idCustomer=" + customer.getIdCliente() +
                        "&direccion=" + customer.getDireccionOtorgada() + "&telefono=" + customer.getTelefonoOtorgado() +
                        "&eMail=" + customer.getEmailOtorgado() + "&contraseña=" + customer.getContrasena();
                responseObject = Utilities.putResponse(getApplicationContext(), url, 5000);

                code = responseObject.getResponseCode();

                if (code == Constants.SERVER_ERROR || code == Constants.EXCEPTION || code == Constants.NO_DATA)
                    responseObject = Utilities.putResponse(getApplicationContext(), url, 5000);
            }
        }

        switch (responseObject.getResponseCode()) {
            case Constants.NO_INTERNET:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString(R.string.msgErrInternet));
                break;

            case Constants.OK:
                responseObject.setResponseData(getString(R.string.msgSuccUpdateData));
                break;

            case Constants.EXCEPTION:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString(R.string.msgErrException) + " (" + responseObject.getResponseData() + ")");
                break;

            case Constants.SERVER_ERROR:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString((R.string.msgErrServer)));
                break;

            case Constants.NO_DATA:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString(R.string.msgErrorUpdateData));
                break;

            case Constants.INVALID_TOKEN:
                responseObject.setResponseCode(Constants.SHOW_ERROR);
                responseObject.setResponseData(getString((R.string.msgErrToken)));
                break;
        }

        return responseObject;
    }

    private void callActivity() {
      // TODO
      // ** dependiendo de la cantidad de distribuidores que tiene el cliente, llamar a la actividad que los muestra todos o bien
      //    ir directamente a la carga de pedido
        Class c = distributors.size() > 1 ? DistributorSelectionActivity.class : OrderActivity.class;

        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra("distributors", distributors);
        startActivity(intent);
    }

    private void callOrder() {
        Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
        intent.putExtra("customer", customer);
        startActivity(intent);
    }

    private boolean checkEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);

        return !pattern.matcher(email).matches();
    }

    private boolean checkPhone(String phone) {
        return phone.length() < 10 || phone.startsWith("0") || phone.startsWith("15");
    }

    private boolean checkPass (String pass) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$";
        Pattern pattern = Pattern.compile(regex);

        return !pattern.matcher(pass).matches();
    }
}
