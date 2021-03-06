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
    boolean won;
    boolean retry = false;
    boolean hacker;
    int questionCount;
    FrameLayout frame;
    AnswerListAdapter adapter;
    ListView answersList;
    ArrayList<answers> myList;
    Questions questions;
    ArrayList<String> questionsList;
    ArrayList<ArrayList<answers>> answerList;
    int randomNumbers;
    Random rnd = new Random();
    boolean answerIsTrue = false;
    boolean answerCame = false;
    TextView questionNumber;
    TextView questiontxt;
    int ans;
    boolean gameEnded;
    Integer sessionID;
    BluetoothDevice bluetoothDevice;
    boolean mConnected;
    BluetoothGatt mGatt;
    UUID SERVICE_MY_UUID;
    UUID CHARACTERISTIC_MY_UUID;
    UUID SERVICE_READ_UUID;
    UUID CHARACTERISTIC_READ_UUID;
    boolean mInitialized;
    BluetoothGattService myservice;
    BluetoothGattCharacteristic mycharacteristic;
    BluetoothGattCharacteristic readcharacteristic;
    boolean correct;
    byte node;
    boolean found = false;


    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (!(result.getDevice().getName() == null)) {
                Log.d("BLEScan", "Scanned and found: " + result.getDevice().getName() + " @ " + result.getDevice().getAddress());
                if (result.getDevice().getAddress().substring(6).equals("28:46:73:23") && result.getRssi() > -70) {
                    //Log.d("Found device", "Found our device");
                    stopScanning();
                    if(!found){
                        found = true;
                        bluetoothDevice = result.getDevice();
                        connectDevice(bluetoothDevice);
                    }
                    else{
                        //Log.d("SCAN", "Already found ma man");
                    }
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
        setContentView(R.layout.activity_questions);
        hacker = getIntent().getBooleanExtra("HACKER", true);
        node = getIntent().getByteExtra("NODE", (byte) 0x00);
        int k = getIntent().getIntExtra("SESSION_ID", 99);
        sessionID = k;
        frame = findViewById(R.id.questionFrame);

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
        SERVICE_MY_UUID = byteToUUID(buf);

        buf[0] = (byte) 0x09;
        buf[1] = (byte) 0x6F;
        buf[2] = (byte) 0x3D;
        buf[3] = (byte) 0x98;
        buf[4] = (byte) 0x1F;
        buf[5] = (byte) 0x90;
        buf[6] = (byte) 0x49;
        buf[7] = (byte) 0xF9;
        buf[8] = (byte) 0xB1;
        buf[9] = (byte) 0x1D;
        buf[10] = (byte) 0x22;
        buf[11] = (byte) 0x28;
        buf[12] = (byte) 0x6B;
        buf[13] = (byte) 0xF3;
        buf[14] = (byte) 0x0C;
        buf[15] = (byte) 0x21;
        CHARACTERISTIC_READ_UUID = byteToUUID(buf);

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
        if (hacker) {
            frame.setBackgroundResource(R.drawable.hackerbackground);
        } else {
            frame.setBackgroundResource(R.drawable.policebackground);
        }
        myList = new ArrayList<>();
        answersList = findViewById(R.id.answerList);
        adapter = new AnswerListAdapter(this, myList);
        answersList.setAdapter(adapter);

        randomNumbers = rnd.nextInt(8);                                                     // question count + 1
        questiontxt = findViewById(R.id.questionText);
        questionNumber = findViewById(R.id.questionNumber);

        answersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {
                Log.d("progress", "You have clicked the ID: " + id + " Position: " + position);
                                                                                                    // ToDo maybe wait?
                if (position == ans) {
                    Toast.makeText(getApplicationContext(), "Answer is true", Toast.LENGTH_SHORT).show();
                    doCorrectTask();
                } else {
                    Toast.makeText(getApplicationContext(), "Answer is wrong", Toast.LENGTH_LONG).show();
                    doWrongTask();
                }
            }
        });
        showQuestion(randomNumbers);
    }

    public void showQuestion(int random) {
        questions = new Questions();
        questionsList = questions.getQuestions();
        answerList = questions.getAnswers();
        String qText = questionsList.get(random);
        for (int j = 0; j < 4; j++) {
            answers a = answerList.get(random).get(j);
            if (a.isCorrect()) {
                ans = j;
            }

            myList.add(a);
        }
        setText(questionNumber, questiontxt, "Question", qText);
    }

    public void doCorrectTask() {
        correct = true;
        startScanning();
    }

    public void doWrongTask() {
        correct = false;
        startScanning();
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

    private boolean sendMessage() {
        if (!mConnected || !mInitialized) {
            Log.d("GATT", "Problem Sending Message: " + mConnected + " " + mInitialized);
            return false;
        }
        byte[] data = new byte[3];
        data[0] = (byte) 0x94;
        data[1] = sessionID.byteValue();
        if (correct) {
            if (hacker) {
                // this is the hacker (0x1NODE)
                data[2] = (byte) (node + 0x10);
            } else {
                // this is the police (0x0NODE)
                data[2] = (byte) (node + 0x00);
            }
        } else {
            // this can be anyone
            data[2] = (byte) 0x20;
        }
        Log.d("CHAR DATA","Setting Char to: " + data[0] + " " + data[1] + " " + data[2]);
        mycharacteristic.setValue(data);
        Log.d("SET","setValue");
        return mGatt.writeCharacteristic(mycharacteristic);
    }

    private void connectDevice(BluetoothDevice device) {
        GattClientCallback gattClientCallback = new GattClientCallback();
        mGatt = device.connectGatt(this, false, gattClientCallback);
    }

    private void setText(final TextView t1, final TextView t2, final String v1, final String v2) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                t1.setText(v1);
                t2.setText(v2);
                adapter = new AnswerListAdapter(getApplicationContext(), myList);
                answersList.setAdapter(adapter);
            }
        });
    }

    public void goToScan() {
        Intent intent = new Intent(getApplicationContext(), ScannerActivity.class);
        intent.putExtra("SESSION_ID", sessionID);
        startActivity(intent);
    }

    public void goToInfect() {
        Intent intent = new Intent(getApplicationContext(), InfectNodeActivity.class);
        intent.putExtra("SESSION_ID", sessionID);
        startActivity(intent);
    }

    private class GattClientCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_FAILURE) {
                Log.d("GATT", "Faliure");
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
                    found = false;
                    startScanning();
                }
                else{
                    Log.d("RETRY", "Dont have to retry");
                }
                //disconnectGattServer();
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                return;
            }
            myservice = gatt.getService(SERVICE_MY_UUID);
            mycharacteristic = myservice.getCharacteristic(CHARACTERISTIC_READ_UUID);
            mycharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            mInitialized = gatt.setCharacteristicNotification(mycharacteristic, false);
            Log.d("SERVICE","Sending Message...");
            boolean b = sendMessage();
            if(!b){
                Log.d("SUCC","WRONG");
                retry = true;
                disconnectGattServer();
            }
            else{
                Log.d("SUCC","RIGHT");
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d("GATT", "Char written");
            disconnectGattServer();
            found = false;
            if (correct) {
                finish();
            } else {
                randomNumbers = rnd.nextInt(8);
                showQuestion(randomNumbers);
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
