package com.mrtan.bluetooth.Bridge;

import com.example.myapplication.Callback;
import com.example.myapplication.commandType;

import java.util.Arrays;

public class Command {
    private long handler;
    static {
        System.loadLibrary("lock");
    }

    public Command(Callback callback) {
        this.handler = -1L;
        this.handler = init(callback);
    }

    private void check() {
        if (this.handler == -1) {
            throw new IllegalStateException("destroy has bean called");
        }
    }

    private static native void commandData(long j, String str, int i, byte[] bArr);

    private static native void connect(long j, String str, int i);

    private static native void destroy(long j);

    private static native void disconnect(long j, String str);

    private static native long init(Callback callback);

    private static native boolean isDeviceSupportOffline(long j, String str);

    private static native void receiveData(long j, String str, byte[] bArr);

    private static native void updateDeviceGap(long j, String str, int i);

    public void command(String deviceAddress, commandType aCommand) {
        System.out.println("Command : "+deviceAddress+" "+aCommand);
        check();
        commandData(this.handler, deviceAddress, aCommand.ordinal(), new byte[0]);
    }

    public void commandData(String str, commandType theCommand, byte[] bArr) {
        check();
        commandData(this.handler, str, theCommand.getValue(), bArr);
    }

    public void connect(String str, boolean z) {
        System.out.println("Connect : "+str+" "+z);
        check();
        if (z) {
            connect(this.handler, str, 1);
        } else {
            connect(this.handler, str, 0);
        }
    }

    public void destroy() {
        long j = this.handler;
        if (j != -1) {
            destroy(j);
            this.handler = -1L;
        }
    }

    public void disconnect(String str) {
        check();
        disconnect(this.handler, str);
    }

    public boolean isDeviceSupportOffline(String str) {
        check();
        return isDeviceSupportOffline(this.handler, str);
    }

    public void receiveData(String str, byte[] bArr) {
        System.out.println("ReceiveData : "+str+" "+ Arrays.toString(bArr));
        check();
        receiveData(this.handler, str, bArr);
    }

    public void updateDeviceGap(String str, int i) {
        check();
        updateDeviceGap(this.handler, str, i);
    }
}
