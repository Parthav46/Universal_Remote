package iouniversal_remote.github.parthav46.universalremote;

import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import static android.text.format.Formatter.formatIpAddress;

public class MainActivity extends AppCompatActivity {

    public static String selected = null;
    private WifiManager wifi;
    private ArrayList<String> device_list = new ArrayList<>();
    private ListView device_select;
    private ServerAsyncTask task = new ServerAsyncTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        device_select = (ListView) findViewById(R.id.device_select);
        ArrayAdapter<String> device_list_adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, device_list);
        device_select.setAdapter(device_list_adapter);

        fillDeviceList(); //TODO: stop asynctask upon connect

        Button connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected != null) {
                    task.cancel(true);
                    Intent connect = new Intent(MainActivity.this, Controller.class);
                    startActivity(connect);
                } else {
                    Toast.makeText(MainActivity.this, "Select Appropriate device", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task.isCancelled() == true) {
                    task = new ServerAsyncTask();
                    fillDeviceList();
                }
            }
        });

        device_select.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < device_list.size(); i++) {
                    if (i == position) {
                        if (selected != device_list.get(i)) {
                            selected = device_list.get(i);
                            device_select.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        } else {
                            selected = null;
                            device_select.getChildAt(i).setBackgroundColor(Color.WHITE);
                        }

                    } else {
                        device_select.getChildAt(i).setBackgroundColor(Color.WHITE);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        selected = null;
        if (device_list != null) {
            for (int i = 0; i < device_list.size(); i++) {
                device_select.getChildAt(i).setBackgroundColor(Color.WHITE);
            }
        }
    }

    private void fillDeviceList() {
        RelativeLayout bgimage = (RelativeLayout) findViewById(R.id.loadImage);
        bgimage.setVisibility(View.VISIBLE);
        device_list = new ArrayList<>();
        String subnet = getIPaddress();
        int address = subnet.lastIndexOf('.');
        subnet = subnet.substring(0, address);
        String[] deviceIP = new String[255];
        for (int i = 0; i < deviceIP.length; i++) {
            deviceIP[i] = subnet + "." + (i + 1);
        }
        task.execute(deviceIP);
    }

    private String getIPaddress() {
        wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ip = wifi.getConnectionInfo().getIpAddress();
        @SuppressWarnings("deprecation")
        String sip = formatIpAddress(ip);
        return sip;
    }

    private class ServerAsyncTask extends AsyncTask<String, Boolean, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            Boolean status;
            for (int i = 0; i < strings.length; i++) {
                try {
                    String Url = "http://" + strings[i] + "/inline";
                    status = AppUtils.makeHttpRequestBoolean(Url);
                    if (status) {
                        device_list.add(strings[i]);
                    } else status = false;
                } catch (IOException e) {
                    status = false;
                }
                publishProgress(status);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... aBooleans) {
            if (aBooleans[0]) {
                RelativeLayout bgimage = (RelativeLayout) findViewById(R.id.loadImage);
                bgimage.setVisibility(View.GONE);
                final ArrayAdapter<String> device_list_adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, device_list);
                device_select.setAdapter(device_list_adapter);
                runOnUiThread(new Runnable() {
                    public void run() {
                        device_list_adapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            this.cancel(true);
        }
    }


}
