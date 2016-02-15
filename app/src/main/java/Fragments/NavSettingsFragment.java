package Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import alphalogistics.com.alphalogistics.MainActivity;
import alphalogistics.com.alphalogistics.R;

/**
 * Created by Neha on 1/31/2016.
 */
public class NavSettingsFragment extends Fragment implements View.OnClickListener {

    View rootView;

    EditText client_id_edt;
    Button save_btn, logout_btn;
    SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_settings, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initialise();
        return rootView;
    }

    private void initialise() {
        client_id_edt = (EditText) rootView.findViewById(R.id.client_id_edt);
        save_btn = (Button) rootView.findViewById(R.id.save_btn);
        logout_btn = (Button) rootView.findViewById(R.id.logout_btn);

        save_btn.setOnClickListener(this);
        logout_btn.setOnClickListener(this);

        String clientID= sp.getString("client_id","");
        client_id_edt.setText(clientID);
    }

    @Override
    public void onClick(View v) {
        if(v== save_btn){
            String clientID= client_id_edt.getText().toString();
            int length = clientID.trim().length();
            if(length>0){
                SharedPreferences.Editor e = sp.edit();
                e.putString("client_id",clientID);
                e.commit();
            } else {
                client_id_edt.setError("Please enter driver id first.");
            }
        } else if(v==logout_btn){
            ShowLogoutDialog("Are you sure you want to logout ?");
        }
    }

    protected void ShowLogoutDialog(String msg) {
        final Dialog dialog;
        dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);

        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(0);
        dialog.getWindow().setBackgroundDrawable(d);
        TextView static_tv, message;
        Button yes_bt, no_bt;

        dialog.setContentView(R.layout.logout_dialog);
        static_tv = (TextView) dialog.findViewById(R.id.static_tv);
        message = (TextView) dialog.findViewById(R.id.message);
        yes_bt = (Button) dialog.findViewById(R.id.yes_bt);
        no_bt = (Button) dialog.findViewById(R.id.no_bt);


        message.setText(msg);

        dialog.show();

        no_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //    logout_tv.setEnabled(true);
            }
        });
        yes_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //    logout_tv.setEnabled(true);
                CallLogoutAPI();
            }
        });

    }

    private void CallLogoutAPI() {

        sp.edit().clear().commit();
        getActivity().finish();
    }

}
