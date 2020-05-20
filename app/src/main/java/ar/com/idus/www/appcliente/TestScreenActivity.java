package ar.com.idus.www.appcliente;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ar.com.idus.www.appcliente.utilities.SoftInputAssist;

public class TestScreenActivity extends AppCompatActivity {
    SoftInputAssist softInputAssist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_screen);

        softInputAssist = new SoftInputAssist(this);
    }

    @Override
    protected void onResume() {
        softInputAssist.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        softInputAssist.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        softInputAssist.onDestroy();
        super.onDestroy();
    }
}
