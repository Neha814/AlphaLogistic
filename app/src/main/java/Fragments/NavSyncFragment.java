package Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.zbar.Symbol;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import alphalogistics.com.alphalogistics.R;
import qrscanner.ZBarConstants;
import qrscanner.ZBarScannerActivity;

/**
 * Created by Neha on 1/31/2016.
 */
public class NavSyncFragment extends Fragment  {

    View rootView;

    EditText date_edt ;
    private Calendar cal;
    private int day;
    private int month;
    private int year;
    Calendar myCalendar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_sync, container, false);
        initialise();
       return rootView;
    }

    private void initialise() {

        date_edt = (EditText) rootView.findViewById(R.id.date_edt);


        date_edt.setClickable(true);
        date_edt.setFocusable(false);
        date_edt.setLongClickable(false);
        date_edt.setFocusableInTouchMode(false);

        long date1 = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String dateString = sdf.format(date1);
        date_edt.setText(dateString);


         myCalendar = Calendar.getInstance();
        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                view.setMinDate(System.currentTimeMillis() - 1000);

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd MMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                date_edt.setText(sdf.format(myCalendar.getTime()));


            }

        };

        date_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }



}
