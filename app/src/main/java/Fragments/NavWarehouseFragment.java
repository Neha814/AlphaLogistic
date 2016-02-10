package Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.zbar.Symbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import alphalogistics.com.alphalogistics.R;
import database.DatabaseHandler;
import model.BarcodeData;
import qrscanner.ZBarConstants;
import qrscanner.ZBarScannerActivity;

/**
 * Created by Neha on 1/31/2016.
 */
public class NavWarehouseFragment extends Fragment implements View.OnClickListener  {
    View rootView;
    Button camerascan_btn;
    private static final int ZBAR_SCANNER_REQUEST = 0;
    Spinner warehouse_spinner;
    MyAdapter mAdapter;
    MyAdapter1 mAdapter1;
    List<BarcodeData> contacts = new ArrayList<BarcodeData>();
    LinearLayout listview;
    View view;
    LinearLayout ll;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.warehouse_fragment, container, false);
        initialise();
        return rootView;
    }

    private void initialise() {
        camerascan_btn = (Button) rootView.findViewById(R.id.camerascan_btn);
        warehouse_spinner = (Spinner) rootView.findViewById(R.id.warehouse_spinner);
        listview = (LinearLayout) rootView.findViewById(R.id.listview);
        view = (View) rootView.findViewById(R.id.view);
        ll = (LinearLayout) rootView.findViewById(R.id.ll);

        camerascan_btn.setOnClickListener(this);

        ArrayList<String> menuItems = new ArrayList<String>();
        menuItems.add("Received");
        menuItems.add("12 partial damaged");
        menuItems.add("10 total damage");
        menuItems.add("Empty tote scan");

        mAdapter = new MyAdapter(getActivity(),
                menuItems);
        warehouse_spinner.setAdapter(mAdapter);

        RetreiveAllData();
    }

    private void RetreiveAllData() {
        DatabaseHandler db = new DatabaseHandler(getActivity());
        contacts = db.getAllContacts();


        if(contacts.size()>0){
            // setAdpater
            listview.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            ll.setVisibility(View.VISIBLE);

            for (int i=0;i<contacts.size();i++) {
                String log = "Id: "+contacts.get(i).getId()+" ,barcode: " + contacts.get(i).getBarcode() +
                        " ,driver id: " + contacts.get(i).getDriverId()+
                        " ,modified: "+contacts.get(i).getModified()+" ,activity type id: "+contacts.get(i).getActivityTypeId();
                // Writing Contacts to log
                Log.d("Name: ", log);

                LayoutInflater inflater = null;
                inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mLinearView = inflater.inflate(R.layout.warehouse_listitem, null);
                TextView barcode_text = (TextView) mLinearView.findViewById(R.id.barcode_text);
                barcode_text.setText(contacts.get(i).getBarcode());

                listview.addView(mLinearView);
            }



        } else {
            listview.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            ll.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
            if(v==camerascan_btn){
                if (isCameraAvailable()) {
                    Intent intent = new Intent(getActivity(), ZBarScannerActivity.class);
                    intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE,Symbol.NONE,
                            Symbol.PARTIAL,Symbol.EAN8,Symbol.UPCE,Symbol.ISBN10,Symbol.UPCA,Symbol.EAN13,
                            Symbol.ISBN13,Symbol.I25,Symbol.DATABAR,Symbol.DATABAR_EXP,Symbol.CODABAR,
                            Symbol.CODE39,Symbol.PDF417,Symbol.CODE128,Symbol.CODE93});
                    startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
                } else {
                    Toast.makeText(getActivity(), "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
                }
            }
    }

    public boolean isCameraAvailable() {
        PackageManager pm = getActivity().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    class MyAdapter1 extends BaseAdapter {

        LayoutInflater mInflater = null;

        public MyAdapter1(FragmentActivity activity, List<BarcodeData> contacts) {
            mInflater = LayoutInflater.from(getActivity());
        }


        @Override
        public int getCount() {

            return contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return contacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.warehouse_listitem,
                        null);

                holder.barcode_text = (TextView) convertView.findViewById(R.id.barcode_text);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Log.e("size==>",""+contacts.size());
            holder.barcode_text.setText(contacts.get(position).getBarcode());

            return convertView;
        }

        class ViewHolder {
            TextView barcode_text;
        }

    }

}
