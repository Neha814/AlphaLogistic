package Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import alphalogistics.com.alphalogistics.MainActivity;
import alphalogistics.com.alphalogistics.R;

/**
 * Created by nehabh on 2/15/2016.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    View rootView;
    ImageView sync_btn,load_btn, delivery_btn, directions_btn, settings_btn,warehouse_btn;
    LinearLayout sync_layout, load_layout, delivery_layout, directions_layout, settings_laoyut, warehouse_layout;
    String title = "Home";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home, container, false);
        initialise();
        return rootView;
    }

    private void initialise() {
        warehouse_layout = (LinearLayout) rootView.findViewById(R.id.warehouse_layout);
        settings_laoyut = (LinearLayout) rootView.findViewById(R.id.settings_laoyut);
        directions_layout = (LinearLayout) rootView.findViewById(R.id.directions_layout);
        delivery_layout = (LinearLayout) rootView.findViewById(R.id.delivery_layout);
        load_layout = (LinearLayout) rootView.findViewById(R.id.load_layout);
        sync_layout = (LinearLayout) rootView.findViewById(R.id.sync_layout);

        sync_btn = (ImageView) rootView.findViewById(R.id.sync_btn);
        load_btn = (ImageView) rootView.findViewById(R.id.load_btn);
        delivery_btn = (ImageView) rootView.findViewById(R.id.delivery_btn);
        directions_btn = (ImageView) rootView.findViewById(R.id.directions_btn);
        settings_btn = (ImageView) rootView.findViewById(R.id.settings_btn);
        warehouse_btn = (ImageView) rootView.findViewById(R.id.warehouse_btn);

        warehouse_btn.setOnClickListener(this);
        settings_btn.setOnClickListener(this);
        directions_btn.setOnClickListener(this);
        delivery_btn.setOnClickListener(this);
        load_btn.setOnClickListener(this);
        sync_btn.setOnClickListener(this);

        sync_layout.setOnClickListener(this);
        load_layout.setOnClickListener(this);
        delivery_layout.setOnClickListener(this);
        directions_layout.setOnClickListener(this);
        settings_laoyut.setOnClickListener(this);
        warehouse_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==sync_btn || v==sync_layout){
            title = "Sync";
            Fragment fragment = null;
            fragment = new NavSyncFragment();
            MainActivity.showFragment(0);
            showFragment(fragment);

        } else if(v==load_btn || v==load_layout){
            title  = "Load Truck";
            Fragment fragment = null;
            fragment = new NavLoadTruckFragment();
            MainActivity.showFragment(1);
            showFragment(fragment);

        } else if(v==delivery_btn || v==delivery_layout){
            title  = "Deliver";
            Fragment fragment = null;
            fragment = new NavDeliverFragment();
            MainActivity.showFragment(2);
            showFragment(fragment);

        } else if(v==directions_btn || v==directions_layout){
            title  = "Directions";
            Fragment fragment = null;
            fragment = new NavDiretionsFragment();
            MainActivity.showFragment(3);
            showFragment(fragment);

        } else if (v==settings_btn || v==settings_laoyut) {
            title  = "Settings";
            Fragment fragment = null;
            fragment = new NavSettingsFragment();
            MainActivity.showFragment(4);
            showFragment(fragment);

        }
        else if(v==warehouse_btn || v==warehouse_layout){
            title  = "Warehouse";
            Fragment fragment = null;
            fragment = new NavWarehouseFragment();
            MainActivity.showFragment(5);
            showFragment(fragment);
        }
    }

    private void showFragment(Fragment f) {
        if (f != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, f);
         //   ft.addToBackStack(null);
            ft.commit();
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(title);
        }
    }

}
