package iouniversal_remote.github.parthav46.universalremote;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import static android.text.format.Formatter.formatIpAddress;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifi;
    private ArrayList<String> device_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView device_select = (ListView) findViewById(R.id.device_select);
        ArrayAdapter<String> device_list_adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, device_list);
        device_select.setAdapter(device_list_adapter);

        fillDeviceList();

        Button connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillDeviceList();
            }
        });
    }

    private void fillDeviceList() {
        device_list = new ArrayList<>();
        String subnet = getIPaddress();
        int address = subnet.lastIndexOf('.');
        subnet = subnet.substring(0, address);
        for (int i = 1; i < 256; i++) {
            String deviceIP = subnet + "." + i;
            ServerAsyncTask task = new ServerAsyncTask();
            task.execute(deviceIP);
        }

    }

    private String getIPaddress() {
        wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ip = wifi.getConnectionInfo().getIpAddress();
        @SuppressWarnings("deprecation")
        String sip = formatIpAddress(ip);
        return sip;
    }

    private class ServerAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean status = false;
            for (int i = 0; i < strings.length; i++) {
                try {
                    String Url = "http://" + strings[i] + "/inline";
                    status = AppUtils.makeHttpRequestBoolean(Url);
                    if (status) {
                        device_list.add(strings[i]);
                        Log.e("added", strings[i]);
                    }
                } catch (IOException e) {
                    this.cancel(true);
                }
            }
            return status;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){


                ListView device_select = (ListView) findViewById(R.id.device_select);
                final ArrayAdapter<String> device_list_adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, device_list);
                device_select.setAdapter(device_list_adapter);
                runOnUiThread(new Runnable() {
                    public void run() {
                        device_list_adapter.notifyDataSetChanged();
                    }
                });
            }
        }

    }


}
