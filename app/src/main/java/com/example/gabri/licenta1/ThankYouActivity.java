package com.example.gabri.licenta1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ThankYouActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thankyou_activity);
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1500);
                    // timp cat o sa fie activ spash screen-ul => 1000 = 1 sec
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    // deschide o noua oactivitate
                    finish();
                    // termina actiunea de run
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();

    }
}
