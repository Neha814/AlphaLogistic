package Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import alphalogistics.com.alphalogistics.R;

/**
 * Created by Neha on 1/31/2016.
 */
public class NavDeliverFragment extends Fragment implements View.OnClickListener  {
    View rootView;
    Spinner deliver_spinner;
    MyAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_deliver, container, false);
        initialise();
        return rootView;
    }

    private void initialise() {
        deliver_spinner = (Spinner) rootView.findViewById(R.id.deliver_spinner);


        ArrayList<String> menuItems = new ArrayList<String>();
        menuItems.add("Delivered to customer");
        menuItems.add("Other");
        menuItems.add("Refused by customer");
        menuItems.add("12 - partial Damaged");
        menuItems.add("10 - Total Damage");

        mAdapter = new MyAdapter(getActivity(),
                menuItems);
        deliver_spinner.setAdapter(mAdapter);
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

