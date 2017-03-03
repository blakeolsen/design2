package design2.prototype1.clientside;

import design2.prototype1.clientside.Exceptions.UnableToFindComPortException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 * SerialTest
 * def: This is the client side code of the sensor crutch. It is responsible for
 *		recieving the data from the actuator, writing that data to file, and
 * 		presenting a real time data stream of the incoming force measurements
 *
 * Libraries:
 * 	- gnu.io - included in the RXTXcomm.jar file
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
    /** The output stream to the port */
    private OutputStream output;
    /** Milliseconds to block while waiting for port open */
    private static final int TIME_OUT = 2000;
    /** Default bits per second for COM port. */
    private static final int DATA_RATE = 9600;

    /**
     * initialize
     * def: connect to the serial stream of the arduino
     */
    public void initialize()
            throws Exception {
    	// initialize the port reade
    CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			if (currPortId.getName().equals(PORT_NAME)) {
				portId = currPortId;
				break;
			}
		}

		if (portId == null) {
            throw new UnableToFindComPortException();
		}

        // open serial port, and use class name for the appName.
        serialPort = (SerialPort) portId.open(this.getClass().getName(),
                TIME_OUT);

        // set port parameters
        serialPort.setSerialPortParams(DATA_RATE,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);

        // open the streams
        input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        output = serialPort.getOutputStream();

        // add event listeners
        serialPort.addEventListener(this);
        serialPort.notifyOnDataAvailable(true);

    }

    /**
     * close
     * def: cleans up the event listener
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * serialEvent
     * def: action when a new datapoint is written
     * @param oEvent - the event
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine = input.readLine();
                System.out.println(inputLine);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    /**
     * main
     * def: program runner
     * @param args - not checked
     * @throws Exception - throws any downstream exception
     */
    public static void main(String[] args) throws Exception {
        Client main = new Client();
        main.initialize();
        Thread t = new Thread() {
            public void run() {
                //the following line will keep this app alive for 1000 seconds,
                //waiting for events to occur and responding to them (printing incoming messages to console).
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException ie) {

                }
            }
        };
        t.start();
        System.out.println("Started");
    }
}