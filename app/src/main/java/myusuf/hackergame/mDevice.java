package myusuf.hackergame;


import android.bluetooth.le.ScanResult;

public class mDevice {
    private ScanResult result;
    private boolean infected;
    private int rssi;

    public mDevice(ScanResult result, boolean infected, int rssi){
        this.result = result;
        this.infected = infected;
        this.rssi = rssi;
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    public ScanResult getResult() {
        return result;
    }

    public void setResult(ScanResult result) {
        this.result = result;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
