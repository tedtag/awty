package edu.washington.tedtag.awty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    PendingIntent alarmIntent = null;
    String msg;
    String number;
    int interval;
    BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @Override public void onReceive(final Context c, Intent i) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, msg, null, null);
                Toast.makeText(getApplicationContext(), "SMS Sent!",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "SMS failed, please try again later!",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    };

    public boolean validateNumber(String S) {
        String Regex = "[^\\d]";
        String PhoneDigits = S.replaceAll(Regex, "");
        return (PhoneDigits.length()!=10);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context thisContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startButton.getText().toString() == "Start") {

                    EditText msgText = (EditText) findViewById(R.id.msg);
                    EditText intervalText = (EditText) findViewById(R.id.interval);
                    EditText phoneText = (EditText) findViewById(R.id.phone);


                    if (msgText.getText().toString() == null || msgText.getText().toString() == "") {
                        Toast.makeText(MainActivity.this, "Invalid message", Toast.LENGTH_SHORT).show();
                    } else if (validateNumber(phoneText.getText().toString())) {
                        Toast.makeText(MainActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            interval = Integer.parseInt(intervalText.getText().toString());
                            if (interval <= 0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(MainActivity.this, "Invalid interval", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        registerReceiver(alarmReceiver, new IntentFilter("edu.washington.tedtag.AREWETHEREYET"));

                        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent i = new Intent();
                        i.setAction("edu.washington.tedtag.AREWETHEREYET");
                        alarmIntent = PendingIntent.getBroadcast(thisContext, 0, i, 0);

                        number = phoneText.getText().toString();
                        msg    = msgText.getText().toString();

                        am.setRepeating(AlarmManager.RTC,
                                System.currentTimeMillis() + interval * 1000 * 60,
                                interval * 1000 * 60, alarmIntent);
                        startButton.setText("Stop");
                    }
                } else {
                    startButton.setText("Start");
                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    am.cancel(alarmIntent);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
