package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import ar.com.idus.www.appcliente.utilities.Utilities;

public class RegisterActivity extends AppCompatActivity {
    Button btnConfirm;
    EditText editName, editAddress, editPhone, editPass, editPassRep, editId, editEmail, editGivenAddress, editGivenEmail, editGivenPhone;
    TextView txtMsg;
    Client client;
    Customer customer;
    ArrayList<Distributor> distributors = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
//        bundle = null;


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
                boolean errorEmail, errorPhone, errorPass;
                String id, name, email, phone, pass, passRep, address;
                id = editName.getText().toString();
                name = editName.getText().toString();
                email = editEmail.getText().toString();
                phone = editPhone.getText().toString();
                address = editAddress.getText().toString();
                pass = editPass.getText().toString();
                passRep = editPassRep.getText().toString();

                if (email.isEmpty() || phone.isEmpty() || pass.isEmpty() || passRep.isEmpty())
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
                        distributors = getDistributors(email);

                        if (!distributors.isEmpty()) {
                            callActivity();
                        } else
                            Toast.makeText(getApplicationContext(), R.string.msgErrDistribData, Toast.LENGTH_SHORT).show();
                    }
                }

//                    if (!checkEmail(email)) {
//                        allOK = false;
//                        editEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_light)));;
//                    }
//
//                    if (!checkPhone(phone)) {
//                        allOK = false;
//                        editPhone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_light)));
//
//                    }
//
//                    if (!checkPass(pass)) {
//                        allOK = false;
////                        editPass.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_light)));
//                        setEditColor(editPass, false);
//
//                    } else
//                        setEditColor(editPass, true);


            }
        });
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


        txtMsg.setText(msg);
        btnConfirm.setText(R.string.btnExit);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("salio");
                System.exit(0);
            }
        });
    }

    private ArrayList<Distributor> getDistributors(String email) {
        // TODO
        // deberia llamar al web service que obtiene los distribuidores de cada cliente

        ArrayList<Distributor> response = new ArrayList<>();
        Distributor distributor;

        int count = 1;
        String name = "100";

        if (!email.equals("pepe@honguito.com")) {
            count = 30;
            name = "400";
        }

        for (int i = 1; i <= count; i++) {
            distributor = new Distributor();
            distributor.setId(String.valueOf(i));
            distributor.setName("Dustrubuidor " + name + i);
            response.add(distributor);
        }

        return response;
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
