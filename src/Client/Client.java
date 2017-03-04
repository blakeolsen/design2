import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener;
import java.util.Date;
import java.util.Enumeration;

/**
 * Client
 * def: This is the client side code of the sensor crutch. It is responsible for
 * recieving the data from the actuator, writing that data to file, and
 * presenting a real time data stream of the incoming force measurements
 * <p>
 * Libraries:
 * - gnu.io - included in the RXTXcomm.jar file
 */
public class Client implements SerialPortEventListener {
    private static final String PORT_NAME = "/dev/tty.usbmodem1411";
    SerialPort serialPort;
    /**
     * A BufferedReader which will be fed by a InputStreamReader
     * converting the bytes into characters
     * making the displayed results codepage independent
     */
    private BufferedReader input;
    /**
     * The output stream to the port
     */
    private OutputStream output;
    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port.
     */
    private static final int DATA_RATE = 9600;
    /**
     * File where data is written
     **/
    private BufferedWriter data_file;
    /**
     * Records the time until new datapoints are recorded
     */
    private StopWatch time;


    /**
     * initialize
     * def: connect to the serial stream of the arduino
     */
    public void initialize()
            throws Exception {
        // initialize the port reader
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            System.out.println(currPortId.getName());
            if (currPortId.getName().equals(PORT_NAME)) {
                portId = currPortId;
                break;
            }
        }

        if (portId == null) {
            throw new Exception("Could Not Connect to Port");
        }

        // open serial port, and use class name for the appName.
        serialPort = (SerialPort) portId.open(this.getClass().getName(),
                TIME_OUT);

        // set port parameters
        serialPort.setSerialPortParams(DATA_RATE,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);

        // create the data_file
        DateFormat df = new SimpleDateFormat("MM:dd_HH:mm:ss");
        Date date = new Date();
        String FILENAME = System.getProperty("user.dir")+"/../../data/data_"+df.format(date)+".txt";
        data_file = new BufferedWriter(new FileWriter(FILENAME));

        // open the streams
        input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        output = serialPort.getOutputStream();

        // add event listeners
        serialPort.addEventListener(this);
        serialPort.notifyOnDataAvailable(true);

        // start the timer
        time = new StopWatch();
        time.start();
    }

    /**
     * close
     * def: cleans up the event listener
     */
    public synchronized void close() 
            throws Exception {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
            data_file.close();
        }
    }

    /**
     * serialEvent
     * def: action when a new datapoint is written
     *
     * @param oEvent - the event
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            String inputLine = "";
            double data_point;
            try {
                inputLine = input.readLine();
                data_point = Double.parseDouble(inputLine);
            } catch (NumberFormatException e) {
                // If the arduino did not throw a data point
                System.out.println("[OUTPUT] "+inputLine);
                return;
            } catch (Exception e) {
                System.out.println("[ERROR] "+e);
                return;
            }
            System.out.println("Data: "+data_point);

            // Record the Data in a file
            try {
                data_file.write(time.lap()+"\t"+inputLine);
                data_file.newLine();
                data_file.flush();
            } catch (Exception e) {
                System.out.println("[ERROR] "+e);
            }
        }
    }

    /**
     * main
     * def: program runner
     *
     * @param args - not checked
     * @throws Exception - throws any downstream exception
     */
    public static void main(String[] args) 
            throws Exception {
        Client main = new Client();
        main.initialize();
        Thread t = new Thread() {
            public void run() {
                //the following line will keep this app alive for 1000 seconds,
                //waiting for events to occur and responding to them (printing incoming messages to console).
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException ie) {
                    try {
                        main.close();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        };
        t.start();
        System.out.println("Started");
        System.out.println("________________________________________________________________________________");
    }

    /**
     * Stopwatch
     * def: finds the amount of time the program has been running
     */
    public class StopWatch {
        private double starttime;
        private boolean started;

        /**
         * Stopwatch
         * def: initiate watch
         */
        public StopWatch() {
            started = false;
        }

        /** 
         * Start
         * def: start the watch
         */
        public void start() throws Exception {
            if (started) {
                throw new Exception("Timer Already Started");
            }
            starttime = System.currentTimeMillis();
            started = true;
        }

        /**
         * lap
         * def: get the elapsed time after the start
         */
        public double lap() throws Exception {
            if (!started) {
                throw new Exception("Timer Hasn't Been Started");
            }
            return (System.currentTimeMillis()-starttime)/1000;
        }
    }
}







