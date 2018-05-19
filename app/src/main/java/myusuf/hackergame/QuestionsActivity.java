package myusuf.hackergame;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
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
import android.os.ParcelUuid;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class QuestionsActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    BluetoothLeAdvertiser btAdvertiser;

    boolean hacker;
    int questionCount;
    FrameLayout frame;
    AnswerListAdapter adapter;
    ListView answersList;
    ArrayList<answers> myList;
    Questions questions;
    ArrayList<String> questionsList;
    ArrayList<ArrayList<String>> answerList;
    int[] randomNumbers;
    Random rnd = new Random();
    boolean answerIsTrue = false;
    boolean answerCame = false;
    TextView questionNumber;
    TextView questiontxt;
    int ans;
    boolean gameEnded;
    int sessionID;
    BluetoothDevice bluetoothDevice;
    boolean mConnected;
    BluetoothGatt mGatt;
    BluetoothGattServer mGattServer;
    UUID SERVICE_UUID;
    UUID SERVICE_SERVER_UUID;
    UUID CHARACTERISTIC_UUID;
    UUID CHARACTERISTIC_SERVER_UUID;
    boolean mInitialized;
    BluetoothGattService service;
    BluetoothGattCharacteristic characteristic;
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
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.d("adv", "Peripheral advertising started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.d("adv", "Peripheral advertising failed: " + errorCode);
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
        setContentView(R.layout.activity_questions);
        hacker = getIntent().getBooleanExtra("HACKER", true);
        bluetoothDevice = getIntent().getParcelableExtra("DEVICE");
        frame = findViewById(R.id.questionFrame);

        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        byte[] buf = new byte[16];                                                                  // ToDo chose nice UUID's
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
        SERVICE_SERVER_UUID = byteToUUID(buf);

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
        CHARACTERISTIC_SERVER_UUID = byteToUUID(buf);
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
        if (hacker) {
            questionCount = 5;
            frame.setBackgroundResource(R.drawable.hackerbackground);
        } else {
            questionCount = 2;
            frame.setBackgroundResource(R.drawable.policebackground);
        }
        myList = new ArrayList<>();
        answersList = findViewById(R.id.answerList);
        adapter = new AnswerListAdapter(this, myList);
        answersList.setAdapter(adapter);

        // Select questioncount number of random numbers
        randomNumbers = new int[questionCount];
        int max = 1;
        int min = 0;
        for (int i = 0; i < questionCount; i++) {
            randomNumbers[i] = rnd.nextInt((max - min) + 1) + min;
        }
        questionNumber = findViewById(R.id.questionNumber);
        questiontxt = findViewById(R.id.questionText);
        Quiz q = new Quiz();
        q.execute();
        answersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {
                //Log.d("progress", "You have clicked the ID: " + id + " Position: " + position);

                if (position == ans) {
                    Toast.makeText(getApplicationContext(), "Answer is true", Toast.LENGTH_SHORT).show();
                    answerIsTrue = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Answer is wrong", Toast.LENGTH_SHORT).show();
                }
                answerCame = true;
            }
        });
        btAdvertiser = btAdapter.getBluetoothLeAdvertiser();
        GattServerCallback gattServerCallback = new GattServerCallback();
        mGattServer = btManager.openGattServer(this, gattServerCallback);
        setupServer();
        startAdvertising();

    }

    private void setupServer() {
        BluetoothGattService serviceServer = new BluetoothGattService(SERVICE_SERVER_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        mGattServer.addService(serviceServer);
        BluetoothGattCharacteristic writeCharacteristic = new BluetoothGattCharacteristic(
                CHARACTERISTIC_SERVER_UUID,
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
        serviceServer.addCharacteristic(writeCharacteristic);
    }

    private void startAdvertising() {
        if (btAdvertiser == null) {
            return;
        }
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .build();
        ParcelUuid parcelUuid = new ParcelUuid(SERVICE_SERVER_UUID);
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(parcelUuid)
                .build();

        btAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
    }

    protected void onPause() {
        super.onPause();
        stopAdvertising();
        stopServer();
    }

    private void stopServer() {
        if (mGattServer != null) {
            mGattServer.close();
        }
    }

    private void stopAdvertising() {
        if (btAdvertiser != null) {
            btAdvertiser.stopAdvertising(mAdvertiseCallback);
        }
    }

    public void disconnectGattServer() {
        mConnected = false;
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
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

    private boolean sendMessage() {
        if (!mConnected || !mInitialized) {
            Log.d("GATT", "Problem Sending Message: " + mConnected + " " + mInitialized);
            return false;
        }
        byte[] data = new byte[3];
        data[0] = (byte) 0x56;
        data[1] = (byte) 0x57;
        data[2] = (byte) 0x58;
        characteristic.setValue(data);
        boolean success = mGatt.writeCharacteristic(characteristic);
        return success;
    }

    private void connectDevice(BluetoothDevice device) {
        GattClientCallback gattClientCallback = new GattClientCallback();
        mGatt = device.connectGatt(this, false, gattClientCallback);

    }

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
        }
    }

    private class GattServerCallback extends BluetoothGattServerCallback {
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            if (characteristic.getUuid().equals(CHARACTERISTIC_SERVER_UUID)) {
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
                byte end = (byte) 0x55;
                if(value[0] == end){
                    endGame(false);
                }
            }

        }
    }

    public void endGame(boolean won){
        Intent intent = new Intent(getApplicationContext(),EndGameActivity.class);
        intent.putExtra("WON",won);
        if(won){
            connectDevice(bluetoothDevice);
        }
        startActivity(intent);
    }

    private class Quiz extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            questions = new Questions();
            questionsList = questions.getQuestions();
            answerList = questions.getAnswers();
            String questionText = "";
            int index = 0;
            while (index < questionCount) {
                questionNumber.setText("Question " + index);

                questionText = questionsList.get(randomNumbers[index]);
                questiontxt.setText(questionText);
                ans = 0;
                for (int j = 0; j < 4; j++) {
                    String s = answerList.get(index).get(j);
                    if (s.substring(0, 1).equals("T")) {
                        ans = j;
                    }
                    answers a = new answers(false, s);
                    myList.add(a);
                }
                adapter = new AnswerListAdapter(getApplicationContext(), myList);
                answersList.setAdapter(adapter);
                while (!answerCame) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                answerCame = false;
                if (answerIsTrue) {
                    index++;
                    answerIsTrue = false;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            endGame(true);
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
