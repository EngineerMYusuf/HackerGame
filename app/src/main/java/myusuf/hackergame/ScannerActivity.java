package myusuf.hackergame;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class ScannerActivity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    DeviceListAdapter adapter;
    ListView devicesList;
    ArrayList<mDevice> myList;
    String[] callbackString;
    SharedPreferences dataBase;
    Switch scan;
    int sessionID;
    BluetoothDevice device;

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (!(result.getDevice().getName() == null)) {
                Log.d("BLEScan", "Scanned and found: " + result.getDevice().getName() + " @ " + result.getDevice().getAddress());
                byte[] a;
                String b = "";
                if (result.getScanRecord().getBytes() != null) {
                    a = result.getScanRecord().getBytes();
                    for (int j = 0; j < a.length; j++) {
                        b = b + ";" + a[j];
                    }
                }
                for (int i = 0; i < myList.size() + 1; i++) {
                    if (i == myList.size()) {

                        Log.d("BLEScan", "Found new Device called: " + result.getDevice().getAddress() + " Scan Record byte: " + b);
                        if (result.getDevice().getAddress().substring(6).equals("28:46:73:23")) {
                            Log.d("BLEScan", "Its from us");
                            boolean infected = false;
                            byte inf = (byte) 0x05;
                            byte res = (byte) result.getScanRecord().getBytes()[28];
                            Log.d("BLESCAN", "28th byte is: " + res);
                            if (inf == res) {
                                Log.d("BLEScan", "Found an infected node");
                                infected = true;
                            }
                            mDevice newDevice = new mDevice(result, infected, result.getRssi());
                            addItemToList(newDevice);
                        }
                        break;
                    }
                    mDevice oldDevice = myList.get(i);
                    if ((result.getDevice().getAddress().equals(oldDevice.getResult().getDevice().getAddress())) && (result.getRssi() == oldDevice.getResult().getRssi())) {
                         Log.d("BLEScan", "I saw you before Mr. " + result.getDevice().getAddress() + " You have not changed...");
                        break;
                    } else if (result.getDevice().getAddress().equals(oldDevice.getResult().getDevice().getAddress())) {
                        Log.d("BLEScan", result.getDevice().getAddress() + " changed its range to " + result.getRssi());
                        oldDevice.setRssi(result.getRssi());
                        changeItemInList(i, oldDevice);
                        break;
                    }
                }

            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        scan.setChecked(false);
        finish();
    }
    @Override
    public void onResume() {
        super.onResume();
        myList = new ArrayList<>();
        adapter = new DeviceListAdapter(this, myList);
        devicesList.setAdapter(adapter);
    }

    private void changeItemInList(int i, mDevice newDevice) {
        //Log.d("progress", "changing: " + i + " to " + newDevice.getRssi());
        myList.set(i, newDevice);
        adapter = new DeviceListAdapter(this, myList);
        devicesList.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        sessionID = getIntent().getIntExtra("SESSION_ID", 0);

        devicesList = findViewById(android.R.id.list);

        myList = new ArrayList<>();

        adapter = new DeviceListAdapter(this, myList);
        devicesList.setAdapter(adapter);

        // BLE Stuff

        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);


        if (btManager.getAdapter() != null && !btManager.getAdapter().isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();
        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
        if (!btAdapter.isMultipleAdvertisementSupported()) {
            Toast.makeText(getApplicationContext(), "This device cannot advertise. End of game notification disabled", Toast.LENGTH_LONG).show();
        }

        // Scan Switch
        scan = (Switch) findViewById(R.id.scan);
        //boolean scanState = scan.isChecked();

        scan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {
                    startScanning();
                } else {
                    stopScanning();
                }
            }
        });
        devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {
                //Log.d("progress", "You have clicked the ID: " + id + " Position: " + position);
                if (myList.get(position).isInfected()) {
                    device = myList.get(position).getResult().getDevice();
                    goToQuestions(myList.get(position).getResult().getDevice());
                } else {
                    Toast.makeText(getApplicationContext(), "This is not an infected node",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public byte getNode(){
        String s = device.getAddress().substring(0,2);
        return hexStringToByteArray(s)[0];
    }

    public void goToQuestions(BluetoothDevice device) {
        scan.setChecked(false);
        Intent intent = new Intent(this, QuestionsActivity.class);                          // Go to Questions activity
        intent.putExtra("HACKER", false);
        intent.putExtra("NODE", getNode());
        intent.putExtra("SESSION_ID", sessionID);
        startActivity(intent);
    }

    public void startScanning() {
        Log.d("progress", "Start Scanning");
        StartScanTask t = new StartScanTask();
        t.execute();
    }


    public void stopScanning() {
        Log.d("progress", "Stopped Scanning");
        StopScanTask t = new StopScanTask();
        t.execute();
    }

    public void addItemToList(mDevice newDevice) {
        //Log.d("progress", "adding new Device Named: " + newDevice.getResult().getDevice().getName());
        myList.add(newDevice);
        adapter = new DeviceListAdapter(this, myList);
        devicesList.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Log.d("progress", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    private class StartScanTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("progress", "Running startScan");
            btScanner.startScan(leScanCallback);
            return null;
        }
    }

    private class StopScanTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //Log.d("progress", "Running stopScan");
            btScanner.stopScan(leScanCallback);
            return null;
        }
    }
}
