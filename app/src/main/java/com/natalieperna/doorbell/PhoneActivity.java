package com.natalieperna.doorbell;

import android.app.Activity;
import android.os.Bundle;

public class PhoneActivity extends Activity {
    protected static Phone phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (phone == null) {
            phone = new Phone(getApplicationContext());
        }
    }
}
