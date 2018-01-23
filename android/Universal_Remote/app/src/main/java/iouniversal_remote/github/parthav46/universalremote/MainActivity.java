package iouniversal_remote.github.parthav46.universalremote;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

//        fillDeviceList();

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

        refreshList();
    }

    private void refreshList(){
        fillDeviceList();
        Spinner device_select = (Spinner) findViewById(R.id.device_select);
        ArrayAdapter<String> device_list_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, device_list);
        device_list_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        device_select.setAdapter(device_list_adapter);
    }

    private void fillDeviceList() {
        device_list = new ArrayList<>();
        String subnet = getIPaddress();
        int address = subnet.lastIndexOf('.');
        subnet = subnet.substring(0, address);
        String temp = "";
        for (int i = 1; i < 256; i++) {
            String deviceIP = subnet + "." + i;
            //Enter func to check if IP is available
            String HTTPaddress = "http://"+deviceIP+"/inline"; //TODO: insert nodemcu server id page address
            ServerAsyncTask task = new ServerAsyncTask(HTTPaddress);
            Log.e("task",HTTPaddress + " ongoing");
            task.execute();
        }
    }



    private class ServerAsyncTask extends AsyncTask<URL, Void, String> {

        private String Url = "";
        private boolean status = false;

        public ServerAsyncTask(String url) {
            super();
            Url = url;
        }

        @Override
        protected String doInBackground(URL... urls) {
            String response = "";
            try {
                URL url = new URL(Url);

                response = makeHttpRequest(url);
            }
            catch (MalformedURLException e){
            }
            catch (IOException e){

            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s != ""){
                Log.e("message",Url + " available: " + s);
                device_list.add(Url);
                status = true;
            }
        }


        private String makeHttpRequest(URL url) throws IOException {
            String Response = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(1000 /* milliseconds */);
                urlConnection.setConnectTimeout(1500 /* milliseconds */);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                Response = readFromStream(inputStream);
            } catch (IOException e) {
                this.cancel(true);

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return Response;
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
