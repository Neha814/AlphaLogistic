package Fragments;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import alphalogistics.com.alphalogistics.R;

/**
 * Created by nehabh on 2/4/2016.
 */
public class NavBluetoothScannerFragment extends Fragment implements View.OnClickListener {

    View rootView;
    BluetoothAdapter BTAdapter;
    BluetoothDevice mDevice;
    Thread mConnectThread, mConnectedThread;
    String TAG = "NavBluetoothScannerFragment";
    TextView bt_enabled_devices_tv;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_btscanner, container, false);
        initialise();
        return rootView;
    }

    private void initialise() {
        BTAdapter = BluetoothAdapter.getDefaultAdapter();

        bt_enabled_devices_tv = (TextView) rootView.findViewById(R.id.bt_enabled_devices_tv);
        // Phone does not support Bluetooth so let the user know and exit.
        //Determine if Android supports Bluetooth
        if (BTAdapter == null) {
            Log.e("if","if");
            new AlertDialog.Builder(getActivity())
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Log.e("else", "else");
            //Turn on Bluetooth if disabled
            if (!BTAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, 1);
            } else {
                getBluetoothDevices();

       /* form the connection between the Android and the bluetooth scanner device*/
                // creating the connection thread

                Log.e(TAG, "device=>"+mDevice);
                mConnectThread = new ConnectThread(mDevice);
                mConnectThread.start();
            }
        }

    }

    private void getBluetoothDevices() {

        // Get the Bluetooth module device
        Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            String deviceName = "";
            for (BluetoothDevice device : pairedDevices) {
                mDevice = device;

                Log.e(TAG,"device="+mDevice+" name="+mDevice.getName());

                deviceName = deviceName+mDevice+" , "+mDevice.getName()+"\n\n\n";

            }

            bt_enabled_devices_tv.setText(deviceName);
        }
    }

    @Override
    public void onClick(View v) {

    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {
            BTAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }

             /*Creating the data transfer thread Results*/
            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }


    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int)msg.arg1;
            int end = (int)msg.arg2;
            switch(msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    break;
            }
        }
    };


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while (true) {
                try {
                    bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    for (int i = begin; i < bytes; i++) {
                        if (buffer[i] == "#".getBytes()[0]) {
                            mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if (i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG,"requestCode="+requestCode+" resultCode=>"+resultCode+" data=>"+data);

        if(BTAdapter.isEnabled()){
            getBluetoothDevices();

       /* form the connection between the Android and the bluetooth scanner device*/
            // creating the connection thread

            Log.e(TAG, "device=>"+mDevice);
            mConnectThread = new ConnectThread(mDevice);
            mConnectThread.start();
        }
    }
}

