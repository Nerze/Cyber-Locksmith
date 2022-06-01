package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.*;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.mrtan.bluetooth.Bridge.Command;

import javax.crypto.Cipher;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Cipher cip;
    private BluetoothLeScanner scan;
    boolean isScanning=false;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ArrayList<BluetoothDevice> devices;

    ScanCallback bleCallbackFunction = new ScanCallback() {
        @Override
        public void onScanFailed(int errorCode) {
            Toast.makeText(getApplicationContext(), "Couldn't start scan", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onScanResult(int callbacktype, ScanResult result) {
            BluetoothDevice resultDevice = result.getDevice();
            boolean exists=false;
            for (BluetoothDevice aDevice:devices
                 ) {
                if (aDevice.getAddress().equals(resultDevice.getAddress())){
                    exists=true;
                }
            }
                if(!exists){
                    System.out.println("Found new device");
                    addDevice(resultDevice);
                }
        }
        @Override
        public void onBatchScanResults (List<ScanResult> results){
            System.out.println("ScanBatchResult");
            for (ScanResult device:results) {
                addDevice(device.getDevice());
            }
        }
    };
    private Command control;

    @SuppressLint("GetInstance")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("=========================================================");
        System.out.println(System.getProperty("java.library.path"));
        devices=new ArrayList<>();
        super.onCreate(savedInstanceState);
        ScanFilter filter=(new ScanFilter.Builder()).setServiceUuid(ParcelUuid.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")).build();
        ArrayList<ScanFilter> filterList= new ArrayList<>();
        filterList.add(filter);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isScanning){
                    System.out.println("======= Stop Scan");
                    binding.scanButton.setText(R.string.launchScan);
                    scan.stopScan(bleCallbackFunction);
                }
                else {
                    System.out.println("======= Start Scan");
                    BluetoothManager manage = (BluetoothManager) getApplicationContext().getSystemService(BLUETOOTH_SERVICE);
                    scan = manage.getAdapter().getBluetoothLeScanner();
                    scan.startScan(filterList, (new ScanSettings.Builder()).build(), bleCallbackFunction);
                    binding.scanButton.setText(R.string.stopScan);
                }
                isScanning=!isScanning;
            }
        });
    }

    private void addDevice(BluetoothDevice device){
        System.out.println(device.getAddress());
        devices.add(device);
        LayoutParams params =
                new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
        Button deviceButton= new Button(this);
        deviceButton.setText(device.getAddress());
        deviceButton.setLayoutParams(params);
        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hackDevice(device);
            }
        });
        binding.devicesLayout.addView(deviceButton);
    }

    private void hackDevice(BluetoothDevice device){
        System.out.println("Hacking device "+device.getAddress());
        NetworkHandler net= new NetworkHandler(device);
        this.control=new Command(net);
        net.init(control);
        String deviceAddr=device.getAddress();
        control.connect(deviceAddr,true);
        control.command(deviceAddr,commandType.VERSION);
        byte[] received=new byte[]{(byte) 0xaa,0x55,0x45,0x01,0x05,0x00,0x01, (byte) 0x80, (byte) 0xa6,0x00,0x02,0x73,0x02};
        control.receiveData(deviceAddr,received);
        net.getToken();
    }
}