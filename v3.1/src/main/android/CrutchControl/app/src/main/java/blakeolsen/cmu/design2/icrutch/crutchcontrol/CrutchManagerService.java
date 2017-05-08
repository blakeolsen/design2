package blakeolsen.cmu.design2.icrutch.crutchcontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A separate background service responsible for managing the connection
 * between the phone and the crutches
 */

public class CrutchManagerService implements DataAnalyzer {
    private static final String CRUTCH_ONE = "HC-05";
    private static final String CRUTCH_TWO = "HC-05-01";
    private static final int MAX_POINTS = 50;

    Activity scope;

    // All of the data series that are being watched
    private Map<DataStreamType, Collection<LineGraphSeries<DataPoint>>> series;

    // The individual crutches
    private Crutch crutch_one;
    private Crutch crutch_two;
    private Thread crutch_one_thread;
    private Thread crutch_two_thread;

    // the user information
    private Double userWeight;
    private Double maxWeight;

    /**
     * run when the service is started
     * begins the bluetooth connections with each crutch
     */
    public CrutchManagerService(BluetoothAdapter btAdapter, Activity activity) {
        this.userWeight = null;
        this.maxWeight = null;
        this.scope = activity;
        this.series = new HashMap<>();

        BluetoothConnector.BluetoothSocketWrapper wrapper = null;
        for (BluetoothDevice device : btAdapter.getBondedDevices()) {
            if (device.getName().equals(CRUTCH_ONE)) {
                try {
                    System.out.println("NEW CONNECTOR");
                    BluetoothConnector conn1 = new BluetoothConnector(device, true, btAdapter, null);
                    System.out.println("WRAPPER");
                    wrapper = conn1.connect();
                    System.out.println("CONNECTED WRAPPER");
                    wrapper.connect();
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        }

        while (true) {
            try {
                InputStream in = wrapper.getInputStream();
                while (true) {
                    System.out.println("READ");
                    byte[] data = new byte[Double.SIZE];
                    in.read(data);
                    System.out.println(Double.valueOf(String.valueOf(data)));
                }
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
    }

    /**
     * adds a data series to be updated
     *
     * @param data the data series that is to be updated
     * @param type the type of data stream
     */
    void addSeries(LineGraphSeries<DataPoint> data, DataStreamType type) {
        if (!series.containsKey(type)) {
            series.put(type, new ArrayList<LineGraphSeries<DataPoint>>());
        }
        series.get(type).add(data);
    }

    /**
     * stop updating the data series
     *
     * @param data the data series that is to be updated
     * @param type the type of the data stream
     * @return whether the data stream was removed
     */
    boolean removeSeries(LineGraphSeries<DataPoint> data, DataStreamType type) {
        return series.get(type).remove(data);
    }

    /**
     * update the estimated weight of the user
     *
     * @param weight the weight of the user
     */
    void updateUserWeight(double weight) {
        userWeight = weight;
    }

    /**
     * update the maximum weight that can be put on the injured limb
     *
     * @param weight the maximum weight
     */
    void updateMaxWeight(double weight) {
        maxWeight = weight;
    }

    /**
     * how to handle the service stopping
     */
    public void close() {
        crutch_one_thread.interrupt();
        crutch_two_thread.interrupt();
    }

    /**
     * used to update data stream when new items are read
     *
     * @param data the data point received
     * @param type the type of data point
     */
    @Override
    public void onReceive(double data, DataStreamType type) {
        DataPoint point = new DataPoint(new Date(), data);
        for (LineGraphSeries<DataPoint> stream : series.get(type)) {
            stream.appendData(point, true, MAX_POINTS);
        }
    }
}
