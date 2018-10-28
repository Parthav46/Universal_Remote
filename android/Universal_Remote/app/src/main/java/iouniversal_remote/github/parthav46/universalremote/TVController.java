package iouniversal_remote.github.parthav46.universalremote;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;

public class TVController extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>> {

    ArrayList<String> controls = new ArrayList<>();
    ButtonAsyncTask buttonAsyncTask = new ButtonAsyncTask();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvcontroller);
        setTitle(Controller.devicename);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String[] control = new String[2];
        control[0] = "data";

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(2,null,this);

        Button power = (Button) findViewById(R.id.power);
        power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAsyncTask.cancel(true);
                if(buttonAsyncTask.isCancelled()){
                    buttonAsyncTask = new ButtonAsyncTask();
                }
                control[1] = controls.get(0);
                buttonAsyncTask.execute(control);
            }
        });
    }

    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
        return new ControlAsyncLoader(getBaseContext(), getResources().getString(R.string.TV_sheets_id),Controller.selected);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        if(buttonAsyncTask.isCancelled()){
            buttonAsyncTask = new ButtonAsyncTask();
        }
        String[] frequency = {"frequency",data.get(0)};
        buttonAsyncTask.execute(frequency);
        data.remove(0);
        controls = data;
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {
        controls.clear();
    }

    private class ButtonAsyncTask extends AsyncTask<String,Void,Boolean>{
        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean status =false;
            String url = "http://" + MainActivity.selected + "/" + strings[0] + "?data=\"" + strings[1] + "\"";
            try {
                if(!this.isCancelled()) status = AppUtils.makeHttpRequestBoolean(url);
            } catch (IOException e) {
                status = false;
            }
            return status;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.e("Button",aBoolean.toString());
            this.cancel(true);
        }
    }
}
