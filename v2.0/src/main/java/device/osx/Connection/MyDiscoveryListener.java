package device.osx.Connection;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import java.io.IOException;
import java.util.concurrent.locks.Condition;

/**
 * called whenever a bluetooth device is discovered
 */
public class MyDiscoveryListener implements DiscoveryListener {

    private Condition cv;

    /**
     * @param cv - the condition variable signalling a search has
     *             been completed
     */
    MyDiscoveryListener(Condition cv) {
        this.cv = cv;
    }

    /**
     * @param btDevice - the device that was located
     * @param arg - an argument describing the device
     */
    @Override
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg) {
        String name;
        try {
            name = btDevice.getFriendlyName(false);
        } catch (IOException err) {
            name = btDevice.getBluetoothAddress();
        }

        System.out.println("Device Found: " + name);
    }

    /**
     * @param arg - an argument describing the search
     */
    @Override
    public void inquiryCompleted(int arg) {
        synchronized (cv) {
            cv.signal();
        }
    }

    /**
     * @param arg0 - the arguments describing the search
     * @param arg1 - the arguments describing the search
     */
    @Override
    public void serviceSearchCompleted(int arg0, int arg1) {

    }

    /**
     * @param arg0 - the arguments describing the search
     * @param arg1 - the arguments describing the search
     */
    @Override
    public void servicesDiscovered(int arg0, ServiceRecord[] arg1) {

    }
}
