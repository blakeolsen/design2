package blakeolsen.cmu.design2.icrutch.crutchcontrol;

import android.bluetooth.BluetoothSocket;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * communicates with the crutch
 *
 * sends over the user weight and the maximum load on the injured limb
 *
 * continuously reads output force from the crutch
 */
public class CrutchCommunicator implements Runnable {
    private static final int MAX_DATAPOINTS = 100;
    private final byte[] CONF = {
            0x01,
            0x02
    };

    LineGraphSeries<DataPoint> master;
    LineGraphSeries<DataPoint> slave;
    LineGraphSeries<DataPoint> estimated;
    InputStream in;
    int count;


    /**
     * Initiate the communicator
     *
     * @param socket create communication socket
     * @param graph the updating graph
     */
    CrutchCommunicator(BluetoothSocket socket, GraphView graph) throws IOException {
        this.in = socket.getInputStream();
        this.master = new LineGraphSeries<>();
        graph.addSeries(this.master);
        this.slave = new LineGraphSeries<>();
        graph.addSeries(this.slave);
        this.estimated = new LineGraphSeries<>();
        graph.addSeries(this.estimated);

        this.count = 0;
    }

    /**
     * main runner
     */
    public void run() {
        try {
            in.skip(in.available());
        } finally {
            while (true) {
                float f;
                byte identifier;

                byte[] bytes = new byte[4];
                while (true) {
                    try {
                        if ((byte) in.read() == '\n') {
                            identifier = (byte) in.read();
                            for (int i = 0; i < 4; i++) {
                                bytes[i] = (byte) in.read();
                            }
                            f = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                            break;
                        }
                    } catch (IOException err) {
                        err.printStackTrace();
                    }
                }

                if (count % 15 == 0 || count % 15 == 1 || count % 15  == 2) {
                    DataPoint point = new DataPoint(count, f);
                    if (identifier == 'm') { // slave data point
                        master.appendData(point, true, MAX_DATAPOINTS);
                        //System.out.println("MASTER: " + String.valueOf(f));
                    } else if (identifier == 'o') { // master data point
                        slave.appendData(point, true, MAX_DATAPOINTS);
                        //System.out.println("SLAVE: " + String.valueOf(f));
                    } else if (identifier == 'e') { // estimated data poin
                        estimated.appendData(point, true, MAX_DATAPOINTS);
                        //System.out.println("ESTIMATED: " + String.valueOf(f));
                    } else if (identifier == 'f') {
                        System.out.println("OVER MAX: "+String.valueOf(f));
                    } else if (identifier == 'n') {
                        System.out.println("UNDER MAX: "+String.valueOf(f));
                    }
                }
                count++;
            }
        }
    }

}
