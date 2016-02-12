package Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


import alphalogistics.com.alphalogistics.R;
import cz.msebera.android.httpclient.Header;
import functions.NetConnection;

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
    LinearLayout ll,ll2;

    String TAG = "NavSyncFragment" ;
    View view;

    SharedPreferences sp;

    ArrayList<HashMap<String, String>> SyncRoutesList = new ArrayList<HashMap<String, String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_sync, container, false);
        initialise();
       return rootView;
    }

    private void initialise() {
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
        ll2 = (LinearLayout) rootView.findViewById(R.id.ll2);
        view = (View) rootView.findViewById(R.id.view);


        view.setVisibility(View.GONE);
        ll.setVisibility(View.GONE);
        ll2.setVisibility(View.GONE);

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


            String inputDate = date_edt.getText().toString();
            String inputFormat = "dd MMM yyyy";
            String outputFormat = "yyyy-MM-dd";    //2016-02-10

            String dateToSend = functions.StringUtils.formateDateFromstring(inputFormat, outputFormat, inputDate);

            if(isConnected) {
                getSyncRoutes(dateToSend);
            } else {
                functions.StringUtils.showDialog("No internet connection.",getActivity());
            }
        }
    }

    //*************************** API ******************************************

    private void getSyncRoutes(String dateToSend) {

        // http://ship2.als-otg.com/api/GetDeliveryDetailsByDate3/5678/2016-02-10

        RequestParams params = new RequestParams();
        params.put("","");

        Log.e("date", dateToSend);
        Log.e("client_id", sp.getString("client_id",""));

        client.post(getActivity(), "http://ship2.als-otg.com/api/GetDeliveryDetailsByDate3/"+
                sp.getString("client_id","")+"/"+dateToSend, params, new JsonHttpResponseHandler() {

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
                    view.setVisibility(View.VISIBLE);
                    ll.setVisibility(View.VISIBLE);
                    ll2.setVisibility(View.VISIBLE);
                    Log.e(TAG, ""+response);
                    JSONArray array = response.getJSONArray("Table");
                    int size = array.length();
                    for(int i=0;i<size;i++){
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("user1",array.getJSONObject(i).getString("user1"));
                        map.put("Route",array.getJSONObject(i).getString("Route"));
                        map.put("StopNum",array.getJSONObject(i).getString("StopNum"));
                        map.put("TrackingID",array.getJSONObject(i).getString("TrackingID"));
                        map.put("CompanyID",array.getJSONObject(i).getString("CompanyID"));
                        map.put("notes",array.getJSONObject(i).getString("notes"));
                        map.put("loadnum", array.getJSONObject(i).getString("loadnum"));

                        SyncRoutesList.add(map);
                    }

                    showRoutes();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, responseString + "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    view.setVisibility(View.GONE);
                    ll.setVisibility(View.GONE);
                    ll2.setVisibility(View.GONE);

                    functions.StringUtils.showDialog("No route found",getActivity());
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

    private void showRoutes() {
        int size = SyncRoutesList.size();
        for (int i=0;i<size;i++) {

            LayoutInflater inflater = null;
            inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mLinearView = inflater.inflate(R.layout.sync_listitem, null);
            TextView route_tv = (TextView) mLinearView.findViewById(R.id.route_tv);
            TextView stop_tv = (TextView) mLinearView.findViewById(R.id.stop_tv);
            TextView peices_tv = (TextView) mLinearView.findViewById(R.id.peices_tv);

            route_tv.setText(SyncRoutesList.get(i).get("Route"));
            stop_tv.setText(SyncRoutesList.get(i).get("StopNum"));
            peices_tv.setText(SyncRoutesList.get(i).get("loadnum"));

            listview.addView(mLinearView);
        }
    }
}
