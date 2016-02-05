package alphalogistics.com.alphalogistics;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.sourceforge.zbar.Symbol;

import Fragments.NavBluetoothScannerFragment;
import Fragments.NavDeliverFragment;
import Fragments.NavDiretionsFragment;
import Fragments.NavLoadTruckFragment;
import Fragments.NavSettingsFragment;
import Fragments.NavSyncFragment;
import Fragments.NavWarehouseFragment;
import qrscanner.ZBarConstants;
import qrscanner.ZBarScannerActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int ZBAR_SCANNER_REQUEST = 0;
    private static final int ZBAR_QR_SCANNER_REQUEST = 1;

    private static final int GALLERY_IAMGE = 2;
    private static final int CAMERA_IMAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                ScanItems();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(6).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displayView(id);

        return true;
    }

    private void displayView(int id) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        if (id == R.id.nav_sync) {
            fragment = new NavSyncFragment();
            title  = "Sync";
        } else if (id == R.id.nav_load_truck) {
            fragment = new NavLoadTruckFragment();
            title  = "Load Truck";
        } else if (id == R.id.nav_deliver) {
            fragment = new NavDeliverFragment();
            title  = "Deliver";
        } else if (id == R.id.nav_directions) {
            fragment = new NavDiretionsFragment();
            title  = "Directions";
        } else if (id == R.id.nav_settings) {
            fragment = new NavSettingsFragment();
            title  = "Settings";
        } else if (id == R.id.nav_warehouse) {
            fragment = new NavWarehouseFragment();
            title  = "Warehouse";
        } else if(id == R.id.nav_btscanner){
            fragment = new NavBluetoothScannerFragment();
            title  = "Bluetooth Scanner";
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    private void ScanItems() {
        if (isCameraAvailable()) {
            Intent intent = new Intent(getApplicationContext(), ZBarScannerActivity.class);
            intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE,Symbol.NONE
            ,Symbol.PARTIAL,Symbol.EAN8,Symbol.UPCE,Symbol.ISBN10,Symbol.UPCA,Symbol.EAN13,
                    Symbol.ISBN13,Symbol.I25,Symbol.DATABAR,Symbol.DATABAR_EXP,Symbol.CODABAR,Symbol.CODE39
                    ,Symbol.PDF417,Symbol.CODE93,Symbol.CODE128});
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
        } else {
            Toast.makeText(getApplicationContext(), "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isCameraAvailable() {
        PackageManager pm = getApplicationContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
