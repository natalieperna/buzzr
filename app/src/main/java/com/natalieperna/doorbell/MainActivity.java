package com.natalieperna.doorbell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.twilio.client.Connection;
import com.twilio.client.Device;


public class MainActivity extends Activity implements View.OnClickListener {
    private Phone phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phone = new Phone(getApplicationContext());

        Button disallowButton = (Button) findViewById(R.id.disallowEntryButton);
        disallowButton.setOnClickListener(this);

        Button talkButton = (Button) findViewById(R.id.talkButton);
        talkButton.setOnClickListener(this);

        Button allowButton = (Button) findViewById(R.id.allowEntryButton);
        allowButton.setOnClickListener(this);
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
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.disallowEntryButton:
                phone.disconnect();
                break;
            case R.id.talkButton:
                phone.accept();
                break;
            case R.id.allowEntryButton:
                phone.send9();
                break;
        }
    }
}
