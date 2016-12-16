package com.ioannina.ieeesb.ieee_sb_tree;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Enable the app icon as the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ((EditText)findViewById(R.id.adddress_editText)).setText(pref.getString("arduinoIp", "192.168.168.254"));
        ((EditText)findViewById(R.id.port_editText)).setText(pref.getString("arduinoPort", "80"));

    }

    public void saveSettings(View view) {
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = pref.edit();

        String user_address = ((EditText)findViewById(R.id.adddress_editText)).getText().toString();
        String user_port = ((EditText)findViewById(R.id.port_editText)).getText().toString();

        if(!user_address.isEmpty() && !user_port.isEmpty()) {
            editor.putString("arduinoIp", user_address);
            editor.putString("arduinoPort", user_port);
            editor.commit();

            finish();
        }
        else
            Toast.makeText(getApplicationContext(), "A field is empty", Toast.LENGTH_LONG).show();
    }
}
