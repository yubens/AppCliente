package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ErrorActivity extends AppCompatActivity {
    TextView txtErrorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        Bundle bundle = getIntent().getExtras();
        String error = bundle.getString("error");

        txtErrorMsg = findViewById(R.id.txtErrorMsg);
        txtErrorMsg.setText(error);
    }

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
