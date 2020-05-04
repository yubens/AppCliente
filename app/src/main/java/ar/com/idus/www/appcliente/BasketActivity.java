package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import ar.com.idus.www.appcliente.models.Distributor;
import ar.com.idus.www.appcliente.models.Product;

public class BasketActivity extends AppCompatActivity {
    ArrayList<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            showExit(getString(R.string.msgErrBasket));
        }

        productList = (ArrayList<Product>) bundle.getSerializable("productList");
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
