package com.example.myapplication;

public interface Callback {
    void receiveData(String address, int code, int ret, String data);

    void sendData(String address, byte[] data);
}
