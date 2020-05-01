package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.idus.www.appcliente.models.Distributor;

public class OrderActivity extends AppCompatActivity {
    ArrayList<Distributor> distributors;
    Distributor distributor;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        textView = findViewById(R.id.textViewTest);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            distributors = (ArrayList<Distributor>) bundle.getSerializable("distributors");
            distributor = distributors.get(0);

            textView.setText("ORDER " + distributor.getName()) ;
        }
    }
}
