package iouniversal_remote.github.parthav46.universalremote;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by dell on 1/28/2018.
 */

public class AppUtils {
    public static String api_key = "AIzaSyDiCmDax_xTFyd75027BPEzKhsKkwgOBBM";

    public AppUtils() {

    }

    @NonNull
    public static String readFromStream(InputStream inputStream) throws IOException {
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

    public static ArrayList<String> fetchControlDevices(String ID) {
        String jsonResponse = null;
        ArrayList<String> response;
        String Url = "https://sheets.googleapis.com/v4/spreadsheets/" + ID + "/values/A1%3AA100?key=" + api_key + "&majorDimension=COLUMNS";
        try {
            jsonResponse = makeHttpRequestString(Url);
            response = extractJson(jsonResponse);
        } catch (IOException e) {
            return null;
        }
        return response;
    }

    public static ArrayList<String> fetchControls(String ID, int line) {
        String jsonResponse = null;
        ArrayList<String> response;
        String Url = "https://sheets.googleapis.com/v4/spreadsheets/" + ID + "/values/B" + line + "%3AZ" + line+ "?key=" + api_key + "&majorDimension=ROWS";
        try {
            jsonResponse = makeHttpRequestString(Url);
            response = extractJson(jsonResponse);
        } catch (IOException e) {
            return null;
        }
        return response;
    }

    public static ArrayList<String> extractJson(String jsonresponse) {
        if (jsonresponse == null) {
            return null;
        }
        ArrayList<String> devices = new ArrayList<>();
        try {
            JSONObject baseJsonObject = new JSONObject(jsonresponse);
            JSONArray valueArray = baseJsonObject.getJSONArray("values");
            JSONArray values = valueArray.getJSONArray(0);
            for (int i = 0; i < values.length(); i++) {
                devices.add(values.getString(i));
            }
        } catch (JSONException e) {
            return null;
        }
        return devices;
    }

    public static String makeHttpRequestString(String Url) throws IOException {
        String Response = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(Url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            if (urlConnection.getResponseCode() == 200) {
                Response = readFromStream(inputStream);
            }
        } catch (IOException e) {
            throw e;
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

    public static Boolean makeHttpRequestBoolean(String Url) throws IOException {
        Boolean Status = false;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(Url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(1000);
            urlConnection.setConnectTimeout(1500);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();

            if (urlConnection.getResponseCode() == 200) {
                Status = true;
            }
        } catch (IOException e) {
            throw e;

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return Status;
    }
}
