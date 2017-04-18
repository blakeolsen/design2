package device.osx;

import device.osx.Connection.ConnectToBluetooth;

/**
 * receives data transmission from bluetooth module on arduino
 * displays information graphically and writes information to file
 */
public class Main {
    /**
     * begin the program
     */
    public static void main(String[] args) {
        ConnectToBluetooth connector = new ConnectToBluetooth();
        connector.connect();
    }
}