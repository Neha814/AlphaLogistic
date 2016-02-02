package alphalogistics.com.alphalogistics;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import functions.PrefStore;
import functions.StringUtils;

/**
 * Created by Neha on 12/28/2015.
 */
public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout client_id_layout;
    EditText client_id_edt;
    Button login_btn;
    PrefStore sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        sp = new PrefStore(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
        }

        inIt();
    }

    private void inIt() {
        client_id_edt = (EditText) findViewById(R.id.client_id_edt);
        client_id_layout = (TextInputLayout) findViewById(R.id.client_id_layout);
        login_btn = (Button) findViewById(R.id.login_btn);

        login_btn.setOnClickListener(this);

        client_id_edt.addTextChangedListener(generalTextWatcher);
    }

    private TextWatcher generalTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            if (client_id_edt.getText().hashCode() == s.hashCode()) {

            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

            if (client_id_edt.getText().hashCode() == s.hashCode()) {

            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (client_id_edt.getText().hashCode() == s.hashCode()) {
                client_id_layout.setError(null);
            }

        }

    };

    @Override
    public void onClick(View v) {
        if (v == login_btn) {
            CheckValidations();
        }
    }

    private void CheckValidations() {
        if (StringUtils.getLength(client_id_edt) > 0) {
            sp.SaveClientID(StringUtils.getText(client_id_edt));
            Intent i = new Intent(LoginScreen.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            client_id_layout.setError("Please enter client id.");
        }
    }
}
