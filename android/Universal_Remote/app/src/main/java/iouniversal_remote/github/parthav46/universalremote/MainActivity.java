package iouniversal_remote.github.parthav46.universalremote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> device_list = new ArrayList<>();
        device_list.add("Select Device");

        Spinner device_select = (Spinner) findViewById(R.id.device_select);
        ArrayAdapter<String> device_list_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,device_list);
        device_list_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        device_select.setAdapter(device_list_adapter);
    }
}
