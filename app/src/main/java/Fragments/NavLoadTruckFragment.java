package Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.zbar.Symbol;

import java.util.ArrayList;

import alphalogistics.com.alphalogistics.R;
import qrscanner.ZBarConstants;
import qrscanner.ZBarScannerActivity;

/**
 * Created by Neha on 1/31/2016.
 */
public class NavLoadTruckFragment extends Fragment implements View.OnClickListener  {
    View rootView;
    Spinner load_spinner;
    MyAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_load, container, false);
        initialise();
        return rootView;
    }

    private void initialise() {
        load_spinner = (Spinner) rootView.findViewById(R.id.load_spinner);


        ArrayList<String> menuItems = new ArrayList<String>();
        menuItems.add("Received");
        menuItems.add("12 partial damaged");
        menuItems.add("10 total damage");
        menuItems.add("Empty tote scan");

        mAdapter = new MyAdapter(getActivity(),
                menuItems);
        load_spinner.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {

    }


    class MyAdapter extends BaseAdapter {

        LayoutInflater mInflater = null;

        ArrayList<String> menuItems = new ArrayList<String>();

        public MyAdapter(Context context,
                         ArrayList<String> menuList) {
            mInflater = LayoutInflater.from(getActivity());
            menuItems = menuList;

        }


        @Override
        public int getCount() {

            return menuItems.size();
        }

        @Override
        public Object getItem(int position) {
            return menuItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent, R.layout.menu_spinner_item, true);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent, R.layout.menu_spinner_item_dropdown, false);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent, int spinnerRow, boolean isDefaultRow) {

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(spinnerRow, parent, false);
            TextView txt = (TextView) row.findViewById(R.id.text);

            txt.setText(menuItems.get(position));


            return row;
        }

    }
}
