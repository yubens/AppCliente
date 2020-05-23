package ar.com.idus.www.appcliente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

import ar.com.idus.www.appcliente.models.Company;
import ar.com.idus.www.appcliente.models.Customer;
import ar.com.idus.www.appcliente.models.OrderState;
import ar.com.idus.www.appcliente.utilities.OrderInquiryAdapter;

public class OrderInquiryActivity extends AppCompatActivity {
    Customer customer;
    Company company;
    SharedPreferences sharedPreferences;
    ArrayList<OrderState> listOrders;
    OrderInquiryAdapter adapter;
    Button btnNewOrder;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_inquiry);

        Bundle bundle = getIntent().getExtras();
;
        btnNewOrder = findViewById(R.id.btnNewOrder);
        listView = findViewById(R.id.listOrdersInquiry);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (bundle == null) {
            showExit(getString(R.string.msgErrClientData));
            return;
        }

        listOrders = (ArrayList<OrderState>) bundle.getSerializable("orders");
        adapter = new OrderInquiryAdapter(getApplicationContext(), R.layout.order_inquiry_item, listOrders);
        listView.setAdapter(adapter);

        if (listOrders == null) {
            showExit(getString(R.string.msgErrOrderInquiry));
            return;
        }

        customer = (Customer) bundle.getSerializable("customer");
        company = (Company) bundle.getSerializable("company");

        btnNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderInquiryActivity.this, OrderActivity.class);
                intent.putExtra("customer", customer);
                intent.putExtra("company",  company);
                startActivity(intent);
            }
        });
    }

    private void showMsg(String msg) {
        if (!OrderInquiryActivity.this.isFinishing())
            Toast.makeText(OrderInquiryActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private void showExit(String msg) {
        Intent intent = new Intent(this, ErrorActivity.class);
        intent.putExtra("error", msg);
        startActivity(intent);
    }
}
