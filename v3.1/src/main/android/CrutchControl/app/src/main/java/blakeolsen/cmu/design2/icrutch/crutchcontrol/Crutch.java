package blakeolsen.cmu.design2.icrutch.crutchcontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.UUID;

import static blakeolsen.cmu.design2.icrutch.crutchcontrol.R.id.parent;

/**
 * manages the connection of the crutch and the app
 *
 * handles communication between the crutch and the app
 */

public class Crutch implements Runnable {
    private static final int MAX_RETRY = 10;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private DataStreamType type;
    private String name;
    private BluetoothAdapter btAdapter;
    private DataAnalyzer analyzer;
    private BluetoothSocket socket;
    private BufferedOutputStream out;
    private InputStream in;
    private Activity scope;

    /**
     * initialize the adapter, ensure that the we are
     * able to connect with the device
     *
     * @param name the name of the device
     * @param btAdapter the bluetooth adapter
     * @param analyzer analyzes the readings from the crutch
     */
    Crutch(DataStreamType type, String name, BluetoothAdapter btAdapter, DataAnalyzer analyzer, Activity scope) {
        this.type = type;
        this.name = name;
        this.btAdapter = btAdapter;
        this.analyzer = analyzer;
        this.scope = scope;
    }

    /**
     * provide the name of the crutch
     *
     * @return the name of the crutch
     */
    public String getName() {
        return name;
    }

    /**
     * the main runner thread
     */
    public void run() {
        while (true) { // continuously attempt to connect to the device
            connect();
            System.out.println("CONNECTED");
            while (true) {
                try {
                    analyzer.onReceive(read(), type);
                } catch (Exception err) {
                    err.printStackTrace();
                    break;
                }
            }
        }

    }

    /**
     * attempts to connect to the device, if unable then throws exception
     */
    private void connect() {
        while (true) {
            for (BluetoothDevice bt : btAdapter.getBondedDevices()) {
                if (bt.getName().equals(name)) {
                    try {
                        BluetoothDevice device = btAdapter.getRemoteDevice(bt.getAddress());
                        this.socket = device.createRfcommSocketToServiceRecord(myUUID);
                        this.socket.connect();
                        btAdapter.cancelDiscovery();

                        System.out.println(this.socket.isConnected());

                        this.out = new BufferedOutputStream(socket.getOutputStream());
                        this.in = socket.getInputStream();
                        return;
                    } catch (Exception err) {
                        err.printStackTrace();
                        //Toast.makeText(parent.getApplicationContext(), "Unable to Connect to Device: "+this.name, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            //Toast.makeText(scope.getApplicationContext(), "Unable to Locate Device: "+this.name, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * attempts to send the current state, if unable to send, attempts
     * to reconnect and try again
     *        the crutch
     */
    public void send(byte msg) {
        try {
            out.write(msg);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    /**
     * attempts to read the current force reading, if unable to send, attempts
     * to reconnect and try again
     *
     * @return the force reading of the crutch
     * @throws IOException if unable to connect to the board
     */
    private double read() throws IOException {
        if (in.available() > 0) {
            System.out.println("FILLED BUFFER");
            byte[] reading = new byte[Double.SIZE];
            in.read(reading);
            double value = Double.valueOf(String.valueOf(reading));
            System.out.println(value);
            return value;
        }
        return 0.0;
    }

    /**
     * disconnects the crutch
     */
    private void disconnect() {
        if (socket!=null) //If the btSocket is busy
        {
            try {
                socket.close(); //close connection
            } catch (IOException err) {
                System.out.println("Failed to Disconnect From: "+name);
            }
        }
    }

}
