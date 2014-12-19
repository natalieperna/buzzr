package com.natalieperna.doorbell;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.twilio.client.Connection;
import com.twilio.client.Device;
import com.twilio.client.DeviceListener;
import com.twilio.client.PresenceEvent;
import com.twilio.client.Twilio;

import java.io.IOException;

class Phone implements Twilio.InitListener, DeviceListener {
    private static final String TAG = "Phone";

    private Context context;
    private Device device;
    private Connection connection;
    private MediaPlayer sound;

    public Phone(Context context) {
        this.context = context;
        sound = MediaPlayer.create(context, R.raw.doorbell);
        sound.setLooping(true);
        Twilio.initialize(context, this);
    }

    @Override
    public void onInitialized() {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://doorbell-app-server.herokuapp.com/auth.php?clientName=doorbell";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String capabilityToken) {
                        device = Twilio.createDevice(capabilityToken, Phone.this);

                        Intent intent = new Intent(context, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        device.setIncomingIntent(pendingIntent);
                    }
                }, null);
        queue.add(stringRequest);
    }

    @Override
    protected void finalize() {
        if (connection != null)
            connection.disconnect();
        if (device != null)
            device.release();
    }

    @Override
    public void onError(Exception e) {
        Log.e(TAG, "Twilio SDK couldn't start: " + e.getLocalizedMessage());
    }

    @Override
    public void onStartListening(Device inDevice) {
        Log.i(TAG, "Device is now listening for incoming connections");
    }

    @Override
    public void onStopListening(Device inDevice) {
        Log.i(TAG, "Device is no longer listening for incoming connections");
    }

    @Override
    public void onStopListening(Device inDevice, int inErrorCode, String inErrorMessage) {
        Log.i(TAG, "Device is no longer listening for incoming connections due to error " +
                inErrorCode + ": " + inErrorMessage);
    }

    @Override
    public boolean receivePresenceEvents(Device inDevice) {
        return false;  // indicate we don't care about presence events
    }

    @Override
    public void onPresenceChanged(Device inDevice, PresenceEvent inPresenceEvent) {
    }

    public void handleIncomingConnection(Device inDevice, Connection inConnection) {
        Log.i(TAG, "Device received incoming connection");
        if (connection != null)
            connection.disconnect();
        connection = inConnection;

        sound.start();
    }

    public void disconnect() {
        resetRingtone();
        if (connection != null) {
            connection.disconnect(); // if call has been accepted
            connection.reject(); // if call hasn't been accepted
            connection = null;
        }
    }

    public void accept() {
        resetRingtone();
        if (connection != null) {
            connection.accept();
        }
    }

    // @todo This doesn't work if you haven't answered the call first for some reason
    public void send9() {
        resetRingtone();
        if (connection != null) {
            connection.accept();
            connection.sendDigits("9"); // Send DTMF tone
            connection.disconnect();
        }
    }

    private void resetRingtone() {
        sound.stop();
        try {
            sound.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Couldn't prepare ringtone");
        }
    }

}
