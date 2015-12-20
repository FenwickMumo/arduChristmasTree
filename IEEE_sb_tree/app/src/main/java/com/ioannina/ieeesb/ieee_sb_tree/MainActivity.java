package com.ioannina.ieeesb.ieee_sb_tree;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private boolean lightsOn;
    private MainActivity me;
    private String user_server_name;
    private String user_server_port;
    private Handler handler;

    private ImageView red;
    private ImageView green;
    private ImageView blue;
    private ImageView sound;
    private AnimationDrawable red_anim;
    private AnimationDrawable green_anim;
    private AnimationDrawable blue_anim;

    private MediaPlayer mediaPlayer;
    private boolean isSoundOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lightsOn = false;
        isSoundOn = true;
        me = this;
        handler = new Handler(Looper.getMainLooper());

        red   = (ImageView) findViewById(R.id.redImage);
        green = (ImageView) findViewById(R.id.greenImage);
        blue  = (ImageView) findViewById(R.id.blueImage);
        sound = (ImageView) findViewById(R.id.soundButton);

        red.setBackgroundResource(R.drawable.red_anim);
        green.setBackgroundResource(R.drawable.green_anim);
        blue.setBackgroundResource(R.drawable.blue_anim);

        red.setVisibility(View.INVISIBLE);
        green.setVisibility(View.INVISIBLE);
        blue.setVisibility(View.INVISIBLE);
        sound.setVisibility(View.INVISIBLE);

        red_anim = (AnimationDrawable)red.getBackground();
        green_anim = (AnimationDrawable)green.getBackground();
        blue_anim = (AnimationDrawable)blue.getBackground();

        mediaPlayer = MediaPlayer.create(this, R.raw.tc);
        mediaPlayer.setLooping(true);
    }

    public void connectToArduino(View view) {
        Thread connect_thread = new Thread(new ConnectionThread());
        connect_thread.start();
    }

    public void toggleSound(View view) {
        if(isSoundOn) {
            sound.setImageResource(R.drawable.mute);
            mediaPlayer.pause();
        } else {
            sound.setImageResource(R.drawable.sound_on);
            mediaPlayer.start();
        }

        isSoundOn = !isSoundOn;
    }

    @Override
    public void onResume() {

        if(lightsOn) {
            sound.setImageResource(R.drawable.sound_on);
            mediaPlayer.start();
            isSoundOn = !isSoundOn;
        }
        super.onResume();
    }

    @Override
    public void onPause() {

        if(lightsOn) {
            sound.setImageResource(R.drawable.mute);
            mediaPlayer.pause();
            isSoundOn = !isSoundOn;
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {

        if(lightsOn) {
            mediaPlayer.stop();
            isSoundOn = !isSoundOn;
        }

        super.onDestroy();
    }

    // Handle Back button
    @Override
    public void onBackPressed () {

        if(lightsOn) {
            mediaPlayer.stop();
            isSoundOn = !isSoundOn;
        }

        Toast.makeText(getApplicationContext(), getString(R.string.app_bye_message), Toast.LENGTH_LONG).show();
        finish();
    }

    private void changeIcons()
    {
        if(lightsOn) {
            ((ImageButton) findViewById(R.id.powerButton)).setImageResource(R.drawable.poweroff);

            red_anim.stop();
            green_anim.stop();
            blue_anim.stop();
            red.setVisibility(View.INVISIBLE);
            green.setVisibility(View.INVISIBLE);
            blue.setVisibility(View.INVISIBLE);

            sound.setVisibility(View.INVISIBLE);
            mediaPlayer.pause();
        }
        else
        {
            ((ImageButton) findViewById(R.id.powerButton)).setImageResource(R.drawable.poweron);

            red.setVisibility(View.VISIBLE);
            green.setVisibility(View.VISIBLE);
            blue.setVisibility(View.VISIBLE);

            red_anim.start();
            green_anim.start();
            blue_anim.start();

            sound.setImageResource(R.drawable.sound_on);
            mediaPlayer.start();
            sound.setVisibility(View.VISIBLE);
            isSoundOn = true;
        }

        lightsOn = !lightsOn;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(me, Settings.class);
            startActivity(intent);

            return true;
        } else if (id == R.id.action_about) {

            AlertDialog.Builder builder;

            View view = getLayoutInflater().inflate( R.layout.about, null );
            String icons_link = "<a href=\"https://icons8.com\">Link to icons8.com</a>";
            TextView about_icons = (TextView) view.findViewById(R.id.iconsLinktextView);
            about_icons.setText(
                    Html.fromHtml(icons_link));

            about_icons.setMovementMethod(LinkMovementMethod.getInstance());

            builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_about);
            builder.setView(view);

            // Set up the buttons
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.show();

            return true;
        }
        return true;
    }

    private class ConnectionThread implements Runnable {
        private final static int TIMEOUT = 5000;

        @Override
        public void run() {
            SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(getBaseContext());

            user_server_name = pref.getString("arduinoIp", "192.168.168.254");
            user_server_port = pref.getString("arduinoPort", "80");

            try {
                URL url = new URL("http://"+ user_server_name+ ":" + user_server_port + "/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                urlConnection.disconnect();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changeIcons();
                    }
                });

            } catch (UnknownHostException e1) {
                 e1.printStackTrace();
                 handler.post(new Runnable() {
                     @Override
                     public void run() {
                         Toast.makeText(getApplicationContext(), "Unknown Host", Toast.LENGTH_LONG).show();
                     }
                 });

             } catch (IOException e1) {
                 e1.printStackTrace();
                 handler.post(new Runnable() {
                     @Override
                     public void run() {
                             Toast.makeText(getApplicationContext(), "Cannot open socket", Toast.LENGTH_LONG).show();
                         }
                 });
            }
        }
    }

}
