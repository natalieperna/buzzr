package com.natalieperna.doorbell;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.twilio.client.Connection;
import com.twilio.client.Device;


public class MainActivity extends PhoneActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        Device device = intent.getParcelableExtra(Device.EXTRA_DEVICE);
        Connection connection = intent.getParcelableExtra(Device.EXTRA_CONNECTION);
        if (device != null && connection != null) {
            intent.removeExtra(Device.EXTRA_DEVICE);
            intent.removeExtra(Device.EXTRA_CONNECTION);
            phone.handleIncomingConnection(device, connection);


            this.startActivity(new Intent(this, IncomingActivity.class));
            finish();

        }

    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.disallowEntryButton:
                phone.disconnect();
                break;
            case R.id.talkButton:
                phone.accept();
                TextView t;
                t = new TextView(this);
                t=(TextView)findViewById(R.id.inc);
                t.setText("Call Connected");
                break;
            case R.id.allowEntryButton:
                if (phone.checkAccepted() == true) {
                    phone.send9();
                    break;
                }



        }
    }
}
