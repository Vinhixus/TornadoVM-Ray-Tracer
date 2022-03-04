package com.vinhderful.raytracer.misc;

public class TornadoDeviceInfo {

    private final int driverIndex;
    private final int deviceIndex;
    private final String driverName;
    private final String deviceName;
    private final boolean isTornadoDevice;

    public TornadoDeviceInfo(String driverName, String deviceName, boolean isTornadoDevice) {
        driverIndex = -1;
        deviceIndex = -1;
        this.driverName = driverName;
        this.deviceName = deviceName;
        this.isTornadoDevice = isTornadoDevice;
    }

    public TornadoDeviceInfo(int driverIndex, int deviceIndex, String driverName, String deviceName, boolean isTornadoDevice) {
        this.driverIndex = driverIndex;
        this.deviceIndex = deviceIndex;
        this.driverName = driverName;
        this.deviceName = deviceName;
        this.isTornadoDevice = isTornadoDevice;
    }

    public int getDriverIndex() {
        return driverIndex;
    }

    public int getDeviceIndex() {
        return deviceIndex;
    }

    public boolean isTornadoDevice() {
        return isTornadoDevice;
    }

    public String getName() {
        return "(" + driverName + ") " + deviceName;
    }
}
