package iouniversal_remote.github.parthav46.universalremote;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;

public class ACController extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>>{

    ArrayList<String> controls = new ArrayList<>();

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
        setContentView(R.layout.activity_accontroller);
        setTitle(Controller.devicename);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(3,null,this);


    }

    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
        return new ControlAsyncLoader(getBaseContext(), getResources().getString(R.string.AC_sheets_id),Controller.selected);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {

    }
}
