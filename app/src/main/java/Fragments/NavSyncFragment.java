package Fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import net.sourceforge.zbar.Symbol;

import alphalogistics.com.alphalogistics.R;
import qrscanner.ZBarConstants;
import qrscanner.ZBarScannerActivity;

/**
 * Created by Neha on 1/31/2016.
 */
public class NavSyncFragment extends Fragment implements View.OnClickListener {

    View rootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_sync, container, false);
        initialise();
       return rootView;
    }

    private void initialise() {

    }

    @Override
    public void onClick(View v) {

    }

}
