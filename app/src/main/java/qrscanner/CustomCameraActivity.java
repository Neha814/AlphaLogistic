package qrscanner;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import java.io.IOException;
import android.app.Activity;

import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;

import alphalogistics.com.alphalogistics.R;

/**
 * Created by Neha on 2/8/2016.
 */
public class CustomCameraActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback, ZBarConstants {

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;
    private ImageScanner mScanner;
    String codes="";
    private Handler mAutoFocusHandler;
    private boolean mPreviewing = true;
    //  private Camera mCamera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);*/

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.custom, null);
        LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);

        setupScanner();
        mAutoFocusHandler = new Handler();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(previewing){
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null){
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
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
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();

        Image barcode = new Image(size.width, size.height, "Y800");
        barcode.setData(data);

        int result = mScanner.scanImage(barcode);

        if (result != 0) {
            //    turnOnFlashLight();
                /* mCamera.cancelAutoFocus();
                 mCamera.setPreviewCallback(null);
                  mCamera.release();
                // mCamera.stopPreview();
                 mPreviewing = false;*/
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
                    Toast.makeText(getApplicationContext(), codes, Toast.LENGTH_SHORT).show();
                    // finish();
                    syms = null;
                    //    turnOffFlashLight();
                    break;
                }
            }
        }}

      /*  private void turnOnFlashLight() {
            Camera.Parameters p = mCamera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(p);
            mCamera.startPreview();
            // mp.start();
        }*/


   /* private void turnOffFlashLight() {
        Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(p);
        RecreateSurface();
        OpenCamera();
    }*/

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if(camera != null && mPreviewing) {
                camera.autoFocus(autoFocusCB);
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            mAutoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
}
