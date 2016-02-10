package Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import alphalogistics.com.alphalogistics.R;

/**
 * Created by Neha on 1/31/2016.
 */
public class NavSettingsFragment extends Fragment implements View.OnClickListener {

    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_settings, container, false);
        initialise();
        return rootView;
    }

    private void initialise() {

    }

    @Override
    public void onClick(View v) {

    }
}
