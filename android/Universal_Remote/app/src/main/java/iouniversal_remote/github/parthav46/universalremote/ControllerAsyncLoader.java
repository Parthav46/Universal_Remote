package iouniversal_remote.github.parthav46.universalremote;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by dell on 1/29/2018.
 */

public class ControllerAsyncLoader extends android.support.v4.content.AsyncTaskLoader<ArrayList<String>> {
    private String mUrl;

    public ControllerAsyncLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<String> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        ArrayList<String> response = AppUtils.fetchControlDevices(mUrl);
        return response;
    }
}
