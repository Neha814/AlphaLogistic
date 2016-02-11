package qrscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import alphalogistics.com.alphalogistics.R;
import cz.msebera.android.httpclient.Header;
import database.DatabaseHandler;
import functions.NetConnection;
import functions.StringUtils;
import model.BarcodeData;

public class ZBarScannerActivity extends Activity implements Camera.PreviewCallback, ZBarConstants,
        View.OnClickListener{

    private static final String TAG = "ZBarScannerActivity";
    private CameraPreview mPreview;
    private Camera mCamera;
    private ImageScanner mScanner;
    private Handler mAutoFocusHandler;
    private boolean mPreviewing = true;
    String codes = "";
    MediaPlayer mp;
    ArrayList<String> scannedItems = new ArrayList<String>();
    FrameLayout cameraPreview;

    Button done_btn, back_btn, sync_btn;

    SharedPreferences sp;

    JSONArray array = new JSONArray();

    Boolean isConnected;
    private AsyncHttpClient client;
    private ProgressDialog dialog;

    DatabaseHandler db ;


    static {
        System.loadLibrary("iconv");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        isConnected = NetConnection.checkInternetConnectionn(getApplicationContext());

        setContentView(R.layout.barcode_content);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        db = new DatabaseHandler(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        client = new AsyncHttpClient();
        client.setTimeout(40*1000);
        dialog = new ProgressDialog(ZBarScannerActivity.this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        cameraPreview = (FrameLayout) findViewById(R.id.cameraPreview);
        sync_btn = (Button) findViewById(R.id.sync_btn);
        back_btn = (Button) findViewById(R.id.back_btn);
        done_btn = (Button) findViewById(R.id.done_btn);

        sync_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        done_btn.setOnClickListener(this);

        mp = MediaPlayer.create(ZBarScannerActivity.this,R.raw.beep);

        if (!isCameraAvailable()) {
            // Cancel request if there is no rear-facing camera.
            cancelRequest();
            return;
        }

        mAutoFocusHandler = new Handler();

        // Create and configure the ImageScanner;
        setupScanner();

        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        mPreview = new CameraPreview(this, this, autoFocusCB);
        cameraPreview.addView(mPreview);
      //  setContentView(mPreview);
    }

    public void setupScanner() {
        mScanner = new ImageScanner();
        mScanner.setConfig(0, Config.X_DENSITY, 3);
        mScanner.setConfig(0, Config.Y_DENSITY, 3);

        int[] symbols = getIntent().getIntArrayExtra(SCAN_MODES);
        if (symbols != null) {
            mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
            for (int symbol : symbols) {
                mScanner.setConfig(symbol, Config.ENABLE, 1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        OpenCamera();


    }

    private void OpenCamera() {
        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open();
        if (mCamera == null) {
            // Cancel request if mCamera is null.
            cancelRequest();
            return;
        }

        mPreview.setCamera(mCamera);
        mPreview.showSurfaceView();

        mPreviewing = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        RecreateSurface();


    }

    private void RecreateSurface() {
        try {
            // Because the Camera object is a shared resource, it's very
            // important to release it when the activity is paused.
            if (mCamera != null) {
                mPreview.setCamera(null);
                mCamera.cancelAutoFocus();
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();

                // According to Jason Kuang on http://stackoverflow.com/questions/6519120/how-to-recover-camera-preview-from-sleep,
                // there might be surface recreation problems when the device goes to sleep. So lets just hide it and
                // recreate on resume
                mPreview.hideSurfaceView();

                mPreviewing = false;
                mCamera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("e=>", "" + e);
        }
    }

    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void cancelRequest() {
        Intent dataIntent = new Intent();
        dataIntent.putExtra(ERROR_INFO, "Camera unavailable");
        setResult(Activity.RESULT_CANCELED, dataIntent);
        finish();
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();

        Image barcode = new Image(size.width, size.height, "Y800");
        barcode.setData(data);

        int result = mScanner.scanImage(barcode);

        if (result != 0) {
            turnOnFlashLight();

            SymbolSet syms = mScanner.getResults();
            Log.e("symset==>", "" + syms);
            for (Symbol sym : syms) {

                String symData = sym.getData();
                if (!TextUtils.isEmpty(symData)) {
                    Intent dataIntent = new Intent();
                    dataIntent.putExtra(SCAN_RESULT, symData);
                    dataIntent.putExtra(SCAN_RESULT_TYPE, sym.getType());
                    setResult(Activity.RESULT_OK, dataIntent);
                    Log.e("symData==>", "" + symData);
                    codes = symData + "\n" + codes;


                    Toast.makeText(getApplicationContext(), symData, Toast.LENGTH_SHORT).show();
                    putDataInDb(symData);
                    syms = null;
                    turnOffFlashLight();
                    break;
                }
            }
        }
    }

    private void putDataInDb(String symData) {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());

        db.addContact(new BarcodeData(symData, sp.getString("client_id", ""), formattedDate, "37"));
    }

    private void putDataInAnArray() {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        List<BarcodeData> contacts = db.getAllContacts();
        for(int i=0;i<contacts.size();i++){
            JSONObject object = new JSONObject();
            try{
                //{Barcode:'1234',DriverID:'99',Modified:'02-06-2016',ActivityTypeID:'37'}

                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = df.format(c.getTime());

                object.put("Barcode", contacts.get(i).getBarcode());
                object.put("DriverID", contacts.get(i).getDriverId());
                object.put("Modified", contacts.get(i).getModified());
                object.put("ActivityTypeID",contacts.get(i).getActivityTypeId());
            } catch(Exception e){
                e.printStackTrace();
            }
            array.put(object);
        }

        if(isConnected) {
            submitScannedData();
        }else {
            StringUtils.showDialog("No internet connecyion.Please try again",getApplicationContext());
        }
    }

    private void turnOnFlashLight() {
        Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(p);
        mCamera.startPreview();
        mp.start();

    }

    private void turnOffFlashLight() {

        Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(p);



        RecreateSurface();
        OpenCamera();

    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (mCamera != null && mPreviewing) {
                mCamera.autoFocus(autoFocusCB);
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            mAutoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    @Override
    public void onClick(View v) {
      if(v==back_btn){
          finish();
      }else if(v==done_btn){
          finish();
      }else if(v==sync_btn){
          DatabaseHandler db = new DatabaseHandler(getApplicationContext());
          List<BarcodeData> contacts = db.getAllContacts();
          if(contacts.size()<1){
              Toast.makeText(getApplicationContext(), "No data available to sync.", Toast.LENGTH_SHORT).show();
          } else {
              putDataInAnArray();

          }
      }
    }

    //*************************** API ******************************************

    private void submitScannedData() {

        RequestParams params = new RequestParams();
        params.put("Post",array.toString());
     //   client.addHeader("Content-Type","application/json");

        Log.e("parameters", params.toString());

        client.post(getApplicationContext(), "http://ship2.als-otg.com/api/AddSortingFacilityBarcodes3/", params, new JsonHttpResponseHandler() {

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
                // idhr aaa rha h  wait
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }

                db.deleteWholeData();
                showDialog("Data synced successfully.", ZBarScannerActivity.this);

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

    public void showDialog(String msg, Context context) {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    context).create();


            // Setting Dialog Message
            alertDialog.setMessage(msg);

            // Setting Icon to Dialog
            //	alertDialog.setIcon(R.drawable.browse);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    dialog.cancel();
                    finish();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
