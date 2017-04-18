package device.osx.Connection;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * connects via bluetooth using bluecove library
 *
 * surfs the locations of every bluetooth
 */
public class ConnectToBluetooth {

    public void connect() {
        Lock lock = new ReentrantLock();
        Condition cv = lock.newCondition();

        try {
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            DiscoveryAgent agent = localDevice.getDiscoveryAgent();
            agent.startInquiry(DiscoveryAgent.GIAC, new MyDiscoveryListener(cv));
            try {
                synchronized (cv) {
                    cv.await();
                }
            } catch (InterruptedException err) {
                throw new RuntimeException(err);
            }
        } catch (BluetoothStateException err) {
            throw new RuntimeException(err);
        }
    }

}
