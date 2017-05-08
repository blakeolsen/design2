package blakeolsen.cmu.design2.icrutch.crutchcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jjoe64.graphview.GraphView;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    /**
     * called when the app is opened
     *
     * @param savedInstanceState ignored
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothSocket socket = getSocket();
        GraphView graph = (GraphView)findViewById(R.id.graph);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(500);

        try {
            CrutchCommunicator comm = new CrutchCommunicator(socket, graph);
            new Thread(comm).start();
        } catch (IOException err) {
            err.printStackTrace();
            finish();
        }
    }

    private BluetoothSocket getSocket() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.isEnabled()) {
            BluetoothAdapter mBlurAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = mBlurAdapter.getBondedDevices();
            if (pairedDevices.isEmpty()) {
                Log.e("DeviceActivity ",
                        "Device not founds");
                return null;
            }

            for (BluetoothDevice devices : pairedDevices) {
                Log.d("DeviceActivity", "Device : address : " + devices.getAddress() + " name :"
                        + devices.getName());
            }

            BluetoothDevice device = btAdapter.getRemoteDevice("00:18:91:D9:1A:52");

            UUID SERIAL_UUID = device.getUuids()[0].getUuid(); //if you don't know the UUID of the bluetooth device service, you can get it like this from android cache

            BluetoothSocket socket = null;

            try {
                socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
            } catch (Exception e) {
                System.out.println("Error creating socket");
            }

            try {
                socket.connect();
                System.out.println("Connected");
                return socket;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                try {
                    System.out.println("trying fallback...");

                    socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                    socket.connect();

                    System.out.println("Connected");
                    return socket;
                } catch (Exception e2) {
                    Log.e("", "Couldn't establish Bluetooth connection!");
                }
            }
        }
        return null;
    }


}