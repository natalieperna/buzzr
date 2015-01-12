package com.natalieperna.doorbell;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.twilio.client.Connection;
import com.twilio.client.Device;


public class IncomingActivity extends PhoneActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming);

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
                goToMainActivity();
                break;
            case R.id.talkButton:
                phone.accept();
                Button talkButton = (Button) findViewById(R.id.talkButton);
                talkButton.setVisibility(View.INVISIBLE);
                TextView message = (TextView) findViewById(R.id.incomingMessage);
                message.setText("On call with guest...");
                break;
            case R.id.allowEntryButton:
                phone.send9();
                goToMainActivity();
                break;
        }
    }

    private void goToMainActivity() {
        this.startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
