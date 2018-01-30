package iouniversal_remote.github.parthav46.universalremote;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ACFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<String>> {


    private ArrayAdapter adapter;

    public ACFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View base = inflater.inflate(R.layout.controller_list, container, false);
        ArrayList<String> list = new ArrayList<>();

        android.support.v4.app.LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(1, null, this);

        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        ListView listView = (ListView) base.findViewById(R.id.controller_listview);
        listView.setAdapter(adapter);
        return base;
    }

    @Override
    public android.support.v4.content.Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
        return new ControllerAsyncLoader(getContext(), getResources().getString(R.string.AC_sheets_id));
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<ArrayList<String>> loader, ArrayList<String> data) {
        adapter.clear();
        if (data != null && !data.isEmpty()) {
            adapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<ArrayList<String>> loader) {
        adapter.clear();
    }
}
