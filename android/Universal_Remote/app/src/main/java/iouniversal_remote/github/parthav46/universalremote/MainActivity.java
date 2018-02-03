package iouniversal_remote.github.parthav46.universalremote;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static android.text.format.Formatter.formatIpAddress;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifi;
    private ArrayList<String> device_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshList();

        ListView device_select = (ListView) findViewById(R.id.device_select);
        ArrayAdapter<String> device_list_adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, device_list);
        device_select.setAdapter(device_list_adapter);
    }
        /**
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
                refreshList();
            }
        });
    }
         **/
    private void refreshList(){
        fillDeviceList();
        ListView device_select = (ListView) findViewById(R.id.device_select);
        ArrayAdapter<String> device_list_adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, device_list);
        device_select.setAdapter(device_list_adapter);
    }

    private void fillDeviceList() {
        device_list = new ArrayList<>();
        String subnet = getIPaddress();
        int address = subnet.lastIndexOf('.');
        subnet = subnet.substring(0, address);

        for (int i = 1; i < 256; i++) {
            String temp = subnet + "." + i;
            String deviceIP = temp;
            ServerAsyncTask task = new ServerAsyncTask();
            task.execute(deviceIP);
        }


    }





    private class ServerAsyncTask extends AsyncTask<String, Void, Boolean> {

        private boolean status = false;

        @Override
        protected Boolean doInBackground(String... strings) {
            for (int i = 0; i < strings.length; i++) {
                try {
                    makeHttpRequest(strings[i]);
                } catch (IOException e) {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                Log.e("Task refresh", "complete");
                ListView device_select = (ListView) findViewById(R.id.device_select);
                final ArrayAdapter<String> device_list_adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, device_list);
                device_select.setAdapter(device_list_adapter);

                runOnUiThread(new Runnable() {
                    public void run() {
                        device_list_adapter.notifyDataSetChanged();
                    }
                });
            }
            else Log.e("Task refresh", "incomplete");
        }

        private void makeHttpRequest(String deviceIP) throws IOException {
            String Response = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                String HTTPaddress = "http://" + deviceIP + "/inline"; //TODO: insert nodemcu server id page address
                Log.e("current: ",deviceIP);
                URL url = new URL(HTTPaddress);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(1000 /* milliseconds */);
                urlConnection.setConnectTimeout(1500 /* milliseconds */);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                if(urlConnection.getResponseCode() == 200){
                    device_list.add(deviceIP);
                    Log.e("added: ",deviceIP);
                }
                else
                {
                    Log.e("no connect: ",urlConnection.getResponseMessage());
                }
            } catch (IOException e) {
                Log.e("error",e.toString());
                this.cancel(true);

            }

            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }

        }
    }



    @NonNull
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private String getIPaddress() {
        wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ip = wifi.getConnectionInfo().getIpAddress();
        @SuppressWarnings("deprecation")
        String sip = formatIpAddress(ip);
        return sip;
    }
}
