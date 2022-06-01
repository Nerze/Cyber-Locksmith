package com.example.myapplication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
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
import java.util.Base64;

public class NetworkHandler implements okhttp3.Callback, com.example.myapplication.Callback {
    private final Cipher cip;
    private final Cipher decip;
    private final BluetoothDevice device;
    private final OkHttpClient client = new OkHttpClient();
    private final MediaType JSON= MediaType.get("application/json; charset=utf-8");
    private static final byte[] KEY="tprjrA5kkKyvh4nw".getBytes();
    public Command control;

    private final int lockId=71891;
    private String accessToken;

    @SuppressLint("GetInstance")
    public NetworkHandler(BluetoothDevice device){
        this.device=device;
        try {
            cip = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cip.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(KEY, "AES"));
            decip = Cipher.getInstance("AES/ECB/PKCS5Padding");
            decip.init(Cipher.DECRYPT_MODE,new SecretKeySpec(KEY, "AES"));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public void init(Command comm){
        this.control=comm;
    }
    public void getToken() {
        String params;
        try {
            params= Base64.getEncoder().encodeToString(cip.doFinal("{\"mail\":\"groupe5bleu@protonmail.com\",\"password\":\"E9Rdam3736k$*Mw9>3f7a2twt\"}".getBytes()));
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
        Request request= new Request.Builder()
                .url("https://consapi.tapplock.com/api/v1/users/login")
                .post(RequestBody.create(params,JSON))
                .build();
        client.newCall(request).enqueue(this);
    }

    private void challenge(String random){
        Request request= new Request.Builder()
                .url("https://consapi.tapplock.com/api/v1/common/generateKeyTL2?lockId="+ this.lockId+"&randNumber="+random)
                .header("Authorization","Bearer "+this.accessToken)
                .build();
        client.newCall(request).enqueue(this);
    }

    private String decryptResponse(Response response) throws IllegalBlockSizeException, BadPaddingException, IOException {
        assert response.body() != null;
        String strMess = response.body().string();
        byte[] message = Base64.getDecoder().decode(strMess);
        strMess = new String(decip.doFinal(message));
        return strMess;
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {

    }

    @SuppressLint("GetInstance")
    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        try {
            if (response.code()==200) {
                String url= String.valueOf(response.request().url());
                if (url.contains("/api/v1/users/login")) {
                    String strMess = this.decryptResponse(response);
                    this.accessToken = new JSONObject(strMess).getJSONObject("data").getString("accessToken");
                    System.out.println("Access Token : "+accessToken);
                    byte[] received=new byte[]{};
                    control.commandData(this.device.getAddress(),commandType.RANDOM,received);
                    received = new byte[]{(byte) 0xaa, 0x55, 0x01, 0x03, 0x05, 0x00, 0x01, 0x06, (byte) 0xaa, (byte) 0xb5, 0x2d, (byte) 0x9b, 0x02};
                    control.receiveData(device.getAddress(), received);
                }
                else if (url.contains("/api/v1/common/generateKeyTL2")){
                    String strMess = this.decryptResponse(response);
                    String challengeResponse=new JSONObject(strMess).getString("data");
                    System.out.println("Challenge Response : "+challengeResponse);
                    byte[] data= new byte[challengeResponse.length()/2];
                    for (int i = 0; i < challengeResponse.length()/2; i++) {
                        data[i]= (byte) Integer.parseInt(challengeResponse.substring(i*2,i*2+2),16);
                    }
                    control.commandData(device.getAddress(),commandType.VERIFY,data);
                    byte[] received = new byte[]{(byte) 0xaa,0x55,0x02,0x03,0x01,0x00,0x01,0x06,0x01};
                    control.receiveData(device.getAddress(),received);
                }
                else{
                    System.out.println("Unknown url : "+url);
                }
            }
            else{
                System.err.println("Could not connect : \nCode : "+response.toString()+"\nBody:"+response.body().string());
            }
        } catch (IllegalBlockSizeException | BadPaddingException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void pair(){
        String addr=this.device.getAddress();
        this.control.commandData(addr,commandType.PAIR,new byte[]{0x50,0x57,0x25,0x15, (byte) 0x84, (byte) 0xae,0x0b, (byte) 0xb2});
        String tabRet="70306d8264676e243fc8a7344b7c3c70";
        receiveBluetoothData(addr, tabRet);

        /*control.commandData(addr,commandType.TL1HIS,new byte[]{});
        tabRet="b36258104601603fd01d73a7ec30753a";
        receiveBluetoothData(addr, tabRet);

        tabRet="4d389752886473ef2b64f3d668ffd8c2";
        receiveBluetoothData(addr, tabRet);

        control.commandData(addr,commandType.BATTERY,new byte[]{});
        tabRet="53a4f0a1e7fbabc3ca708da8f38c3177";
        receiveBluetoothData(addr, tabRet);

        control.commandData(addr,commandType.UNLOCK,new byte[]{});
        tabRet="5fa50d71072ece6e206d67b80b29aec4";
        receiveBluetoothData(addr, tabRet);


        tabRet="3d81a6cbd4d177de9e7ee46adecbeefc";
        receiveBluetoothData(addr, tabRet);*/

    }

    private void receiveBluetoothData(String addr, String tabRet) {
        byte[] data;
        data= new byte[tabRet.length()/2];
        for (int i = 0; i < tabRet.length()/2; i++) {
            data[i]= (byte) Integer.parseInt(tabRet.substring(i*2,i*2+2),16);
        }
        control.receiveData(addr,data);
    }

    @Override
    public void receiveData(String address, int code, int returnValue, String data) {
        if(code==2) {
            this.challenge(data);
        }
        else if (code==8){
            this.pair();
        }
        else{
            System.out.println("======== Receiving data ========");
            System.out.println("Address : "+ address);
            System.out.println("Code : "+code);
            System.out.println("Ret : "+returnValue);
            System.out.println("Data : "+ data);
        }

    }

    @Override
    public void sendData(String address, byte[] data) {

    }
}
