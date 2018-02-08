package iouniversal_remote.github.parthav46.universalremote;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by dell on 2/8/2018.
 */

public class ControlAsyncLoader extends AsyncTaskLoader<ArrayList<String>> {
    private String mUrl;
    private int mPosition;

    public ControlAsyncLoader(Context context, String url, int position){
        super(context);
        mUrl = url;
        mPosition = position;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<String> loadInBackground() {
        if (mUrl == null || mPosition <= 0) {
            return null;
        }
        ArrayList<String> response = AppUtils.fetchControls(mUrl,mPosition);
        return response;
    }
}
