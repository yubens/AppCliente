package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

        listOrders = (ArrayList<OrderState>) bundle.getSerializable("orders");

        if (listOrders == null) {
//            Toast.makeText(getApplicationContext(), R.string.msgErrCustomerData, Toast.LENGTH_LONG).show();
            showExit(getString(R.string.msgErrOrderInquiry));
            return;
        }

        btnExit = findViewById(R.id.btnExitApp);
        btnNewOrder = findViewById(R.id.btnNewOrder);
        listView = findViewById(R.id.listOrdersInquiry);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        adapter = new ListOrderAdapter(getApplicationContext(), R.layout.order_item, listOrders);
        listView.setAdapter(adapter);

    }

    private void showMsg(String msg) {
        if (!OrderInquiryActivity.this.isFinishing())
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void showExit(String msg) { }
}
