package blakeolsen.cmu.design2.icrutch.crutchcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    CrutchManagerService service;
    EditText userWeight;
    EditText maxWeight;

    /**
     * called when the app is opened
     *
     * @param savedInstanceState ignored
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.isEnabled()) {
            SharedPreferences prefs_btdev = getSharedPreferences("HC-05", 0);
            String btdevaddr=prefs_btdev.getString("HC-05","?");
            System.out.println("HERE");

            BluetoothAdapter mBlurAdapter= BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = mBlurAdapter.getBondedDevices();
            if (pairedDevices.isEmpty()) {
                Log.e("DeviceActivity ",
                        "Device not founds");
                return ;
            }

            for (BluetoothDevice devices : pairedDevices) {
                Log.d("DeviceActivity", "Device : address : " + devices.getAddress() + " name :"
                        + devices.getName());
            }

            //if (!btdevaddr.equals("?"))
            //{
                BluetoothDevice device = btAdapter.getRemoteDevice("00:18:91:D9:1A:D8");

                //UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // bluetooth serial port service
                UUID SERIAL_UUID = device.getUuids()[0].getUuid(); //if you don't know the UUID of the bluetooth device service, you can get it like this from android cache

                BluetoothSocket socket = null;

                try {
                    socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
                } catch (Exception e) {System.out.println("Error creating socket");}

                try {
                    socket.connect();
                    System.out.println("Connected");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    try {
                        System.out.println("trying fallback...");

                        socket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                        socket.connect();

                        System.out.println("Connected");

                    }
                    catch (Exception e2) {
                        Log.e("", "Couldn't establish Bluetooth connection!");
                    }
                }
            try {
                InputStream in = socket.getInputStream();
                StringBuilder builder = new StringBuilder();
                while (true) {
                    byte data = 0b0000;
                    System.out.println(in.read());
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
            //}
            //else
            //{
            //    Log.e("","BT device not selected");
            //}
        }
        /*
        service = new CrutchManagerService(connectBluetooth(), this);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        createGraph(graph);

        userWeight = (EditText) findViewById(R.id.userWeight);
        maxWeight = (EditText) findViewById(R.id.recommendedWeight);
        Button update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
        */
            /**
             * action when the button is clicked
             *
             * @param v ignored
             *//*
            @Override
            public void onClick(View v) {
                service.updateUserWeight(Double.valueOf(userWeight.getText().toString()));
                service.updateMaxWeight(Double.valueOf(maxWeight.getText().toString()));
            }
        });
        try {
            new Object().wait();
        } catch (Exception err) {
            err.printStackTrace();
        }
        */
    }

    /**
     * createGraph, show the data as it is gathered
     *
     * @param graph the graph
     */
    private void createGraph(GraphView graph) {
        LineGraphSeries<DataPoint> crutchOneSeries = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> crutchTwoSeries = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> estimatedSeries = new LineGraphSeries<>();

        graph.addSeries(crutchOneSeries);
        graph.addSeries(crutchTwoSeries);
        graph.addSeries(estimatedSeries);

        service.addSeries(crutchOneSeries, DataStreamType.CRUTCH_ONE);
        service.addSeries(crutchTwoSeries, DataStreamType.CRUTCH_TWO);
        service.addSeries(estimatedSeries, DataStreamType.ESTIMATED_FORCE);
    }

    /**
     * connect to both crutches
     */
    private BluetoothAdapter connectBluetooth() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Enabled", Toast.LENGTH_LONG).show();
            finish();
        } else if (!btAdapter.isEnabled()) {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }
        return btAdapter;
    }

}