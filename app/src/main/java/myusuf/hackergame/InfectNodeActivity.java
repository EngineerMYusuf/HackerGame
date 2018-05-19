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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

public class InfectNodeActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    ArrayList<mDevice> myList;
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    DeviceListAdapter adapter;
    ListView devicesList;
    Switch scan;
    int sessionID;
    BluetoothDevice bluetoothDevice;
    boolean mConnected;
    BluetoothGatt mGatt;
    UUID SERVICE_UUID;
    UUID CHARACTERISTIC_UUID;
    boolean mInitialized;
    BluetoothGattService service;
    BluetoothGattCharacteristic characteristic;
    int node;
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (!(result.getDevice().getName() == null)) {
                //Log.d("BLEScan", "Scanned and found: " + result.getDevice().getName() + " @ " + result.getDevice().getAddress());
                byte[] a;
                String b = "";
                if (result.getDevice().getAddress().substring(6).equals("57:1A:C0:EB")) {
                    //Log.d("Found device", "Found our device");
                    stopScanning();
                    bluetoothDevice = result.getDevice();
                }
            }
        }
    };
    private class GattClientCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_FAILURE) {
                //Log.d("GATT","Faliure");
                disconnectGattServer();
                return;
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                //Log.d("GATT","Success");
                disconnectGattServer();
                return;
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //Log.d("GATT","connected");
                mConnected = true;
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //Log.d("GATT","disconnected");
                //disconnectGattServer();
            }
        }
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                return;
            }
            service = gatt.getService(SERVICE_UUID);
            characteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            mInitialized = gatt.setCharacteristicNotification(characteristic, false);
            sendMessage();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            //Log.d("GATT", "Char written");
            disconnectGattServer();
            Intent intent = new Intent(getApplicationContext(),QuestionsActivity.class);
            intent.putExtra("HACKER",true);
            intent.putExtra("SESSION_ID", sessionID);
            intent.putExtra("DEVICE",bluetoothDevice);
            startActivity(intent);
        }
    }
    public UUID byteToUUID(byte[] buf){
        UUID temp;
        long msb = ((buf[0] & 0xFFL) << 56) |
                ((buf[1] & 0xFFL) << 48) |
                ((buf[2] & 0xFFL) << 40) |
                ((buf[3] & 0xFFL) << 32) |
                ((buf[4] & 0xFFL) << 24) |
                ((buf[5] & 0xFFL) << 16) |
                ((buf[6] & 0xFFL) <<  8) |
                ((buf[7] & 0xFFL) <<  0) ;

        long lsb = ((buf[8] & 0xFFL) << 56) |
                ((buf[9] & 0xFFL) << 48) |
                ((buf[10] & 0xFFL) << 40) |
                ((buf[11] & 0xFFL) << 32) |
                ((buf[12] & 0xFFL) << 24) |
                ((buf[13] & 0xFFL) << 16) |
                ((buf[14] & 0xFFL) <<  8) |
                ((buf[15] & 0xFFL) <<  0) ;
        temp = new UUID(msb,lsb);
        return temp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infect_node);
        node = 0;
        byte[] buf = new byte[16];
        buf[0] = (byte) 0xE6;
        buf[1] = (byte) 0xC4;
        buf[2] = (byte) 0x4C;
        buf[3] = (byte) 0xFF;
        buf[4] = (byte) 0xE3;
        buf[5] = (byte) 0x04;
        buf[6] = (byte) 0x47;
        buf[7] = (byte) 0xE2;

        buf[8] = (byte) 0xAC;
        buf[9] = (byte) 0xE0;
        buf[10] = (byte) 0x0C;
        buf[11] = (byte) 0x90;
        buf[12] = (byte) 0xD7;
        buf[13] = (byte) 0x29;
        buf[14] = (byte) 0xE0;
        buf[15] = (byte) 0xF7;
        SERVICE_UUID = byteToUUID(buf);

        buf[0] = (byte) 0xD2;
        buf[1] = (byte) 0x78;
        buf[2] = (byte) 0x8E;
        buf[3] = (byte) 0x91;
        buf[4] = (byte) 0xE7;
        buf[5] = (byte) 0xE3;
        buf[6] = (byte) 0x4E;
        buf[7] = (byte) 0x53;
        buf[8] = (byte) 0xA8;
        buf[9] = (byte) 0x2E;
        buf[10] = (byte) 0x5D;
        buf[11] = (byte) 0xB1;
        buf[12] = (byte) 0xB4;
        buf[13] = (byte) 0x92;
        buf[14] = (byte) 0x04;
        buf[15] = (byte) 0x06;
        CHARACTERISTIC_UUID = byteToUUID(buf);
        sessionID = getIntent().getIntExtra("SESSION_ID", 0);

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

        ImageButton node1 = findViewById(R.id.node1);
        node1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Infecting", "Infecting Node 1");
                if(bluetoothDevice != null){
                    node = 1;
                    connectDevice(bluetoothDevice);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please come closer to a node",Toast.LENGTH_LONG).show();
                }
                //disconnectGattServer();
            }
        });
        ImageButton node2 = findViewById(R.id.node2);
        node2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Infecting", "Infecting Node 1");
                if(bluetoothDevice != null){
                    node = 2;
                    connectDevice(bluetoothDevice);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please come closer to a node",Toast.LENGTH_LONG).show();
                }
                //disconnectGattServer();
            }
        });
        ImageButton node3 = findViewById(R.id.node3);
        node3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Infecting", "Infecting Node 1");
                if(bluetoothDevice != null){
                    node = 3;
                    connectDevice(bluetoothDevice);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please come closer to a node",Toast.LENGTH_LONG).show();
                }
                //disconnectGattServer();
            }
        });
        ImageButton node4 = findViewById(R.id.node4);
        node4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Infecting", "Infecting Node 1");
                if(bluetoothDevice != null){
                    node = 4;
                    connectDevice(bluetoothDevice);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please come closer to a node",Toast.LENGTH_LONG).show();
                }
                //disconnectGattServer();
            }
        });
        ImageButton node5 = findViewById(R.id.node5);
        node5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Infecting", "Infecting Node 1");
                if(bluetoothDevice != null){
                    node = 5;
                    connectDevice(bluetoothDevice);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please come closer to a node",Toast.LENGTH_LONG).show();
                }
                //disconnectGattServer();
            }
        });
        ImageButton node6 = findViewById(R.id.node6);
        node6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Infecting", "Infecting Node 1");
                if(bluetoothDevice != null){
                    node = 6;
                    connectDevice(bluetoothDevice);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please come closer to a node",Toast.LENGTH_LONG).show();
                }
                //disconnectGattServer();
            }
        });
        ImageButton node7 = findViewById(R.id.node7);
        node7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Infecting", "Infecting Node 1");
                if(bluetoothDevice != null){
                    node = 7;
                    connectDevice(bluetoothDevice);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please come closer to a node",Toast.LENGTH_LONG).show();
                }
                //disconnectGattServer();
            }
        });
        ImageButton node8 = findViewById(R.id.node8);
        node8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Infecting", "Infecting Node 1");
                if(bluetoothDevice != null){
                    node = 8;
                    connectDevice(bluetoothDevice);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please come closer to a node",Toast.LENGTH_LONG).show();
                }
                //disconnectGattServer();
            }
        });
        ImageButton node9 = findViewById(R.id.node9);
        node9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Infecting", "Infecting Node 1");
                if(bluetoothDevice != null){
                    node = 9;
                    connectDevice(bluetoothDevice);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please come closer to a node",Toast.LENGTH_LONG).show();
                }
                //disconnectGattServer();
            }
        });
        ImageButton node10 = findViewById(R.id.node10);
        node10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Infecting", "Infecting Node 1");
                if(bluetoothDevice != null){
                    node = 10;
                    connectDevice(bluetoothDevice);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please come closer to a node",Toast.LENGTH_LONG).show();
                }
                //disconnectGattServer();
            }
        });

        startScanning();
    }

    private boolean sendMessage() {
        if (!mConnected || !mInitialized) {
            Log.d("GATT","Problem Sending Message: " + mConnected + " " +  mInitialized);
            return false;
        }
        byte[] data = new byte[3];
        switch (node){                                                                              // ToDo messages for infecting nodes
            case 1:
                data[0] = (byte) 0x0F;
                data[1] = (byte) 0xF0;
                data[2] = (byte) 0x03;
                break;
            case 2:
                data[0] = (byte) 0x0F;
                data[1] = (byte) 0xF0;
                data[2] = (byte) 0x03;
                break;
            case 3:
                data[0] = (byte) 0x0F;
                data[1] = (byte) 0xF0;
                data[2] = (byte) 0x03;
                break;
            case 4:
                data[0] = (byte) 0x0F;
                data[1] = (byte) 0xF0;
                data[2] = (byte) 0x03;
                break;
            case 5:
                data[0] = (byte) 0x0F;
                data[1] = (byte) 0xF0;
                data[2] = (byte) 0x03;
                break;
            case 6:
                data[0] = (byte) 0x0F;
                data[1] = (byte) 0xF0;
                data[2] = (byte) 0x03;
                break;
            case 7:
                data[0] = (byte) 0x0F;
                data[1] = (byte) 0xF0;
                data[2] = (byte) 0x03;
                break;
            case 8:
                data[0] = (byte) 0x0F;
                data[1] = (byte) 0xF0;
                data[2] = (byte) 0x03;
                break;
            case 9:
                data[0] = (byte) 0x0F;
                data[1] = (byte) 0xF0;
                data[2] = (byte) 0x03;
                break;
            case 10:
                data[0] = (byte) 0x0F;
                data[1] = (byte) 0xF0;
                data[2] = (byte) 0x03;
                break;
        }
        characteristic.setValue(data);
        boolean success = mGatt.writeCharacteristic(characteristic);
        return success;
    }
    private void connectDevice(BluetoothDevice device) {
        GattClientCallback gattClientCallback = new GattClientCallback();
        mGatt = device.connectGatt(this, false, gattClientCallback);

    }
    public void disconnectGattServer() {
        mConnected = false;
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopScanning();
        finish();
    }

    private void changeItemInList(int i, mDevice newDevice) {
        //Log.d("progress", "changing: " + i + " to " + newDevice.getRssi());
        myList.set(i, newDevice);
        adapter = new DeviceListAdapter(this, myList);
        devicesList.setAdapter(adapter);
    }


    public void startScanning() {
        //Log.d("progress", "Start Scanning");
        StartScanTask t = new StartScanTask();
        t.execute();
    }


    public void stopScanning() {
        //Log.d("progress", "Stopped Scanning");
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
                   // Log.d("progress", "coarse location permission granted");
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
