package Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.sourceforge.zbar.Symbol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import alphalogistics.com.alphalogistics.R;
import cz.msebera.android.httpclient.Header;
import functions.NetConnection;
import qrscanner.ZBarConstants;
import qrscanner.ZBarScannerActivity;

/**
 * Created by Neha on 1/31/2016.
 */
public class NavSyncFragment extends Fragment implements View.OnClickListener {

    View rootView;

    EditText date_edt ;
    private Calendar cal;
    private int day;
    private int month;
    private int year;
    Calendar myCalendar;
    Button sync_routes_btn;

    Boolean isConnected;
    private AsyncHttpClient client;
    private ProgressDialog dialog;

    LinearLayout listview;
    LinearLayout ll;

    String TAG = "NavSyncFragment" ;
    View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_sync, container, false);
        initialise();
       return rootView;
    }

    private void initialise() {

        isConnected = NetConnection.checkInternetConnectionn(getActivity());
        client = new AsyncHttpClient();
        client.setTimeout(40*1000);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        date_edt = (EditText) rootView.findViewById(R.id.date_edt);
        sync_routes_btn = (Button) rootView.findViewById(R.id.sync_routes_btn);
        listview = (LinearLayout) rootView.findViewById(R.id.listview);
        ll = (LinearLayout) rootView.findViewById(R.id.ll);
        view = (View) rootView.findViewById(R.id.view);


        view.setVisibility(View.GONE);
        ll.setVisibility(View.GONE);



        date_edt.setClickable(true);
        date_edt.setFocusable(false);
        date_edt.setLongClickable(false);
        date_edt.setFocusableInTouchMode(false);

        sync_routes_btn.setOnClickListener(this);

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


    @Override
    public void onClick(View v) {
        if(v==sync_routes_btn){
            listview.removeAllViews();
            view.setVisibility(View.VISIBLE);
            ll.setVisibility(View.VISIBLE);

           // getSyncRoutes();

            for (int i=0;i<5;i++) {

                LayoutInflater inflater = null;
                inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mLinearView = inflater.inflate(R.layout.warehouse_listitem, null);
                TextView barcode_text = (TextView) mLinearView.findViewById(R.id.barcode_text);
             if(i==0)
                 barcode_text.setText("1122365467878464");

                if(i==1)
                    barcode_text.setText("687675465654654");

                if(i==2)
                    barcode_text.setText("35436547687575776");

                if(i==3)
                    barcode_text.setText("54657567676765777");

                if(i==4)
                    barcode_text.setText("4354365657567567");

                listview.addView(mLinearView);
            }
        }
    }

    //*************************** API ******************************************

    private void getSyncRoutes() {

        RequestParams params = new RequestParams();
        params.put("","");
        //   client.addHeader("Content-Type","application/json");

        Log.e("parameters", params.toString());

        client.post(getActivity(), "http://ship2.als-otg.com/api/AddSortingFacilityBarcodes3/", params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.e(TAG, ""+response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, responseString + "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }
        });
    }
}
