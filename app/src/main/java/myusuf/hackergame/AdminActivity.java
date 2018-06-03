package myusuf.hackergame;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class AdminActivity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    ScoreListAdapter adapter;
    ArrayList<Integer> myList;
    ListView scoresList;
    BluetoothDevice bluetoothDevice;
    boolean mConnected;
    BluetoothGatt mGatt;
    UUID SERVICE_MY_UUID;
    UUID CHARACTERISTIC_MY_UUID;
    UUID SERVICE_WRITE_UUID;
    UUID CHARACTERISTIC_WRITE_UUID;
    UUID SERVICE_BT_UUID;
    UUID CHARACTERISTIC_BT_UUID;
    boolean retry;
    BluetoothGattService myservice;
    BluetoothGattCharacteristic mycharacteristic;
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (!(result.getDevice().getName() == null)) {
                Log.d("BLEScan", "Scanned and found: " + result.getDevice().getName() + " @ " + result.getDevice().getAddress());
                if (result.getDevice().getAddress().equals("00:01:28:46:73:23")) {
                    Log.d("Found device", "Found Central Device");
                    stopScanning();
                    bluetoothDevice = result.getDevice();
                    connectDevice(bluetoothDevice);
                }
            }
        }
    };

    public UUID byteToUUID(byte[] buf) {
        UUID temp;
        long msb = ((buf[0] & 0xFFL) << 56) |
                ((buf[1] & 0xFFL) << 48) |
                ((buf[2] & 0xFFL) << 40) |
                ((buf[3] & 0xFFL) << 32) |
                ((buf[4] & 0xFFL) << 24) |
                ((buf[5] & 0xFFL) << 16) |
                ((buf[6] & 0xFFL) << 8) |
                ((buf[7] & 0xFFL) << 0);

        long lsb = ((buf[8] & 0xFFL) << 56) |
                ((buf[9] & 0xFFL) << 48) |
                ((buf[10] & 0xFFL) << 40) |
                ((buf[11] & 0xFFL) << 32) |
                ((buf[12] & 0xFFL) << 24) |
                ((buf[13] & 0xFFL) << 16) |
                ((buf[14] & 0xFFL) << 8) |
                ((buf[15] & 0xFFL) << 0);
        temp = new UUID(msb, lsb);
        return temp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        byte[] buf = new byte[16];

        buf[0] = (byte) 0x6B;
        buf[1] = (byte) 0x96;
        buf[2] = (byte) 0x2C;
        buf[3] = (byte) 0xAE;
        buf[4] = (byte) 0x2C;
        buf[5] = (byte) 0xEA;
        buf[6] = (byte) 0x4C;
        buf[7] = (byte) 0x43;
        buf[8] = (byte) 0xB1;
        buf[9] = (byte) 0x66;
        buf[10] = (byte) 0x07;
        buf[11] = (byte) 0x51;
        buf[12] = (byte) 0x08;
        buf[13] = (byte) 0xB5;
        buf[14] = (byte) 0xB5;
        buf[15] = (byte) 0xD1;
        SERVICE_MY_UUID = byteToUUID(buf);

        buf[0] = (byte) 0x45;
        buf[1] = (byte) 0x26;
        buf[2] = (byte) 0x3B;
        buf[3] = (byte) 0xCB;
        buf[4] = (byte) 0xFA;
        buf[5] = (byte) 0xFB;
        buf[6] = (byte) 0x41;
        buf[7] = (byte) 0x8C;
        buf[8] = (byte) 0xBD;
        buf[9] = (byte) 0xDC;
        buf[10] = (byte) 0x44;
        buf[11] = (byte) 0x28;
        buf[12] = (byte) 0x1C;
        buf[13] = (byte) 0x5C;
        buf[14] = (byte) 0xB9;
        buf[15] = (byte) 0xF7;
        CHARACTERISTIC_MY_UUID = byteToUUID(buf);

        myList = new ArrayList<>();
        adapter = new ScoreListAdapter(this, myList);
        scoresList = findViewById(R.id.sList);
        scoresList.setAdapter(adapter);

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
        Button getScores = findViewById(R.id.getScores);
        getScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Scores", "Getting Scores");
                myList = new ArrayList<>();
                adapter = new ScoreListAdapter(getApplicationContext(), myList);
                scoresList.setAdapter(adapter);
                startScanning();
            }
        });
    }

    private void setText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new ScoreListAdapter(getApplicationContext(), myList);
                scoresList.setAdapter(adapter);
            }
        });
    }
    public void addItemToList(byte newScore) {
        //Log.d("progress", "adding new Device Named: " + newDevice.getResult().getDevice().getName());
        int k = (newScore & 0xFF) - 50;
        Log.d("SCORE",String.valueOf(k));
        myList.add(k);
        setText();
    }

    public void disconnectGattServer() {
        mConnected = false;
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
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

    private void connectDevice(BluetoothDevice device) {
        GattClientCallback gattClientCallback = new GattClientCallback();
        mGatt = device.connectGatt(this, false, gattClientCallback);
    }

    private boolean readMessage(BluetoothGattCharacteristic characteristic){
        byte[] data;
        data = characteristic.getValue();
        Log.d("DATA LENGTH", "Data length: " + data.length);
        for(int i = 0; i < data.length; i++){
            Log.d("BYTE", String.valueOf(data[i]));
        }
        processScores(data);
        return true;
    }

    private void processScores(byte[] data){
        byte[] b = new byte[10];
        for(int i = 0; i < 10; i++){
            b[i] = data[i];
        }
        for (byte aData : b) {
            addItemToList(aData);
        }
    }


    private class GattClientCallback extends BluetoothGattCallback {
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_FAILURE) {
                Log.d("GATT", "Failure");
                disconnectGattServer();
                try {
                    wait(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connectDevice(bluetoothDevice);
                return;
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d("GATT", "Not Success");
                disconnectGattServer();
                try {
                    wait(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connectDevice(bluetoothDevice);
                return;
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("GATT", "connected");
                mConnected = true;
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("GATT", "disconnected");
                if(retry){
                    Log.d("RETRY","Retrying");
                    retry = false;
                    startScanning();
                }
                else{
                    Log.d("RETRY", "Dont have to retry");
                }
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d("WEIRD","1");
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d("SERVICE","No Success");
                return;
            }
            Log.d("WEIRD","2");
            myservice = gatt.getService(SERVICE_MY_UUID);
            Log.d("UUID", myservice.getUuid().toString());
            Log.d("SERVICE","Before service1");
            mycharacteristic = myservice.getCharacteristic(CHARACTERISTIC_MY_UUID);
            Log.d("UUID", mycharacteristic.getUuid().toString());
            if(mycharacteristic != null){
                Log.d("QUE", "WE OK");
            }
            gatt.readCharacteristic(mycharacteristic);
            Log.d("SERVICE","Reading Message...");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if(status == BluetoothGatt.GATT_SUCCESS){
                Log.d("GATT","Char Read");
            }
            else{
                Log.d("GATT","FAIL");
            }
            readMessage(mycharacteristic);
            disconnectGattServer();
        }
    }
    private class StartScanTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // Log.d("progress", "Running startScan");
            btScanner.startScan(leScanCallback);
            return null;
        }
    }

    private class StopScanTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // Log.d("progress", "Running stopScan");
            btScanner.stopScan(leScanCallback);
            return null;
        }
    }
}
