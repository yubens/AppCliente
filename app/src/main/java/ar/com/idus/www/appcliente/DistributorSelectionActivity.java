package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ar.com.idus.www.appcliente.R;
import ar.com.idus.www.appcliente.models.Distributor;
import ar.com.idus.www.appcliente.utilities.DistributorArrayAdapter;

public class DistributorSelectionActivity extends AppCompatActivity {
    ArrayList<Distributor> distributors;
    Distributor distributorSelected;
    ListView distListView;
    DistributorArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_selection);
        distListView = findViewById(R.id.distListView);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            distributors = (ArrayList<Distributor>) bundle.getSerializable("distributors");

            if (distributors != null && !distributors.isEmpty()) {
                distributorSelected = distributors.get(0);
                adapter = new DistributorArrayAdapter(getApplicationContext(), R.layout.dist_item_list, distributors);
                distListView.setAdapter(adapter);

                distListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Distributor distributor =  (Distributor) parent.getItemAtPosition(position);
                        ArrayList<Distributor> distributorSelected = new ArrayList<>();
                        distributorSelected.add(distributor);

                        Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
                        intent.putExtra("distributors", distributorSelected);
                        startActivity(intent);
                    }
                });
            }
        }


    }
}
