package ar.com.idus.www.appcliente;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import ar.com.idus.www.appcliente.models.Customer;
import ar.com.idus.www.appcliente.models.OrderState;
import ar.com.idus.www.appcliente.utilities.ListOrderAdapter;

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

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            showExit(getString(R.string.msgErrClientData));
            return;
        }

        listOrders = (ArrayList<OrderState>) bundle.getSerializable("orders");

        if (listOrders == null) {
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
