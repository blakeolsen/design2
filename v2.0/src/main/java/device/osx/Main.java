package device.osx;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * receives data transmission from bluetooth module on arduino
 * displays information graphically and writes information to file
 */
public class Main {
    // names of all devices attempting to find
    private static String[] BluetoothDevices = { };

    // all of the connected devices
    private Map<String, RemoteDevice> Connections;

    // locks all of the remote devices
    private Map<String, Lock> DeviceReady;


    /**
     * begin the program
     */
    public static void main(String[] args) {
        Main runner = new Main();
        runner.setup();
        try {
            while (true) {
                runner.loop();
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * establish bluetooth connection and prepare display for new data
     */
    private void setup() {
        tryConnect();

    }

    /**
     * continuously loop and receive new data from the arduino bluetooth
     * update display with new data
     */
    private void loop() throws Exception {

    }

    /**
     * begins attempting to connect to arduino bluetooth
     */
    private void tryConnect() {
        try {
            LocalDevice connection = LocalDevice.getLocalDevice();
            DiscoveryAgent agent = connection.getDiscoveryAgent();
            agent.startInquiry(DiscoveryAgent.GIAC, new bluetoothListener());
        } catch (BluetoothStateException err) {
            err.printStackTrace();
        }
    }

    /**
     * an event listener responsible for finding and connecting to
     *      known bluetooth devices
     */
    private class bluetoothListener implements DiscoveryListener {

        /**
         * called whenever the a bluetooth device is detected
         * @param device - the device that was connected
         * @param type - the classifications of the device
         */
        @Override
        public void deviceDiscovered(RemoteDevice device, DeviceClass type) {
            String name = null;
            try {
                name = device.getFriendlyName(false);
            } catch (IOException err) {
                err.printStackTrace();
            }

            if (Connections.containsKey(name)) {
                DeviceReady.get(name).lock();
                System.out.println("Connected to: " + device.getBluetoothAddress());

                BluetoothAdapter
                Connections.put(name, device.);
                DeviceReady.get(name).unlock();
            }
        }

        /**
         * called when the entire discovery has finished
         * @param discType - the status of the discovery
         */
        @Override
        public void inquiryCompleted(int discType) {
            // TODO: Not Yet Implemented
        }

        /**
         * called when a service is found during a service search
         * @param transID - the number of the transaction
         * @param serviceRecords - all services found
         */
        @Override
        public void servicesDiscovered(int transID, ServiceRecord[] serviceRecords) {
            // TODO: Not Yet Implemented
        }

        /**
         * called when a search has completed
         * @param transID - the number of the transaction
         * @param respCode - the status of the transaction
         */
        @Override
        public void serviceSearchCompleted(int transID, int respCode) {
            // TODO: Not Yet Implemented
        }

    }

}


public class Main {

    public static void main(String[] args) {
        //TODO: Create GUI

    }



}