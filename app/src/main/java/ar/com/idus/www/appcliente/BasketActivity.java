package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import ar.com.idus.www.appcliente.models.Distributor;
import ar.com.idus.www.appcliente.models.HeadOrder;
import ar.com.idus.www.appcliente.models.Product;
import ar.com.idus.www.appcliente.utilities.BasketAdapter;
import ar.com.idus.www.appcliente.utilities.ResponseObject;
import ar.com.idus.www.appcliente.utilities.Utilities;

public class BasketActivity extends AppCompatActivity {
    ArrayList<Product> productList;
    HeadOrder headOrder;
    BasketAdapter adapter;
    ListView listView;
    Button btnSendOrder, btnCancel;
    SharedPreferences sharedPreferences;
    EditText editObs;
    SimpleDateFormat formatter;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            showExit(getString(R.string.msgErrBasket));
            return;
        }

        headOrder = (HeadOrder) bundle.getSerializable("order");

        if (headOrder == null) {
            showExit(getString(R.string.msgErrBasket));
            return;
        }

        btnCancel = findViewById(R.id.btnCancel);
        btnSendOrder = findViewById(R.id.btnSendOrder);
        editObs = findViewById(R.id.editObservations);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        listView = findViewById(R.id.listBody);
        adapter = new BasketAdapter(getApplicationContext(), R.layout.basket_item, headOrder.getBodyOrders());
        listView.setAdapter(adapter);

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        btnSendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResponseObject responseSend = sendOrder();
            }
        });

        //TODO
        // responden token invalidos los 3 endpoints
    }

    private ResponseObject sendOrder() {
        ResponseObject responseObject;
        String idOrder = UUID.randomUUID().toString();
        String geo = "";
        String observations = editObs.getText().toString();

        date = new Date();
        headOrder.setDateEnd(formatter.format(date));

        String url = "http://widus-app-bygvs.dyndns.info:8086/WebServiceIdusApp/putB2BOrderHead.php?token=" + Utilities.getData(sharedPreferences, "token") +
                        "&idOrder=" + idOrder + "&idCustomer=" + headOrder.getIdCustomer() + "&cantItems=" + headOrder.getBodyOrders().size() +
                        "&dateOrder=" + headOrder.getDateOrder() + "&dateStar=" + headOrder.getDateStart() + "&dateEnd=" + headOrder.getDateEnd() +
                        "&geoPos=" + geo + "&obsOrder=" + observations + "&dateDelivery=" + headOrder.getDateDelivery();

        responseObject = Utilities.putResponse(getApplicationContext(), url, 5000);

        return responseObject;
    }

    private ResponseObject sendBody() {
        ResponseObject responseObject = null;

        return responseObject;
    }

    private ResponseObject confirmOrder() {
        ResponseObject responseObject = null;

        return responseObject;
    }

    private void showExit(String msg) {
//        txtMultiple.setVisibility(View.GONE);
//        txtSalePrice.setVisibility(View.GONE);
//        txtTotal.setVisibility(View.GONE);
//        txtStock.setVisibility(View.GONE);
//        editQuantity.setVisibility(View.GONE);
//        editDescription.setVisibility(View.GONE);
//        editCode.setVisibility(View.GONE);
//        txtOfferPrice.setVisibility(View.GONE);
//        imgButFindCode.setVisibility(View.GONE);
//        imgButFindDesc.setVisibility(View.GONE);
//        btnAdd.setVisibility(View.GONE);
//        btnWatch.setVisibility(View.GONE);
//
//        btnExit.setVisibility(View.VISIBLE);
//
//        btnExit.setText(R.string.btnExit);
//
//        txtError.setText(msg);
//
//        btnExit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println("salio");
//                System.exit(0);
//            }
//        });
    }
}
